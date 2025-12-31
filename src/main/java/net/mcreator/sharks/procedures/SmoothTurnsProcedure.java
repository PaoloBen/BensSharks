package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

import net.mcreator.sharks.entity.ShrakEntity;
import net.mcreator.sharks.entity.MakoSharkEntity;
import net.mcreator.sharks.entity.TigerSharkEntity;
import net.mcreator.sharks.entity.BlueSharkEntity;
import net.mcreator.sharks.entity.BonnetheadSharkEntity;
import net.mcreator.sharks.entity.BlacktipReefSharkEntity;
import net.mcreator.sharks.entity.BaskingSharkEntity;
import net.mcreator.sharks.entity.BullSharkEntity;
import net.mcreator.sharks.entity.MegalodonEntity;
import net.mcreator.sharks.entity.LemonSharkEntity;
import net.mcreator.sharks.entity.WhaleSharkEntity;
import net.mcreator.sharks.entity.GreenlandSharkEntity;
import net.mcreator.sharks.entity.WhitetipSharkEntity;
import net.mcreator.sharks.entity.ThresherSharkEntity;
import net.mcreator.sharks.entity.GoblinSharkEntity;
import net.mcreator.sharks.entity.MegamouthSharkEntity;

import net.mcreator.sharks.init.BenssharksModMobEffects;

