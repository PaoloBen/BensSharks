
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;

import net.mcreator.sharks.procedures.IfTamedProcedure;
import net.mcreator.sharks.procedures.GoblinSharkPlayerCollidesWithThisEntityProcedure;
import net.mcreator.sharks.procedures.GoblinSharkOnInitialEntitySpawnProcedure;
import net.mcreator.sharks.procedures.GoblinSharkOnEntityTickUpdateProcedure;
import net.mcreator.sharks.procedures.GoblinSharkEntityIsHurtProcedure;
import net.mcreator.sharks.init.BenssharksModEntities;

import javax.annotation.Nullable;

public class GoblinSharkEntity extends PathfinderMob implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(GoblinSharkEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(GoblinSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(GoblinSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_Sprinting = SynchedEntityData.defineId(GoblinSharkEntity.class, EntityDataSerializers.BOOLEAN);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public GoblinSharkEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(BenssharksModEntities.GOBLIN_SHARK.get(), world);
	}

	public GoblinSharkEntity(EntityType<GoblinSharkEntity> type, Level world) {
		super(type, world);
		xpReward = 3;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (GoblinSharkEntity.this.isInWater())
					GoblinSharkEntity.this.setDeltaMovement(GoblinSharkEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !GoblinSharkEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - GoblinSharkEntity.this.getX();
					double dy = this.wantedY - GoblinSharkEntity.this.getY();
					double dz = this.wantedZ - GoblinSharkEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * GoblinSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					GoblinSharkEntity.this.setYRot(this.rotlerp(GoblinSharkEntity.this.getYRot(), f, 10));
					GoblinSharkEntity.this.yBodyRot = GoblinSharkEntity.this.getYRot();
					GoblinSharkEntity.this.yHeadRot = GoblinSharkEntity.this.getYRot();
					if (GoblinSharkEntity.this.isInWater()) {
						GoblinSharkEntity.this.setSpeed((float) GoblinSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						GoblinSharkEntity.this.setXRot(this.rotlerp(GoblinSharkEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(GoblinSharkEntity.this.getXRot() * (float) (Math.PI / 180.0));
						GoblinSharkEntity.this.setZza(f3 * f1);
						GoblinSharkEntity.this.setYya((float) (f1 * dy));
					} else {
						GoblinSharkEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					GoblinSharkEntity.this.setSpeed(0);
					GoblinSharkEntity.this.setYya(0);
					GoblinSharkEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "goblinshark");
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
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(3, new RandomSwimmingGoal(this, 1, 40));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, false) {
			@Override
			public boolean canUse() {
				double x = GoblinSharkEntity.this.getX();
				double y = GoblinSharkEntity.this.getY();
				double z = GoblinSharkEntity.this.getZ();
				Entity entity = GoblinSharkEntity.this;
				Level world = GoblinSharkEntity.this.level();
				return super.canUse() && IfTamedProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = GoblinSharkEntity.this.getX();
				double y = GoblinSharkEntity.this.getY();
				double z = GoblinSharkEntity.this.getZ();
				Entity entity = GoblinSharkEntity.this;
				Level world = GoblinSharkEntity.this.level();
				return super.canContinueToUse() && IfTamedProcedure.execute(entity);
			}
		});
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Drowned.class, true, false) {
			@Override
			public boolean canUse() {
				double x = GoblinSharkEntity.this.getX();
				double y = GoblinSharkEntity.this.getY();
				double z = GoblinSharkEntity.this.getZ();
				Entity entity = GoblinSharkEntity.this;
				Level world = GoblinSharkEntity.this.level();
				return super.canUse() && IfTamedProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = GoblinSharkEntity.this.getX();
				double y = GoblinSharkEntity.this.getY();
				double z = GoblinSharkEntity.this.getZ();
				Entity entity = GoblinSharkEntity.this;
				Level world = GoblinSharkEntity.this.level();
				return super.canContinueToUse() && IfTamedProcedure.execute(entity);
			}
		});
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, GlowSquid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = GoblinSharkEntity.this.getX();
				double y = GoblinSharkEntity.this.getY();
				double z = GoblinSharkEntity.this.getZ();
				Entity entity = GoblinSharkEntity.this;
				Level world = GoblinSharkEntity.this.level();
				return super.canUse() && IfTamedProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = GoblinSharkEntity.this.getX();
				double y = GoblinSharkEntity.this.getY();
				double z = GoblinSharkEntity.this.getZ();
				Entity entity = GoblinSharkEntity.this;
				Level world = GoblinSharkEntity.this.level();
				return super.canContinueToUse() && IfTamedProcedure.execute(entity);
			}
		});
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Squid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = GoblinSharkEntity.this.getX();
				double y = GoblinSharkEntity.this.getY();
				double z = GoblinSharkEntity.this.getZ();
				Entity entity = GoblinSharkEntity.this;
				Level world = GoblinSharkEntity.this.level();
				return super.canUse() && IfTamedProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = GoblinSharkEntity.this.getX();
				double y = GoblinSharkEntity.this.getY();
				double z = GoblinSharkEntity.this.getZ();
				Entity entity = GoblinSharkEntity.this;
				Level world = GoblinSharkEntity.this.level();
				return super.canContinueToUse() && IfTamedProcedure.execute(entity);
			}
		});
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Squid.class, true, false) {
			@Override
			public boolean canUse() {
				double x = GoblinSharkEntity.this.getX();
				double y = GoblinSharkEntity.this.getY();
				double z = GoblinSharkEntity.this.getZ();
				Entity entity = GoblinSharkEntity.this;
				Level world = GoblinSharkEntity.this.level();
				return super.canUse() && IfTamedProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = GoblinSharkEntity.this.getX();
				double y = GoblinSharkEntity.this.getY();
				double z = GoblinSharkEntity.this.getZ();
				Entity entity = GoblinSharkEntity.this;
				Level world = GoblinSharkEntity.this.level();
				return super.canContinueToUse() && IfTamedProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, WaterAnimal.class, (float) 128));
		this.goalSelector.addGoal(12, new AvoidEntityGoal<>(this, MegalodonEntity.class, (float) 32, 1, 1.2));
		this.goalSelector.addGoal(13, new AvoidEntityGoal<>(this, Dolphin.class, (float) 16, 1, 1.2));
		this.goalSelector.addGoal(14, new AvoidEntityGoal<>(this, ShrakEntity.class, (float) 16, 1, 1.2));
		this.goalSelector.addGoal(15, new AvoidEntityGoal<>(this, TigerSharkEntity.class, (float) 16, 1, 1.2));
		this.goalSelector.addGoal(16, new AvoidEntityGoal<>(this, MakoSharkEntity.class, (float) 16, 1, 1.2));
		this.goalSelector.addGoal(17, new AvoidEntityGoal<>(this, RemoraEntity.class, (float) 16, 1, 1.2));
		this.goalSelector.addGoal(18, new AvoidEntityGoal<>(this, Drowned.class, (float) 16, 1, 1.2));
		this.goalSelector.addGoal(19, new AvoidEntityGoal<>(this, AxodileEntity.class, (float) 16, 1, 1.2));
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
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.tropical_fish.ambient"));
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
	public boolean hurt(DamageSource source, float amount) {
		GoblinSharkEntityIsHurtProcedure.execute(this.level(), this);
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		GoblinSharkOnInitialEntitySpawnProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
		return retval;
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
	public void baseTick() {
		super.baseTick();
		GoblinSharkOnEntityTickUpdateProcedure.execute(this.level(), this);
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1.5);
	}

	@Override
	public void playerTouch(Player sourceentity) {
		super.playerTouch(sourceentity);
		GoblinSharkPlayerCollidesWithThisEntityProcedure.execute();
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
		builder = builder.add(Attributes.MAX_HEALTH, 30);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.25);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 0.9);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isInWaterOrBubble()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
			}
			if (this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("sprint"));
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
			this.remove(GoblinSharkEntity.RemovalReason.KILLED);
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
}
