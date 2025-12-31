package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

import net.mcreator.sharks.entity.SawsharkEntity;
import net.mcreator.sharks.entity.NurseSharkEntity;
import net.mcreator.sharks.entity.LeopardSharkEntity;
import net.mcreator.sharks.init.BenssharksModMobEffects;

@Mod.EventBusSubscriber(modid = "benssharks")
public class SmoothTurns2Procedure {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.level instanceof ServerLevel serverLevel) {
                for (Entity entity : serverLevel.getAllEntities()) {
                    // Check for Nurse, Leopard, OR Sawshark
                    if (entity instanceof NurseSharkEntity || entity instanceof LeopardSharkEntity || entity instanceof SawsharkEntity) {
                        if (entity.isInWater() && entity instanceof LivingEntity living) {
                            applySmoothMotion(living);
                        }
                    }
                }
            }
        }
    }

    public static void applySmoothMotion(LivingEntity entity) {
        // 1. SIT CHECK (Only for tameables)
        if (entity instanceof TamableAnimal tameable && tameable.isOrderedToSit()) {
            return;
        }

        boolean isFrenzied = entity.hasEffect(BenssharksModMobEffects.FRENZY.get());
        Vec3 currentVel = entity.getDeltaMovement();
        
        // 2. IDLE CHECK
        if (!isFrenzied && currentVel.length() < 0.02) {
            return;
        }

        // 3. PHYSICS
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

        if (isFrenzied) {
            finalVelocity *= 1.3; 
        }

        Vec3 currentDir = currentVel.normalize();
        Vec3 lookVec = entity.getLookAngle(); 
        
        double verticalInput = lookVec.y * 1.8;
        verticalInput = Mth.clamp(verticalInput, -0.9, 0.9);
        Vec3 targetVec = new Vec3(lookVec.x, verticalInput, lookVec.z).normalize();
        
        double hTurn = 0.03; 
        double vTurn = 0.1; 

        if (entity instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            if (target != null && target.isAlive()) {
                double dist = entity.distanceTo(target);
                if (dist < 5.0) {
                    hTurn = 0.6; 
                    vTurn = 0.6; 
                }
            }
        }

        // ANTI-STUCK (FRENZY ONLY)
        if (isFrenzied && currentVel.length() < 0.02) {
            hTurn = 1.0; 
            vTurn = 1.0; 
            currentDir = targetVec;
        }

        double newX = Mth.lerp(hTurn, currentDir.x, targetVec.x);
        double newY = Mth.lerp(vTurn, currentDir.y, targetVec.y); 
        double newZ = Mth.lerp(hTurn, currentDir.z, targetVec.z);
        
        Vec3 newDir = new Vec3(newX, newY, newZ).normalize();

        double targetYaw = Math.toDegrees(Math.atan2(-newDir.x, newDir.z));
        double targetPitch = Math.toDegrees(Math.atan2(newDir.y, Math.sqrt(newDir.x * newDir.x + newDir.z * newDir.z)));
        
        entity.setYRot(rotLerp(entity.getYRot(), (float)targetYaw, 5.0f)); 
        entity.setYHeadRot(entity.getYRot());
        entity.setXRot(rotLerp(entity.getXRot(), (float)targetPitch, 8.0f));

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