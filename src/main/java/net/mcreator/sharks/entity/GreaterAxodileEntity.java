package net.mcreator.sharks.entity;

import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity; 
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;

import net.mcreator.sharks.init.BenssharksModEntities;

import java.util.EnumSet;
import net.minecraft.world.entity.animal.PolarBear;

public class GreaterAxodileEntity extends PathfinderMob implements GeoEntity {
    // --- SYNCED DATA ---
    public static final EntityDataAccessor<Boolean> ROLLING = SynchedEntityData.defineId(GreaterAxodileEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> STUNNED = SynchedEntityData.defineId(GreaterAxodileEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(GreaterAxodileEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(GreaterAxodileEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    // Logic Variables
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty"; 
    
    // State Flags
    public boolean isBreaching = false;

    // Custom Logic
    private int stunTimer = 0;
    public int rollCooldown = 0;
    public int breachDelay = 0;
    private boolean wasOnGround = false;

    public GreaterAxodileEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(BenssharksModEntities.GREATER_AXODILE.get(), world);
    }

    public GreaterAxodileEntity(EntityType<GreaterAxodileEntity> type, Level world) {
        super(type, world);
        xpReward = 10;
        setNoAi(false);
        setMaxUpStep(1.0f); 
        this.setPathfindingMalus(BlockPathTypes.WATER, 0);
        this.moveControl = new AxodileMoveControl(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ROLLING, false);
        this.entityData.define(STUNNED, false);
        this.entityData.define(TEXTURE, "greateraxodile");
        this.entityData.define(ANIMATION, "undefined");
    }

    // --- ACCESSORS ---
    public String getTexture() { return this.entityData.get(TEXTURE); }
    public void setTexture(String texture) { this.entityData.set(TEXTURE, texture); }
    public static void init() {}
    public String getSyncedAnimation() { return this.entityData.get(ANIMATION); }
    public void setAnimation(String animation) { this.entityData.set(ANIMATION, animation); }

    public void setRolling(boolean rolling) {
        this.entityData.set(ROLLING, rolling);
        this.refreshDimensions();
    }
    public boolean isRolling() { return this.entityData.get(ROLLING); }

    public void setStunned(boolean stunned) {
        this.entityData.set(STUNNED, stunned);
    }
    public boolean isStunned() { return this.entityData.get(STUNNED); }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // --- SOUNDS ---
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.TURTLE_AMBIENT_LAND; 
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SHULKER_HURT; 
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHULKER_DEATH;
    }

    // --- LOGIC ---

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag) {
            this.swing(InteractionHand.MAIN_HAND);
        }
        return flag;
    }

    // PASTE THE NEW HURT METHOD HERE
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // In 1.20.1, we check tags to see if the damage is from a projectile
        if (this.isRolling() && source.is(DamageTypeTags.IS_PROJECTILE)) {
            this.playSound(SoundEvents.ARMOR_STAND_BREAK, 1.0F, 0.5F);
            
            if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMALL_FLAME, 
                    this.getX(), this.getY() + 1, this.getZ(), 
                    5, 0.2, 0.2, 0.2, 0.05);
                
