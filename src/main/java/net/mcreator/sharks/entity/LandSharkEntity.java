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

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.Mth;
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

import net.mcreator.sharks.procedures.LandSharkRightClickedOnEntityProcedure;
import net.mcreator.sharks.procedures.LandSharkOnInitialEntitySpawnProcedure;
import net.mcreator.sharks.procedures.LandSharkOnEntityTickUpdateProcedure;
import net.mcreator.sharks.procedures.LandSharkEntityIsHurtProcedure;
import net.mcreator.sharks.procedures.IfTamedProcedure;
import net.mcreator.sharks.procedures.IfSittingProcedure;
import net.mcreator.sharks.procedures.IFNOTSITTINGProcedure;
import net.mcreator.sharks.init.BenssharksModEntities;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

public class LandSharkEntity extends TamableAnimal implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(LandSharkEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(LandSharkEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(LandSharkEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> DATA_Sitting = SynchedEntityData.defineId(LandSharkEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_Tamed = SynchedEntityData.defineId(LandSharkEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_InWater = SynchedEntityData.defineId(LandSharkEntity.class, EntityDataSerializers.BOOLEAN);
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    // Dual Navigation System
    protected PathNavigation waterNavigation;
    protected PathNavigation groundNavigation;

    public LandSharkEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(BenssharksModEntities.LAND_SHARK.get(), world);
    }

    public LandSharkEntity(EntityType<LandSharkEntity> type, Level world) {
        super(type, world);
        xpReward = 5;
        setNoAi(false);
        setMaxUpStep(0.6f);
        setPersistenceRequired();
        
        // Initialize Dual Navigation
        this.setPathfindingMalus(BlockPathTypes.WATER, 0);
        this.groundNavigation = this.navigation; 
        this.waterNavigation = new WaterBoundPathNavigation(this, world);

        // Custom Move Control for Swapping & Speed
        this.moveControl = new MoveControl(this) {
            @Override
            public void tick() {
                if (LandSharkEntity.this.isInWater()) {
                    LandSharkEntity.this.setDeltaMovement(LandSharkEntity.this.getDeltaMovement().add(0, 0.005, 0));
                    
                    if (this.operation == MoveControl.Operation.MOVE_TO && !LandSharkEntity.this.getNavigation().isDone()) {
                        double dx = this.wantedX - LandSharkEntity.this.getX();
                        double dy = this.wantedY - LandSharkEntity.this.getY();
                        double dz = this.wantedZ - LandSharkEntity.this.getZ();
                        
                        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        
                        float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
                        LandSharkEntity.this.setYRot(this.rotlerp(LandSharkEntity.this.getYRot(), f, 10));
                        LandSharkEntity.this.yBodyRot = LandSharkEntity.this.getYRot();
                        LandSharkEntity.this.yHeadRot = LandSharkEntity.this.getYRot();
                        
                        float speed = (float) (this.speedModifier * LandSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
                        LandSharkEntity.this.setSpeed(speed);
                        
                        float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
                        f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
                        LandSharkEntity.this.setXRot(this.rotlerp(LandSharkEntity.this.getXRot(), f2, 5));
                        float f3 = Mth.cos(LandSharkEntity.this.getXRot() * (float) (Math.PI / 180.0));
                        
                        LandSharkEntity.this.setZza(f3 * speed);
                        LandSharkEntity.this.setYya((float) (speed * dy / distance));
                    } else {
                        LandSharkEntity.this.setSpeed(0.0f);
                        LandSharkEntity.this.setYya(0);
                        LandSharkEntity.this.setZza(0);
                    }
                } else {
                    LandSharkEntity.this.setYya(0);
                    LandSharkEntity.this.setZza(0);
                    super.tick();

                    if (this.operation == MoveControl.Operation.MOVE_TO && !LandSharkEntity.this.getNavigation().isDone()) {
                        double d1 = this.wantedY - LandSharkEntity.this.getY();
                        if (d1 > LandSharkEntity.this.maxUpStep() && LandSharkEntity.this.horizontalCollision) {
                            LandSharkEntity.this.getJumpControl().jump();
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
        this.entityData.define(TEXTURE, "land_shark");
        this.entityData.define(DATA_Sitting, false);
        this.entityData.define(DATA_Tamed, false);
        this.entityData.define(DATA_InWater, false);
    }

    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new GroundPathNavigation(this, pLevel);
    }
    
    // --- FRIENDLY FIRE PROTECTION ---
    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof TamableAnimal tamedAnimal && tamedAnimal.isTame()) {
            if (tamedAnimal.getOwner() == owner) {
                return false; 
            }
        }
        if (target instanceof Player player && owner instanceof Player ownerPlayer && !ownerPlayer.canHarmPlayer(player)) {
            return false; 
        }
        return super.wantsToAttack(target, owner);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 1. Owner Protection
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && IfTamedProcedure.execute(LandSharkEntity.this);
            }
        });
        this.goalSelector.addGoal(2, new OwnerHurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && IfTamedProcedure.execute(LandSharkEntity.this);
            }
        });

        // 2. Melee Attack
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }
            @Override
            public boolean canUse() {
                return super.canUse() && IFNOTSITTINGProcedure.execute(LandSharkEntity.this);
            }
        });

        this.targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        
        // 3. Breeding & Parenting
        this.goalSelector.addGoal(5, new BreedGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && IfSittingProcedure.execute(LandSharkEntity.this);
            }
        });
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 0.8) {
            @Override
            public boolean canUse() {
                return super.canUse() && IfSittingProcedure.execute(LandSharkEntity.this);
            }
        });

        // 4. Combat Targets
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && IFNOTSITTINGProcedure.execute(LandSharkEntity.this);
            }
        });
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, ThalassogerEntity.class, true, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && IFNOTSITTINGProcedure.execute(LandSharkEntity.this);
            }
        });

        // 5. Custom Amphibious Follow Owner Goal
        this.goalSelector.addGoal(10, new LandSharkFollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));

        // 6. Idle Movement (Land Only)
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && IFNOTSITTINGProcedure.execute(LandSharkEntity.this);
            }
        });
    }

    // --- Custom Goal Class ---
    static class LandSharkFollowOwnerGoal extends Goal {
        private final TamableAnimal tamable;
        private LivingEntity owner;
        private final Level level;
        private final double speedModifier;
        private int timeToRecalcPath;
        private float minDist;
        private float maxDist;
        private boolean canFly;

        public LandSharkFollowOwnerGoal(TamableAnimal pTamable, double pSpeedModifier, float pMinDist, float pMaxDist, boolean pCanFly) {
            this.tamable = pTamable;
            this.level = pTamable.level();
            this.speedModifier = pSpeedModifier;
            this.minDist = pMinDist;
            this.maxDist = pMaxDist;
            this.canFly = pCanFly;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.tamable.getOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } 
            // FIX: Check BOTH vanilla sitting state AND custom Data Sitting state
            else if (this.tamable.isOrderedToSit() || (this.tamable instanceof LandSharkEntity && ((LandSharkEntity)this.tamable).getEntityData().get(DATA_Sitting))) {
                return false;
            } else if (this.tamable.distanceToSqr(livingentity) < (double)(this.minDist * this.minDist)) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (this.tamable.getNavigation().isDone()) {
                return false;
            } 
            // FIX: Check BOTH vanilla sitting state AND custom Data Sitting state
            else if (this.tamable.isOrderedToSit() || (this.tamable instanceof LandSharkEntity && ((LandSharkEntity)this.tamable).getEntityData().get(DATA_Sitting))) {
                return false;
            } else {
                return !(this.tamable.distanceToSqr(this.owner) <= (double)(this.maxDist * this.maxDist));
            }
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            this.owner = null;
            this.tamable.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                    if (this.tamable.distanceToSqr(this.owner) >= 144.0D) {
                        this.teleportToOwner();
                    } else {
                        this.tamable.getNavigation().moveTo(this.owner, this.speedModifier);
                    }
                }
            }
        }

        private void teleportToOwner() {
            BlockPos blockpos = this.owner.blockPosition();
            for(int i = 0; i < 10; ++i) {
                int j = this.randomIntInclusive(-3, 3);
                int k = this.randomIntInclusive(-1, 1);
                int l = this.randomIntInclusive(-3, 3);
                boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }
        }

        private boolean maybeTeleportTo(int pX, int pY, int pZ) {
            if (Math.abs((double)pX - this.owner.getX()) < 2.0D && Math.abs((double)pZ - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
                return false;
            } else {
                this.tamable.moveTo((double)pX + 0.5D, (double)pY, (double)pZ + 0.5D, this.tamable.getYRot(), this.tamable.getXRot());
                this.tamable.getNavigation().stop();
                return true;
            }
        }

        private boolean canTeleportTo(BlockPos pPos) {
            BlockPathTypes blockpathtypes = net.minecraft.world.level.pathfinder.WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pPos.mutable());
            if (blockpathtypes != BlockPathTypes.WALKABLE && blockpathtypes != BlockPathTypes.WATER) { 
                return false;
            } else {
                BlockState blockstate = this.level.getBlockState(pPos.below());
                if (!this.canFly && blockstate.getBlock() instanceof net.minecraft.world.level.block.LeavesBlock) {
                    return false;
                } else {
                    return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move((double)pPos.getX() + 0.5D - this.tamable.getX(), (double)pPos.getY() - this.tamable.getY(), (double)pPos.getZ() + 0.5D - this.tamable.getZ()));
                }
            }
        }
        
        private int randomIntInclusive(int pMin, int pMax) {
            return this.tamable.getRandom().nextInt(pMax - pMin + 1) + pMin;
        }
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean canBreatheInWater() {
        return true;
    }

    public boolean isPaddlingAndSteering() {
        return !this.isInWater();
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wolf.step")), 0.15f, 1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.guardian.hurt_land"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.tropical_fish.death"));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        LandSharkEntityIsHurtProcedure.execute(this);
        if (source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        LandSharkOnInitialEntitySpawnProcedure.execute();
        return retval;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putBoolean("DataSitting", this.entityData.get(DATA_Sitting));
        compound.putBoolean("DataTamed", this.entityData.get(DATA_Tamed));
        compound.putBoolean("DataInWater", this.entityData.get(DATA_InWater));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture"))
            this.setTexture(compound.getString("Texture"));
        if (compound.contains("DataSitting"))
            this.entityData.set(DATA_Sitting, compound.getBoolean("DataSitting"));
        if (compound.contains("DataTamed"))
            this.entityData.set(DATA_Tamed, compound.getBoolean("DataTamed"));
        if (compound.contains("DataInWater"))
            this.entityData.set(DATA_InWater, compound.getBoolean("DataInWater"));
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        ItemStack itemstack = sourceentity.getItemInHand(hand);
        InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
        Item item = itemstack.getItem();
        if (itemstack.getItem() instanceof SpawnEggItem) {
            retval = super.mobInteract(sourceentity, hand);
        } else if (this.level().isClientSide()) {
            retval = (this.isTame() && this.isOwnedBy(sourceentity) || this.isFood(itemstack)) ? InteractionResult.sidedSuccess(this.level().isClientSide()) : InteractionResult.PASS;
        } else {
            if (this.isTame()) {
                if (this.isOwnedBy(sourceentity)) {
                    if (item.isEdible() && this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                        this.usePlayerItem(sourceentity, hand, itemstack);
                        this.heal((float) item.getFoodProperties().getNutrition());
                        retval = InteractionResult.sidedSuccess(this.level().isClientSide());
                    } else if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                        this.usePlayerItem(sourceentity, hand, itemstack);
                        this.heal(4);
                        retval = InteractionResult.sidedSuccess(this.level().isClientSide());
                    } else {
                        retval = super.mobInteract(sourceentity, hand);
                    }
                }
            } else if (this.isFood(itemstack)) {
                this.usePlayerItem(sourceentity, hand, itemstack);
                if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, sourceentity)) {
                    this.tame(sourceentity);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
                this.setPersistenceRequired();
                retval = InteractionResult.sidedSuccess(this.level().isClientSide());
            } else {
                retval = super.mobInteract(sourceentity, hand);
                if (retval == InteractionResult.SUCCESS || retval == InteractionResult.CONSUME)
                    this.setPersistenceRequired();
            }
        }
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = this;
        Level world = this.level();
        return LandSharkRightClickedOnEntityProcedure.execute(entity, sourceentity);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.entityData.get(DATA_Sitting) && this.isInWater()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.1D, 0.0D)); 
        }
        LandSharkOnEntityTickUpdateProcedure.execute(this);
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        LandSharkEntity retval = BenssharksModEntities.LAND_SHARK.get().create(serverWorld);
        retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
        return retval;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return List.of(Items.COD).contains(stack.getItem());
    }

    @Override
    public void aiStep() {
        // --- Navigation Swapping & Speed Control ---
        if (this.isInWater()) {
            if (this.navigation != this.waterNavigation) {
                this.navigation = this.waterNavigation;
            }
            // Set speed to 1.5 when in water (Fast Swim)
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(1.5); 
        } else {
            if (this.navigation != this.groundNavigation) {
                this.navigation = this.groundNavigation;
            }
            // Set speed to 0.3 when on land (Walk)
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);
        }

        super.aiStep();
        this.updateSwingTime();
        
        // Leaping logic (From Sea Lion)
        if (this.isInWater() && this.horizontalCollision && !this.getNavigation().isDone()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4, 0));
        }
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 40);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 10);
        builder = builder.add(Attributes.FOLLOW_RANGE, 64);
        builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.5);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && this.onGround()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            if (this.isInWaterOrBubble()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
            }
            if (this.isShiftKeyDown()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("sit"));
            }
            if (!this.onGround()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
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
            this.remove(LandSharkEntity.RemovalReason.KILLED);
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
        data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}