package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob; // Required for disabling navigation
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;

import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Field;

@Mod.EventBusSubscriber
public class GlobalFrenzyFlopperProcedure {

    // --- CONFIGURATION ---
    // Add the registry names of the sharks you want to be affected by this logic here
    private static final List<String> FRENZY_MOBS = Arrays.asList(
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

    // CHANGE THIS to the exact registry name of your Frenzy effect
    private static final String FRENZY_EFFECT_ID = "benssharks:frenzy";
    
    private static final String LAND_ANIMATION = "land";
    private static final String RESET_ANIMATION = "empty";
    private static final String FLOP_TAG = "BenSharksFrenzyFlopping";

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) return;

        // 1. Check if this is a valid mob from the list
        ResourceLocation registryKey = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (registryKey == null || !FRENZY_MOBS.contains(registryKey.toString())) {
            return;
        }

        // 2. Check if the entity actually has the Frenzy Effect
        // We cast to LivingEntity (which event.getEntity() usually implies, but safe to be sure)
        MobEffect frenzyEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(FRENZY_EFFECT_ID));
        
        // If the effect doesn't exist in registry or the mob doesn't have it active, stop.
        if (frenzyEffect == null || !event.getEntity().hasEffect(frenzyEffect)) {
            return;
        }

        boolean inWater = entity.isInWater();
        boolean onGround = entity.onGround();

        // ================================================= //
        //        LOGIC: OUT OF WATER (Flop + Stop AI)       //
        // ================================================= //
        if (!inWater) {
            
            // --- SERVER SIDE: PHYSICS & STATE ---
            if (!entity.level().isClientSide()) {
                
                // *** DISABLE NAVIGATOR ***
                // This prevents the "fast land movement" by clearing the AI's pathfinding every tick.
                if (entity instanceof Mob mob) {
                    mob.getNavigation().stop();
                }

                // If touching ground, refresh the flop state and jump
                if (onGround) {
                    // Mark as flopping (Sticky state)
                    entity.getPersistentData().putBoolean(FLOP_TAG, true);

                    // Physics (Jump)
                    // You can increase jumpStrength if you want them to be more chaotic during frenzy
                    double jumpStrength = 0.4; 
                    double xDir = (Math.random() * 0.4) - 0.2; // Increased randomness slightly for frenzy
                    double zDir = (Math.random() * 0.4) - 0.2;

                    entity.setDeltaMovement(entity.getDeltaMovement().add(xDir, jumpStrength, zDir));
                    entity.setYRot((float) (Math.random() * 360));
                    entity.setOnGround(false);
                    entity.hasImpulse = true;

                    // Sound (Play every jump)
                    entity.playSound(SoundEvents.SALMON_FLOP, 1.0f, 1.0f);
                }
            }

            // --- CLIENT SIDE: FORCE ANIMATION ---
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
        //        LOGIC: IN WATER (Reset)                    //
        // ================================================= //
        else {
            // --- SERVER SIDE: CLEANUP ---
            if (!entity.level().isClientSide()) {
                if (entity.getPersistentData().getBoolean(FLOP_TAG)) {
                    entity.getPersistentData().putBoolean(FLOP_TAG, false);
                }
                // Note: We do not need to "re-enable" navigation explicitly. 
                // Once we stop calling .stop(), the AI tasks will naturally resume pathfinding 
                // on the next tick they attempt to move.
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