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

// --- SHARK IMPORTS ---
import net.mcreator.sharks.entity.WhitetipSharkEntity;
import net.mcreator.sharks.entity.TigerSharkEntity;
import net.mcreator.sharks.entity.ThresherSharkEntity;
import net.mcreator.sharks.entity.SawsharkEntity;
import net.mcreator.sharks.entity.ShrakEntity;
import net.mcreator.sharks.entity.NurseSharkEntity;
import net.mcreator.sharks.entity.MakoSharkEntity;
import net.mcreator.sharks.entity.LeopardSharkEntity;
import net.mcreator.sharks.entity.LemonSharkEntity;
import net.mcreator.sharks.entity.GreenlandSharkEntity;
import net.mcreator.sharks.entity.GreaterAxodileEntity;
import net.mcreator.sharks.entity.GoblinSharkEntity;
import net.mcreator.sharks.entity.BullSharkEntity;
import net.mcreator.sharks.entity.BonnetheadSharkEntity;
import net.mcreator.sharks.entity.BlueSharkEntity;
import net.mcreator.sharks.entity.BlacktipReefSharkEntity;
import net.mcreator.sharks.entity.AxodileEntity;

// --- FOOD/PREY IMPORTS ---
import net.mcreator.sharks.entity.BarracudaEntity;
import net.mcreator.sharks.entity.SardineEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Zombie;

// --- ITEM IMPORT ---
import net.mcreator.sharks.init.BenssharksModItems;

