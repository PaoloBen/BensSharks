package net.mcreator.sharks.entity;

import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.Animation;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.tags.BlockTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.init.BenssharksModEntities;
import net.mcreator.sharks.procedures.SeaLionSprintProcedure;

import java.util.List;

public class SeaLionEntity extends Animal implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SeaLionEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SeaLionEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SeaLionEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> IDLE_VARIANT = SynchedEntityData.defineId(SeaLionEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;
    public String animationprocedure = "empty";
    
    private int idleSwitchTimer = 0;

    protected PathNavigation waterNavigation;
    protected PathNavigation groundNavigation;

    public SeaLionEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(BenssharksModEntities.SEA_LION.get(), world);
    }

    public SeaLionEntity(EntityType<SeaLionEntity> type, Level world) {
        super(type, world);
        xpReward = 1;
        setNoAi(false);
        setMaxUpStep(0.6f);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0);

        this.waterNavigation = this.navigation; 
        this.groundNavigation = new GroundPathNavigation(this, world);

        this.moveControl = new MoveControl(this) {
            @Override
            public void tick() {
                if (SeaLionEntity.this.isInWater()) {
                    SeaLionEntity.this.setDeltaMovement(SeaLionEntity.this.getDeltaMovement().add(0, 0.005, 0));
                    if (this.operation == MoveControl.Operation.MOVE_TO && !SeaLionEntity.this.getNavigation().isDone()) {
                        double dx = this.wantedX - SeaLionEntity.this.getX();
                        double dy = this.wantedY - SeaLionEntity.this.getY();
                        double dz = this.wantedZ - SeaLionEntity.this.getZ();
                        float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
                        float f1 = (float) (this.speedModifier * SeaLionEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
                        SeaLionEntity.this.setYRot(this.rotlerp(SeaLionEntity.this.getYRot(), f, 10));
                        SeaLionEntity.this.yBodyRot = SeaLionEntity.this.getYRot();
                        SeaLionEntity.this.yHeadRot = SeaLionEntity.this.getYRot();
                        
                        SeaLionEntity.this.setSpeed((float) SeaLionEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
                        float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
                        f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
                        SeaLionEntity.this.setXRot(this.rotlerp(SeaLionEntity.this.getXRot(), f2, 5));
                        float f3 = Mth.cos(SeaLionEntity.this.getXRot() * (float) (Math.PI / 180.0));
                        SeaLionEntity.this.setZza(f3 * f1);
                        SeaLionEntity.this.setYya((float) (f1 * dy));
                    } else {
                        SeaLionEntity.this.setSpeed(0.5f);
                        SeaLionEntity.this.setYya(0);
                        SeaLionEntity.this.setZza(0.5f);
                    }
                } else {
                    SeaLionEntity.this.setYya(0);
                    SeaLionEntity.this.setZza(0);
                    super.tick();

                    if (this.operation == MoveControl.Operation.MOVE_TO && !SeaLionEntity.this.getNavigation().isDone()) {
                        double d1 = this.wantedY - SeaLionEntity.this.getY();
                        if (d1 > SeaLionEntity.this.maxUpStep() && SeaLionEntity.this.horizontalCollision) {
                            SeaLionEntity.this.getJumpControl().jump();
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "sea_lion");
        this.entityData.define(IDLE_VARIANT, 0);
    }

    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    public int getIdleVariant() {
        return this.entityData.get(IDLE_VARIANT);
    }

    public void setIdleVariant(int variant) {
        this.entityData.set(IDLE_VARIANT, variant);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new WaterBoundPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, MakoSharkEntity.class, (float) 16, 1, 1));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, AxodileEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, BaskingSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, BlacktipReefSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, BlueSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(6, new AvoidEntityGoal<>(this, BonnetheadSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(7, new AvoidEntityGoal<>(this, BullSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(8, new AvoidEntityGoal<>(this, GreaterAxodileEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, GreenlandSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(10, new AvoidEntityGoal<>(this, LandSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(11, new AvoidEntityGoal<>(this, LemonSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(12, new AvoidEntityGoal<>(this, MegalodonEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(13, new AvoidEntityGoal<>(this, NurseSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(14, new AvoidEntityGoal<>(this, ShrakEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(15, new AvoidEntityGoal<>(this, TigerSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(16, new AvoidEntityGoal<>(this, WhaleSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(17, new AvoidEntityGoal<>(this, WhitetipSharkEntity.class, (float) 6, 1, 1.2));
        this.goalSelector.addGoal(18, new PanicGoal(this, 1.0));
        this.goalSelector.addGoal(19, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(20, new TemptGoal(this, 1.0, Ingredient.of(Items.COD), false));
        this.goalSelector.addGoal(21, new MeleeAttackGoal(this, 1.0, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }
        });
        this.goalSelector.addGoal(22, new RandomStrollGoal(this, 1));
        this.targetSelector.addGoal(23, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(24, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(25, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, true));
        this.targetSelector.addGoal(26, new NearestAttackableTargetGoal(this, SardineEntity.class, true, true));
        this.targetSelector.addGoal(27, new NearestAttackableTargetGoal(this, BarracudaEntity.class, true, true));
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wolf.step")), 0.15f, 1);
    }

    @Override
    public SoundEvent getAmbientSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("benssharks:sea_lion.ambient"));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("benssharks:sea_lion.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("benssharks:sea_lion.death"));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putInt("IdleVariant", this.getIdleVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture"))
            this.setTexture(compound.getString("Texture"));
        if (compound.contains("IdleVariant"))
            this.setIdleVariant(compound.getInt("IdleVariant"));
    }
    
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.hasCustomName();
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        SeaLionSprintProcedure.execute(this.level(), this);
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        SeaLionEntity retval = BenssharksModEntities.SEA_LION.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
        return retval;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return List.of(Items.COD).contains(stack.getItem());
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader world) {
        return world.isUnobstructed(this);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void aiStep() {
        if (this.isInWater()) {
            if (this.navigation != this.waterNavigation) {
                this.navigation = this.waterNavigation;
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(1.5);
            }
        } else {
            if (this.navigation != this.groundNavigation) {
                this.navigation = this.groundNavigation;
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2);
            }
        }

        if (!this.level().isClientSide) {
            if (this.idleSwitchTimer > 0) {
                this.idleSwitchTimer--;
            } else {
                int chance = this.random.nextInt(10);
                
                if (chance < 6) {
                    this.setIdleVariant(0);
                    this.idleSwitchTimer = 120 + this.random.nextInt(80); 
                } else if (chance < 8) {
                    this.setIdleVariant(1);
                    this.idleSwitchTimer = 60 + this.random.nextInt(40); 
                } else {
                    this.setIdleVariant(2);
                    this.idleSwitchTimer = 40 + this.random.nextInt(40);
                }
            }
        }

        super.aiStep();
        
        this.updateSwingTime();
        
        if (this.isInWater() && this.horizontalCollision && !this.getNavigation().isDone()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4, 0));
        }
    }

    public static void init() {
    }
    
    public static boolean checkSeaLionSpawnRules(EntityType<SeaLionEntity> type, LevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return (world.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) || 
                world.getBlockState(pos.below()).is(Blocks.SAND) || 
                world.getBlockState(pos.below()).is(Blocks.GRAVEL) || 
                world.getBlockState(pos.below()).is(Blocks.STONE) || 
                world.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)) &&
                world.getRawBrightness(pos, 0) > 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 1.5);
        builder = builder.add(Attributes.MAX_HEALTH, 10);
        builder = builder.add(Attributes.ARMOR, 2);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.5);
        return builder;
    }

    // --- MOVEMENT PREDICATE (Walk/Swim/Idle) ---
    private PlayState movementPredicate(AnimationState event) {
        if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && this.onGround()) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        if (this.isInWaterOrBubble()) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
        }
        if (!this.onGround()) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
        }
        
        int variant = this.getIdleVariant();
        if (variant == 1) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("idle2"));
        } else if (variant == 2) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("dance"));
        } else {
            return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
    }

    // --- ATTACKING PREDICATE (Your Custom Structure) ---
    private PlayState attackingPredicate(AnimationState event) {
        double d1 = this.getX() - this.xOld;
        double d0 = this.getZ() - this.zOld;
        float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
        
        // This detects the vanilla swing event (set by the procedure)
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().then("swing", Animation.LoopType.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
    }

    String prevAnim = "empty";

    private PlayState procedurePredicate(AnimationState event) {
        if (!animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
            if (!this.animationprocedure.equals(prevAnim))
                event.getController().forceAnimationReset();
            event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                this.animationprocedure = "empty";
                event.getController().forceAnimationReset();
            }
        } else if (animationprocedure.equals("empty")) {
            prevAnim = "empty";
            return PlayState.STOP;
        }
        prevAnim = this.animationprocedure;
        return PlayState.CONTINUE;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(SeaLionEntity.RemovalReason.KILLED);
            this.dropExperience();
        }
    }

    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "attack", 4, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}