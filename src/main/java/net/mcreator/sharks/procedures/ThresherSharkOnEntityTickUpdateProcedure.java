package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.ThresherSharkEntity;

import java.util.List;

public class ThresherSharkOnEntityTickUpdateProcedure {
    public static void execute(Entity entity) {
        if (entity == null) return;
        LevelAccessor world = entity.level();

        if (entity instanceof ThresherSharkEntity shark) {
            
            // ================================================= //
            //        PART 1: SERVER SIDE LOGIC                  //
            // ================================================= //
            if (!world.isClientSide()) {
                
                // --- A. NBT SUFFOCATION ---
                if (!entity.isInWaterOrBubble()) {
                    double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
                    entity.getPersistentData().putDouble("DryTime", dryTimer);
                    if (dryTimer > 300 && dryTimer % 20 == 0) {
                        entity.hurt(entity.damageSources().dryOut(), 2.0F);
                        if (entity instanceof LivingEntity _entity)
                            _entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.DRYOUT_EFFECT.get(), 40, 0, false, false));
                    }
                } else {
                    entity.getPersistentData().putDouble("DryTime", 0);
                    if (entity instanceof LivingEntity _entity)
                        _entity.removeEffect(BenssharksModMobEffects.DRYOUT_EFFECT.get());
                }

                // --- B. WHIP ATTACK LOGIC ---
                boolean isWhipping = false;
                double cooldown = entity.getPersistentData().getDouble("WhipCooldown");
                double timer = entity.getPersistentData().getDouble("WhipTimer");

                if (cooldown > 0) entity.getPersistentData().putDouble("WhipCooldown", cooldown - 1);

                if (timer > 0) {
                    isWhipping = true;
                    entity.getPersistentData().putDouble("WhipTimer", timer - 1);
                    
                    // Stop movement
                    if (timer > 1) {
                        shark.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 10, false, false));
                    }

                    // DAMAGE EVENT (Tick 6 = 14th Tick of animation)
                    if (timer == 6) {
                        entity.getPersistentData().putBoolean("WhipHitFrame", true);
                        double baseDamage = shark.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue();
                        DamageSource damageSource = shark.damageSources().mobAttack(shark);
                        
                        // CHANGED: Limited Vertical Range to 1 block up/down (inflate(5, 1, 5))
                        AABB box = entity.getBoundingBox().inflate(5, 1, 5);
                        
                        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, box);
                        
                        // NEW: Hit Counter initialized
                        int hitCount = 0;

                        for (LivingEntity vic : targets) {
                            // NEW: Stop loop if we hit limit of 3
                            if (hitCount >= 3) break;

                            if (vic != entity && vic.isAlive() && !vic.isInvulnerable() && entity.distanceToSqr(vic) <= 25) {
                                Vec3 lookDir = entity.getViewVector(1.0F).normalize();
                                Vec3 vicDir = vic.position().subtract(entity.position()).normalize();
                                if (lookDir.dot(vicDir) > 0.3) {
                                    vic.hurt(damageSource, (float) (baseDamage * 2));
                                    vic.knockback(2.5, entity.getX() - vic.getX(), entity.getZ() - vic.getZ());
                                    if (world instanceof net.minecraft.world.level.Level _level) {
                                        _level.playSound(null, vic.blockPosition(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.attack.knockback")), SoundSource.HOSTILE, 1, 1);
                                    }
                                    
                                    // NEW: Increment counter on successful hit
                                    hitCount++;
                                }
                            }
                        }
                        entity.getPersistentData().putBoolean("WhipHitFrame", false);
                    }
                } 
                // Trigger Whip
                else if (cooldown == 0 && shark.getTarget() != null) {
                    LivingEntity target = shark.getTarget();
                    if (target.isAlive() && entity.distanceToSqr(target) <= 25) {
                        entity.getPersistentData().putDouble("WhipTimer", 20); 
                        entity.getPersistentData().putDouble("WhipCooldown", 100); 
                        isWhipping = true;
                    }
                }

                // --- C. SYNC VARIABLES ---
                shark.getEntityData().set(ThresherSharkEntity.SHOOT, isWhipping);

                // --- D. SPRINT LOGIC ---
                boolean shouldSprint = false;
                if (!isWhipping && entity.isInWaterOrBubble()) {
                    LivingEntity target = shark.getTarget();
                    if (target != null && target.isAlive() && shark.distanceTo(target) <= 16) {
                        shouldSprint = true;
                    }
                }
                
                shark.getEntityData().set(ThresherSharkEntity.DATA_Sprinting, shouldSprint);

                if (shouldSprint) {
                    shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 0, false, false));
                }
            }

            // ================================================= //
            //        PART 3: CLIENT SIDE ANIMATION OVERRIDE     //
            // ================================================= //
            
            if (shark.getEntityData().get(ThresherSharkEntity.SHOOT)) {
                shark.animationprocedure = "whip";
            } 
            else if (shark.getEntityData().get(ThresherSharkEntity.DATA_Sprinting)) {
                shark.animationprocedure = "sprint";
            } 
            else {
                if (shark.animationprocedure.equals("sprint") || shark.animationprocedure.equals("whip")) {
                    shark.animationprocedure = "empty";
                }
            }
        }
    }
}