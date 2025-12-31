package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.BlockTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block; // Added this import
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.mcreator.sharks.entity.AxodileEntity;

public class AxodileOnEntityTickUpdateProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null) return;
        if (!(entity instanceof AxodileEntity)) return;
        
        // SERVER SIDE ONLY
        if (world.isClientSide()) return;

        AxodileEntity axodile = (AxodileEntity) entity;

        // --- TIMER UPDATE ---
        double jumpTimer = entity.getPersistentData().getDouble("JumpTimer");
        double cooldown = entity.getPersistentData().getDouble("LeapCooldown");
        double delay = entity.getPersistentData().getDouble("LeapDelay");

        if (cooldown > 0) entity.getPersistentData().putDouble("LeapCooldown", cooldown - 1);
        
        // --- PHASE 2: WIND-UP ---
        if (delay > 0) {
            String currentAnim = entity.getPersistentData().getString("CurrentLeapAnim");
            axodile.setAnimation(currentAnim); 
            
            // Stop sliding
            entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
            
            // Face Target (Initial)
            if (axodile.getTarget() != null) faceTarget(axodile, axodile.getTarget());
            
            entity.getPersistentData().putDouble("LeapDelay", delay - 1);
            if (delay - 1 <= 0) {
                performLaunch(axodile, world, currentAnim);
            }
            return; 
        }

        // --- PHASE 3: AIRBORNE ---
        if (jumpTimer > 0) {
            String currentAnim = entity.getPersistentData().getString("CurrentLeapAnim");
            axodile.setAnimation(currentAnim); 
            
            // CONSTANT FACING: Update rotation every tick while airborne
            if (axodile.getTarget() != null) {
                faceTarget(axodile, axodile.getTarget());
            }
            
            entity.getPersistentData().putDouble("JumpTimer", jumpTimer - 1);
            
            // RESET at end of jump
            if (jumpTimer - 1 <= 0) {
                axodile.setAnimation("empty");
            }
            return; 
        }

        // --- PHASE 1: TRIGGER ---
        if (cooldown <= 0 && axodile.getTarget() != null) {
            LivingEntity target = axodile.getTarget();
            double dist = entity.distanceTo(target);
            
            // A. WATER BREACH
            if (entity.isInWater()) {
                if (target.getY() > entity.getY() + 1.0 || (dist > 2 && dist < 12 && !target.isInWater())) {
                    BlockPos posAbove = entity.blockPosition().above();
                    boolean brokeIce = breakIceInBox(world, entity.getBoundingBox().move(0, 1, 0));

                    if (world.isEmptyBlock(posAbove) || brokeIce) {
                        triggerWindup(axodile, "landjump", 1); 
                        return;
                    }
                }
            } 
            // B. LAND LUNGE
            else if (entity.onGround()) {
                if (dist > 3.0 && dist < 12.0 || target.getY() > entity.getY() + 1.0) {
                     triggerWindup(axodile, "landjumpold", 1); 
                     return;
                }
            }
        }

        // --- DEFAULT RESET ---
        if (!axodile.getSyncedAnimation().equals("empty")) {
            axodile.setAnimation("empty");
        }

        // --- LANDING IMPACT ---
        if (!entity.isInWater() && entity.onGround()) {
            breakIceInBox(world, entity.getBoundingBox().move(0, -0.5, 0));
        }
    }

    private static void triggerWindup(AxodileEntity axodile, String animName, int delayTicks) {
        axodile.getPersistentData().putString("CurrentLeapAnim", animName);
        axodile.getPersistentData().putDouble("LeapDelay", delayTicks);
        axodile.getPersistentData().putDouble("LeapCooldown", 40); 
        axodile.setAnimation(animName);
    }

    private static void performLaunch(AxodileEntity axodile, LevelAccessor world, String currentAnim) {
        LivingEntity target = axodile.getTarget();
        if (target == null) return;

        // Force Face Target one last time before calculating vector
        faceTarget(axodile, target);

        Vec3 dir = target.position().subtract(axodile.position()).normalize();
        
        if (currentAnim.equals("landjump")) {
            // WATER: 1.2 H, 1.0 V
            axodile.setDeltaMovement(dir.x * 1.2, 1.0, dir.z * 1.2);
            axodile.getPersistentData().putDouble("JumpTimer", 15); 
            playJumpSound(axodile, world);
        } 
        else if (currentAnim.equals("landjumpold")) {
            // LAND: 0.6 H, 0.5 V
            axodile.setDeltaMovement(dir.x * 0.6, 0.5, dir.z * 0.6);
            axodile.getPersistentData().putDouble("JumpTimer", 15); 
        }
    }

    private static void faceTarget(AxodileEntity axodile, LivingEntity target) {
        double d0 = target.getX() - axodile.getX();
        double d2 = target.getZ() - axodile.getZ();
        float f = (float) (Math.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90F;
        axodile.setYRot(f);
        axodile.setYHeadRot(f);
        axodile.yBodyRot = f; 
    }

    private static void playJumpSound(AxodileEntity axodile, LevelAccessor world) {
        if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
                _level.playSound(null, axodile.blockPosition(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dolphin.jump")), SoundSource.HOSTILE, 1.0f, 1.0f);
            } else {
                _level.playLocalSound(axodile.getX(), axodile.getY(), axodile.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dolphin.jump")), SoundSource.HOSTILE, 1.0f, 1.0f, false);
            }
        }
    }

    private static boolean breakIceInBox(LevelAccessor world, AABB box) {
        boolean brokeIce = false;
        AABB checkArea = box.inflate(0.1);
        int minX = (int) Math.floor(checkArea.minX);
        int maxX = (int) Math.ceil(checkArea.maxX);
        int minY = (int) Math.floor(checkArea.minY);
        int maxY = (int) Math.ceil(checkArea.maxY);
        int minZ = (int) Math.floor(checkArea.minZ);
        int maxZ = (int) Math.ceil(checkArea.maxZ);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    
                    if (state.is(BlockTags.ICE) || 
                        state.getBlock() == Blocks.PACKED_ICE ||
                        state.getBlock() == Blocks.BLUE_ICE ||
                        state.getBlock() == Blocks.FROSTED_ICE) {
                        
                        BlockPos belowPos = pos.below();
                        BlockState belowState = world.getBlockState(belowPos);
                        
                        // Check if block below is effectively liquid/water
                        if (!belowState.getFluidState().isEmpty() || belowState.is(Blocks.BUBBLE_COLUMN)) {
                            // 1. Play Break Particles/Sound
                            world.levelEvent(2001, pos, Block.getId(state));

                            // 2. Convert Ice -> Water
                            world.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
                            brokeIce = true;
                        } 
                        else if (belowState.isAir()) {
                             // 1. Play Break Particles/Sound
                             world.levelEvent(2001, pos, Block.getId(state));

                             // 2. Destroy (Gravity/Air)
                             world.destroyBlock(pos, false);
                             brokeIce = true;
                        }
                    }
                }
            }
        }
        return brokeIce;
    }
}