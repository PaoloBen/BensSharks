package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.SawsharkEntity;

public class SawsharkOnEntityTickUpdateProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null) return;
        
        // Ensure we only run this on Sawsharks
        if (!(entity instanceof SawsharkEntity)) return;
        SawsharkEntity shark = (SawsharkEntity) entity;

        // --- SERVER SIDE LOGIC ---
        if (!entity.level().isClientSide()) {
            
            // 1. SUFFOCATION MECHANIC (Copied from Nurse Shark)
            if (!entity.isInWaterOrBubble()) {
                double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
                entity.getPersistentData().putDouble("DryTime", dryTimer);
                // 300 ticks = 15 seconds before damage starts
                if (dryTimer > 300 && dryTimer % 20 == 0) {
                    entity.hurt(entity.damageSources().dryOut(), 2.0F);
                }
            } else {
                entity.getPersistentData().putDouble("DryTime", 0);
            }

            // 2. SPRINT/FRENZY LOGIC
            boolean shouldSprint = false;
            if (entity.isInWaterOrBubble()) {
                // Chase Target if close (Aggressive behavior)
                LivingEntity target = shark.getTarget();
                if (target != null && target.isAlive() && shark.distanceTo(target) <= 16) {
                    shouldSprint = true;
                }
            }

            // Sync Sprinting Data
            shark.getEntityData().set(SawsharkEntity.DATA_Sprinting, shouldSprint);

            // Apply Frenzy Effect for Speed
            if (shouldSprint) {
                shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 0, false, false));
            }
        }

        // --- CLIENT SIDE ANIMATION ---
        if (shark.getEntityData().get(SawsharkEntity.DATA_Sprinting)) {
            shark.animationprocedure = "sprint";
        } else {
            if (shark.animationprocedure.equals("sprint")) {
                shark.animationprocedure = "empty";
            }
        }
    }
}