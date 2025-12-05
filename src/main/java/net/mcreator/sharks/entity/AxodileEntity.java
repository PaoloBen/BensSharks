
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
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
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

import net.mcreator.sharks.procedures.AxodileOnInitialEntitySpawnProcedure;
import net.mcreator.sharks.procedures.AxodileOnEntityTickUpdateProcedure;
import net.mcreator.sharks.procedures.AggressiveSharksProcedureProcedure;
import net.mcreator.sharks.init.BenssharksModEntities;

import javax.annotation.Nullable;

public class AxodileEntity extends PathfinderMob implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(AxodileEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(AxodileEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AxodileEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public AxodileEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(BenssharksModEntities.AXODILE.get(), world);
	}

	public AxodileEntity(EntityType<AxodileEntity> type, Level world) {
		super(type, world);
		xpReward = 3;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (AxodileEntity.this.isInWater())
					AxodileEntity.this.setDeltaMovement(AxodileEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !AxodileEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - AxodileEntity.this.getX();
					double dy = this.wantedY - AxodileEntity.this.getY();
					double dz = this.wantedZ - AxodileEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * AxodileEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					AxodileEntity.this.setYRot(this.rotlerp(AxodileEntity.this.getYRot(), f, 10));
					AxodileEntity.this.yBodyRot = AxodileEntity.this.getYRot();
					AxodileEntity.this.yHeadRot = AxodileEntity.this.getYRot();
					if (AxodileEntity.this.isInWater()) {
						AxodileEntity.this.setSpeed((float) AxodileEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						AxodileEntity.this.setXRot(this.rotlerp(AxodileEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(AxodileEntity.this.getXRot() * (float) (Math.PI / 180.0));
						AxodileEntity.this.setZza(f3 * f1);
						AxodileEntity.this.setYya((float) (f1 * dy));
					} else {
						AxodileEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					AxodileEntity.this.setSpeed(0);
					AxodileEntity.this.setYya(0);
					AxodileEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "axodile");
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
				return 4;
			}
		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(3, new RandomSwimmingGoal(this, 1, 40));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Turtle.class, (float) 256));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, true));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Player.class, true, true));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, PolarBear.class, true, true));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Wolf.class, true, true));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, Fox.class, true, true));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, Pufferfish.class, true, true));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, MakoSharkEntity.class, true, true));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, BlueSharkEntity.class, true, true));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, GoblinSharkEntity.class, true, true));
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, BullSharkEntity.class, true, true));
		this.targetSelector.addGoal(15, new NearestAttackableTargetGoal(this, WhitetipSharkEntity.class, true, true));
		this.targetSelector.addGoal(16, new NearestAttackableTargetGoal(this, LemonSharkEntity.class, true, true));
		this.targetSelector.addGoal(17, new NearestAttackableTargetGoal(this, NurseSharkEntity.class, true, true));
		this.targetSelector.addGoal(18, new NearestAttackableTargetGoal(this, LeopardSharkEntity.class, true, true));
		this.targetSelector.addGoal(19, new NearestAttackableTargetGoal(this, SawsharkEntity.class, true, true));
		this.targetSelector.addGoal(20, new NearestAttackableTargetGoal(this, BlacktipReefSharkEntity.class, true, true));
		this.targetSelector.addGoal(21, new NearestAttackableTargetGoal(this, BonnetheadSharkEntity.class, true, true));
		this.targetSelector.addGoal(22, new NearestAttackableTargetGoal(this, SeaLionEntity.class, true, true));
		this.targetSelector.addGoal(23, new NearestAttackableTargetGoal(this, BarracudaEntity.class, true, true));
		this.targetSelector.addGoal(24, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, true));
		this.targetSelector.addGoal(25, new NearestAttackableTargetGoal(this, Salmon.class, true, true));
		this.targetSelector.addGoal(26, new NearestAttackableTargetGoal(this, Cod.class, true, true));
		this.targetSelector.addGoal(27, new NearestAttackableTargetGoal(this, Dolphin.class, true, true));
		this.targetSelector.addGoal(28, new NearestAttackableTargetGoal(this, Axolotl.class, true, true));
		this.targetSelector.addGoal(29, new NearestAttackableTargetGoal(this, RemoraEntity.class, true, true));
		this.targetSelector.addGoal(30, new NearestAttackableTargetGoal(this, PilotFishEntity.class, true, true));
		this.targetSelector.addGoal(31, new NearestAttackableTargetGoal(this, Allay.class, true, true));
		this.targetSelector.addGoal(32, new NearestAttackableTargetGoal(this, Bee.class, true, true));
		this.targetSelector.addGoal(33, new NearestAttackableTargetGoal(this, Blaze.class, true, true));
		this.targetSelector.addGoal(34, new NearestAttackableTargetGoal(this, Camel.class, true, true));
		this.targetSelector.addGoal(35, new NearestAttackableTargetGoal(this, Cat.class, true, true));
		this.targetSelector.addGoal(36, new NearestAttackableTargetGoal(this, SkeletonHorse.class, true, true));
		this.targetSelector.addGoal(37, new NearestAttackableTargetGoal(this, ZombieHorse.class, true, true));
		this.targetSelector.addGoal(38, new NearestAttackableTargetGoal(this, Husk.class, true, true));
		this.targetSelector.addGoal(39, new NearestAttackableTargetGoal(this, Illusioner.class, true, true));
		this.targetSelector.addGoal(40, new NearestAttackableTargetGoal(this, Llama.class, true, true));
		this.targetSelector.addGoal(41, new NearestAttackableTargetGoal(this, Mule.class, true, true));
		this.targetSelector.addGoal(42, new NearestAttackableTargetGoal(this, Parrot.class, true, true));
		this.targetSelector.addGoal(43, new NearestAttackableTargetGoal(this, Phantom.class, true, true));
		this.targetSelector.addGoal(44, new NearestAttackableTargetGoal(this, Pig.class, true, true));
		this.targetSelector.addGoal(45, new NearestAttackableTargetGoal(this, Piglin.class, true, true));
		this.targetSelector.addGoal(46, new NearestAttackableTargetGoal(this, PiglinBrute.class, true, true));
		this.targetSelector.addGoal(47, new NearestAttackableTargetGoal(this, Pillager.class, true, true));
		this.targetSelector.addGoal(48, new NearestAttackableTargetGoal(this, Rabbit.class, true, true));
		this.targetSelector.addGoal(49, new NearestAttackableTargetGoal(this, Sheep.class, true, true));
		this.targetSelector.addGoal(50, new NearestAttackableTargetGoal(this, Silverfish.class, true, true));
		this.targetSelector.addGoal(51, new NearestAttackableTargetGoal(this, Skeleton.class, true, true));
		this.targetSelector.addGoal(52, new NearestAttackableTargetGoal(this, SnowGolem.class, true, true));
		this.targetSelector.addGoal(53, new NearestAttackableTargetGoal(this, Spider.class, true, true));
		this.targetSelector.addGoal(54, new NearestAttackableTargetGoal(this, Stray.class, true, true));
		this.targetSelector.addGoal(55, new NearestAttackableTargetGoal(this, Tadpole.class, true, true));
		this.targetSelector.addGoal(56, new NearestAttackableTargetGoal(this, TraderLlama.class, true, true));
		this.targetSelector.addGoal(57, new NearestAttackableTargetGoal(this, TropicalFish.class, true, true));
		this.targetSelector.addGoal(58, new NearestAttackableTargetGoal(this, Vex.class, true, true));
		this.targetSelector.addGoal(59, new NearestAttackableTargetGoal(this, ThalassogerEntity.class, true, true));
		this.targetSelector.addGoal(60, new NearestAttackableTargetGoal(this, Villager.class, true, true));
		this.targetSelector.addGoal(61, new NearestAttackableTargetGoal(this, Vindicator.class, true, true));
		this.targetSelector.addGoal(62, new NearestAttackableTargetGoal(this, WanderingTrader.class, true, true));
		this.targetSelector.addGoal(63, new NearestAttackableTargetGoal(this, Witch.class, true, true));
		this.targetSelector.addGoal(64, new NearestAttackableTargetGoal(this, WitherSkeleton.class, true, true));
		this.targetSelector.addGoal(65, new NearestAttackableTargetGoal(this, Zoglin.class, true, true));
		this.targetSelector.addGoal(66, new NearestAttackableTargetGoal(this, ZombifiedPiglin.class, true, true));
		this.targetSelector.addGoal(67, new NearestAttackableTargetGoal(this, Zombie.class, true, true));
		this.targetSelector.addGoal(68, new NearestAttackableTargetGoal(this, Ocelot.class, true, true));
		this.targetSelector.addGoal(69, new NearestAttackableTargetGoal(this, CaveSpider.class, true, true));
		this.targetSelector.addGoal(70, new NearestAttackableTargetGoal(this, EnderMan.class, true, true));
		this.targetSelector.addGoal(71, new NearestAttackableTargetGoal(this, Endermite.class, true, true));
		this.targetSelector.addGoal(72, new NearestAttackableTargetGoal(this, Evoker.class, true, true));
		this.targetSelector.addGoal(73, new NearestAttackableTargetGoal(this, Frog.class, true, true));
		this.targetSelector.addGoal(74, new NearestAttackableTargetGoal(this, Goat.class, true, true));
		this.targetSelector.addGoal(75, new NearestAttackableTargetGoal(this, Hoglin.class, true, true));
		this.targetSelector.addGoal(76, new NearestAttackableTargetGoal(this, Horse.class, true, true));
		this.targetSelector.addGoal(77, new NearestAttackableTargetGoal(this, Donkey.class, true, true));
		this.targetSelector.addGoal(78, new NearestAttackableTargetGoal(this, Drowned.class, true, true));
		this.targetSelector.addGoal(79, new NearestAttackableTargetGoal(this, Zombie.class, true, true));
		this.targetSelector.addGoal(80, new NearestAttackableTargetGoal(this, Skeleton.class, true, true));
		this.targetSelector.addGoal(81, new NearestAttackableTargetGoal(this, Guardian.class, true, true));
		this.targetSelector.addGoal(82, new NearestAttackableTargetGoal(this, Bat.class, true, true));
		this.targetSelector.addGoal(83, new NearestAttackableTargetGoal(this, Squid.class, true, true));
		this.targetSelector.addGoal(84, new NearestAttackableTargetGoal(this, GlowSquid.class, true, true));
		this.targetSelector.addGoal(85, new NearestAttackableTargetGoal(this, LivingEntity.class, true, true) {
			@Override
			public boolean canUse() {
				double x = AxodileEntity.this.getX();
				double y = AxodileEntity.this.getY();
				double z = AxodileEntity.this.getZ();
				Entity entity = AxodileEntity.this;
				Level world = AxodileEntity.this.level();
				return super.canUse() && AggressiveSharksProcedureProcedure.execute(world);
			}

			@Override
			public boolean canContinueToUse() {
				double x = AxodileEntity.this.getX();
				double y = AxodileEntity.this.getY();
				double z = AxodileEntity.this.getZ();
				Entity entity = AxodileEntity.this;
				Level world = AxodileEntity.this.level();
				return super.canContinueToUse() && AggressiveSharksProcedureProcedure.execute(world);
			}
		});
		this.goalSelector.addGoal(86, new LeapAtTargetGoal(this, (float) 0.5));
		this.goalSelector.addGoal(87, new AvoidEntityGoal<>(this, WaterAnimal.class, (float) 32, 1, 1.2));
		this.goalSelector.addGoal(88, new AvoidEntityGoal<>(this, MegalodonEntity.class, (float) 32, 1, 1.2));
		this.goalSelector.addGoal(89, new AvoidEntityGoal<>(this, WhaleSharkEntity.class, (float) 32, 1, 1.2));
		this.goalSelector.addGoal(90, new AvoidEntityGoal<>(this, BaskingSharkEntity.class, (float) 32, 1, 1.2));
		this.goalSelector.addGoal(91, new AvoidEntityGoal<>(this, ShrakEntity.class, (float) 32, 1, 1.2));
		this.goalSelector.addGoal(92, new AvoidEntityGoal<>(this, TigerSharkEntity.class, (float) 32, 1, 1.2));
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
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.turtle.ambient_land"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.turtle.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.turtle.death"));
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		AxodileOnInitialEntitySpawnProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		AxodileOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.5);
		builder = builder.add(Attributes.MAX_HEALTH, 20);
		builder = builder.add(Attributes.ARMOR, 20);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 8);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.5);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isInWaterOrBubble()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle_land"));
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
			this.remove(AxodileEntity.RemovalReason.KILLED);
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
		data.add(new AnimationController<>(this, "movement", 3, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 3, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 3, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
