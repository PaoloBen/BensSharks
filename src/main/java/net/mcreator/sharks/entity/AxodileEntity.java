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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
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
import net.minecraft.util.Mth;
import net.minecraft.tags.BlockTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.procedures.AxodileOnEntityTickUpdateProcedure;
import net.mcreator.sharks.init.BenssharksModEntities;

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
				// MODIFICATION: Stop moving if custom animation is playing
				if (!AxodileEntity.this.animationprocedure.equals("empty")) {
					return;
				}

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
		this.entityData.define(ANIMATION, "empty");
		this.entityData.define(TEXTURE, "axodile");
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}
	
	public String getAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
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
		
		// Modded Targets
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, MakoSharkEntity.class, false, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, ThresherSharkEntity.class, false, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, BlueSharkEntity.class, false, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, GoblinSharkEntity.class, false, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, BullSharkEntity.class, false, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, WhitetipSharkEntity.class, false, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, LemonSharkEntity.class, false, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, NurseSharkEntity.class, false, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, LeopardSharkEntity.class, false, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, SawsharkEntity.class, false, false));
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, BlacktipReefSharkEntity.class, false, false));
		this.targetSelector.addGoal(15, new NearestAttackableTargetGoal(this, BonnetheadSharkEntity.class, false, false));
		this.targetSelector.addGoal(16, new NearestAttackableTargetGoal(this, SeaLionEntity.class, false, false));
		this.targetSelector.addGoal(17, new NearestAttackableTargetGoal(this, SardineEntity.class, false, false));
		this.targetSelector.addGoal(18, new NearestAttackableTargetGoal(this, BarracudaEntity.class, false, false));
		this.targetSelector.addGoal(19, new NearestAttackableTargetGoal(this, RemoraEntity.class, false, false));
		this.targetSelector.addGoal(20, new NearestAttackableTargetGoal(this, PilotFishEntity.class, false, false));
		
		// Vanilla Targets
		this.targetSelector.addGoal(21, new NearestAttackableTargetGoal(this, Player.class, true, false));
		this.targetSelector.addGoal(22, new NearestAttackableTargetGoal(this, Drowned.class, true, false));
		this.targetSelector.addGoal(23, new NearestAttackableTargetGoal(this, Zombie.class, true, false));
		this.targetSelector.addGoal(24, new NearestAttackableTargetGoal(this, Skeleton.class, true, false));
		this.targetSelector.addGoal(25, new NearestAttackableTargetGoal(this, Squid.class, true, false));
		this.targetSelector.addGoal(26, new NearestAttackableTargetGoal(this, GlowSquid.class, true, false));
		this.targetSelector.addGoal(27, new NearestAttackableTargetGoal(this, Cod.class, true, false));
		this.targetSelector.addGoal(28, new NearestAttackableTargetGoal(this, Salmon.class, true, false));
		this.targetSelector.addGoal(29, new NearestAttackableTargetGoal(this, TropicalFish.class, true, false));
		this.targetSelector.addGoal(30, new NearestAttackableTargetGoal(this, Turtle.class, true, false));
		
		this.goalSelector.addGoal(31, new RandomSwimmingGoal(this, 1, 40));
		this.goalSelector.addGoal(32, new LookAtPlayerGoal(this, Player.class, (float) 6));
		
		this.goalSelector.addGoal(33, new AvoidEntityGoal<>(this, MegalodonEntity.class, (float) 32, 1, 1.2));
		this.goalSelector.addGoal(34, new AvoidEntityGoal<>(this, WhaleSharkEntity.class, (float) 32, 1, 1.2));
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
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("intentionally_empty")), 0.15f, 1);
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
	public void tick() {
		super.tick();
		if (this.level().isClientSide()) {
			this.animationprocedure = this.entityData.get(ANIMATION);
			// FIX: Prevent "undefined" animation spam
			if (this.animationprocedure == null || this.animationprocedure.equals("undefined")) {
				this.animationprocedure = "empty";
			}
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		AxodileOnEntityTickUpdateProcedure.execute(this.level(), this);
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
		// 1. Run Phasing Logic BEFORE standard movement is calculated
		handleIcePhasing();
		
		super.aiStep();
		this.updateSwingTime();
	}

	/**
	 * Scans the entity's hitbox for ice. 
	 * If ice is found AND we are mid-leap OR falling, it disables physics (Vex Mode) AND breaks the ice.
	 */
	private void handleIcePhasing() {
		if (this.level().isClientSide()) return; // Server side logic only

		// === RESTRICTION: NOCLIP IF LEAPING OR FALLING ===
		// We check if:
		// 1. "JumpTimer" > 0 (Currently performing a leap attack)
		// 2. Vertical Speed < -0.1 (Falling significantly, e.g. onto ice)
		
		double jumpTimer = this.getPersistentData().getDouble("JumpTimer");
		boolean isFalling = this.getDeltaMovement().y < -0.1;

		if (jumpTimer <= 0 && !isFalling) {
			this.noPhysics = false;
			return;
		}

		boolean touchingIce = false;
		boolean touchingSolid = false;
		
		// Get hitbox and inflate slightly to detect blocks we are about to hit
		int minX = Mth.floor(this.getBoundingBox().minX + 0.1);
		int maxX = Mth.ceil(this.getBoundingBox().maxX - 0.1);
		int minY = Mth.floor(this.getBoundingBox().minY + 0.1);
		int maxY = Mth.ceil(this.getBoundingBox().maxY - 0.1);
		int minZ = Mth.floor(this.getBoundingBox().minZ + 0.1);
		int maxZ = Mth.ceil(this.getBoundingBox().maxZ - 0.1);

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				for (int z = minZ; z < maxZ; z++) {
					pos.set(x, y, z);
					BlockState state = this.level().getBlockState(pos);

					if (!state.isAir()) {
						if (isBreakableIce(state)) {
							touchingIce = true;
							
							// BREAK ICE LOGIC
							BlockPos belowPos = pos.below();
							BlockState belowState = this.level().getBlockState(belowPos);
							
							// Play break sound/particles
							this.level().levelEvent(2001, pos, Block.getId(state));

							// Convert to Water (if above liquid) or destroy
							if (!belowState.getFluidState().isEmpty() || belowState.is(Blocks.BUBBLE_COLUMN)) {
								this.level().setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
							} else {
								this.level().destroyBlock(pos, false);
							}
						} 
						// COMPILATION FIX: Use modern checks for Solidity
						// We check if the block shape is NOT empty (it has a collider) AND it is NOT a liquid.
						else if (!state.getCollisionShape(this.level(), pos).isEmpty() && state.getFluidState().isEmpty()) {
							touchingSolid = true;
						}
					}
				}
			}
		}

		// LOGIC: If touching ice AND NOT touching a solid wall/floor
		// Enable "noPhysics" (Vex Mode) to ghost through the ice we just broke.
		if (touchingIce && !touchingSolid) {
			this.noPhysics = true;
		} else {
			this.noPhysics = false;
		}
	}

	private boolean isBreakableIce(BlockState state) {
		return state.is(BlockTags.ICE) || 
			   state.getBlock() == Blocks.PACKED_ICE || 
			   state.getBlock() == Blocks.BLUE_ICE || 
			   state.getBlock() == Blocks.FROSTED_ICE ||
			   state.getBlock() == Blocks.ICE;
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.5);
		builder = builder.add(Attributes.MAX_HEALTH, 30);
		builder = builder.add(Attributes.ARMOR, 7);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 8);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.5);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			// FALLING LOGIC: Play landjump immediately when airborne
			if (!this.onGround() && !this.isInWater()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("landjump"));
			}
			
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
		// FIX: Added checks for "undefined" to prevent log spam
		if (!animationprocedure.equals("empty") && !animationprocedure.equals("undefined") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty") && !this.animationprocedure.equals("undefined"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (animationprocedure.equals("empty") || animationprocedure.equals("undefined")) {
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

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		// MODIFICATION: Set movement transition to 0 for instant falling check
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 3, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 3, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}