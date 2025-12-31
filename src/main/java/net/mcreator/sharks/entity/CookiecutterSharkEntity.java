
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.procedures.CookiecutterSharkRightClickedOnEntityProcedure;
import net.mcreator.sharks.procedures.CookiecutterSharkOnEntityTickUpdateProcedure;
import net.mcreator.sharks.procedures.AggressiveSharksProcedureProcedure;
import net.mcreator.sharks.init.BenssharksModEntities;

public class CookiecutterSharkEntity extends PathfinderMob implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(CookiecutterSharkEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(CookiecutterSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(CookiecutterSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_health = SynchedEntityData.defineId(CookiecutterSharkEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<String> DATA_nametag = SynchedEntityData.defineId(CookiecutterSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_oxygen = SynchedEntityData.defineId(CookiecutterSharkEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<String> DATA_uuid = SynchedEntityData.defineId(CookiecutterSharkEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public CookiecutterSharkEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(BenssharksModEntities.COOKIECUTTER_SHARK.get(), world);
	}

	public CookiecutterSharkEntity(EntityType<CookiecutterSharkEntity> type, Level world) {
		super(type, world);
		xpReward = 2;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (CookiecutterSharkEntity.this.isInWater())
					CookiecutterSharkEntity.this.setDeltaMovement(CookiecutterSharkEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !CookiecutterSharkEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - CookiecutterSharkEntity.this.getX();
					double dy = this.wantedY - CookiecutterSharkEntity.this.getY();
					double dz = this.wantedZ - CookiecutterSharkEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * CookiecutterSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					CookiecutterSharkEntity.this.setYRot(this.rotlerp(CookiecutterSharkEntity.this.getYRot(), f, 10));
					CookiecutterSharkEntity.this.yBodyRot = CookiecutterSharkEntity.this.getYRot();
					CookiecutterSharkEntity.this.yHeadRot = CookiecutterSharkEntity.this.getYRot();
					if (CookiecutterSharkEntity.this.isInWater()) {
						CookiecutterSharkEntity.this.setSpeed((float) CookiecutterSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						CookiecutterSharkEntity.this.setXRot(this.rotlerp(CookiecutterSharkEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(CookiecutterSharkEntity.this.getXRot() * (float) (Math.PI / 180.0));
						CookiecutterSharkEntity.this.setZza(f3 * f1);
						CookiecutterSharkEntity.this.setYya((float) (f1 * dy));
					} else {
						CookiecutterSharkEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					CookiecutterSharkEntity.this.setSpeed(0);
					CookiecutterSharkEntity.this.setYya(0);
					CookiecutterSharkEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "cookie");
		this.entityData.define(DATA_health, 4);
		this.entityData.define(DATA_nametag, "");
		this.entityData.define(DATA_oxygen, 0);
		this.entityData.define(DATA_uuid, "");
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
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.2));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, MegalodonEntity.class, true, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, GreaterAxodileEntity.class, true, true));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, GreenlandSharkEntity.class, true, true));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, ShrakEntity.class, true, true));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, TigerSharkEntity.class, true, true));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, MakoSharkEntity.class, true, true));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, BlueSharkEntity.class, true, true));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, GoblinSharkEntity.class, true, true));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, BullSharkEntity.class, true, true));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, WhitetipSharkEntity.class, true, true));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, LemonSharkEntity.class, true, true));
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, ThresherSharkEntity.class, true, true));
		this.targetSelector.addGoal(15, new NearestAttackableTargetGoal(this, NurseSharkEntity.class, true, true));
		this.targetSelector.addGoal(16, new NearestAttackableTargetGoal(this, LeopardSharkEntity.class, true, true));
		this.targetSelector.addGoal(17, new NearestAttackableTargetGoal(this, SawsharkEntity.class, true, true));
		this.targetSelector.addGoal(18, new NearestAttackableTargetGoal(this, BlacktipReefSharkEntity.class, true, true));
		this.targetSelector.addGoal(19, new NearestAttackableTargetGoal(this, BonnetheadSharkEntity.class, true, true));
		this.targetSelector.addGoal(20, new NearestAttackableTargetGoal(this, BarracudaEntity.class, true, true));
		this.targetSelector.addGoal(21, new NearestAttackableTargetGoal(this, GlowSquid.class, true, true));
		this.targetSelector.addGoal(22, new NearestAttackableTargetGoal(this, Squid.class, true, true));
		this.targetSelector.addGoal(23, new NearestAttackableTargetGoal(this, AxodileEntity.class, true, true));
		this.targetSelector.addGoal(24, new NearestAttackableTargetGoal(this, Turtle.class, true, true));
		this.targetSelector.addGoal(25, new NearestAttackableTargetGoal(this, Dolphin.class, true, true));
		this.targetSelector.addGoal(26, new NearestAttackableTargetGoal(this, WhaleSharkEntity.class, true, true));
		this.targetSelector.addGoal(27, new NearestAttackableTargetGoal(this, BaskingSharkEntity.class, true, true));
		this.targetSelector.addGoal(28, new NearestAttackableTargetGoal(this, LandSharkEntity.class, true, true));
		this.targetSelector.addGoal(29, new NearestAttackableTargetGoal(this, LivingEntity.class, true, true) {
			@Override
			public boolean canUse() {
				double x = CookiecutterSharkEntity.this.getX();
				double y = CookiecutterSharkEntity.this.getY();
				double z = CookiecutterSharkEntity.this.getZ();
				Entity entity = CookiecutterSharkEntity.this;
				Level world = CookiecutterSharkEntity.this.level();
				return super.canUse() && AggressiveSharksProcedureProcedure.execute(world);
			}

			@Override
			public boolean canContinueToUse() {
				double x = CookiecutterSharkEntity.this.getX();
				double y = CookiecutterSharkEntity.this.getY();
				double z = CookiecutterSharkEntity.this.getZ();
				Entity entity = CookiecutterSharkEntity.this;
				Level world = CookiecutterSharkEntity.this.level();
				return super.canContinueToUse() && AggressiveSharksProcedureProcedure.execute(world);
			}
		});
		this.goalSelector.addGoal(30, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.goalSelector.addGoal(32, new RandomSwimmingGoal(this, 1, 40));
		this.goalSelector.addGoal(34, new LookAtPlayerGoal(this, MegalodonEntity.class, (float) 6));
		this.goalSelector.addGoal(35, new LookAtPlayerGoal(this, BaskingSharkEntity.class, (float) 6));
		this.goalSelector.addGoal(36, new LookAtPlayerGoal(this, ShrakEntity.class, (float) 6));
		this.goalSelector.addGoal(37, new LookAtPlayerGoal(this, TigerSharkEntity.class, (float) 6));
		this.goalSelector.addGoal(38, new LookAtPlayerGoal(this, MakoSharkEntity.class, (float) 6));
		this.goalSelector.addGoal(39, new LookAtPlayerGoal(this, BlueSharkEntity.class, (float) 6));
		this.goalSelector.addGoal(40, new LookAtPlayerGoal(this, NurseSharkEntity.class, (float) 6));
		this.goalSelector.addGoal(41, new LookAtPlayerGoal(this, Turtle.class, (float) 6));
		this.goalSelector.addGoal(42, new LookAtPlayerGoal(this, Dolphin.class, (float) 6));
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
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
		return CookiecutterSharkRightClickedOnEntityProcedure.execute(world, x, y, z, entity, sourceentity);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		CookiecutterSharkOnEntityTickUpdateProcedure.execute(this.level(), this);
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1.5);
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.05);
		builder = builder.add(Attributes.MAX_HEALTH, 4);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 1);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.05);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
			}
			if (this.isInWaterOrBubble()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("land"));
		}
		return PlayState.STOP;
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
			this.remove(CookiecutterSharkEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
