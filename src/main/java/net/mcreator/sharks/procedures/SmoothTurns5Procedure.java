package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.entity.GreaterAxodileEntity;

@Mod.EventBusSubscriber(modid = "benssharks")
public class SmoothTurns5Procedure {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.level instanceof ServerLevel serverLevel) {
                for (Entity entity : serverLevel.getAllEntities()) {
                    if (entity instanceof GreaterAxodileEntity greater) {
                        applySmoothTurning(greater);
                    }
                }
            }
        }
    }

    public static void applySmoothTurning(GreaterAxodileEntity entity) {
        // 1. CONFLICT GUARD
        if (entity.isRolling() || entity.isStunned() || entity.isBreaching || !entity.animationprocedure.equals("empty")) {
            return;
        }

        // 2. DATA GATHERING
        AttributeInstance moveAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance swimAttr = entity.getAttribute(ForgeMod.SWIM_SPEED.get());

        double moveVal = (moveAttr != null) ? moveAttr.getValue() : 0.0;
        double swimVal = (swimAttr != null) ? swimAttr.getValue() : 0.0;
        
        double rawSpeed = Math.max(moveVal, swimVal);
        if (rawSpeed <= 0) rawSpeed = 0.2;

        // Reduced speed factor to 0.05 for slower swimming
        double speedFactor = 0.05; 
        double finalVelocity = rawSpeed * speedFactor;

        Vec3 currentVel = entity.getDeltaMovement();
        Vec3 currentDir = currentVel.normalize();
        Vec3 lookVec = entity.getLookAngle(); 
        
        // 3. VECTOR CALCULATION
        double verticalInput = lookVec.y * 1.8;
        verticalInput = Mth.clamp(verticalInput, -0.9, 0.9);
        Vec3 targetVec = new Vec3(lookVec.x, verticalInput, lookVec.z).normalize();
        
        // 4. TURNING SPEED
        double hTurn = 0.05; 
        double vTurn = 0.1; 

        LivingEntity target = entity.getTarget();
        if (target != null && target.isAlive()) {
            double dist = entity.distanceTo(target);
            if (dist < 5.0) { hTurn = 0.6; vTurn = 0.6; }
        }

        if (currentVel.length() < 0.02) {
            hTurn = 1.0; vTurn = 1.0; currentDir = targetVec;
        }

        // 5. INTERPOLATION
        double newX = Mth.lerp(hTurn, currentDir.x, targetVec.x);
        double newY = Mth.lerp(vTurn, currentDir.y, targetVec.y); 
        double newZ = Mth.lerp(hTurn, currentDir.z, targetVec.z);
        Vec3 newDir = new Vec3(newX, newY, newZ).normalize();

        double targetYaw = Math.toDegrees(Math.atan2(-newDir.x, newDir.z));
        double targetPitch = Math.toDegrees(Math.atan2(newDir.y, Math.sqrt(newDir.x * newDir.x + newDir.z * newDir.z)));
        
        // 6. APPLY ROTATION
        entity.setYRot(rotLerp(entity.getYRot(), (float)targetYaw, 5.0f)); 
        entity.setYHeadRot(entity.getYRot());
        entity.setXRot(rotLerp(entity.getXRot(), (float)targetPitch, 5.0f));

        // 7. VELOCITY APPLICATION
        // Robust Water Check: Checks internal flag OR the block at the feet.
        // This fixes the "AI turns off" bug when touching the floor.
        boolean isReallyInWater = entity.isInWater() || 
                                  entity.level().getBlockState(entity.blockPosition()).is(Blocks.WATER) ||
                                  entity.level().getBlockState(entity.blockPosition()).is(Blocks.BUBBLE_COLUMN);

        if (isReallyInWater) {
            // SWIMMING: Apply custom velocity vector
            entity.setDeltaMovement(newDir.scale(finalVelocity)); 
        } else {
            // LAND/AIR: Stop navigator to prevent fighting
            if (!entity.getNavigation().isDone()) {
                entity.getNavigation().stop();
            }
            entity.setXxa(0);
            entity.setZza(0);
            entity.setSpeed(0);
        }
    }

    private static float rotLerp(float current, float target, float maxChange) {
        float f = Mth.wrapDegrees(target - current);
        if (f > maxChange) f = maxChange;
        if (f < -maxChange) f = -maxChange;
        return current + f;
    }
}