package net.mcreator.sharks.procedures;

import net.mcreator.sharks.configuration.SharkAggroConfig; // <--- NEW IMPORT

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Comparator;

@Mod.EventBusSubscriber
public class DolphinEatSardineProcedure {

    private static final String TARGET_ITEM = "benssharks:raw_sardine";

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        execute(null, world, x, y, z, entity);
    }

    private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        if (world.isClientSide()) return;

        // --- CONFIG CHECK ---
        // If eating is disabled in config, stop here.
        try {
            if (!SharkAggroConfig.COMMON.enableDolphinEating.get()) {
                return;
            }
        } catch (Exception e) {
            // Safety: if config fails to load, ignore and proceed.
        }

        if (entity instanceof Dolphin) {
            
            // --- DATA TRACKING ---
            double stomachAmount = entity.getPersistentData().getDouble("DolphinStomach");
            double eatCooldown = entity.getPersistentData().getDouble("DolphinHunger");
            double digestionTimer = entity.getPersistentData().getDouble("DolphinDigestionTimer");

            // --- 1. PASSIVE DIGESTION LOGIC ---
            // If there is food in the stomach, slowly digest it.
            if (stomachAmount > 0) {
                 // 1200 ticks = 60 seconds to digest 1 unit of food
                 if (digestionTimer >= 1200) {
                     entity.getPersistentData().putDouble("DolphinStomach", Math.max(0, stomachAmount - 1));
                     entity.getPersistentData().putDouble("DolphinDigestionTimer", 0);
                 } else {
                     entity.getPersistentData().putDouble("DolphinDigestionTimer", digestionTimer + 1);
                 }
            }

            // --- 2. COOLDOWN MANAGEMENT ---
            if (eatCooldown > 0) {
                entity.getPersistentData().putDouble("DolphinHunger", eatCooldown - 1);
                // We do NOT return here, because digestion (above) still needs to happen even if eating is on cooldown.
            }

            // --- 3. EATING LOGIC ---
            // Only scan for food if:
            // A. Not on short-term cooldown
            // B. Stomach is not full (Limit: 20)
            if (eatCooldown == 0 && stomachAmount < 20) {

                List<ItemEntity> itemsInRange = world.getEntitiesOfClass(ItemEntity.class, 
                    AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), 
                    e -> {
                        ResourceLocation id = ForgeRegistries.ITEMS.getKey(e.getItem().getItem());
                        return id != null && id.toString().equals(TARGET_ITEM);
                    }
                );
                
                ItemEntity nearestFood = itemsInRange.stream() 
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(entity)))
                    .orElse(null);

                if (nearestFood != null) {
                    double distance = Math.sqrt(entity.distanceToSqr(nearestFood));
                    
                    // If close enough (no pathfinding needed, just proximity check)
                    if (distance <= 2.0) {
                        
                        ItemStack itemStack = nearestFood.getItem();

                        // VISUALS
                        if (entity instanceof LivingEntity _entity) _entity.swing(InteractionHand.MAIN_HAND, true);

                        // SOUND
                        if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), 
                                ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), 
                                SoundSource.NEUTRAL, 1, 1);
                        }

                        // PARTICLES
                        if (world instanceof ServerLevel _serverLevel) {
                            _serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack), 
                                nearestFood.getX(), nearestFood.getY() + 0.2, nearestFood.getZ(), 
                                10, 0.1, 0.1, 0.1, 0.05);
                        }

                        // HEAL
                        if (entity instanceof LivingEntity _livEnt) {
                            _livEnt.heal(2.0f); 
                        }

                        // UPDATE DATA
                        // Add nutrition value (e.g., 4) to stomach. Cap at 20.
                        entity.getPersistentData().putDouble("DolphinStomach", Math.min(20, stomachAmount + 4));
                        entity.getPersistentData().putDouble("DolphinHunger", 20); // 1 second cooldown between bites

                        // CONSUME ITEM
                        nearestFood.getItem().shrink(1);
                    }
                }
            }
        }
    }
}