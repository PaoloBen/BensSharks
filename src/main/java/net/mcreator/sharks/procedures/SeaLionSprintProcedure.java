package net.mcreator.sharks.procedures;

import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.TigerSharkEntity;
import net.mcreator.sharks.entity.ShrakEntity;
import net.mcreator.sharks.entity.SeaLionEntity;
import net.mcreator.sharks.entity.MakoSharkEntity;
import net.mcreator.sharks.entity.GreaterAxodileEntity;
import net.mcreator.sharks.entity.BullSharkEntity;
import net.mcreator.sharks.entity.AxodileEntity;

import java.util.List;
import java.util.Comparator;

public class SeaLionSprintProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null) return;
        if (!(entity instanceof SeaLionEntity seaLion)) return;

        // ================================================= //
        //        PART A: LAND ANIMATION FIX                 //
        // ================================================= //
        // If we are NOT in water (or bubble column), we must abort immediately.
        if (!seaLion.isInWaterOrBubble()) {
            if (seaLion.hasEffect(BenssharksModMobEffects.FRENZY.get())) {
                seaLion.removeEffect(BenssharksModMobEffects.FRENZY.get());
                seaLion.setAnimation("empty");
            }
            
            String currentAnim = seaLion.getSyncedAnimation();
            // If the animation is stuck on sprint/swim, clear it so Idle/Walk can play.

            return; // Stop execution here.
        }

        // ================================================= //
        //        PART B: DETECTION LOGIC                    //
        // ================================================= //
        boolean shouldSprint = false;
        boolean isFleeing = false; // Distinguishes between attacking vs running away
        Entity fleeingFrom = null;
        final double detectionRange = 6.0;

        // 1. Check Target (Attacking)
        LivingEntity target = seaLion.getTarget();
        if (target != null && target.isAlive()) {
            if (seaLion.distanceTo(target) <= detectionRange) {
                boolean isInvalidPlayer = (target instanceof Player _plr && (_plr.isCreative() || _plr.isSpectator()));
                if (!isInvalidPlayer) {
                    shouldSprint = true;
                }
            }
        }

        // 2. Check Predators (Fleeing)
        if (!shouldSprint) {
            List<Entity> predators = world.getEntitiesOfClass(Entity.class, 
                new AABB(
                    seaLion.getX() - detectionRange, seaLion.getY() - detectionRange, seaLion.getZ() - detectionRange, 
                    seaLion.getX() + detectionRange, seaLion.getY() + detectionRange, seaLion.getZ() + detectionRange
                ), e -> (
                    e instanceof ShrakEntity ||         
                    e instanceof TigerSharkEntity || 
                    e instanceof BullSharkEntity || 
                    e instanceof MakoSharkEntity || 
                    e instanceof AxodileEntity || 
                    e instanceof GreaterAxodileEntity
                )
            );

            if (!predators.isEmpty()) {
                shouldSprint = true;
                isFleeing = true;
                // Find closest predator to run from
                fleeingFrom = predators.stream().min(Comparator.comparingDouble(seaLion::distanceTo)).orElse(null);
            }
        }

        // ================================================= //
        //        PART C: EXECUTION                          //
        // ================================================= //
        String currentAnim = seaLion.getSyncedAnimation();

        // LOGIC CHANGE: Only enter this block if sprinting AND inside water/bubble
        if (shouldSprint && seaLion.isInWaterOrBubble()) {
            // 1. Set Animation
            if (!"sprint".equals(currentAnim)) {
                seaLion.setAnimation("sprint");
            }
        
            // 2. Apply Effect
            seaLion.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 1, false, false));
        
            // 3. Determine Movement Vector
            Vec3 moveDir = seaLion.getLookAngle(); // Default to looking direction
        
            if (isFleeing && fleeingFrom != null) {
                moveDir = calculateFleeVector(world, seaLion, fleeingFrom);
            } 
            else if (target != null) {
                // If attacking, just move towards target (standard)
                moveDir = target.position().subtract(seaLion.position()).normalize();
            }
        
            // 4. Apply Smooth Motion
            applySmoothSprint(seaLion, moveDir);
        
        } else {
            // EXIT LOGIC
            // If on land, cut immediately to "empty"
            if (!seaLion.isInWaterOrBubble()) {
                if ("sprint".equals(currentAnim) || "swim".equals(currentAnim)) {
                    seaLion.setAnimation("empty");
                }
            } 
            else {
                // We are in water, so we can transition smoothly
                if ("sprint".equals(currentAnim)) {
                    seaLion.setAnimation("swim"); 
                } 
                else if (!"empty".equals(currentAnim) && !"swim".equals(currentAnim)) {
                     seaLion.setAnimation("empty");
                }
            }
        
            if (seaLion.hasEffect(BenssharksModMobEffects.FRENZY.get())) {
                seaLion.removeEffect(BenssharksModMobEffects.FRENZY.get());
            }
        }
    } // <--- THIS WAS THE MISSING BRACKET

    // ================================================= //
    //        HELPER: FLEEING & JUKING LOGIC             //
    // ================================================= //
    private static Vec3 calculateFleeVector(LevelAccessor world, SeaLionEntity seaLion, Entity predator) {
        Vec3 selfPos = seaLion.position();
        
        // 1. Basic Flee Vector (Away from predator)
        Vec3 fleeDir = selfPos.subtract(predator.position()).normalize();

        // 2. LAND SEEKING (Prioritize safety)
        BlockPos safetyPos = findNearestLand(world, seaLion.blockPosition());
        if (safetyPos != null) {
            Vec3 landDir = Vec3.atCenterOf(safetyPos).subtract(selfPos).normalize();
            // Blend: 70% towards land, 30% away from predator
            return landDir.scale(0.7).add(fleeDir.scale(0.3)).normalize();
        }

        // 3. JUKING (If no land found, Zig-Zag)
        long ticks = seaLion.tickCount;
        boolean jukeLeft = (ticks / 10) % 2 == 0;
        
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = fleeDir.cross(up).normalize(); // Perpendicular vector
        
        // Add a strong sideways force
        double jukeStrength = 0.6; 
        Vec3 jukeVec = jukeLeft ? right.scale(jukeStrength) : right.scale(-jukeStrength);
        
        return fleeDir.add(jukeVec).normalize();
    }

    // ================================================= //
    //        HELPER: FIND LAND                          //
    // ================================================= //
    private static BlockPos findNearestLand(LevelAccessor world, BlockPos center) {
        BlockPos nearest = null;
        double minDstSq = Double.MAX_VALUE;
        int radius = 16; // Scan radius

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Check a vertical column for "surface"
                for (int y = -2; y <= 4; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    
                    // Logic: Block is solid, Block above is AIR
                    if (world.getBlockState(pos).isSolid() && 
                        world.getBlockState(pos.above()).isAir() &&
                        !world.getBlockState(pos).is(Blocks.WATER)) { // Not water
                        
                        double dstSq = center.distSqr(pos);
                        if (dstSq < minDstSq) {
                            minDstSq = dstSq;
                            nearest = pos;
                        }
                    }
                }
            }
        }
        return nearest;
    }

    // ================================================= //
    //        HELPER: SMOOTH MOTION                      //
    // ================================================= //
    private static void applySmoothSprint(SeaLionEntity entity, Vec3 desiredDir) {
        // 1. SPEED CALCULATION
        double moveVal = 0.0;
        double swimVal = 0.0;
        AttributeInstance moveAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance swimAttr = entity.getAttribute(ForgeMod.SWIM_SPEED.get());
        if (moveAttr != null) moveVal = moveAttr.getValue();
        if (swimAttr != null) swimVal = swimAttr.getValue();
        double rawSpeed = Math.max(moveVal, swimVal);
        if (rawSpeed <= 0) rawSpeed = 0.2;

        double finalVelocity = rawSpeed * 0.085 * 0.8; // Base * Factor * Boost

        // 2. INTERPOLATION
        Vec3 currentVel = entity.getDeltaMovement();
        Vec3 currentDir = currentVel.normalize();
        
        // Use a slightly faster turn rate for responsive juking
        double hTurn = 0.2; 
        double vTurn = 0.2; 

        // Prevent stuck
        if (currentVel.length() < 0.02) {
            currentDir = desiredDir;
            hTurn = 1.0;
        }

        double newX = Mth.lerp(hTurn, currentDir.x, desiredDir.x);
        double newY = Mth.lerp(vTurn, currentDir.y, desiredDir.y); 
        double newZ = Mth.lerp(hTurn, currentDir.z, desiredDir.z);
        
        Vec3 newDir = new Vec3(newX, newY, newZ).normalize();

        // 3. ROTATION SYNC
        double targetYaw = Math.toDegrees(Math.atan2(-newDir.x, newDir.z));
        double targetPitch = Math.toDegrees(Math.atan2(newDir.y, Math.sqrt(newDir.x * newDir.x + newDir.z * newDir.z)));
        
        float rotSpeed = 8.0f; // Snappier rotation for juking
        entity.setYRot(rotLerp(entity.getYRot(), (float)targetYaw, rotSpeed)); 
        entity.setYHeadRot(entity.getYRot());
        entity.setXRot(rotLerp(entity.getXRot(), (float)targetPitch, rotSpeed));

        // 4. APPLY
        entity.setDeltaMovement(newDir.scale(finalVelocity)); 
    }

    private static float rotLerp(float current, float target, float maxChange) {
        float f = Mth.wrapDegrees(target - current);
        if (f > maxChange) f = maxChange;
        if (f < -maxChange) f = -maxChange;
        return current + f;
    }
}