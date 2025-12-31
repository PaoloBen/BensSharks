package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Field;

@Mod.EventBusSubscriber
public class GlobalFishFlopperProcedure {

    // --- CONFIGURATION ---
    private static final List<String> FLOPPING_MOBS = Arrays.asList(
        "benssharks:barracuda",
        "benssharks:cookiecutter_shark",
        "benssharks:pilot_fish",
        "benssharks:remora",
        "benssharks:bonnethead_shark",
        "benssharks:blacktip_reef_shark",
        "benssharks:krill",
        "benssharks:sardine"
    );

    private static final String LAND_ANIMATION = "land";
    private static final String RESET_ANIMATION = "empty";
    private static final String FLOP_TAG = "BenSharksFlopping";

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) return;

        // 1. Check if this is a flopping mob
        ResourceLocation registryKey = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (registryKey == null || !FLOPPING_MOBS.contains(registryKey.toString())) {
            return;
        }

        boolean inWater = entity.isInWater();
        boolean onGround = entity.onGround();

        // ================================================= //
        //        LOGIC: OUT OF WATER (Flop)                 //
        // ================================================= //
        if (!inWater) {
            
            // --- SERVER SIDE: PHYSICS & STATE ---
            if (!entity.level().isClientSide()) {
                // If touching ground, refresh the flop state and jump
                if (onGround) {
                    // Mark as flopping (Sticky state)
                    entity.getPersistentData().putBoolean(FLOP_TAG, true);

                    // Physics (Jump)
                    double jumpStrength = 0.4; 
                    double xDir = (Math.random() * 0.2) - 0.1; 
                    double zDir = (Math.random() * 0.2) - 0.1;

                    entity.setDeltaMovement(entity.getDeltaMovement().add(xDir, jumpStrength, zDir));
                    entity.setYRot((float) (Math.random() * 360));
                    entity.setOnGround(false);
                    entity.hasImpulse = true;

                    // Sound (Play every jump)
                    entity.playSound(SoundEvents.SALMON_FLOP, 1.0f, 1.0f);
                }
            }

            // --- CLIENT SIDE: FORCE ANIMATION ---
            // If the "Flopping" tag is set (meaning we touched ground recently), force animation.
            // We check this on both Client (if synced) or blindly apply if we assume state consistency.
            // Since NBT sync can be slow, we rely on the logic that if !inWater, we want "land".
            
            if (entity.level().isClientSide()) {
                // We simply force "land" whenever out of water to be safe and responsive
                try {
                    Field animField = entity.getClass().getField("animationprocedure");
                    String currentAnim = (String) animField.get(entity);
                    
                    // Constantly set it to prevent "walk" from taking over
                    if (!LAND_ANIMATION.equals(currentAnim)) {
                        animField.set(entity, LAND_ANIMATION);
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
        } 
        // ================================================= //
        //        LOGIC: IN WATER (Reset)                    //
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
                    
                    // Only reset if it is currently stuck on "land"
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