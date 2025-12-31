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

import net.mcreator.sharks.entity.AxodileEntity;

@Mod.EventBusSubscriber(modid = "benssharks")
public class SmoothTurns3Procedure {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.level instanceof ServerLevel serverLevel) {
                for (Entity entity : serverLevel.getAllEntities()) {
                    if (entity instanceof AxodileEntity) {
                        if (entity instanceof LivingEntity living) {
                            applyAxodileMotion(living);
                        }
                    }
                }
            }
        }
    }

    public static void applyAxodileMotion(LivingEntity entity) {
        // 1. ANIMATION GUARD
        // If the Axodile is doing a custom move (Leap, Attack), we DO NOT touch it.
        // This prevents us from canceling the leap mid-air.
        if (entity instanceof AxodileEntity axodile) {
            if (!axodile.animationprocedure.equals("empty")) {
                return; 
            }
        }

        // --- 2. CALCULATE ROTATION & VECTORS ---
        // (We calculate this first so we can use the rotation data anywhere)

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

        Vec3 currentVel = entity.getDeltaMovement();
        Vec3 currentDir = currentVel.normalize();
        Vec3 lookVec = entity.getLookAngle(); 
        
        double verticalInput = lookVec.y * 1.8;
        verticalInput = Mth.clamp(verticalInput, -0.9, 0.9);
        Vec3 targetVec = new Vec3(lookVec.x, verticalInput, lookVec.z).normalize();
        
        double hTurn = 0.05; 
        double vTurn = 0.1; 

        if (entity instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            if (target != null && target.isAlive()) {
                double dist = entity.distanceTo(target);
                if (dist < 5.0) { hTurn = 0.6; vTurn = 0.6; }
            }
        }

        if (currentVel.length() < 0.02) {
            hTurn = 1.0; vTurn = 1.0; currentDir = targetVec;
        }

        double newX = Mth.lerp(hTurn, currentDir.x, targetVec.x);
        double newY = Mth.lerp(vTurn, currentDir.y, targetVec.y); 
        double newZ = Mth.lerp(hTurn, currentDir.z, targetVec.z);
        Vec3 newDir = new Vec3(newX, newY, newZ).normalize();

        double targetYaw = Math.toDegrees(Math.atan2(-newDir.x, newDir.z));
        double targetPitch = Math.toDegrees(Math.atan2(newDir.y, Math.sqrt(newDir.x * newDir.x + newDir.z * newDir.z)));
        
        // --- 3. APPLY ROTATION (ALWAYS) ---
        // The Axodile will visually track its target at all times (Land, Air, or Water).
        entity.setYRot(rotLerp(entity.getYRot(), (float)targetYaw, 5.0f)); 
        entity.setYHeadRot(entity.getYRot());
        entity.setXRot(rotLerp(entity.getXRot(), (float)targetPitch, 5.0f));

        // --- 4. BEHAVIOR SWITCH ---
        // This is the fix. We split behavior based on whether it is swimming or not.

        if (entity.isInWater() && !entity.onGround()) {
            // CASE A: SWIMMING
            // Apply the custom smooth velocity.
            entity.setDeltaMovement(newDir.scale(finalVelocity)); 
            
        } else {
            // CASE B: LAND / AIR (Not Leaping)
            // The user requested: "Turn off the AI navigator".
            if (entity instanceof Mob mob) {
                // 1. Stop the Vanilla Pathfinder (The Brain)
                if (!mob.getNavigation().isDone()) {
                    mob.getNavigation().stop();
                }
                
                // 2. Kill Movement Inputs (The Legs)
                // This ensures it doesn't try to "walk" slowly on land.
                mob.setXxa(0);
                mob.setZza(0);
                mob.setSpeed(0);

                // We DO NOT set DeltaMovement to 0 here. 
                // We let vanilla friction/gravity handle the stop naturally.
            }
        }
    }

    private static float rotLerp(float current, float target, float maxChange) {
        float f = Mth.wrapDegrees(target - current);
        if (f > maxChange) f = maxChange;
        if (f < -maxChange) f = -maxChange;
        return current + f;
    }
    
    public static void execute() {}
}