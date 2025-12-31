
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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

import net.mcreator.sharks.procedures.BlacktipReefSharkRightClickedOnEntityProcedure;
import net.mcreator.sharks.procedures.AggressiveSharksProcedureProcedure;
import net.mcreator.sharks.init.BenssharksModEntities;

import java.util.List;

public class BlacktipReefSharkEntity extends Animal implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(BlacktipReefSharkEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(BlacktipReefSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BlacktipReefSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_health = SynchedEntityData.defineId(BlacktipReefSharkEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<String> DATA_nametag = SynchedEntityData.defineId(BlacktipReefSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_oxygen = SynchedEntityData.defineId(BlacktipReefSharkEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<String> DATA_uuid = SynchedEntityData.defineId(BlacktipReefSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_Sprinting = SynchedEntityData.defineId(BlacktipReefSharkEntity.class, EntityDataSerializers.BOOLEAN);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public BlacktipReefSharkEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(BenssharksModEntities.BLACKTIP_REEF_SHARK.get(), world);
	}

	public BlacktipReefSharkEntity(EntityType<BlacktipReefSharkEntity> type, Level world) {
		super(type, world);
		xpReward = 3;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (BlacktipReefSharkEntity.this.isInWater())
					BlacktipReefSharkEntity.this.setDeltaMovement(BlacktipReefSharkEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !BlacktipReefSharkEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - BlacktipReefSharkEntity.this.getX();
					double dy = this.wantedY - BlacktipReefSharkEntity.this.getY();
					double dz = this.wantedZ - BlacktipReefSharkEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * BlacktipReefSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					BlacktipReefSharkEntity.this.setYRot(this.rotlerp(BlacktipReefSharkEntity.this.getYRot(), f, 10));
					BlacktipReefSharkEntity.this.yBodyRot = BlacktipReefSharkEntity.this.getYRot();
					BlacktipReefSharkEntity.this.yHeadRot = BlacktipReefSharkEntity.this.getYRot();
					if (BlacktipReefSharkEntity.this.isInWater()) {
						BlacktipReefSharkEntity.this.setSpeed((float) BlacktipReefSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						BlacktipReefSharkEntity.this.setXRot(this.rotlerp(BlacktipReefSharkEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(BlacktipReefSharkEntity.this.getXRot() * (float) (Math.PI / 180.0));
						BlacktipReefSharkEntity.this.setZza(f3 * f1);
						BlacktipReefSharkEntity.this.setYya((float) (f1 * dy));
					} else {
						BlacktipReefSharkEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					BlacktipReefSharkEntity.this.setSpeed(0);
					BlacktipReefSharkEntity.this.setYya(0);
					BlacktipReefSharkEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "blacktip");
		this.entityData.define(DATA_health, 8);
		this.entityData.define(DATA_nametag, "");
		this.entityData.define(DATA_oxygen, 0);
		this.entityData.define(DATA_uuid, "");
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
		this.goalSelector.addGoal(1, new BreedGoal(this, 1));
		this.goalSelector.addGoal(2, new FollowParentGoal(this, 1));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 15, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.goalSelector.addGoal(4, new PanicGoal(this, 1));
		this.targetSelector.addGoal(7, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, true));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, SardineEntity.class, true, true));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, Rabbit.class, true, true));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, Chicken.class, true, true));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, Cod.class, true, true));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, Salmon.class, true, true));
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, GlowSquid.class, true, true));
		this.targetSelector.addGoal(15, new NearestAttackableTargetGoal(this, Squid.class, true, true));
		this.targetSelector.addGoal(16, new NearestAttackableTargetGoal(this, Frog.class, true, true));
		this.targetSelector.addGoal(17, new NearestAttackableTargetGoal(this, Tadpole.class, true, true));
		this.targetSelector.addGoal(18, new NearestAttackableTargetGoal(this, Chicken.class, true, true));
		this.targetSelector.addGoal(19, new NearestAttackableTargetGoal(this, LivingEntity.class, true, true) {
			@Override
			public boolean canUse() {
				double x = BlacktipReefSharkEntity.this.getX();
				double y = BlacktipReefSharkEntity.this.getY();
				double z = BlacktipReefSharkEntity.this.getZ();
				Entity entity = BlacktipReefSharkEntity.this;
				Level world = BlacktipReefSharkEntity.this.level();
				return super.canUse() && AggressiveSharksProcedureProcedure.execute(world);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BlacktipReefSharkEntity.this.getX();
				double y = BlacktipReefSharkEntity.this.getY();
				double z = BlacktipReefSharkEntity.this.getZ();
				Entity entity = BlacktipReefSharkEntity.this;
				Level world = BlacktipReefSharkEntity.this.level();
				return super.canContinueToUse() && AggressiveSharksProcedureProcedure.execute(world);
			}
		});
		this.goalSelector.addGoal(20, new LeapAtTargetGoal(this, (float) 0.5));
		this.goalSelector.addGoal(21, new TemptGoal(this, 1, Ingredient.of(Items.COD), false));
		this.goalSelector.addGoal(23, new LookAtPlayerGoal(this, BonnetheadSharkEntity.class, (float) 16));
		this.goalSelector.addGoal(24, new LookAtPlayerGoal(this, WaterAnimal.class, (float) 16));
		this.goalSelector.addGoal(25, new AvoidEntityGoal<>(this, ElderGuardian.class, (float) 64, 16, 16));
		this.goalSelector.addGoal(26, new AvoidEntityGoal<>(this, Guardian.class, (float) 64, 16, 16));
		this.goalSelector.addGoal(27, new AvoidEntityGoal<>(this, ShrakEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(28, new AvoidEntityGoal<>(this, TigerSharkEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(29, new AvoidEntityGoal<>(this, MakoSharkEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(30, new AvoidEntityGoal<>(this, BullSharkEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(31, new AvoidEntityGoal<>(this, BlueSharkEntity.class, (float) 8, 16, 16));
		this.goalSelector.addGoal(32, new AvoidEntityGoal<>(this, AxodileEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(33, new AvoidEntityGoal<>(this, Dolphin.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(34, new AvoidEntityGoal<>(this, RemoraEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(35, new RandomSwimmingGoal(this, 1, 40));
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
	public void playStepSound(BlockPos pos, BlockState blockIn) {
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
		compound.putInt("Datahealth", this.entityData.get(DATA_health));
		compound.putString("Datanametag", this.entityData.get(DATA_nametag));
		compound.putInt("Dataoxygen", this.entityData.get(DATA_oxygen));
		compound.putString("Datauuid", this.entityData.get(DATA_uuid));
		compound.putBoolean("DataSprinting", this.entityData.get(DATA_Sprinting));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Datahealth"))
			this.entityData.set(DATA_health, compound.getInt("Datahealth"));
		if (compound.contains("Datanametag"))
			this.entityData.set(DATA_nametag, compound.getString("Datanametag"));
		if (compound.contains("Dataoxygen"))
			this.entityData.set(DATA_oxygen, compound.getInt("Dataoxygen"));
		if (compound.contains("Datauuid"))
			this.entityData.set(DATA_uuid, compound.getString("Datauuid"));
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
		return BlacktipReefSharkRightClickedOnEntityProcedure.execute(world, x, y, z, entity, sourceentity);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 0.875);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		BlacktipReefSharkEntity retval = BenssharksModEntities.BLACKTIP_REEF_SHARK.get().create(serverWorld);
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.35);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
		builder = builder.add(Attributes.FOLLOW_RANGE, 8);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.35);
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
			this.remove(BlacktipReefSharkEntity.RemovalReason.KILLED);
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
