package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Field;

@Mod.EventBusSubscriber
public class GlobalSharkFloppingProcedure {

    // --- CONFIGURATION ---
    private static final List<String> SHARK_MOBS = Arrays.asList(
        "benssharks:angelshark",
        "benssharks:basking_shark",
        "benssharks:blacktip_reef_shark",
        "benssharks:blue_shark",
        "benssharks:bonnethead_shark",
        "benssharks:bull_shark",
        "benssharks:goblin_shark",
        "benssharks:greatwhite_shark",
        "benssharks:greenland_shark",
        "benssharks:lemon_shark",
        "benssharks:leopard_shark",
        "benssharks:mako_shark",
        "benssharks:megalodon",
        "benssharks:megamouth_shark",
        "benssharks:nurse_shark",
        "benssharks:sawshark",
        "benssharks:thresher_shark",
        "benssharks:tiger_shark",
        "benssharks:whale_shark",
        "benssharks:whitetip_shark"
    );

    // CONSTANTS
    private static final String LAND_ANIMATION = "land";
    private static final String RESET_ANIMATION = "empty";
    private static final String FLOP_TAG = "BenSharksFlopping";
    private static final String TIMER_TAG = "DryTime"; 

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) return;

        // 1. Check if this is a valid shark
        ResourceLocation registryKey = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (registryKey == null || !SHARK_MOBS.contains(registryKey.toString())) {
            return;
        }

        // 2. DRY TIMER LOGIC (No Damage)
        boolean shouldFlop = false;
        double dryTimer = entity.getPersistentData().getDouble(TIMER_TAG);

        if (!entity.isInWaterOrBubble()) {
            entity.getPersistentData().putDouble(TIMER_TAG, dryTimer);

            // If timer passes 300, enable flopping
            if (dryTimer > 300) {
                shouldFlop = true;
            }
        } else {
            // Reset if back in water
            entity.getPersistentData().putDouble(TIMER_TAG, 0);
            dryTimer = 0;
        }

        // ================================================= //
        //        LOGIC: FLOPPING ACTIVE                     //
        // ================================================= //
        if (shouldFlop) {
            
            // --- SERVER SIDE: PHYSICS & AI ---
            if (!entity.level().isClientSide()) {
                
                // Disable Navigation (Stop AI walking)
                if (entity instanceof Mob mob) {
                    mob.getNavigation().stop();
                }

                // Physics (Jump only when touching ground)
                if (entity.onGround()) {
                    entity.getPersistentData().putBoolean(FLOP_TAG, true);

                    double jumpStrength = 0.4; 
                    double xDir = (Math.random() * 0.4) - 0.2; 
                    double zDir = (Math.random() * 0.4) - 0.2;

                    entity.setDeltaMovement(entity.getDeltaMovement().add(xDir, jumpStrength, zDir));
                    entity.setYRot((float) (Math.random() * 360));
                    entity.setOnGround(false);
                    entity.hasImpulse = true;

                    // Sound
                    entity.playSound(SoundEvents.SALMON_FLOP, 1.0f, 1.0f);
                }
            }

            // --- CLIENT SIDE: ANIMATION ---
            if (entity.level().isClientSide()) {
                try {
                    Field animField = entity.getClass().getField("animationprocedure");
                    String currentAnim = (String) animField.get(entity);
                    
                    if (!LAND_ANIMATION.equals(currentAnim)) {
                        animField.set(entity, LAND_ANIMATION);
                    }
                } catch (Exception e) {
                    // Ignore reflection errors
                }
            }
        } 
        // ================================================= //
        //        LOGIC: SAFE / RESET                        //
        // ================================================= //
        else {
            // --- SERVER SIDE: CLEANUP ---
            if (!entity.level().isClientSide()) {
                if (entity.getPersistentData().getBoolean(FLOP_TAG)) {
                    entity.getPersistentData().putBoolean(FLOP_TAG, false);
                }
            }

            // --- CLIENT SIDE: RESET ANIMATION ---
            if (entity.level().isClientSide()) {
                try {
                    Field animField = entity.getClass().getField("animationprocedure");
                    String currentAnim = (String) animField.get(entity);
                    
                    if (LAND_ANIMATION.equals(currentAnim)) {
                        animField.set(entity, RESET_ANIMATION);
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }
}