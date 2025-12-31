package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.NurseSharkEntity;

public class NurseSharkOnEntityTickUpdateProcedure {
    public static void execute(Entity entity) {
        if (entity == null) return;
        NurseSharkEntity shark = (NurseSharkEntity) entity;

        // --- SERVER SIDE ---
        if (!entity.level().isClientSide()) {
            
            // 1. SIT ENFORCEMENT (Strict)
            // If sitting, disable ALL movement logic immediately
            if (shark.isOrderedToSit()) {
                shark.getEntityData().set(NurseSharkEntity.DATA_Sprinting, false);
                
                // Remove Frenzy if it exists so it doesn't look angry/fast
                if (shark.hasEffect(BenssharksModMobEffects.FRENZY.get())) {
                    shark.removeEffect(BenssharksModMobEffects.FRENZY.get());
                }
                
                // Stop pathfinding
                shark.getNavigation().stop();
                return; // EXIT PROCEDURE
            }

            // 2. Suffocation
            if (!entity.isInWaterOrBubble()) {
                double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
                entity.getPersistentData().putDouble("DryTime", dryTimer);
                if (dryTimer > 300 && dryTimer % 20 == 0) {
                    entity.hurt(entity.damageSources().dryOut(), 2.0F);
                }
            } else {
                entity.getPersistentData().putDouble("DryTime", 0);
            }

            // 3. Sprint Logic (Only if NOT sitting)
            boolean shouldSprint = false;
            if (entity.isInWaterOrBubble()) {
                
                // A. Attack Chase
                LivingEntity target = shark.getTarget();
                if (target != null && target.isAlive() && shark.distanceTo(target) <= 16) {
                    shouldSprint = true;
                }
                
                // B. Follow Owner Catch-up
                if (!shouldSprint && shark.isTame() && shark.getOwner() != null) {
                    double dist = shark.distanceTo(shark.getOwner());
                    // If far away (10+ blocks), sprint to catch up
                    if (dist > 10.0 && dist < 64.0) {
                        shouldSprint = true;
                    }
                }
            }

            // Sync
            shark.getEntityData().set(NurseSharkEntity.DATA_Sprinting, shouldSprint);

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
        else if (shark.getEntityData().get(NurseSharkEntity.DATA_Sprinting)) {
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