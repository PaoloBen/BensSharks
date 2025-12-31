package net.mcreator.sharks.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;

// Vanilla Fish Imports
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.TropicalFish;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.SardineEntity;

// Mod Entity Imports (Verify exact class names match your workspace)
import net.mcreator.sharks.entity.PilotFishEntity;
import net.mcreator.sharks.entity.KrillEntity;
import net.mcreator.sharks.entity.RemoraEntity;
import net.mcreator.sharks.entity.AngelsharkEntity;

import java.util.List;

public class SmoothTurns4Procedure {
    public static void execute(Entity entity) {
        if (entity == null || !(entity instanceof SardineEntity))
            return;

        SardineEntity sardine = (SardineEntity) entity;

        // 1. LAND CHECK
        if (!sardine.isInWater()) {
            sardine.getEntityData().set(SardineEntity.SPRINTING, false);
            return; 
        }

        // 2. SETTINGS
        double VIEW_RADIUS = 16.0;   
        double THREAT_RADIUS = 4.0; 
        double FRENZY_RADIUS = 1.0; 
        
        // WEIGHTS
        double W_COHESION = 1.5;
        double W_ALIGN = 2.0;    
        double W_SEPARATE = 3.5; 
        double W_SURFACE = 0.5; // Base buoyancy (weak)

        double INERTIA = 0.1; 

        Vec3 currentPos = sardine.position();
        Vec3 currentVel = sardine.getDeltaMovement();
        
        // 3. DEPTH CHECK (Strict 24-Block Rule)
        // Check the block 24 blocks above the fish.
        BlockPos deepCheckPos = new BlockPos((int)currentPos.x, (int)(currentPos.y + 8), (int)currentPos.z);
        boolean isTooDeep = false;

        // If the block 24 blocks up is STILL water, we are too deep.
        if (sardine.level().getBlockState(deepCheckPos).is(Blocks.WATER)) {
            isTooDeep = true;
            W_SURFACE = 5.0; // Override all other behaviors to surface
        }

        // Surface Bias Vector
        Vec3 vSurface = Vec3.ZERO;
        
        if (isTooDeep) {
            // STRONG UP (Forced)
            vSurface = new Vec3(0, 1.0, 0);
        } else {
            // Standard buoyancy (Keep them just under surface)
            BlockPos abovePos = new BlockPos((int)currentPos.x, (int)(currentPos.y + 1.5), (int)currentPos.z);
            if (sardine.level().getBlockState(abovePos).is(Blocks.WATER)) {
                vSurface = new Vec3(0, 0.2, 0); 
            }
        }

        // 4. SCAN
        List<Entity> nearby = sardine.level().getEntitiesOfClass(Entity.class, 
            sardine.getBoundingBox().inflate(VIEW_RADIUS));

        Vec3 vCohesion = Vec3.ZERO;
        Vec3 vAlign = Vec3.ZERO;
        Vec3 vSeparate = Vec3.ZERO;
        Vec3 vAvoid = Vec3.ZERO;
        
        int neighbors = 0;
        boolean threatFound = false;
        boolean panic = false;

        for (Entity other : nearby) {
            if (other == sardine) continue;
            
            double dist = other.distanceTo(sardine);

            // THREAT DETECTION LOGIC
            boolean isThreat = false;
            
            if (other instanceof LivingEntity && !(other instanceof Player pl && pl.getAbilities().instabuild)) {
                // By default, assume all LivingEntities are threats, then check exclusions
                isThreat = true;

                // EXCLUSION 1: Self Class
                if (other instanceof SardineEntity) isThreat = false;

                // EXCLUSION 2: Vanilla Passive Fish
                // Pufferfish, Dolphins, and Turtles are intentionally NOT listed here, so they remain threats.
                if (other instanceof Cod || other instanceof Salmon || other instanceof TropicalFish) {
                    isThreat = false;
                }

                // EXCLUSION 3: Mod Passive Fish
                if (other instanceof PilotFishEntity || other instanceof KrillEntity || other instanceof RemoraEntity || other instanceof AngelsharkEntity) {
                    isThreat = false;
                }
            }

            if (isThreat) {
                if (dist < THREAT_RADIUS) {
                    threatFound = true;
                    Vec3 away = currentPos.subtract(other.position()).normalize();
                    vAvoid = vAvoid.add(away.scale(2.0 / (dist + 0.1))); 
                    
                    if (dist < FRENZY_RADIUS) {
                        panic = true;
                    }
                }
            } 
            else if (other instanceof SardineEntity && dist <= VIEW_RADIUS) {
                neighbors++;
                vCohesion = vCohesion.add(other.position());
                vAlign = vAlign.add(other.getDeltaMovement());
                
                if (dist < 1.5) {
                    Vec3 push = currentPos.subtract(other.position()).normalize();
                    vSeparate = vSeparate.add(push.scale(1.0 / (dist + 0.01)));
                }
            }
        }

        // 5. CALCULATE TARGET DIRECTION
        Vec3 targetDir;
        boolean isSprinting = false;

        if (threatFound) {
            targetDir = vAvoid.normalize();
            
            // CORNER FIX: If hitting a wall, force UP to slide over it
            if (sardine.horizontalCollision) {
                targetDir = targetDir.add(0, 0.8, 0).normalize();
            }
            
            if (panic) {
                isSprinting = true;
                sardine.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 20, 0, false, false));
            }
        } else {
            if (neighbors > 0) {
                vCohesion = vCohesion.scale(1.0 / neighbors).subtract(currentPos).normalize();
                vAlign = vAlign.scale(1.0 / neighbors).normalize();
                
                Vec3 boid = vCohesion.scale(W_COHESION)
                           .add(vAlign.scale(W_ALIGN))
                           .add(vSeparate.scale(W_SEPARATE))
                           .add(vSurface.scale(W_SURFACE)); // High priority if deep
                
                targetDir = boid.normalize();
                
                // FLATTENING LOGIC (Prevents sinking spiral)
                // ONLY Apply flattening if we are NOT trying to emergency surface
                if (!isTooDeep) {
                     targetDir = new Vec3(targetDir.x, targetDir.y * 0.2, targetDir.z).normalize();
                }
                
            } else {
                // LONE WANDER
                if (currentVel.lengthSqr() < 0.01) {
                    double angle = Math.random() * Math.PI * 2;
                    targetDir = new Vec3(Math.cos(angle), 0, Math.sin(angle));
                } else {
                    Vec3 forward = currentVel.normalize();
                    targetDir = forward.add(vSurface.scale(0.5)).normalize(); 
                    
                    if (!isTooDeep) {
                        targetDir = new Vec3(targetDir.x, targetDir.y * 0.2, targetDir.z).normalize();
                    }
                }
            }
        }

        sardine.getEntityData().set(SardineEntity.SPRINTING, isSprinting);

        // 6. SPEED
        double baseSpeed = sardine.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
        double finalSpeed = isSprinting ? baseSpeed * 1.5 : baseSpeed; 
        
        if (finalSpeed < 0.1) finalSpeed = 0.1;

        // 7. APPLY PHYSICS
        Vec3 idealVel = targetDir.normalize().scale(finalSpeed);
        // Reduce inertia if we need to surface quickly
        double lerpFactor = (threatFound || isTooDeep) ? 0.5 : INERTIA;
        
        Vec3 finalVel = currentVel.lerp(idealVel, lerpFactor);
        sardine.setDeltaMovement(finalVel);

        // 8. ROTATION
        if (finalVel.lengthSqr() > 0.001) {
            double degreesX = Math.toDegrees(Math.atan2(-finalVel.x, finalVel.z));
            double degreesY = Math.toDegrees(Math.atan2(finalVel.y, Math.sqrt(finalVel.x * finalVel.x + finalVel.z * finalVel.z)));

            sardine.setYRot((float) degreesX);
            sardine.setYHeadRot((float) degreesX);
            sardine.setYBodyRot((float) degreesX); 
            sardine.setXRot((float) degreesY);
        }
    }
}