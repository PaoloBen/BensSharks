package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.entity.SeaLionEntity;

import com.mojang.datafixers.util.Pair;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class SeaLionEatDroppedItemsProcedure {

    // --- HELPER: Is this item part of the Sea Lion diet? ---
    private static boolean isSeaLionFood(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() == Items.COD
            || stack.getItem() == Items.SALMON
            || stack.getItem() == Items.TROPICAL_FISH
            || stack.getItem() == BenssharksModItems.RAW_BARRACUDA.get()
            || stack.getItem() == BenssharksModItems.RAW_SARDINE.get()
            || stack.getItem() == BenssharksModItems.RAW_PILOT_FISH.get();
    }

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

        // Ensure this logic only runs for the Sea Lion
        if (entity instanceof SeaLionEntity) {
            
            // --- DATA TRACKING ---
            double stomachAmount = entity.getPersistentData().getDouble("SeaLionStomach");
            double eatCooldown = entity.getPersistentData().getDouble("SeaLionHunger");
            double playCooldown = entity.getPersistentData().getDouble("SeaLionPlayTimer");
            double regenTimer = entity.getPersistentData().getDouble("SeaLionRegenTimer");
            double digestionTimer = entity.getPersistentData().getDouble("SeaLionDigestionTimer");

            // --- 1. PASSIVE DIGESTION & HEALING ---
            if (stomachAmount > 0) {
                 if (digestionTimer >= 1200) {
                     entity.getPersistentData().putDouble("SeaLionStomach", Math.max(0, stomachAmount - 1));
                     entity.getPersistentData().putDouble("SeaLionDigestionTimer", 0);
                 } else {
                     entity.getPersistentData().putDouble("SeaLionDigestionTimer", digestionTimer + 1);
                 }
            }

            if (entity.isAlive() && ((LivingEntity)entity).getHealth() > 0 && ((LivingEntity)entity).getHealth() < ((LivingEntity)entity).getMaxHealth() && stomachAmount >= 1) {
                if (regenTimer <= 0) {
                    ((LivingEntity)entity).heal(1.0f); 
                    entity.getPersistentData().putDouble("SeaLionStomach", Math.max(0, stomachAmount - 1));
                    entity.getPersistentData().putDouble("SeaLionRegenTimer", 40); 
                } else {
                    entity.getPersistentData().putDouble("SeaLionRegenTimer", regenTimer - 1);
                }
            }
            
            if (playCooldown > 0) {
                entity.getPersistentData().putDouble("SeaLionPlayTimer", playCooldown - 1);
            }

            // --- 2. BEHAVIOR LOGIC ---
            List<ItemEntity> itemsInRange = world.getEntitiesOfClass(ItemEntity.class, AABB.ofSize(new Vec3(x, y, z), 16, 16, 16), e -> true);
            
            ItemEntity nearestFood = null;
            ItemEntity nearestToy = null;
            
            for (ItemEntity itemEnt : itemsInRange) {
                double distSq = itemEnt.distanceToSqr(x, y, z);
                
                if (isSeaLionFood(itemEnt.getItem())) {
                    if (nearestFood == null || distSq < nearestFood.distanceToSqr(x, y, z)) {
                        nearestFood = itemEnt;
                    }
                } else {
                    if (nearestToy == null || distSq < nearestToy.distanceToSqr(x, y, z)) {
                        nearestToy = itemEnt;
                    }
                }
            }

            // --- PRIORITY A: EATING (Hungry & Food Exists) ---
            if (nearestFood != null && eatCooldown == 0 && stomachAmount < 20) {
                double distance = Math.sqrt(entity.distanceToSqr(nearestFood));
                
                if (distance > 2.5) {
                    if (entity instanceof Mob _mob) {
                        _mob.getNavigation().moveTo(nearestFood.getX(), nearestFood.getY(), nearestFood.getZ(), 1.2);
                    }
                } else {
                    ItemStack itemStack = nearestFood.getItem();
                    FoodProperties foodProps = itemStack.getItem().getFoodProperties(itemStack, (LivingEntity) entity);
                    int nutritionValue = (foodProps != null) ? foodProps.getNutrition() : 1;

                    if (foodProps != null && entity instanceof LivingEntity _entity) {
                        for (Pair<MobEffectInstance, Float> pair : foodProps.getEffects()) {
                            if (world.getRandom().nextFloat() < pair.getSecond()) {
                                _entity.addEffect(new MobEffectInstance(pair.getFirst()));
                            }
                        }
                    }

                    if (entity instanceof LivingEntity _entity) _entity.swing(InteractionHand.MAIN_HAND, true);

                    // EATING SOUND
                    if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1);
                    }

                    if (world instanceof ServerLevel _serverLevel) {
                        _serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack), 
                            nearestFood.getX(), nearestFood.getY() + 0.2, nearestFood.getZ(), 
                            10, 0.1, 0.1, 0.1, 0.05);
                    }

                    entity.getPersistentData().putDouble("SeaLionStomach", Math.min(20, stomachAmount + nutritionValue));
                    entity.getPersistentData().putDouble("SeaLionHunger", 20); 
                    nearestFood.getItem().shrink(1);
                }
            }
            // --- PRIORITY B: PLAYING (No Food or Full, but Toy Exists) ---
            else if (nearestToy != null) {
                
                double distance = Math.sqrt(entity.distanceToSqr(nearestToy));
                double playRange = 1.5; 

                // VISUAL TRACKING
                if (entity instanceof Mob _mob) {
                    _mob.getLookControl().setLookAt(nearestToy.getX(), nearestToy.getY(), nearestToy.getZ());
                }
                
                // VOLLEYBALL DATA
                String lastThrowerUUID = nearestToy.getPersistentData().getString("LastThrowerUUID");
                String myUUID = entity.getStringUUID(); 
                boolean amIThrower = lastThrowerUUID.equals(myUUID);
                
                // RESET LOGIC (Ground or Water)
                boolean isBallReset = nearestToy.onGround() || nearestToy.isInWater();

                // 1. MOVE TOWARDS TOY (If allowed)
                if (!amIThrower || isBallReset) {
                    if (distance > playRange) {
                        if (entity instanceof Mob _mob) {
                            _mob.getNavigation().moveTo(nearestToy.getX(), nearestToy.getY(), nearestToy.getZ(), 1.2);
                        }
                    } 
                    // 2. PLAY WITH TOY (When close enough)
                    else {
                        if (playCooldown == 0) {
                            // --- TEAMMATE SEARCH ---
                            List<SeaLionEntity> teammates = world.getEntitiesOfClass(SeaLionEntity.class, 
                                AABB.ofSize(new Vec3(x, y, z), 16, 16, 16), 
                                e -> e != entity && e.isAlive());

                            SeaLionEntity targetTeammate = null;
                            double distanceToMate = 0;

                            if (!teammates.isEmpty()) {
                                targetTeammate = teammates.get(new Random().nextInt(teammates.size()));
                                distanceToMate = Math.sqrt(entity.distanceToSqr(targetTeammate));
                            }

                            double velX, velY, velZ;

                            // ** PASS vs SCRAMBLE **
                            if (targetTeammate != null && distanceToMate > 5.0) {
                                // TARGETED PASS
                                double dx = targetTeammate.getX() - x;
                                double dz = targetTeammate.getZ() - z;
                                double dist = Math.sqrt(dx * dx + dz * dz);
                                
                                double throwSpeed = Math.min(0.4, 0.15 + (dist * 0.04)); 
                                
                                velX = (dx / dist) * throwSpeed;
                                velY = 0.35; 
                                velZ = (dz / dist) * throwSpeed;
                            } else {
                                // SCRAMBLE / SOLO THROW
                                velX = (Math.random() - 0.5) * 0.4;
                                velY = 0.35 + (Math.random() * 0.15); 
                                velZ = (Math.random() - 0.5) * 0.4;
                            }
                            
                            // Execute Throw
                            nearestToy.setDeltaMovement(nearestToy.getDeltaMovement().add(velX, velY, velZ));
                            nearestToy.setOnGround(false);
                            nearestToy.hasImpulse = true;
                            
                            nearestToy.getPersistentData().putString("LastThrowerUUID", myUUID);

                            // SWING HAND (Trigger Animation)
                            if (entity instanceof LivingEntity _entity) _entity.swing(InteractionHand.MAIN_HAND, true);
                            
                            // *** SOUND EFFECT: ITEM PICKUP (Subtle Pop) ***
                            // Volume changed from 1.0 to 0.2 (Vanilla Standard)
                            if (world instanceof Level _level) {
                               _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.item.pickup")), SoundSource.NEUTRAL, 0.2f, 2.0f);
                            }
                            
                            entity.getPersistentData().putDouble("SeaLionPlayTimer", 15);
                        }
                    }
                } else {
                    // STOP MOVING if waiting
                    if (entity instanceof Mob _mob) {
                        _mob.getNavigation().stop();
                    }
                }
            } else {
                if (eatCooldown > 0) {
                    entity.getPersistentData().putDouble("SeaLionHunger", (eatCooldown - 1));
                }
            }
        }
    }
}