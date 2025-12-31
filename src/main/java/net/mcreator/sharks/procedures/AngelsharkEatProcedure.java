package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.BlockPos;

// --- ANGELSHARK IMPORT ---
import net.mcreator.sharks.entity.AngelsharkEntity;
import net.mcreator.sharks.init.BenssharksModItems;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class AngelsharkEatProcedure {

    // --- CONFIG: ONE-BITE LOGIC ---
    // Capacity is small (5.0). Threshold is 1.0. 
    // Since mostly all food has nutrition >= 1, one bite = FULL.
    private static final double MAX_STOMACH = 5.0; 
    private static final double FULL_THRESHOLD = 2.0; 

    // --- HELPER: Is this an Angelshark? ---
    private static boolean isValidShark(Entity entity) {
        return entity instanceof AngelsharkEntity;
    }

    // --- HELPER: Is this entity Food? (Same list as before) ---
    private static boolean isFood(Entity entity) {
        // You can add/remove prey specific to Angelsharks here if you want
        return entity instanceof net.minecraft.world.entity.animal.Animal 
            || entity instanceof net.minecraft.world.entity.animal.WaterAnimal 
            || entity instanceof net.minecraft.world.entity.monster.Zombie
            || entity instanceof net.mcreator.sharks.entity.BarracudaEntity
            || entity instanceof net.mcreator.sharks.entity.SardineEntity; 
    }

    /**
     * EVENT 1: HAND FEEDING (Fish Bucket)
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() == null) return;
        Entity target = event.getTarget();

        if (isValidShark(target) && target instanceof AngelsharkEntity shark) {
            boolean hasBucketMain = event.getEntity().getMainHandItem().getItem() == BenssharksModItems.FISH_BUCKET.get();
            boolean hasBucketOff = event.getEntity().getOffhandItem().getItem() == BenssharksModItems.FISH_BUCKET.get();

            if (hasBucketMain || hasBucketOff) {
                if (!event.getLevel().isClientSide()) {
                    
                    double stomachAmount = target.getPersistentData().getDouble("SharkStomach");
                    
                    // FULL CHECK (Threshold = 1.0)
                    if (stomachAmount >= FULL_THRESHOLD) {
                        event.getEntity().displayClientMessage(Component.literal("The Angelshark is full."), true);
                        event.setCanceled(true); 
                        return;
                    } 
                    
                    // FILL STOMACH
                    // Even a small amount fills it up completely due to low threshold
                    target.getPersistentData().putDouble("SharkStomach", MAX_STOMACH);
                    target.getPersistentData().putDouble("SharkHunger", 40); 
                    
                    // STOP HUNTING (Ambush Cooldown)
                    shark.setAmbushCooldown(100); 
                    shark.setTarget(null);
                } 
            }
        }
    }

    /**
     * EVENT 2: TICK UPDATE
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        execute(null, world, x, y, z, entity);
    }

    private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        if (world.isClientSide()) return;

        if (isValidShark(entity) && entity instanceof AngelsharkEntity shark) { 
            double stomachAmount = entity.getPersistentData().getDouble("SharkStomach");
            double regenTimer = entity.getPersistentData().getDouble("SharkRegenTimer");
            
            // --- PASSIVE DIGESTION ---
            // Slower digestion to keep it full longer (2400 ticks = 2 mins)
            double digestionTimer = entity.getPersistentData().getDouble("SharkDigestionTimer");
            
            if (stomachAmount > 0) {
                 if (digestionTimer >= 2400) { 
                     entity.getPersistentData().putDouble("SharkStomach", Math.max(0, stomachAmount - 1));
                     entity.getPersistentData().putDouble("SharkDigestionTimer", 0);
                 } else {
                     entity.getPersistentData().putDouble("SharkDigestionTimer", digestionTimer + 1);
                 }
            }

            // --- HEALING ---
            if (entity.isAlive() && ((LivingEntity)entity).getHealth() > 0 && ((LivingEntity)entity).getHealth() < ((LivingEntity)entity).getMaxHealth() && stomachAmount >= 1) {
                if (regenTimer <= 0) {
                    if (entity instanceof LivingEntity _entity) _entity.heal(1.0f); 
                    
                    entity.getPersistentData().putDouble("SharkStomach", Math.max(0, stomachAmount - 1));
                    entity.getPersistentData().putDouble("SharkRegenTimer", 40); 
                } else {
                    entity.getPersistentData().putDouble("SharkRegenTimer", regenTimer - 1);
                }
            }

            // --- TARGET CLEANUP (Frenzy Fix) ---
            // If Stomach >= 1.0 (Full), remove target.
            if (stomachAmount >= FULL_THRESHOLD) {
                if (shark.getTarget() != null) {
                    shark.setTarget(null);
                }
            }

            // --- ACTIVE HUNTING ---
            double eatingRange = 2.5;

            if (entity.getPersistentData().getDouble("SharkHunger") == 0) {
                if (stomachAmount < FULL_THRESHOLD) { // Only eat if empty (< 1.0)
                    
                    List<ItemEntity> itemsInRange = world.getEntitiesOfClass(ItemEntity.class, AABB.ofSize(new Vec3(x, y, z), 16, 16, 16), e -> true);
                    
                    ItemEntity nearestItemEntity = itemsInRange.stream()
                            .sorted(Comparator.comparingDouble(ent -> ent.distanceToSqr(x, y, z)))
                            .findFirst()
                            .orElse(null);

                    if (nearestItemEntity != null && nearestItemEntity.getItem().getItem().isEdible()) {
                        double distance = Math.sqrt(entity.distanceToSqr(nearestItemEntity));

                        if (distance > eatingRange) {
                            if (entity instanceof Mob _mob) {
                                _mob.getNavigation().moveTo(nearestItemEntity.getX(), nearestItemEntity.getY(), nearestItemEntity.getZ(), 1.2);
                            }
                        } else {
                            ItemStack itemStack = nearestItemEntity.getItem();
                            FoodProperties foodProps = itemStack.getItem().getFoodProperties(itemStack, (LivingEntity) entity);
                            
                            // Visuals & Sound
                            if (entity instanceof LivingEntity _entity) _entity.swing(InteractionHand.MAIN_HAND, true);
                            if (world instanceof Level _level) _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1);
                            if (world instanceof ServerLevel _serverLevel) _serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack), nearestItemEntity.getX(), nearestItemEntity.getY() + 0.2, nearestItemEntity.getZ(), 10, 0.1, 0.1, 0.1, 0.05);

                            // LOGIC: One item fills it up
                            entity.getPersistentData().putDouble("SharkStomach", MAX_STOMACH);
                            entity.getPersistentData().putDouble("SharkHunger", 20); 
                            
                            // DESTROY ITEM
                            nearestItemEntity.getItem().shrink(1);
                            
                            // STOP HUNTING
                            shark.setAmbushCooldown(100); 
                            shark.setTarget(null);
                        }
                    }
                }
            } else {
                entity.getPersistentData().putDouble("SharkHunger", (entity.getPersistentData().getDouble("SharkHunger") - 1));
            }
        }
    }
    
    /**
     * EVENT 3: TARGET BLOCKING
     * Prevents locking onto new targets if full.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTargetSet(LivingChangeTargetEvent event) {
        if (event.getEntity() == null || event.getNewTarget() == null) return;
        if (event.getEntity().level().isClientSide()) return;

        Entity entity = event.getEntity();
        if (isValidShark(entity) && entity instanceof AngelsharkEntity shark) {
             double stomachAmount = entity.getPersistentData().getDouble("SharkStomach");
             int cooldown = shark.getAmbushCooldown();
             
             // If Full (>= 1.0) OR Cooldown Active -> Block Target
             if (stomachAmount >= FULL_THRESHOLD || cooldown > 0) {
                 LivingEntity newTarget = event.getNewTarget();
                 LivingEntity revengeTarget = shark.getLastHurtByMob();
                 
                 // Allow revenge, block food aggression
                 if (isFood(newTarget) && newTarget != revengeTarget) {
                     event.setNewTarget(null);
                 }
             }
        }
    }
}