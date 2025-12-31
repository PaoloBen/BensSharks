package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.BlueSharkEntity;

public class BlueSharkOnEntityTickUpdateProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null)
            return;

        // ================================================= //
        //        PART 1: NBT SUFFOCATION FIX                //
        // ================================================= //
        if (!entity.isInWaterOrBubble()) {
            double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
            entity.getPersistentData().putDouble("DryTime", dryTimer);

            if (dryTimer > 300) {
                if (dryTimer % 20 == 0) {
                    entity.hurt(entity.damageSources().dryOut(), 2.0F); // Blue shark takes 1 heart (weaker)
                    if (entity instanceof LivingEntity _entity)
                        _entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.DRYOUT_EFFECT.get(), 40, 0, false, false));
                }
            }
        } else {
            if (entity.getPersistentData().getDouble("DryTime") > 0)
                entity.getPersistentData().putDouble("DryTime", 0);
            if (entity instanceof LivingEntity _entity)
                _entity.removeEffect(BenssharksModMobEffects.DRYOUT_EFFECT.get());
        }

        // ================================================= //
        //        PART 2: SPRINT & ANIMATION BUFFER          //
        // ================================================= //
        if (entity instanceof BlueSharkEntity shark && entity.isInWaterOrBubble()) {
            
            LivingEntity target = shark.getTarget();
            boolean shouldSprint = false;

            // 1. Check Target (Range 16)
            if (target != null && target.isAlive()) {
                if (shark.distanceTo(target) <= 16) {
                    shouldSprint = true;
                }
            }

            // 2. Animation Buffer
            String currentAnim = shark.getSyncedAnimation();

            if (shouldSprint) {
                // ENTRY
                if (!currentAnim.equals("sprint")) {
                    shark.setAnimation("sprint");
                }
                // Frenzy I (Amplifier 0)
                shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 0, false, false));
                
            } else {
                // EXIT: Buffer with "walk" (Blue Shark uses "walk" for swim)
                if (currentAnim.equals("sprint")) {
                    shark.setAnimation("walk");
                } 
                else if (currentAnim.equals("walk")) {
                   // Let buffer play out
                }
                else if (!currentAnim.equals("empty")) {
                     shark.setAnimation("empty");
                }

                if (shark.hasEffect(BenssharksModMobEffects.FRENZY.get())) {
                    shark.removeEffect(BenssharksModMobEffects.FRENZY.get());
                }
            }
        }
    }
}