package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.LeopardSharkEntity;

public class LeopardSharkOnEntityTickUpdateProcedure {
    // FIXED SIGNATURE: Added world, x, y, z to match MCreator's call
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        
        if (entity instanceof LeopardSharkEntity shark) {

            // --- SERVER SIDE ---
            if (!entity.level().isClientSide()) {
                
                // 1. SIT ENFORCEMENT
                // If the shark IS sitting, we force it to stop moving.
                if (shark.isOrderedToSit()) {
                    shark.getEntityData().set(LeopardSharkEntity.DATA_Sprinting, false);
                    if (shark.hasEffect(BenssharksModMobEffects.FRENZY.get())) {
                        shark.removeEffect(BenssharksModMobEffects.FRENZY.get());
                    }
                    shark.getNavigation().stop();
                    return; // Stop processing movement logic if sitting
                }

                // 2. SUFFOCATION
                if (!entity.isInWaterOrBubble()) {
                    double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
                    entity.getPersistentData().putDouble("DryTime", dryTimer);
                    if (dryTimer > 300 && dryTimer % 20 == 0) {
                        entity.hurt(entity.damageSources().dryOut(), 2.0F);
                    }
                } else {
                    entity.getPersistentData().putDouble("DryTime", 0);
                }

                // 3. SPRINT LOGIC
                boolean shouldSprint = false;
                if (entity.isInWaterOrBubble()) {
                    // A. Chase
                    LivingEntity target = shark.getTarget();
                    if (target != null && target.isAlive() && shark.distanceTo(target) <= 16) {
                        shouldSprint = true;
                    }
                    
                    // B. Follow Owner Catch-up
                    if (!shouldSprint && shark.isTame() && shark.getOwner() != null) {
                        double dist = shark.distanceTo(shark.getOwner());
                        if (dist > 10.0 && dist < 64.0) {
                            shouldSprint = true;
                        }
                    }
                }

                // Sync
                shark.getEntityData().set(LeopardSharkEntity.DATA_Sprinting, shouldSprint);

                // Apply Frenzy
                if (shouldSprint) {
                    shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 0, false, false));
                }
            }

            // --- CLIENT SIDE ANIMATION ---
            // Priority 1: Not in Water -> Play "land"
            if (!entity.isInWaterOrBubble()) {
                shark.animationprocedure = "land";
            } 
            // Priority 2: Sprinting -> Play "sprint"
            else if (shark.getEntityData().get(LeopardSharkEntity.DATA_Sprinting)) {
                shark.animationprocedure = "sprint";
            } 
            // Priority 3: Reset if neither (allows default swim/idle to play)
            else {
                if (shark.animationprocedure.equals("sprint") || shark.animationprocedure.equals("land")) {
                    shark.animationprocedure = "empty";
                }
            }
        }
    }
}