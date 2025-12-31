package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.BlockPos;

// --- IMPORTS ---
import net.mcreator.sharks.entity.BarracudaEntity;
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
import net.mcreator.sharks.init.BenssharksModItems;

import com.mojang.datafixers.util.Pair;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class BarracudaEatingProcedure {

    // --- HELPER: Is this a Barracuda? ---
    private static boolean isValidBarracuda(Entity entity) {
        return entity instanceof BarracudaEntity;
    }

    // --- HELPER: Is this entity a Shark/Friend? ---
    // We use this to define what is NOT prey.
    private static boolean isFriend(Entity entity) {
        return entity instanceof BarracudaEntity 
            || entity instanceof AxodileEntity || entity instanceof BlacktipReefSharkEntity 
            || entity instanceof BlueSharkEntity || entity instanceof BonnetheadSharkEntity 
            || entity instanceof LemonSharkEntity || entity instanceof MakoSharkEntity
            || entity instanceof NurseSharkEntity || entity instanceof LeopardSharkEntity 
            || entity instanceof TigerSharkEntity || entity instanceof GreenlandSharkEntity 
            || entity instanceof BullSharkEntity || entity instanceof WhitetipSharkEntity 
            || entity instanceof GreaterAxodileEntity || entity instanceof GoblinSharkEntity 
            || entity instanceof SawsharkEntity || entity instanceof ThresherSharkEntity 
            || entity instanceof ShrakEntity;
    }

    /**
     * EVENT 1: BLOCK TARGETING
     * Prevents Barracudas from hunting if they are full.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTargetSet(LivingChangeTargetEvent event) {
        if (event.getEntity() == null || event.getNewTarget() == null) return;
        if (event.getEntity().level().isClientSide()) return;

        Entity entity = event.getEntity();

        if (isValidBarracuda(entity)) {
             double stomachAmount = entity.getPersistentData().getDouble("SharkStomach");

             // If Full (>= 20)
             if (stomachAmount >= 20) {
                 LivingEntity newTarget = event.getNewTarget();
                 LivingEntity revengeTarget = entity instanceof Mob mob ? mob.getLastHurtByMob() : null;

                 // If the target is NOT a friend (meaning it is Prey)
                 if (!isFriend(newTarget)) {
                     // SELF DEFENSE CHECK:
                     // Only block the target if they didn't hit us first.
                     if (newTarget != revengeTarget) {
                         event.setNewTarget(null);
                     }
                 }
             }
        }
    }

    /**
     * EVENT 2: TICK UPDATE
     * Handles Digestion, Healing, and Eating Dropped Items
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

        if (isValidBarracuda(entity)) { 
            // Safety: Initialize stomach if missing
            if (!entity.getPersistentData().contains("SharkStomach")) {
                entity.getPersistentData().putDouble("SharkStomach", 0);
            }

            double stomachAmount = entity.getPersistentData().getDouble("SharkStomach");
            double regenTimer = entity.getPersistentData().getDouble("SharkRegenTimer");
            
            // --- 1. PASSIVE DIGESTION ---
            double digestionTimer = entity.getPersistentData().getDouble("SharkDigestionTimer");
            
            if (stomachAmount > 0) {
                 if (digestionTimer >= 1200) {
                     entity.getPersistentData().putDouble("SharkStomach", Math.max(0, stomachAmount - 1));
                     entity.getPersistentData().putDouble("SharkDigestionTimer", 0);
                 } else {
                     entity.getPersistentData().putDouble("SharkDigestionTimer", digestionTimer + 1);
                 }
            }

            // --- 2. HEALING ---
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

            // --- 3. TARGET CLEANUP ---
            if (entity instanceof Mob mob) {
                LivingEntity currentTarget = mob.getTarget();
                LivingEntity revengeTarget = mob.getLastHurtByMob();
                
                // If full, forget generic prey
                if (stomachAmount >= 20 && currentTarget != null) {
                    if (!isFriend(currentTarget)) {
                        if (currentTarget != revengeTarget) {
                            mob.setTarget(null);
                        }
                    }
                }
            }

            // --- 4. ACTIVE HUNTING (Eating Dropped Items) ---
            double searchRadius = 16.0;
            double eatingRange = 2.5; 

            if (entity.getPersistentData().getDouble("SharkHunger") == 0) {
                if (stomachAmount < 20) {
                    
                    List<ItemEntity> itemsInRange = world.getEntitiesOfClass(ItemEntity.class, AABB.ofSize(new Vec3(x, y, z), searchRadius, searchRadius, searchRadius), e -> true);
                    
                    ItemEntity nearestFood = null;
                    double closestDistSq = Double.MAX_VALUE;

                    for (ItemEntity itemEntity : itemsInRange) {
                        ItemStack stack = itemEntity.getItem();
                        Item item = stack.getItem();

                        // CANNIBALISM CHECK
                        if (item == BenssharksModItems.RAW_BARRACUDA.get() || item == BenssharksModItems.COOKED_BARRACUDA.get()) {
                            continue; 
                        }

                        // Check if Edible
                        if (item.isEdible()) {
                            double d = itemEntity.distanceToSqr(x, y, z);
                            if (d < closestDistSq) {
                                closestDistSq = d;
                                nearestFood = itemEntity;
                            }
                        }
                    }

                    // Eat
                    if (nearestFood != null) {
                        double distance = Math.sqrt(closestDistSq);

                        if (distance > eatingRange) {
                            if (entity instanceof Mob _mob) {
                                _mob.getNavigation().moveTo(nearestFood.getX(), nearestFood.getY(), nearestFood.getZ(), 1.2);
                            }
                        } else {
                            ItemStack itemStack = nearestFood.getItem();
                            FoodProperties foodProps = itemStack.getItem().getFoodProperties(itemStack, (LivingEntity) entity);
                            
                            int nutritionValue = (foodProps != null) ? foodProps.getNutrition() : 1;

                            // Effects
                            if (foodProps != null && entity instanceof LivingEntity _entity) {
                                for (Pair<MobEffectInstance, Float> pair : foodProps.getEffects()) {
                                    if (world.getRandom().nextFloat() < pair.getSecond()) {
                                        _entity.addEffect(new MobEffectInstance(pair.getFirst()));
                                    }
                                }
                            }

                            // Swing
                            if (entity instanceof LivingEntity _entity)
                                _entity.swing(InteractionHand.MAIN_HAND, true);

                            // Sound
                            if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1);
                            }

                            // Particles
                            if (world instanceof ServerLevel _serverLevel) {
                                _serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack), 
                                    nearestFood.getX(), nearestFood.getY() + 0.2, nearestFood.getZ(), 
                                    10, 0.1, 0.1, 0.1, 0.05);
                            }

                            // Fill Stomach
                            double newStomachValue = stomachAmount + nutritionValue;
                            if (newStomachValue > 20) newStomachValue = 20;
                            
                            entity.getPersistentData().putDouble("SharkStomach", newStomachValue);
                            entity.getPersistentData().putDouble("SharkHunger", 20); 
                            
                            nearestFood.getItem().shrink(1);
                        }
                    }
                }
            } else {
                entity.getPersistentData().putDouble("SharkHunger", (entity.getPersistentData().getDouble("SharkHunger") - 1));
            }
        }
    }
}