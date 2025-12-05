
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
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

import net.mcreator.sharks.procedures.BlueSharkRightClickedOnEntityProcedure;
import net.mcreator.sharks.procedures.BlueSharkOnInitialEntitySpawnProcedure;
import net.mcreator.sharks.procedures.BlueSharkOnEntityTickUpdateProcedure;
import net.mcreator.sharks.procedures.BlueSharkEntityIsHurtProcedure;
import net.mcreator.sharks.procedures.AggressiveSharksProcedureProcedure;
import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.init.BenssharksModEntities;

import javax.annotation.Nullable;

public class BlueSharkEntity extends PathfinderMob implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(BlueSharkEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(BlueSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BlueSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_Sprinting = SynchedEntityData.defineId(BlueSharkEntity.class, EntityDataSerializers.BOOLEAN);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public BlueSharkEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(BenssharksModEntities.BLUE_SHARK.get(), world);
	}

	public BlueSharkEntity(EntityType<BlueSharkEntity> type, Level world) {
		super(type, world);
		xpReward = 3;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (BlueSharkEntity.this.isInWater())
					BlueSharkEntity.this.setDeltaMovement(BlueSharkEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !BlueSharkEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - BlueSharkEntity.this.getX();
					double dy = this.wantedY - BlueSharkEntity.this.getY();
					double dz = this.wantedZ - BlueSharkEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * BlueSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					BlueSharkEntity.this.setYRot(this.rotlerp(BlueSharkEntity.this.getYRot(), f, 10));
					BlueSharkEntity.this.yBodyRot = BlueSharkEntity.this.getYRot();
					BlueSharkEntity.this.yHeadRot = BlueSharkEntity.this.getYRot();
					if (BlueSharkEntity.this.isInWater()) {
						BlueSharkEntity.this.setSpeed((float) BlueSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						BlueSharkEntity.this.setXRot(this.rotlerp(BlueSharkEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(BlueSharkEntity.this.getXRot() * (float) (Math.PI / 180.0));
						BlueSharkEntity.this.setZza(f3 * f1);
						BlueSharkEntity.this.setYya((float) (f1 * dy));
					} else {
						BlueSharkEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					BlueSharkEntity.this.setSpeed(0);
					BlueSharkEntity.this.setYya(0);
					BlueSharkEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "blueshark");
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
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 15, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.goalSelector.addGoal(2, new PanicGoal(this, 1.2));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, true));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Guardian.class, true, true));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, BarracudaEntity.class, true, true));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, GlowSquid.class, true, true));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Squid.class, true, true));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, LivingEntity.class, true, true) {
			@Override
			public boolean canUse() {
				double x = BlueSharkEntity.this.getX();
				double y = BlueSharkEntity.this.getY();
				double z = BlueSharkEntity.this.getZ();
				Entity entity = BlueSharkEntity.this;
				Level world = BlueSharkEntity.this.level();
				return super.canUse() && AggressiveSharksProcedureProcedure.execute(world);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BlueSharkEntity.this.getX();
				double y = BlueSharkEntity.this.getY();
				double z = BlueSharkEntity.this.getZ();
				Entity entity = BlueSharkEntity.this;
				Level world = BlueSharkEntity.this.level();
				return super.canContinueToUse() && AggressiveSharksProcedureProcedure.execute(world);
			}
		});
		this.goalSelector.addGoal(10, new TemptGoal(this, 1, Ingredient.of(BenssharksModItems.FISH_BUCKET.get()), false));
		this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, WaterAnimal.class, (float) 128));
		this.goalSelector.addGoal(13, new AvoidEntityGoal<>(this, MegalodonEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(14, new AvoidEntityGoal<>(this, ElderGuardian.class, (float) 64, 16, 16));
		this.goalSelector.addGoal(15, new AvoidEntityGoal<>(this, Guardian.class, (float) 64, 16, 16));
		this.goalSelector.addGoal(16, new AvoidEntityGoal<>(this, ShrakEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(17, new AvoidEntityGoal<>(this, TigerSharkEntity.class, (float) 8, 16, 16));
		this.goalSelector.addGoal(18, new AvoidEntityGoal<>(this, MakoSharkEntity.class, (float) 8, 16, 16));
		this.goalSelector.addGoal(19, new AvoidEntityGoal<>(this, AxodileEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(20, new AvoidEntityGoal<>(this, Dolphin.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(21, new AvoidEntityGoal<>(this, RemoraEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(22, new RandomSwimmingGoal(this, 1, 40));
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
		BlueSharkEntityIsHurtProcedure.execute(this.level(), this);
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		BlueSharkOnInitialEntitySpawnProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
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
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
		super.mobInteract(sourceentity, hand);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity entity = this;
		Level world = this.level();
		return BlueSharkRightClickedOnEntityProcedure.execute(world, x, y, z, entity, sourceentity);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		BlueSharkOnEntityTickUpdateProcedure.execute(this.level(), this);
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1.25);
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
		builder = builder.add(Attributes.MAX_HEALTH, 20);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 0.9);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isInWaterOrBubble()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
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
			this.remove(BlueSharkEntity.RemovalReason.KILLED);
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
