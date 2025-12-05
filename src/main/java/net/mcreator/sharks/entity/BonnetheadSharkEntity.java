
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
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

import net.mcreator.sharks.procedures.BonnetheadSharkRightClickedOnEntityProcedure;
import net.mcreator.sharks.procedures.BonnetheadSharkOnInitialEntitySpawnProcedure;
import net.mcreator.sharks.procedures.BonnetheadSharkOnEntityTickUpdateProcedure;
import net.mcreator.sharks.procedures.BonnetheadSharkEntityIsHurtProcedure;
import net.mcreator.sharks.procedures.AggressiveSharksProcedureProcedure;
import net.mcreator.sharks.init.BenssharksModEntities;

import javax.annotation.Nullable;

import java.util.List;

public class BonnetheadSharkEntity extends Animal implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(BonnetheadSharkEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(BonnetheadSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BonnetheadSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_health = SynchedEntityData.defineId(BonnetheadSharkEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<String> DATA_nametag = SynchedEntityData.defineId(BonnetheadSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_oxygen = SynchedEntityData.defineId(BonnetheadSharkEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<String> DATA_uuid = SynchedEntityData.defineId(BonnetheadSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_Sprinting = SynchedEntityData.defineId(BonnetheadSharkEntity.class, EntityDataSerializers.BOOLEAN);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public BonnetheadSharkEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(BenssharksModEntities.BONNETHEAD_SHARK.get(), world);
	}

	public BonnetheadSharkEntity(EntityType<BonnetheadSharkEntity> type, Level world) {
		super(type, world);
		xpReward = 3;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (BonnetheadSharkEntity.this.isInWater())
					BonnetheadSharkEntity.this.setDeltaMovement(BonnetheadSharkEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !BonnetheadSharkEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - BonnetheadSharkEntity.this.getX();
					double dy = this.wantedY - BonnetheadSharkEntity.this.getY();
					double dz = this.wantedZ - BonnetheadSharkEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * BonnetheadSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					BonnetheadSharkEntity.this.setYRot(this.rotlerp(BonnetheadSharkEntity.this.getYRot(), f, 10));
					BonnetheadSharkEntity.this.yBodyRot = BonnetheadSharkEntity.this.getYRot();
					BonnetheadSharkEntity.this.yHeadRot = BonnetheadSharkEntity.this.getYRot();
					if (BonnetheadSharkEntity.this.isInWater()) {
						BonnetheadSharkEntity.this.setSpeed((float) BonnetheadSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						BonnetheadSharkEntity.this.setXRot(this.rotlerp(BonnetheadSharkEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(BonnetheadSharkEntity.this.getXRot() * (float) (Math.PI / 180.0));
						BonnetheadSharkEntity.this.setZza(f3 * f1);
						BonnetheadSharkEntity.this.setYya((float) (f1 * dy));
					} else {
						BonnetheadSharkEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					BonnetheadSharkEntity.this.setSpeed(0);
					BonnetheadSharkEntity.this.setYya(0);
					BonnetheadSharkEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "bonnet");
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
		this.goalSelector.addGoal(2, new BreedGoal(this, 1));
		this.goalSelector.addGoal(3, new FollowParentGoal(this, 1));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 15, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.goalSelector.addGoal(5, new PanicGoal(this, 1));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, true));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, Frog.class, true, true));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, Tadpole.class, true, true));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, Chicken.class, true, true));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, LivingEntity.class, true, true) {
			@Override
			public boolean canUse() {
				double x = BonnetheadSharkEntity.this.getX();
				double y = BonnetheadSharkEntity.this.getY();
				double z = BonnetheadSharkEntity.this.getZ();
				Entity entity = BonnetheadSharkEntity.this;
				Level world = BonnetheadSharkEntity.this.level();
				return super.canUse() && AggressiveSharksProcedureProcedure.execute(world);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BonnetheadSharkEntity.this.getX();
				double y = BonnetheadSharkEntity.this.getY();
				double z = BonnetheadSharkEntity.this.getZ();
				Entity entity = BonnetheadSharkEntity.this;
				Level world = BonnetheadSharkEntity.this.level();
				return super.canContinueToUse() && AggressiveSharksProcedureProcedure.execute(world);
			}
		});
		this.goalSelector.addGoal(13, new LeapAtTargetGoal(this, (float) 0.5));
		this.goalSelector.addGoal(14, new TemptGoal(this, 1, Ingredient.of(Blocks.SEAGRASS.asItem()), false));
		this.goalSelector.addGoal(15, new RemoveBlockGoal(Blocks.TALL_SEAGRASS, this, 1, (int) 32));
		this.goalSelector.addGoal(17, new LookAtPlayerGoal(this, BlacktipReefSharkEntity.class, (float) 8));
		this.goalSelector.addGoal(18, new LookAtPlayerGoal(this, WaterAnimal.class, (float) 32));
		this.goalSelector.addGoal(19, new AvoidEntityGoal<>(this, MegalodonEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(20, new AvoidEntityGoal<>(this, ElderGuardian.class, (float) 64, 16, 16));
		this.goalSelector.addGoal(21, new AvoidEntityGoal<>(this, Guardian.class, (float) 64, 16, 16));
		this.goalSelector.addGoal(22, new AvoidEntityGoal<>(this, ShrakEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(23, new AvoidEntityGoal<>(this, TigerSharkEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(24, new AvoidEntityGoal<>(this, MakoSharkEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(25, new AvoidEntityGoal<>(this, BlueSharkEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(26, new AvoidEntityGoal<>(this, BullSharkEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(27, new AvoidEntityGoal<>(this, LemonSharkEntity.class, (float) 4, 16, 16));
		this.goalSelector.addGoal(28, new AvoidEntityGoal<>(this, AxodileEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(29, new AvoidEntityGoal<>(this, Dolphin.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(30, new AvoidEntityGoal<>(this, RemoraEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(31, new RandomSwimmingGoal(this, 1, 40));
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
		BonnetheadSharkEntityIsHurtProcedure.execute(this.level(), this);
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		BonnetheadSharkOnInitialEntitySpawnProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
		return retval;
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
		return BonnetheadSharkRightClickedOnEntityProcedure.execute(world, x, y, z, entity, sourceentity);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		BonnetheadSharkOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 0.8);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		BonnetheadSharkEntity retval = BenssharksModEntities.BONNETHEAD_SHARK.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return List.of(Blocks.SEAGRASS.asItem()).contains(stack.getItem());
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.25);
		builder = builder.add(Attributes.MAX_HEALTH, 8);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.25);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isInWaterOrBubble()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("sprint2"));
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
			this.remove(BonnetheadSharkEntity.RemovalReason.KILLED);
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
