package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.ShrakEntity;

public class ShrakOnEntityTickUpdateProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null)
            return;

        // ================================================= //
        //        PART 1: NBT SUFFOCATION FIX                //
        // ================================================= //
        // We use "DryTime" variable so vanilla code can't overwrite it.
        
        if (!entity.isInWaterOrBubble()) {
            // 1. Increment Timer
            double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
            entity.getPersistentData().putDouble("DryTime", dryTimer);

            // 2. Check Threshold (300 ticks = 15 seconds of holding breath)
            if (dryTimer > 300) {
                // Damage every 20 ticks (1 second)
                if (dryTimer % 20 == 0) {
                    entity.hurt(entity.damageSources().dryOut(), 4.0F); // 2 Hearts damage
                    
                    // Apply visual effect
                    if (entity instanceof LivingEntity _entity) {
                        _entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.DRYOUT_EFFECT.get(), 40, 0, false, false));
                    }
                }
            }
        } else {
            // 3. Reset Timer in Water
            if (entity.getPersistentData().getDouble("DryTime") > 0) {
                entity.getPersistentData().putDouble("DryTime", 0);
            }
            
            // Remove Effect
            if (entity instanceof LivingEntity _entity) {
                if (_entity.hasEffect(BenssharksModMobEffects.DRYOUT_EFFECT.get())) {
                    _entity.removeEffect(BenssharksModMobEffects.DRYOUT_EFFECT.get());
                }
            }
        }

        // ================================================= //
        //        PART 2: SPRINT & ANIMATION BUFFER          //
        // ================================================= //
        if (entity instanceof ShrakEntity shark && entity.isInWaterOrBubble()) {
            
            LivingEntity target = shark.getTarget();
            boolean shouldSprint = false;

            // 1. Target Validation
            if (target != null && target.isAlive()) {
                if (shark.distanceTo(target) <= 16) {
                    boolean isInvalidPlayer = (target instanceof Player _plr && (_plr.isCreative() || _plr.isSpectator()));
                    if (!isInvalidPlayer) {
                        shouldSprint = true;
                    }
                }
            }

            // 2. Animation Logic (Buffer Fix)
            String currentAnim = shark.getSyncedAnimation();

            if (shouldSprint) {
                // ENTRY: Start Sprinting
                if (!currentAnim.equals("sprint")) {
                    shark.setAnimation("sprint");
                }
                // Frenzy Effect
                shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 1, false, false));
                
            } else {
                // EXIT: The Buffer Step
                // If we were sprinting, go to "swim" first to smooth the transition.
                if (currentAnim.equals("sprint")) {
                    shark.setAnimation("swim"); 
                } 
                else if (currentAnim.equals("swim")) {
                   // Let "swim" play out. Entity logic will reset to "empty" automatically when done.
                }
                else if (!currentAnim.equals("empty")) {
                     // Force reset if stuck
                     shark.setAnimation("empty");
                }

                // Cleanup Frenzy
                if (shark.hasEffect(BenssharksModMobEffects.FRENZY.get())) {
                    shark.removeEffect(BenssharksModMobEffects.FRENZY.get());
                }
            }
        }
    }
}