@Mod.EventBusSubscriber(modid = "benssharks")
public class SmoothTurnsProcedure {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.level instanceof ServerLevel serverLevel) {
                for (Entity entity : serverLevel.getAllEntities()) {
                    
                    if (entity instanceof ShrakEntity || 
                        entity instanceof MakoSharkEntity || 
                        entity instanceof TigerSharkEntity || 
                        entity instanceof BlueSharkEntity ||
                        entity instanceof BonnetheadSharkEntity || 
                        entity instanceof BlacktipReefSharkEntity ||
                        entity instanceof BaskingSharkEntity || 
                        entity instanceof BullSharkEntity ||
                        entity instanceof MegalodonEntity || 
                        entity instanceof LemonSharkEntity ||
                        entity instanceof WhaleSharkEntity || 
                        entity instanceof GreenlandSharkEntity ||
                        entity instanceof WhitetipSharkEntity || 
                        entity instanceof ThresherSharkEntity ||
                        entity instanceof GoblinSharkEntity ||
                        entity instanceof MegamouthSharkEntity) {
                        
                        if (entity.isInWater() && entity instanceof LivingEntity living) {
                            applySmoothMotion(living);
                        }
                    }
                }
            }
        }
    }

    public static void applySmoothMotion(LivingEntity entity) {
        // === EXCEPTION: THRESHER WHIP ===
        if (entity instanceof ThresherSharkEntity) {
            if (entity.getPersistentData().getDouble("WhipTimer") > 0) {
                return; 
            }
        }

        // === [FIX] EXCEPTION: MAKO BREACHING ===
        // If mako is in breaching state, disable steering completely.
        if (entity instanceof MakoSharkEntity mako) {
            if (mako.getEntityData().get(MakoSharkEntity.DATA_Breaching)) {
                return;
            }
        }

        // === EXCEPTION: GENERAL BREACHING / HIGH VELOCITY ===
        if (entity.getDeltaMovement().y > 0.4) {
            return;
        }

        // 1. DYNAMIC SPEED 
        double moveVal = 0.0;
        double swimVal = 0.0;
        
        AttributeInstance moveAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance swimAttr = entity.getAttribute(ForgeMod.SWIM_SPEED.get());

        if (moveAttr != null) moveVal = moveAttr.getValue();
        if (swimAttr != null) swimVal = swimAttr.getValue();
        
        double rawSpeed = Math.max(moveVal, swimVal);
        if (rawSpeed <= 0) rawSpeed = 0.2;

        double speedFactor = 0.085; 
        double finalVelocity = rawSpeed * speedFactor;

        // === FRENZY MODIFIERS ===
        if (entity.hasEffect(BenssharksModMobEffects.FRENZY.get())) {
            if (entity instanceof MakoSharkEntity) {
                finalVelocity *= 1.4; 
            }
            else if (entity instanceof BaskingSharkEntity || entity instanceof WhaleSharkEntity || entity instanceof MegamouthSharkEntity) {
                finalVelocity *= 0.7; 
            }
            else if (entity instanceof GreenlandSharkEntity) {
                finalVelocity *= 0.6;
            }
            else if (entity instanceof ThresherSharkEntity) {
                finalVelocity *= 2; 
            }
        }

        // 2. GET VECTORS
        Vec3 currentVel = entity.getDeltaMovement();
        Vec3 currentDir = currentVel.normalize();
        Vec3 lookVec = entity.getLookAngle(); 
        
        // 3. VERTICAL OVERDRIVE
        double verticalInput = lookVec.y * 1.8;
        verticalInput = Mth.clamp(verticalInput, -0.9, 0.9);
        Vec3 targetVec = new Vec3(lookVec.x, verticalInput, lookVec.z).normalize();
        
        // 4. TUNED SMOOTHING
        double hTurn = 0.03; 
        double vTurn = 0.1; 

        // === TURNING LOGIC ===
        if (entity instanceof BaskingSharkEntity || entity instanceof WhaleSharkEntity || entity instanceof GreenlandSharkEntity || entity instanceof MegamouthSharkEntity) {
            hTurn = 0.01; vTurn = 0.02;

            if (entity instanceof WhaleSharkEntity && currentVel.length() < 0.05) {
                finalVelocity = 0.1; currentDir = lookVec; 
            }

            if (entity instanceof Mob mob) {
                LivingEntity target = mob.getTarget();
                if (target != null && target.isAlive() && entity.distanceTo(target) < 6.0) {
                    hTurn = 0.15; vTurn = 0.15;
                }
            }
        }
        else if (entity instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            if (target != null && target.isAlive()) {
                double dist = entity.distanceTo(target);
                
                if (entity instanceof MakoSharkEntity) {
                    if (dist < 12.0) { hTurn = 0.9; vTurn = 0.9; }
                }
                else {
                    if (dist < 5.0) { hTurn = 0.6; vTurn = 0.6; }
                }
            }
        }

        // Anti-Stuck
        if (currentVel.length() < 0.02) {
            hTurn = 1.0; vTurn = 1.0; currentDir = targetVec;
        }

        // 5. INTERPOLATE
        double newX = Mth.lerp(hTurn, currentDir.x, targetVec.x);
        double newY = Mth.lerp(vTurn, currentDir.y, targetVec.y); 
        double newZ = Mth.lerp(hTurn, currentDir.z, targetVec.z);
        
        Vec3 newDir = new Vec3(newX, newY, newZ).normalize();

        // 6. VISUAL SYNC
        double targetYaw = Math.toDegrees(Math.atan2(-newDir.x, newDir.z));
        double targetPitch = Math.toDegrees(Math.atan2(newDir.y, Math.sqrt(newDir.x * newDir.x + newDir.z * newDir.z)));
        
        boolean isHeavy = (entity instanceof BaskingSharkEntity || entity instanceof WhaleSharkEntity || entity instanceof GreenlandSharkEntity || entity instanceof MegamouthSharkEntity);
        float rotSpeed = isHeavy ? 0.8f : 5.0f;
        
        entity.setYRot(rotLerp(entity.getYRot(), (float)targetYaw, rotSpeed)); 
        entity.setYHeadRot(entity.getYRot());
        entity.setXRot(rotLerp(entity.getXRot(), (float)targetPitch, rotSpeed));

        // 7. APPLY
        entity.setDeltaMovement(newDir.scale(finalVelocity)); 
    }

    private static float rotLerp(float current, float target, float maxChange) {
        float f = Mth.wrapDegrees(target - current);
        if (f > maxChange) f = maxChange;
        if (f < -maxChange) f = -maxChange;
        return current + f;
    }
    
    public static void execute() {}
}