                // --- RICOCHET LOGIC ---
                // This physically bounces the projectile away if it exists
                Entity projectile = source.getDirectEntity();
                if (projectile != null) {
                    Vec3 bounce = projectile.getDeltaMovement().scale(-0.5).add(0, 0.2, 0);
                    projectile.setDeltaMovement(bounce);
                }
            }
            return false; // Cancel damage
        }
        return super.hurt(source, amount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    @Override
    public void tick() {
        super.tick();
        this.refreshDimensions();
        
        // Cooldowns
        if (this.rollCooldown > 0) this.rollCooldown--;
        if (this.breachDelay > 0) this.breachDelay--;

        // Stun Failsafe
        if (this.stunTimer > 0) {
            this.stunTimer--;
        }
        if (this.stunTimer <= 0 && this.isStunned()) {
            this.setStunned(false);
        }

        // Rolling Rotation
        if (this.isRolling()) {
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
        }

        // --- LANDING SOUND LOGIC ---
        boolean inWaterOrBubble = this.isInWater() || this.level().getBlockState(this.blockPosition()).is(Blocks.WATER);
        
        if (!this.wasOnGround && this.onGround() && !inWaterOrBubble) {
            this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("benssharks", "landing_impact")), 2.0F, 1.0F);
        }
        this.wasOnGround = this.onGround();

        // ICE BREAKING: STANDING
        if (this.onGround()) {
            BlockPos below = this.blockPosition().below();
            breakIceInArea(below, 2, true); 
        }
    }

    // --- ICE BREAKING HELPER ---
    public void breakIceInArea(BlockPos center, int radius, boolean checkBelow) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                tryBreakIce(center.offset(x, 0, z), checkBelow);
            }
        }
    }

    private void tryBreakIce(BlockPos pos, boolean checkBelow) {
        BlockState state = this.level().getBlockState(pos);
        if (state.is(Blocks.ICE) || state.is(Blocks.PACKED_ICE) || state.is(Blocks.BLUE_ICE) || state.is(Blocks.FROSTED_ICE)) {
            
            BlockState stateBelow = this.level().getBlockState(pos.below());

            if (checkBelow) {
                if (!stateBelow.isAir() && !stateBelow.is(Blocks.WATER) && !stateBelow.is(Blocks.BUBBLE_COLUMN)) {
                    return; 
                }
            }

            // Spawn Particles & Play Sound (Simulate break)
            this.level().levelEvent(2001, pos, Block.getId(state));

            // Replacement Logic
            boolean surroundedByWater = stateBelow.is(Blocks.WATER);
            if (!surroundedByWater) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    if (this.level().getBlockState(pos.relative(dir)).is(Blocks.WATER)) {
                        surroundedByWater = true;
                        break;
                    }
                }
            }
            
            // Only replace with water if it is REGULAR ICE and surrounded by water
            if (surroundedByWater && state.is(Blocks.ICE)) {
                this.level().setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
            } else {
                // Otherwise (Packed/Blue/Frosted), just destroy it (turn to air/drops)
                this.level().destroyBlock(pos, true);
            }
        }
    }

    // --- NAVIGATION ---
    @Override
    protected PathNavigation createNavigation(Level world) {
        return new WaterBoundPathNavigation(this, world);
    }
    
    static class AxodileMoveControl extends MoveControl {
        private final GreaterAxodileEntity axodile;

        public AxodileMoveControl(GreaterAxodileEntity entity) {
            super(entity);
            this.axodile = entity;
        }

        @Override
        public void tick() {
            if (this.axodile.isStunned()) {
                this.axodile.setSpeed(0);
                return;
            }

            if (this.axodile.isInWater()) {
                this.axodile.setDeltaMovement(this.axodile.getDeltaMovement().add(0, 0.005, 0));
                
                if (this.operation == Operation.MOVE_TO && !this.axodile.getNavigation().isDone()) {
                    double dx = this.wantedX - this.axodile.getX();
                    double dy = this.wantedY - this.axodile.getY();
                    double dz = this.wantedZ - this.axodile.getZ();
                    
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (dist < 0.1) {
                        this.axodile.setZza(0.0F);
                        return;
                    }
                    
                    float speed = (float) (this.speedModifier * this.axodile.getAttributeValue(Attributes.MOVEMENT_SPEED)) * 2.5F;
                    this.axodile.setSpeed(speed);
                    
                    float targetYRot = (float) (Mth.atan2(dz, dx) * (180F / Math.PI)) - 90.0F;
                    this.axodile.setYRot(this.rotlerp(this.axodile.getYRot(), targetYRot, 10.0F));
                    this.axodile.yBodyRot = this.axodile.getYRot();
                    this.axodile.yHeadRot = this.axodile.getYRot();

                    float targetXRot = (float) -(Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * (180F / Math.PI));
                    this.axodile.setXRot(this.rotlerp(this.axodile.getXRot(), targetXRot, 10.0F));
                    
                    float f3 = Mth.cos(this.axodile.getXRot() * ((float)Math.PI / 180F));
                    float f4 = Mth.sin(this.axodile.getXRot() * ((float)Math.PI / 180F));
                    
                    this.axodile.setZza(f3 * speed);
                    this.axodile.setYya(-f4 * speed);
                } else {
                    this.axodile.setSpeed(0);
                    this.axodile.setYya(0);
                    this.axodile.setZza(0);
                }
            } else {
                if (this.operation == Operation.MOVE_TO && !this.axodile.getNavigation().isDone()) {
                    if (this.axodile.isRolling()) return;

                    double dx = this.wantedX - this.axodile.getX();
                    double dy = this.wantedY - this.axodile.getY();
                    double dz = this.wantedZ - this.axodile.getZ();
                    double distSqr = dx * dx + dy * dy + dz * dz;

                    if (distSqr < 2.5E-7D) {
                        this.mob.setZza(0.0F);
                        return;
                    }

                    float targetYaw = (float) (Mth.atan2(dz, dx) * (double) (180F / (float) Math.PI)) - 90.0F;
                    this.axodile.setYRot(this.rotlerp(this.axodile.getYRot(), targetYaw, 30.0F)); 
                    this.axodile.setYHeadRot(this.axodile.getYRot());
                    this.axodile.setYBodyRot(this.axodile.getYRot());

                    float speed = (float) (this.speedModifier * this.axodile.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    this.axodile.setSpeed(speed);
                    
                    double d4 = this.wantedY - this.axodile.getY();
                    if (d4 > 0.0D && distSqr < 4.0D) { 
                        this.axodile.getJumpControl().jump();
                    }
                } else {
                    this.axodile.setZza(0.0F);
                }
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new AxodileStunGoal(this));
        this.goalSelector.addGoal(1, new AxodileBreachGoal(this));
        this.goalSelector.addGoal(2, new AxodileRollGoal(this));
        
        // Attack Range 1.3F
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return (double)(this.mob.getBbWidth() * 1.3F * this.mob.getBbWidth() * 1.3F + entity.getBbWidth());
            }
        });
        
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1, 40));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Modded Targets
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, GreenlandSharkEntity.class, false, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, ShrakEntity.class, false, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, TigerSharkEntity.class, false, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, MakoSharkEntity.class, false, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, ThresherSharkEntity.class, false, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, BlueSharkEntity.class, false, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, GoblinSharkEntity.class, false, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, BullSharkEntity.class, false, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, WhitetipSharkEntity.class, false, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, LemonSharkEntity.class, false, false));
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, NurseSharkEntity.class, false, false));
		this.targetSelector.addGoal(15, new NearestAttackableTargetGoal(this, LeopardSharkEntity.class, false, false));
		this.targetSelector.addGoal(16, new NearestAttackableTargetGoal(this, SawsharkEntity.class, false, false));
		this.targetSelector.addGoal(17, new NearestAttackableTargetGoal(this, BlacktipReefSharkEntity.class, false, false));
		this.targetSelector.addGoal(18, new NearestAttackableTargetGoal(this, BonnetheadSharkEntity.class, false, false));
		this.targetSelector.addGoal(19, new NearestAttackableTargetGoal(this, SeaLionEntity.class, false, false));
		
		// Vanilla Targets
		this.targetSelector.addGoal(20, new NearestAttackableTargetGoal(this, PolarBear.class, true, false));
		this.targetSelector.addGoal(21, new NearestAttackableTargetGoal(this, Player.class, true, false));
		this.targetSelector.addGoal(22, new NearestAttackableTargetGoal(this, Drowned.class, true, false));
		this.targetSelector.addGoal(23, new NearestAttackableTargetGoal(this, Zombie.class, true, false));
		this.targetSelector.addGoal(24, new NearestAttackableTargetGoal(this, Skeleton.class, true, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.5); 
        builder = builder.add(Attributes.MAX_HEALTH, 50);
        builder = builder.add(Attributes.ARMOR, 17);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
        return builder;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(3.0f);
    }

    @Override
    public boolean canBreatheUnderwater() { return true; }
    @Override
    public MobType getMobType() { return MobType.WATER; }

    // --- ANIMATIONS ---

    private PlayState movementPredicate(AnimationState event) {
        if (this.isStunned()) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("idle_land"));
        }
        if (this.isRolling()) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("roll"));
        }
        if (!this.isInWater() && !this.onGround() && this.getDeltaMovement().y > 0.2) {
             return event.setAndContinue(RawAnimation.begin().thenPlay("waterjump"));
        }
        if (!this.isInWater() && !this.onGround() && this.getDeltaMovement().y < -0.2) {
             return event.setAndContinue(RawAnimation.begin().thenLoop("fall"));
        }
        if (this.isInWater()) {
             if (this.getDeltaMovement().y > 0.1) {
                 return event.setAndContinue(RawAnimation.begin().thenLoop("swimup"));
             }
             return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
        }
        if (event.isMoving()) {
             return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("idle_land"));
    }

    private PlayState attackingPredicate(AnimationState event) {
        double d1 = this.getX() - this.xOld;
        double d0 = this.getZ() - this.zOld;
        float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("bite"));
        }
        return PlayState.CONTINUE;
    }

    String prevAnim = "empty";

    private PlayState procedurePredicate(AnimationState event) {
        String anim = this.getSyncedAnimation();
        if (!anim.equals("undefined") && !anim.equals("empty")) {
            return event.setAndContinue(RawAnimation.begin().thenPlay(anim));
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // --- CUSTOM GOALS ---

    static class AxodileStunGoal extends Goal {
        private final GreaterAxodileEntity mob;
        private int attackTimer;

        public AxodileStunGoal(GreaterAxodileEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }
        @Override
        public boolean canUse() { return this.mob.isStunned(); }
        
        @Override
        public void start() {
            this.mob.getNavigation().stop();
            this.attackTimer = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                if (this.mob.distanceToSqr(target) < 6.0D) {
                    if (this.attackTimer <= 0) {
                        this.mob.doHurtTarget(target);
                        this.attackTimer = 20; 
                    } else {
                        this.attackTimer--;
                    }
                }
            }
        }
    }

    static class AxodileRollGoal extends Goal {
        private final GreaterAxodileEntity mob;
        private int rollDuration;
        private int attackCooldown = 0;
        private int soundTimer = 0;
        private int warmupTimer = 0; 
        private Vec3 rollDirection;

        public AxodileRollGoal(GreaterAxodileEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && !this.mob.isStunned() && !this.mob.isInWater() && this.mob.onGround() && this.mob.distanceToSqr(target) > 16.0D && this.mob.rollCooldown == 0;
        }

        @Override
        public void start() {
            this.mob.setRolling(true);
            this.rollDuration = 0;
            this.attackCooldown = 0;
            this.warmupTimer = 3; 
            
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                Vec3 dir = target.position().subtract(this.mob.position()).normalize();
                this.rollDirection = new Vec3(dir.x * 0.8, 0, dir.z * 0.8);
                
                float yRot = (float) (Mth.atan2(dir.z, dir.x) * (180F / Math.PI)) - 90.0F;
                this.mob.setYRot(yRot);
                this.mob.setYBodyRot(yRot); 
                this.mob.setYHeadRot(yRot);
            }
        }

        @Override
        public void stop() {
            this.mob.setRolling(false);
            this.mob.setXRot(0f);
            
            this.mob.rollCooldown = 100; // 5s Cooldown
            
            if (this.mob.isInWater()) {
                this.mob.breachDelay = 60; // 3s Breach Delay
            }
        }

        @Override
        public void tick() {
            if (this.warmupTimer > 0) {
                this.warmupTimer--;
                return; 
            }

            this.rollDuration++;
            
            if (this.rollDirection != null) {
                this.mob.setDeltaMovement(this.rollDirection.x, this.mob.getDeltaMovement().y, this.rollDirection.z);
                
                float yRot = (float) (Mth.atan2(this.rollDirection.z, this.rollDirection.x) * (180F / Math.PI)) - 90.0F;
                this.mob.setYRot(yRot);
                
                // ICE BREAKING: ROLLING (Force Break)
                Vec3 normDir = this.rollDirection.normalize();
                for (int i = 1; i <= 3; i++) {
                    BlockPos frontPos = BlockPos.containing(
                        this.mob.getX() + normDir.x * i, 
                        this.mob.getY() + 0.5, 
                        this.mob.getZ() + normDir.z * i
                    );
                    this.mob.breakIceInArea(frontPos, 2, false); 
                }
            }

            this.soundTimer++;
            if (this.soundTimer >= 4) { 
                this.mob.playSound(SoundEvents.DEEPSLATE_STEP, 2.5f, 0.1f); 
                this.soundTimer = 0;
            }

            if (this.mob.horizontalCollision) {
                triggerStun();
                return;
            }

            if (this.attackCooldown > 0) {
                this.attackCooldown--;
            } else {
                for (LivingEntity entity : this.mob.level().getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(0.5))) {
                    if (entity != this.mob) {
                        
                        if (entity instanceof Player player && player.isBlocking()) {
                            this.mob.playSound(SoundEvents.SHIELD_BREAK, 1.0F, 1.0F);
                            
                            ItemStack shield = player.getUseItem();
                            InteractionHand hand = player.getUsedItemHand();
                            
                            if (shield.isDamageableItem()) {
                                shield.hurtAndBreak(40, player, (p) -> p.broadcastBreakEvent(hand));
                            }
                            
                            player.disableShield(true);
                            
                        } else {
                            this.mob.doHurtTarget(entity);
                            this.mob.swing(InteractionHand.MAIN_HAND);
                        }
                        
                        double dx = entity.getX() - this.mob.getX();
                        double dz = entity.getZ() - this.mob.getZ();
                        double dist = Math.sqrt(dx * dx + dz * dz);
                        if (dist > 0.01) { dx /= dist; dz /= dist; } else { dx = 1.0; dz = 0.0; }
                        
                        double strength = 1.5;
                        entity.setDeltaMovement(dx * strength, 0.4, dz * strength);
                        entity.hasImpulse = true;
                        
                        if (this.mob.level() instanceof ServerLevel serverLevel) {
                             serverLevel.sendParticles(ParticleTypes.CRIT, entity.getX(), entity.getY() + entity.getBbHeight() / 2.0, entity.getZ(), 15, 0.5, 0.5, 0.5, 0.1);
                        }

                        triggerStun();
                        return; 
                    }
                }
            }
        }

        private void triggerStun() {
            this.mob.setStunned(true);
            this.mob.stunTimer = 60; 
            this.mob.setRolling(false);
            Vec3 currentMotion = this.mob.getDeltaMovement();
            this.mob.setDeltaMovement(currentMotion.scale(-0.5)); 
            this.mob.playSound(SoundEvents.WITHER_BREAK_BLOCK, 1.0f, 1.0f);
        }
        
        @Override
        public boolean canContinueToUse() {
            return !this.mob.isStunned() && !this.mob.isInWater() && this.rollDuration < 100;
        }
    }

    static class AxodileBreachGoal extends Goal {
        private final GreaterAxodileEntity mob;
        private int cooldown = 0;
        private int breachTimer = 0; // Failsafe timer

        public AxodileBreachGoal(GreaterAxodileEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (cooldown > 0) {
                cooldown--;
                return false;
            }
            if (this.mob.breachDelay > 0) return false;
            
            // Ensure there is actually water above to breach through
            if (!this.mob.level().getBlockState(this.mob.blockPosition().above(1)).is(Blocks.WATER)) {
                return false; 
            }
            if (!this.mob.level().getBlockState(this.mob.blockPosition().above()).is(Blocks.WATER)) {
                return false;
            }
            
            LivingEntity target = this.mob.getTarget();
            return this.mob.isInWater() && target != null && target.getY() > this.mob.getY() + 1.5;
        }

        @Override
        public void start() {
            LivingEntity target = this.mob.getTarget();
            if (target == null) return;

            this.mob.isBreaching = true;
            this.breachTimer = 0; // Reset timer

            Vec3 dir = target.position().subtract(this.mob.position()).normalize();
            // Strength 1.0H, 1.2V
            Vec3 jumpForce = new Vec3(dir.x * 1.0, 1.2, dir.z * 1.0);
            this.mob.setDeltaMovement(jumpForce);
            
            float yRot = (float) (Mth.atan2(dir.z, dir.x) * (180F / Math.PI)) - 90.0F;
            this.mob.setYRot(yRot);
            this.mob.setYBodyRot(yRot);
            
            this.mob.getNavigation().stop(); 
            this.cooldown = 80;
        }
        
        @Override
        public void stop() {
            this.mob.isBreaching = false;
        }
        
        @Override
        public boolean canContinueToUse() {
            // FAILSAFE 1: Timeout (Prevent infinite loop if stuck)
            if (this.breachTimer > 40) return false; 

            // FAILSAFE 2: Vertical Collision (Hit a ceiling/block)
            // If we hit something vertically and we aren't on the ground, we hit our head.
            if (this.mob.verticalCollision && !this.mob.onGround()) return false;

            // FAILSAFE 3: Lost Momentum Underwater
            // If we are still in water but lost upward velocity, abort.
            if (this.mob.isInWater() && this.mob.getDeltaMovement().y < 0.05) return false;

            return this.mob.isBreaching && !this.mob.onGround() && (this.mob.isInWater() || this.mob.getDeltaMovement().y > -0.5);
        }

        @Override
        public void tick() {
            this.breachTimer++; // Increment timer

            Vec3 motion = this.mob.getDeltaMovement();
            if (motion.horizontalDistanceSqr() > 0.01) {
                float yRot = (float) (Mth.atan2(motion.z, motion.x) * (180F / Math.PI)) - 90.0F;
                this.mob.setYRot(yRot);
                this.mob.setYBodyRot(yRot);
                this.mob.setYHeadRot(yRot);
            }
            
            BlockPos headPos = this.mob.blockPosition().above();
            this.mob.breakIceInArea(headPos, 2, false);
            this.mob.breakIceInArea(headPos.above(), 2, false);
        }
    }
}