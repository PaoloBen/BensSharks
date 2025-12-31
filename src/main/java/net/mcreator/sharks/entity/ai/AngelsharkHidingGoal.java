package net.mcreator.sharks.entity.ai;

import net.mcreator.sharks.entity.AngelsharkEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.sounds.SoundEvents;
import java.util.EnumSet;
import java.util.List;

public class AngelsharkHidingGoal extends Goal {
    private final AngelsharkEntity shark;
    private int hideTimer;
    private int lungeTimer;
    private boolean isLunging;
    
    private static final double AMBUSH_RANGE = 3.0D; 
    private static final float LUNGE_DAMAGE = 6.0f;

    public AngelsharkHidingGoal(AngelsharkEntity shark) {
        this.shark = shark;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // --- 1. STOMACH KILL SWITCH ---
        // If the shark has ANY food (stomach > 0), strictly DISABLE hiding.
        // This forces it to use the RandomSwimmingGoal instead.
        double stomach = this.shark.getPersistentData().getDouble("SharkStomach");
        if (stomach > 0) return false;

        // 2. Cooldown Check
        if (this.shark.getAmbushCooldown() > 0) return false;

        // 3. Digging Check
        if (this.shark.isDigging()) return false;
        
        // 4. Random Chance (1 in 50)
        if (this.shark.getRandom().nextInt(50) != 0) return false;

        // 5. Floor Check
        if (!this.shark.onGround() && !this.isCloseToFloor()) return false;

        BlockPos posBelow = this.shark.blockPosition().below();
        BlockState stateBelow = this.shark.level().getBlockState(posBelow);
        
        return isSandOrGravel(stateBelow);
    }

    @Override
    public boolean canContinueToUse() {
        // --- 1. STOMACH INTERRUPT ---
        // If it eats while hiding (e.g. fed by player), wake up immediately.
        double stomach = this.shark.getPersistentData().getDouble("SharkStomach");
        if (stomach > 0) return false;

        if (this.shark.isDigging()) return false;
        if (this.isLunging) return lungeTimer > 0;
        return this.hideTimer > 0;
    }

    @Override
    public void start() {
        this.hideTimer = 200 + this.shark.getRandom().nextInt(200); 
        this.lungeTimer = 0;
        this.isLunging = false;
        
        this.shark.getNavigation().stop();
        this.shark.setDeltaMovement(0, -0.05, 0); // Anchor to floor
        
        this.shark.setHidden(true);
        this.spawnBurrowParticles();
        
        this.shark.playSound(SoundEvents.SAND_BREAK, 1.0f, 1.0f);
    }

    @Override
    public void stop() {
        this.shark.setHidden(false);
        this.isLunging = false;
        
        // Wake up nudge
        this.shark.setDeltaMovement(0, 0.05, 0); 
        
        // Apply Cooldown & Clear Target
        this.shark.setAmbushCooldown(100);
        this.shark.setTarget(null);
    }

    @Override
    public void tick() {
        // --- PHASE 1: HIDING ---
        if (!this.isLunging) {
            this.hideTimer--;
            this.shark.setDeltaMovement(0, -0.05, 0); // Keep anchored
            
            LivingEntity target = this.shark.getTarget();
            if (target != null && target.isAlive()) {
                double distance = this.shark.distanceTo(target);
                if (distance <= AMBUSH_RANGE) {
                    this.startLunge(target);
                }
            }
        } 
        // --- PHASE 2: LUNGING ---
        else {
            this.lungeTimer--;
            
            AABB hitBox = this.shark.getBoundingBox().inflate(0.5);
            List<LivingEntity> list = this.shark.level().getEntitiesOfClass(LivingEntity.class, hitBox, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
            
            for (LivingEntity entity : list) {
                if (entity != this.shark && entity == this.shark.getTarget()) {
                    this.shark.doHurtTarget(entity);
                    entity.hurt(this.shark.damageSources().mobAttack(this.shark), LUNGE_DAMAGE);
                    this.stop(); 
                    return;
                }
            }
        }
    }

    private void startLunge(LivingEntity target) {
        this.isLunging = true;
        this.lungeTimer = 15; 
        this.shark.setHidden(false); 
        
        Vec3 sharkPos = this.shark.position();
        Vec3 targetPos = target.position(); 
        Vec3 dir = targetPos.subtract(sharkPos).normalize();
        
        this.shark.setDeltaMovement(dir.x * 0.8, dir.y * 0.8 + 0.1, dir.z * 0.8);
        this.shark.lookAt(target, 100.0F, 100.0F);
        
        BlockPos posBelow = this.shark.blockPosition().below();
        BlockState stateBelow = this.shark.level().getBlockState(posBelow);
        SoundType soundType = stateBelow.getSoundType(this.shark.level(), posBelow, this.shark);
        this.shark.playSound(soundType.getBreakSound(), 1.0f, 1.0f);
        
        this.spawnBurrowParticles();
    }

    private boolean isCloseToFloor() {
        BlockPos pos = this.shark.blockPosition();
        return !this.shark.level().isEmptyBlock(pos.below());
    }

    private boolean isSandOrGravel(BlockState state) {
        return state.is(Blocks.SAND) || state.is(Blocks.GRAVEL) || 
               state.is(Blocks.RED_SAND) || state.is(Blocks.SUSPICIOUS_SAND) || 
               state.is(Blocks.SUSPICIOUS_GRAVEL);
    }

    private void spawnBurrowParticles() {
        if (this.shark.level().isClientSide) return;
        
        BlockPos pos = this.shark.blockPosition().below();
        BlockState state = this.shark.level().getBlockState(pos);
        
        if (!state.isAir() && this.shark.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                    this.shark.getX(), this.shark.getY() + 0.2, this.shark.getZ(),
                    20, 0.5, 0.1, 0.5, 0.05);
        }
    }
}