import com.mojang.datafixers.util.Pair;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class EatDroppedItemsProcedure {

    // --- HELPER: Is this entity a Shark? ---
    private static boolean isValidShark(Entity entity) {
        return entity instanceof AxodileEntity || entity instanceof BlacktipReefSharkEntity || entity instanceof BlueSharkEntity 
            || entity instanceof BonnetheadSharkEntity || entity instanceof LemonSharkEntity || entity instanceof MakoSharkEntity
            || entity instanceof NurseSharkEntity || entity instanceof LeopardSharkEntity || entity instanceof TigerSharkEntity 
            || entity instanceof GreenlandSharkEntity || entity instanceof BullSharkEntity || entity instanceof WhitetipSharkEntity 
            || entity instanceof GreaterAxodileEntity || entity instanceof GoblinSharkEntity || entity instanceof SawsharkEntity 
            || entity instanceof ThresherSharkEntity || entity instanceof ShrakEntity;
    }

    // --- HELPER: Is this entity Food? ---
    private static boolean isFood(Entity entity) {
        return entity instanceof Animal 
            || entity instanceof WaterAnimal 
            || entity instanceof Zombie
            || entity instanceof BarracudaEntity
            || entity instanceof SardineEntity; 
    }

    /**
     * EVENT 1: HAND FEEDING (Fish Bucket)
     * Priority: HIGHEST -> Runs first to intercept the click.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() == null) return;
        Entity target = event.getTarget();

        if (isValidShark(target)) {
            // Global Hand Check (Fixes "Off-Hand Leak")
            boolean hasBucketMain = event.getEntity().getMainHandItem().getItem() == BenssharksModItems.FISH_BUCKET.get();
            boolean hasBucketOff = event.getEntity().getOffhandItem().getItem() == BenssharksModItems.FISH_BUCKET.get();

            if (hasBucketMain || hasBucketOff) {
                
                if (!event.getLevel().isClientSide()) {
                    
                    // 1. COOLDOWN CHECK
                    // If shark is busy eating (animation cooldown), ignore click.
                    if (target.getPersistentData().getBoolean("canBeMilked")) {
                        return; 
                    }

                    double stomachAmount = target.getPersistentData().getDouble("SharkStomach");
                    
                    // 2. FULL CHECK (Gatekeeper)
                    if (stomachAmount >= 20) {
                        event.getEntity().displayClientMessage(Component.literal("The shark is too full to eat."), true);
                        event.setCanceled(true); // Stop the interaction here.
                        return;
                    } 
                    
                    // 3. FILL STOMACH
                    // We identify WHICH hand has the bucket to get the correct item properties.
                    ItemStack stackToUse = hasBucketMain ? event.getEntity().getMainHandItem() : event.getEntity().getOffhandItem();
                    
                    FoodProperties foodProps = stackToUse.getItem().getFoodProperties(stackToUse, event.getEntity());
                    double fillAmount = 7.2; // Default
                    
                    if (foodProps != null) {
                        float nutrition = foodProps.getNutrition();
                        float saturation = nutrition * foodProps.getSaturationModifier() * 2.0f;
                        fillAmount = nutrition + saturation;
                    }

                    double newStomach = stomachAmount + fillAmount;
                    if (newStomach > 20) newStomach = 20;
                    
                    target.getPersistentData().putDouble("SharkStomach", newStomach);
                    target.getPersistentData().putDouble("SharkHunger", 40); // Reset hunger
                    
                    // We let the event pass so the GUI procedure triggers the animation/reward.
                } 
            }
        }
    }

    /**
     * EVENT 2: TICK UPDATE
     * Handles Digestion, Passive Hunger, and Eating Items
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

        if (isValidShark(entity)) { 
            double stomachAmount = entity.getPersistentData().getDouble("SharkStomach");
            double regenTimer = entity.getPersistentData().getDouble("SharkRegenTimer");
            
            // --- NEW: PASSIVE DIGESTION TIMER ---
            // This ensures sharks eventually get hungry even if fully healed.
            // Decays 1 stomach point every 1200 ticks (60 seconds).
            double digestionTimer = entity.getPersistentData().getDouble("SharkDigestionTimer");
            
            if (stomachAmount > 0) {
                 if (digestionTimer >= 1200) {
                     entity.getPersistentData().putDouble("SharkStomach", Math.max(0, stomachAmount - 1));
                     entity.getPersistentData().putDouble("SharkDigestionTimer", 0);
                 } else {
                     entity.getPersistentData().putDouble("SharkDigestionTimer", digestionTimer + 1);
                 }
            }

            // --- HEALING FROM FOOD ---
            // If hurt, burn food faster to heal (1 pt every 40 ticks/2 sec)
            if (entity.isAlive() && ((LivingEntity)entity).getHealth() > 0 && ((LivingEntity)entity).getHealth() < ((LivingEntity)entity).getMaxHealth() && stomachAmount >= 1) {
                if (regenTimer <= 0) {
                    if (entity instanceof LivingEntity _entity) {
                        _entity.heal(1.0f); 
                    }
                    entity.getPersistentData().putDouble("SharkStomach", Math.max(0, stomachAmount - 1));
                    entity.getPersistentData().putDouble("SharkRegenTimer", 40); 
                } else {
                    entity.getPersistentData().putDouble("SharkRegenTimer", regenTimer - 1);
                }
            }

            // --- TARGET CLEANUP ---
            if (stomachAmount >= 20 && entity instanceof Mob mob) {
                LivingEntity currentTarget = mob.getTarget();
                LivingEntity revengeTarget = mob.getLastHurtByMob();
                
                if (currentTarget != null && isFood(currentTarget)) {
                    if (currentTarget != revengeTarget) {
                        mob.setTarget(null);
                    }
                }
            }

            // --- ACTIVE HUNTING (Eating Dropped Items) ---
            double eatingRange = (entity instanceof ShrakEntity) ? 3.0 : 2.5;

            if (entity.getPersistentData().getDouble("SharkHunger") == 0) {
                if (stomachAmount < 20) {
                    
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
                            
                            int nutritionValue = (foodProps != null) ? foodProps.getNutrition() : 1;

                            if (foodProps != null && entity instanceof LivingEntity _entity) {
                                for (Pair<MobEffectInstance, Float> pair : foodProps.getEffects()) {
                                    if (world.getRandom().nextFloat() < pair.getSecond()) {
                                        _entity.addEffect(new MobEffectInstance(pair.getFirst()));
                                    }
                                }
                            }

                            if (!(entity instanceof SawsharkEntity)) {
                                if (entity instanceof LivingEntity _entity)
                                    _entity.swing(InteractionHand.MAIN_HAND, true);
                            }

                            if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1);
                            }

                            if (world instanceof ServerLevel _serverLevel) {
                                _serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack), 
                                    nearestItemEntity.getX(), nearestItemEntity.getY() + 0.2, nearestItemEntity.getZ(), 
                                    10, 0.1, 0.1, 0.1, 0.05);
                            }

                            double newStomachValue = stomachAmount + nutritionValue;
                            if (newStomachValue > 20) newStomachValue = 20;
                            
                            entity.getPersistentData().putDouble("SharkStomach", newStomachValue);
                            entity.getPersistentData().putDouble("SharkHunger", 20); 
                            
                            nearestItemEntity.getItem().shrink(1);
                        }
                    }
                }
            } else {
                entity.getPersistentData().putDouble("SharkHunger", (entity.getPersistentData().getDouble("SharkHunger") - 1));
            }
        }
    }
    
    // --- TARGET BLOCKING ---
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTargetSet(LivingChangeTargetEvent event) {
        if (event.getEntity() == null || event.getNewTarget() == null) return;
        if (event.getEntity().level().isClientSide()) return;

        Entity entity = event.getEntity();
        if (isValidShark(entity)) {
             double stomachAmount = entity.getPersistentData().getDouble("SharkStomach");
             if (stomachAmount >= 20) {
                 LivingEntity newTarget = event.getNewTarget();
                 LivingEntity revengeTarget = entity instanceof Mob mob ? mob.getLastHurtByMob() : null;
                 if (isFood(newTarget) && newTarget != revengeTarget) {
                     event.setNewTarget(null);
                 }
             }
        }
    }
}