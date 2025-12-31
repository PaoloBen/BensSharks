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

import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
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
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.AgeableMob;
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
import net.minecraft.world.level.block.state.BlockState;

import net.mcreator.sharks.procedures.NurseSharkPlayerCollidesWithThisEntityProcedure;
import net.mcreator.sharks.procedures.LeopardSharkRightClickedOnEntityProcedure;
import net.mcreator.sharks.procedures.LeopardSharkOnEntityTickUpdateProcedure;
import net.mcreator.sharks.procedures.IfTamedProcedure;
import net.mcreator.sharks.procedures.AggressiveSharksProcedureProcedure;
import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.init.BenssharksModEntities;

import javax.annotation.Nullable;
import java.util.List;
import java.util.EnumSet;

public class LeopardSharkEntity extends TamableAnimal implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(LeopardSharkEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(LeopardSharkEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(LeopardSharkEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> DATA_Sprinting = SynchedEntityData.defineId(LeopardSharkEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;
    public String animationprocedure = "empty";

    public LeopardSharkEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(BenssharksModEntities.LEOPARD_SHARK.get(), world);
    }

    public LeopardSharkEntity(EntityType<LeopardSharkEntity> type, Level world) {
        super(type, world);
        xpReward = 1;
        setNoAi(false);
        setMaxUpStep(0.6f);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0);
        
        this.moveControl = new MoveControl(this) {
            @Override
            public void tick() {
                if (LeopardSharkEntity.this.isOrderedToSit()) {
                    LeopardSharkEntity.this.setSpeed(0.0F);
                    LeopardSharkEntity.this.setDeltaMovement(0, 0, 0);
                    return;
                }

                if (LeopardSharkEntity.this.isInWater())
                    LeopardSharkEntity.this.setDeltaMovement(LeopardSharkEntity.this.getDeltaMovement().add(0, 0.005, 0));
                
                if (this.operation == MoveControl.Operation.MOVE_TO && !LeopardSharkEntity.this.getNavigation().isDone()) {
                    double dx = this.wantedX - LeopardSharkEntity.this.getX();
                    double dy = this.wantedY - LeopardSharkEntity.this.getY();
                    double dz = this.wantedZ - LeopardSharkEntity.this.getZ();
                    float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
                    float f1 = (float) (this.speedModifier * LeopardSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
                    LeopardSharkEntity.this.setYRot(this.rotlerp(LeopardSharkEntity.this.getYRot(), f, 10));
                    LeopardSharkEntity.this.yBodyRot = LeopardSharkEntity.this.getYRot();
                    LeopardSharkEntity.this.yHeadRot = LeopardSharkEntity.this.getYRot();
                    if (LeopardSharkEntity.this.isInWater()) {
                        LeopardSharkEntity.this.setSpeed((float) LeopardSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
                        float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
                        f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
                        LeopardSharkEntity.this.setXRot(this.rotlerp(LeopardSharkEntity.this.getXRot(), f2, 5));
                        float f3 = Mth.cos(LeopardSharkEntity.this.getXRot() * (float) (Math.PI / 180.0));
                        LeopardSharkEntity.this.setZza(f3 * f1);
                        LeopardSharkEntity.this.setYya((float) (f1 * dy));
                    } else {
                        LeopardSharkEntity.this.setSpeed(f1 * 0.05F);
                    }
                } else {
                    LeopardSharkEntity.this.setSpeed(0);
                    LeopardSharkEntity.this.setYya(0);
                    LeopardSharkEntity.this.setZza(0);
                }
            }
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "leopardshark");
        this.entityData.define(DATA_Sprinting, false);
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
    protected PathNavigation createNavigation(Level world) {
        return new WaterBoundPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }
        });
        this.goalSelector.addGoal(4, new SharkFollowOwnerGoal(this, 1.0, 10.0f, 64.0f));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 0.8));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1, Ingredient.of(BenssharksModItems.FISH_BUCKET.get()), false));
        
        this.targetSelector.addGoal(7, new OwnerHurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                if (!super.canUse()) return false;
                LivingEntity owner = LeopardSharkEntity.this.getOwner();
                if (owner == null) return false;
                LivingEntity attacker = owner.getLastHurtByMob();
                if (attacker == null) return false;
                if (attacker instanceof TamableAnimal pet && pet.isOwnedBy(owner)) return false; 
                if (attacker.isAlliedTo(owner)) return false; 
                return true;
            }
        });

        this.targetSelector.addGoal(8, new OwnerHurtTargetGoal(this) {
            @Override
            public boolean canUse() {
                if (!super.canUse()) return false;
                LivingEntity owner = LeopardSharkEntity.this.getOwner();
                if (owner == null) return false;
                LivingEntity victim = owner.getLastHurtMob();
                if (victim == null) return false;
                if (victim instanceof TamableAnimal pet && pet.isOwnedBy(owner)) return false; 
                if (victim.isAlliedTo(owner)) return false; 
                return true;
            }
        });

        this.targetSelector.addGoal(9, new HurtByTargetGoal(this));
        
        this.goalSelector.addGoal(10, new RandomSwimmingGoal(this, 1, 40));
        
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, false) {
            @Override
            public boolean canUse() {
                double x = LeopardSharkEntity.this.getX();
                double y = LeopardSharkEntity.this.getY();
                double z = LeopardSharkEntity.this.getZ();
                Entity entity = LeopardSharkEntity.this;
                Level world = LeopardSharkEntity.this.level();
                return super.canUse() && IfTamedProcedure.execute(entity);
            }
        });
        
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, Squid.class, true, false));
        this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, GlowSquid.class, true, false));
        
        this.targetSelector.addGoal(14, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 10, true, false, (entity) -> {
            if (!AggressiveSharksProcedureProcedure.execute(this.level())) return false;
            if (this.isTame() && this.getOwner() != null) {
                if (entity == this.getOwner()) return false;
                if (entity instanceof TamableAnimal pet && pet.isOwnedBy(this.getOwner())) return false;
                if (entity.isAlliedTo(this.getOwner())) return false;
            }
            return true;
        }) {
            @Override
            public boolean canUse() {
                return super.canUse();
            }
        });
        
        this.goalSelector.addGoal(15, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(17, new LookAtPlayerGoal(this, WaterAnimal.class, (float) 128));
        this.goalSelector.addGoal(18, new AvoidEntityGoal<>(this, MegalodonEntity.class, (float) 32, 1, 1.2));
        this.goalSelector.addGoal(19, new AvoidEntityGoal<>(this, Dolphin.class, (float) 16, 1, 1.2));
        this.goalSelector.addGoal(20, new AvoidEntityGoal<>(this, ShrakEntity.class, (float) 16, 1, 1.2));
        this.goalSelector.addGoal(21, new AvoidEntityGoal<>(this, TigerSharkEntity.class, (float) 16, 1, 1.2));
        this.goalSelector.addGoal(22, new AvoidEntityGoal<>(this, MakoSharkEntity.class, (float) 16, 1, 1.2));
        this.goalSelector.addGoal(23, new AvoidEntityGoal<>(this, RemoraEntity.class, (float) 16, 1, 1.2));
        this.goalSelector.addGoal(24, new AvoidEntityGoal<>(this, Drowned.class, (float) 16, 1, 1.2));
        this.goalSelector.addGoal(25, new AvoidEntityGoal<>(this, AxodileEntity.class, (float) 16, 1, 1.2));
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + 0.5;
    }

	@Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("intentionally_empty")), 0.15f, 1);
    }
	
    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.tropical_fish.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.tropical_fish.death"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putBoolean("DataSprinting", this.entityData.get(DATA_Sprinting));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture"))
            this.setTexture(compound.getString("Texture"));
        if (compound.contains("DataSprinting"))
            this.entityData.set(DATA_Sprinting, compound.getBoolean("DataSprinting"));
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        ItemStack itemstack = sourceentity.getItemInHand(hand);
        
        // --- 1. PRIORITY CHECK: BUCKET PROCEDURE ---
        // We run the procedure FIRST. If the player has a water bucket, we catch the fish 
        // and return immediately. This stops the "Sit" logic from triggering.
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = this;
        Level world = this.level();
        
        InteractionResult procedureResult = LeopardSharkRightClickedOnEntityProcedure.execute(world, x, y, z, entity, sourceentity);
        if (procedureResult == InteractionResult.SUCCESS || procedureResult == InteractionResult.CONSUME || procedureResult == InteractionResult.sidedSuccess(world.isClientSide())) {
            return procedureResult;
        }

        // --- 2. STANDARD VANILLA INTERACTION ---
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
                    } else if (!this.isFood(itemstack)) {
                        // SIT TOGGLE (This only runs if no bucket was found above)
                        this.setOrderedToSit(!this.isOrderedToSit());
                        this.jumping = false;
                        this.navigation.stop();
                        this.setDeltaMovement(0,0,0);
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
        return retval;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        // Fixed compilation error by passing all required arguments (world, x, y, z, entity)
        LeopardSharkOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 0.85);
    }

    @Override
    public void playerTouch(Player sourceentity) {
        super.playerTouch(sourceentity);
        NurseSharkPlayerCollidesWithThisEntityProcedure.execute(this, sourceentity);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        LeopardSharkEntity retval = BenssharksModEntities.LEOPARD_SHARK.get().create(serverWorld);
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
        super.aiStep();
        this.updateSwingTime();
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.9);
        builder = builder.add(Attributes.MAX_HEALTH, 15);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(ForgeMod.SWIM_SPEED.get(), 0.9);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.isOrderedToSit()) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && !this.entityData.get(DATA_Sprinting)) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            if (this.isInWaterOrBubble()) {
                if (this.entityData.get(DATA_Sprinting)) {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("sprint"));
                }
                return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("land"));
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
			this.remove(LeopardSharkEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "movement", 2, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 2, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 2, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
	
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		return super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
	}

	// --- CUSTOM FOLLOW OWNER GOAL ---
	static class SharkFollowOwnerGoal extends Goal {
		private final LeopardSharkEntity shark;
		private LivingEntity owner;
		private final double speedModifier;
		private final float startDistance;
		private final float stopDistance;
		private final PathNavigation navigation;
		private int timeToRecalcPath;

		public SharkFollowOwnerGoal(LeopardSharkEntity shark, double speed, float minDir, float maxDist) {
			this.shark = shark;
			this.speedModifier = speed;
			this.startDistance = minDir;
			this.stopDistance = 2.0f;
			this.navigation = shark.getNavigation();
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			LivingEntity owner = this.shark.getOwner();
			if (owner == null) return false;
			if (this.shark.isOrderedToSit()) return false;
			if (this.shark.distanceToSqr(owner) < (double) (this.startDistance * this.startDistance)) return false;
			this.owner = owner;
			return true;
		}

		@Override
		public boolean canContinueToUse() {
			if (this.navigation.isDone()) return false;
			if (this.shark.isOrderedToSit()) return false;
			return this.shark.distanceToSqr(this.owner) > (double) (this.stopDistance * this.stopDistance);
		}

		@Override
		public void start() {
			this.timeToRecalcPath = 0;
		}

		@Override
		public void tick() {
			if (this.shark.isOrderedToSit()) {
				this.navigation.stop();
				return;
			}
			this.shark.getLookControl().setLookAt(this.owner, 10.0F, (float) this.shark.getMaxHeadXRot());
			if (--this.timeToRecalcPath <= 0) {
				this.timeToRecalcPath = 10;
				if (!this.shark.isLeashed() && !this.shark.isPassenger()) {
					this.navigation.moveTo(this.owner, this.speedModifier);
				}
			}
		}
	}
}