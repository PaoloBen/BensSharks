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
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.BlockPos;

// --- SHARK & KRILL IMPORTS ---
import net.mcreator.sharks.entity.WhaleSharkEntity;
import net.mcreator.sharks.entity.BaskingSharkEntity;
import net.mcreator.sharks.entity.MegamouthSharkEntity;
import net.mcreator.sharks.entity.KrillEntity;
import net.mcreator.sharks.init.BenssharksModItems;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class EatingKrillProcedure {

    // --- HELPER: Is this a Filter Feeder? ---
    private static boolean isValidShark(Entity entity) {
        return entity instanceof WhaleSharkEntity || entity instanceof BaskingSharkEntity || entity instanceof MegamouthSharkEntity;
    }

    /**
     * EVENT 1: HAND FEEDING (Krill Bucket)
     * Priority: HIGHEST -> Intercepts click before MCreator procedures.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() == null) return;
        Entity target = event.getTarget();

        if (isValidShark(target)) {
            // Check BOTH hands for Krill Bucket
            boolean isKrillBucketMain = isKrillBucket(event.getEntity().getMainHandItem());
            boolean isKrillBucketOff = isKrillBucket(event.getEntity().getOffhandItem());

            if (isKrillBucketMain || isKrillBucketOff) {
                
                // Server Side Only
                if (!event.getLevel().isClientSide()) {
                    
                    // 1. COOLDOWN CHECK
                    if (target.getPersistentData().getBoolean("canBeMilked")) {
                        return; 
                    }

                    double stomachAmount = target.getPersistentData().getDouble("SharkStomach");
                    
                    // 2. FULL CHECK (Gatekeeper)
                    if (stomachAmount >= 64) {
                        event.getEntity().displayClientMessage(Component.literal("The shark is too full to eat."), true);
                        event.setCanceled(true); // Stop the interaction / specific procedure.
                        return;
                    } 
                    
                    // 3. FILL STOMACH
                    ItemStack stackToUse = isKrillBucketMain ? event.getEntity().getMainHandItem() : event.getEntity().getOffhandItem();
                    
                    FoodProperties foodProps = stackToUse.getItem().getFoodProperties(stackToUse, event.getEntity());
                    double fillAmount = 7.2; // Default
                    
                    if (foodProps != null) {
                        float nutrition = foodProps.getNutrition();
                        float saturation = nutrition * foodProps.getSaturationModifier() * 2.0f;
                        fillAmount = nutrition + saturation;
                    }

                    double newStomach = stomachAmount + fillAmount;
                    if (newStomach > 64) newStomach = 64; // Cap at 64
                    
                    target.getPersistentData().putDouble("SharkStomach", newStomach);
                    target.getPersistentData().putDouble("SharkHunger", 40); // Reset hunger cooldown
                } 
            }
        }
    }

    // Helper to identify Krill Bucket by registry name safely
    private static boolean isKrillBucket(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation reg = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return reg != null && reg.getPath().contains("krill_bucket");
    }

    /**
     * EVENT 2: BLOCK TARGETING
     * Prevents sharks from targeting live Krill.
     * UPDATED: REMOVED SELF-DEFENSE CHECK. They will never target Krill, period.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTargetSet(LivingChangeTargetEvent event) {
        if (event.getEntity() == null || event.getNewTarget() == null) return;
        if (event.getEntity().level().isClientSide()) return;

        Entity entity = event.getEntity();

        if (isValidShark(entity)) {
             double stomachAmount = entity.getPersistentData().getDouble("SharkStomach");

             // If Full (>= 64)
             if (stomachAmount >= 64) {
                 LivingEntity newTarget = event.getNewTarget();

                 // If target is Krill, stop targeting IMMEDIATELY.
                 // We removed the 'revengeTarget' check so they remain passive.
                 if (newTarget instanceof KrillEntity) {
                     event.setNewTarget(null);
                 }
             }
        }
    }

    /**
     * EVENT 3: TICK UPDATE
     * Handles Digestion, Healing, and Eating Logic
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
            // Force forget target if it is Krill (No self-defense check here either)
            if (stomachAmount >= 64 && entity instanceof Mob mob) {
                LivingEntity currentTarget = mob.getTarget();
                if (currentTarget instanceof KrillEntity) {
                    mob.setTarget(null);
                }
            }

            // --- 4. ACTIVE HUNTING (Eating) ---
            double searchRadius = 16.0;
            double eatingRange = 1.5; 

            if (entity.getPersistentData().getDouble("SharkHunger") == 0) {
                if (stomachAmount < 64) {
                    
                    // A. Find Dropped Items (Krill Item)
                    List<ItemEntity> itemsInRange = world.getEntitiesOfClass(ItemEntity.class, AABB.ofSize(new Vec3(x, y, z), searchRadius, searchRadius, searchRadius), 
                        e -> e.getItem().getItem() == BenssharksModItems.KRILL_ITEM.get());
                    
                    // B. Find Live Entities (Krill Mob)
                    List<KrillEntity> mobsInRange = world.getEntitiesOfClass(KrillEntity.class, AABB.ofSize(new Vec3(x, y, z), searchRadius, searchRadius, searchRadius), 
                        e -> e.isAlive());

                    // C. Find Closest
                    Entity nearestFood = null;
                    double closestDistSq = Double.MAX_VALUE;

                    for (ItemEntity item : itemsInRange) {
                        double d = item.distanceToSqr(x, y, z);
                        if (d < closestDistSq) {
                            closestDistSq = d;
                            nearestFood = item;
                        }
                    }
                    for (KrillEntity mob : mobsInRange) {
                        double d = mob.distanceToSqr(x, y, z);
                        if (d < closestDistSq) {
                            closestDistSq = d;
                            nearestFood = mob;
                        }
                    }

                    // D. Eat
                    if (nearestFood != null) {
                        double distance = Math.sqrt(closestDistSq);

                        if (distance > eatingRange) {
                            if (entity instanceof Mob _mob) {
                                _mob.getNavigation().moveTo(nearestFood.getX(), nearestFood.getY(), nearestFood.getZ(), 1.0);
                            }
                        } else {
                            // Calculate Value
                            double nutritionValue = 1.0;
                            ItemStack particleStack = new ItemStack(BenssharksModItems.KRILL_ITEM.get());

                            if (nearestFood instanceof ItemEntity itemEnt) {
                                FoodProperties props = itemEnt.getItem().getItem().getFoodProperties(itemEnt.getItem(), (LivingEntity)entity);
                                if (props != null) nutritionValue = props.getNutrition();
                                itemEnt.getItem().shrink(1);
                            } else if (nearestFood instanceof KrillEntity krill) {
                                krill.discard();
                            }

                            // Effects
                            if (entity instanceof LivingEntity _entity) _entity.swing(InteractionHand.MAIN_HAND, true);

                            if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.panda.bite")), SoundSource.NEUTRAL, 1, 1);
                            }

                            if (world instanceof ServerLevel _serverLevel) {
                                _serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, particleStack), 
                                    nearestFood.getX(), nearestFood.getY() + 0.2, nearestFood.getZ(), 
                                    5, 0.1, 0.1, 0.1, 0.05);
                            }

                            // Update Stomach
                            double newStomachValue = stomachAmount + nutritionValue;
                            if (newStomachValue > 64) newStomachValue = 64;
                            
                            entity.getPersistentData().putDouble("SharkStomach", newStomachValue);
                            entity.getPersistentData().putDouble("SharkHunger", 20); 
                        }
                    }
                }
            } else {
                entity.getPersistentData().putDouble("SharkHunger", (entity.getPersistentData().getDouble("SharkHunger") - 1));
            }
        }
    }
}