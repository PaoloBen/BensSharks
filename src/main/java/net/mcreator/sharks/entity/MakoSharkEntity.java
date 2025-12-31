
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
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

import net.mcreator.sharks.procedures.MakoSharkThisEntityKillsAnotherOneProcedure;
import net.mcreator.sharks.procedures.MakoSharkRightClickedOnEntityProcedure;
import net.mcreator.sharks.procedures.MakoSharkEntityIsHurtProcedure;
import net.mcreator.sharks.procedures.AggressiveSharksProcedureProcedure;
import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.init.BenssharksModEntities;

public class MakoSharkEntity extends PathfinderMob implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(MakoSharkEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(MakoSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(MakoSharkEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_Sprinting = SynchedEntityData.defineId(MakoSharkEntity.class, EntityDataSerializers.BOOLEAN);
	// [NEW] Add this line to sync breaching state
    public static final EntityDataAccessor<Boolean> DATA_Breaching = SynchedEntityData.defineId(MakoSharkEntity.class, EntityDataSerializers.BOOLEAN);

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public MakoSharkEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(BenssharksModEntities.MAKO_SHARK.get(), world);
	}

	public MakoSharkEntity(EntityType<MakoSharkEntity> type, Level world) {
		super(type, world);
		xpReward = 3;
		setNoAi(false);
		setMaxUpStep(0.6f);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
                // [FIX] IF BREACHING, DISABLE AI MOVEMENT CONTROL
                // This prevents the AI from turning the shark mid-air.
                if (MakoSharkEntity.this.entityData.get(DATA_Breaching)) {
                    return; 
                }

                if (MakoSharkEntity.this.isInWater())
                    MakoSharkEntity.this.setDeltaMovement(MakoSharkEntity.this.getDeltaMovement().add(0, 0.005, 0));
                
				if (this.operation == MoveControl.Operation.MOVE_TO && !MakoSharkEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - MakoSharkEntity.this.getX();
					double dy = this.wantedY - MakoSharkEntity.this.getY();
					double dz = this.wantedZ - MakoSharkEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * MakoSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					MakoSharkEntity.this.setYRot(this.rotlerp(MakoSharkEntity.this.getYRot(), f, 10));
					MakoSharkEntity.this.yBodyRot = MakoSharkEntity.this.getYRot();
					MakoSharkEntity.this.yHeadRot = MakoSharkEntity.this.getYRot();
					if (MakoSharkEntity.this.isInWater()) {
						MakoSharkEntity.this.setSpeed((float) MakoSharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						MakoSharkEntity.this.setXRot(this.rotlerp(MakoSharkEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(MakoSharkEntity.this.getXRot() * (float) (Math.PI / 180.0));
						MakoSharkEntity.this.setZza(f3 * f1);
						MakoSharkEntity.this.setYya((float) (f1 * dy));
					} else {
						MakoSharkEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					MakoSharkEntity.this.setSpeed(0);
					MakoSharkEntity.this.setYya(0);
					MakoSharkEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "mako");
		this.entityData.define(DATA_Sprinting, false);
		// [NEW] Define the new synced data
        this.entityData.define(DATA_Breaching, false);
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
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, MegalodonEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, ShrakEntity.class, (float) 8, 16, 16));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 15, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, CookiecutterSharkEntity.class, true, true));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Guardian.class, true, true));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, SardineEntity.class, true, true));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, AxodileEntity.class, true, true));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, BarracudaEntity.class, true, true));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, SeaLionEntity.class, true, true));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, Turtle.class, true, true));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, Ravager.class, true, true));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, Hoglin.class, true, true));
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, Zoglin.class, true, true));
		this.targetSelector.addGoal(15, new NearestAttackableTargetGoal(this, Sniffer.class, true, true));
		this.targetSelector.addGoal(16, new NearestAttackableTargetGoal(this, Horse.class, true, true));
		this.targetSelector.addGoal(17, new NearestAttackableTargetGoal(this, Donkey.class, true, true));
		this.targetSelector.addGoal(18, new NearestAttackableTargetGoal(this, Mule.class, true, true));
		this.targetSelector.addGoal(19, new NearestAttackableTargetGoal(this, Cow.class, true, true));
		this.targetSelector.addGoal(20, new NearestAttackableTargetGoal(this, MushroomCow.class, true, true));
		this.targetSelector.addGoal(21, new NearestAttackableTargetGoal(this, TraderLlama.class, true, true));
		this.targetSelector.addGoal(22, new NearestAttackableTargetGoal(this, Llama.class, true, true));
		this.targetSelector.addGoal(23, new NearestAttackableTargetGoal(this, Camel.class, true, true));
		this.targetSelector.addGoal(24, new NearestAttackableTargetGoal(this, Sheep.class, true, true));
		this.targetSelector.addGoal(25, new NearestAttackableTargetGoal(this, Goat.class, true, true));
		this.targetSelector.addGoal(26, new NearestAttackableTargetGoal(this, Pig.class, true, true));
		this.targetSelector.addGoal(27, new NearestAttackableTargetGoal(this, Wolf.class, true, true));
		this.targetSelector.addGoal(28, new NearestAttackableTargetGoal(this, Fox.class, true, true));
		this.targetSelector.addGoal(29, new NearestAttackableTargetGoal(this, Ocelot.class, true, true));
		this.targetSelector.addGoal(30, new NearestAttackableTargetGoal(this, Cat.class, true, true));
		this.targetSelector.addGoal(31, new NearestAttackableTargetGoal(this, Chicken.class, true, true));
		this.targetSelector.addGoal(32, new NearestAttackableTargetGoal(this, Parrot.class, true, true));
		this.targetSelector.addGoal(33, new NearestAttackableTargetGoal(this, Pufferfish.class, true, true));
		this.targetSelector.addGoal(34, new NearestAttackableTargetGoal(this, GlowSquid.class, true, true));
		this.targetSelector.addGoal(35, new NearestAttackableTargetGoal(this, Squid.class, true, true));
		this.targetSelector.addGoal(36, new NearestAttackableTargetGoal(this, Frog.class, true, true));
		this.targetSelector.addGoal(37, new NearestAttackableTargetGoal(this, Tadpole.class, true, true));
		this.targetSelector.addGoal(38, new NearestAttackableTargetGoal(this, LivingEntity.class, true, true) {
			@Override
			public boolean canUse() {
				double x = MakoSharkEntity.this.getX();
				double y = MakoSharkEntity.this.getY();
				double z = MakoSharkEntity.this.getZ();
				Entity entity = MakoSharkEntity.this;
				Level world = MakoSharkEntity.this.level();
				return super.canUse() && AggressiveSharksProcedureProcedure.execute(world);
			}

			@Override
			public boolean canContinueToUse() {
				double x = MakoSharkEntity.this.getX();
				double y = MakoSharkEntity.this.getY();
				double z = MakoSharkEntity.this.getZ();
				Entity entity = MakoSharkEntity.this;
				Level world = MakoSharkEntity.this.level();
				return super.canContinueToUse() && AggressiveSharksProcedureProcedure.execute(world);
			}
		});
		this.goalSelector.addGoal(39, new TemptGoal(this, 1, Ingredient.of(BenssharksModItems.FISH_BUCKET.get()), false));
		this.goalSelector.addGoal(41, new LookAtPlayerGoal(this, WaterAnimal.class, (float) 128));
		this.goalSelector.addGoal(42, new AvoidEntityGoal<>(this, ElderGuardian.class, (float) 32, 16, 16));
		this.goalSelector.addGoal(43, new AvoidEntityGoal<>(this, TigerSharkEntity.class, (float) 8, 16, 16));
		this.goalSelector.addGoal(44, new AvoidEntityGoal<>(this, AxodileEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(45, new AvoidEntityGoal<>(this, Dolphin.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(46, new AvoidEntityGoal<>(this, RemoraEntity.class, (float) 16, 16, 16));
		this.goalSelector.addGoal(47, new RandomSwimmingGoal(this, 1, 40));
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
	public boolean hurt(DamageSource source, float amount) {
		MakoSharkEntityIsHurtProcedure.execute();
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("DataSprinting", this.entityData.get(DATA_Sprinting));
		// [NEW] Save logic
        compound.putBoolean("DataBreaching", this.entityData.get(DATA_Breaching));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DataSprinting"))
			this.entityData.set(DATA_Sprinting, compound.getBoolean("DataSprinting"));
		// [NEW] Load logic
        if (compound.contains("DataBreaching"))
            this.entityData.set(DATA_Breaching, compound.getBoolean("DataBreaching"));
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
		return MakoSharkRightClickedOnEntityProcedure.execute(world, x, y, z, entity, sourceentity);
	}

	@Override
	public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
		super.awardKillScore(entity, score, damageSource);
		MakoSharkThisEntityKillsAnotherOneProcedure.execute(this.level(), entity);
	}

	@Override
	public void baseTick() {
		super.baseTick();
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.8);
		builder = builder.add(Attributes.MAX_HEALTH, 30);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.8);
		return builder;
	}

	// ================= ANIMATION LOGIC =================

    private PlayState movementPredicate(AnimationState event) {
        // [FIX] Use Synced Data (DATA_Breaching) instead of PersistentData
        // Also: We do not check "onGround" here to allow it to start playing the moment it leaves water
        boolean isBreaching = this.entityData.get(DATA_Breaching);
        
        if (isBreaching && !this.isInWater()) {
             return event.setAndContinue(RawAnimation.begin().thenLoop("fall"));
        }

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
			this.remove(MakoSharkEntity.RemovalReason.KILLED);
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
