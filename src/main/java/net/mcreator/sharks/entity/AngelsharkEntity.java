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
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
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

import net.mcreator.sharks.procedures.AngelsharkRightClickedOnEntityProcedure;
import net.mcreator.sharks.procedures.AngelsharkOnEntityTickUpdateProcedure;
import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.init.BenssharksModEntities;
import net.mcreator.sharks.entity.ai.AngelsharkHidingGoal;
import net.mcreator.sharks.entity.ai.AngelsharkScavengeGoal;

public class AngelsharkEntity extends PathfinderMob implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(AngelsharkEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(AngelsharkEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AngelsharkEntity.class, EntityDataSerializers.STRING);
    
    public static final EntityDataAccessor<Boolean> DATA_Hiding = SynchedEntityData.defineId(AngelsharkEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_Burrowing = SynchedEntityData.defineId(AngelsharkEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> AMBUSH_COOLDOWN = SynchedEntityData.defineId(AngelsharkEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";
    public int scavengeCooldown = 0;

    public AngelsharkEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(BenssharksModEntities.ANGELSHARK.get(), world);
    }

    public AngelsharkEntity(EntityType<AngelsharkEntity> type, Level world) {
        super(type, world);
        xpReward = 1;
        setNoAi(false);
        setMaxUpStep(0.6f);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0);
        
        this.moveControl = new MoveControl(this) {
            @Override
            public void tick() {
                // If Hidden, pin to floor and STOP.
                if (AngelsharkEntity.this.isHidden()) { 
                    AngelsharkEntity.this.setSpeed(0);
                    AngelsharkEntity.this.setDeltaMovement(0, -0.05, 0); 
                    return; 
                }

                if (AngelsharkEntity.this.isInWater()) {
                    // Slight lift (0.003) to help it pathfind off the ground when not hidden
                    AngelsharkEntity.this.setDeltaMovement(AngelsharkEntity.this.getDeltaMovement().add(0, 0.003, 0));
                }
                
                if (this.operation == MoveControl.Operation.MOVE_TO && !AngelsharkEntity.this.getNavigation().isDone()) {
                    double dx = this.wantedX - AngelsharkEntity.this.getX();
                    double dy = this.wantedY - AngelsharkEntity.this.getY();
                    double dz = this.wantedZ - AngelsharkEntity.this.getZ();
                    float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
                    
                    float f1 = (float) (this.speedModifier * AngelsharkEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
                    
                    AngelsharkEntity.this.setYRot(this.rotlerp(AngelsharkEntity.this.getYRot(), f, 10));
                    AngelsharkEntity.this.yBodyRot = AngelsharkEntity.this.getYRot();
                    AngelsharkEntity.this.yHeadRot = AngelsharkEntity.this.getYRot();
                    if (AngelsharkEntity.this.isInWater()) {
                        AngelsharkEntity.this.setSpeed(f1); 
                        float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
                        f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
                        AngelsharkEntity.this.setXRot(this.rotlerp(AngelsharkEntity.this.getXRot(), f2, 5));
                        float f3 = Mth.cos(AngelsharkEntity.this.getXRot() * (float) (Math.PI / 180.0));
                        AngelsharkEntity.this.setZza(f3 * f1);
                        AngelsharkEntity.this.setYya((float) (f1 * dy));
                    } else {
                        AngelsharkEntity.this.setSpeed(f1 * 0.05F);
                    }
                } else {
                    AngelsharkEntity.this.setSpeed(0);
                    AngelsharkEntity.this.setYya(0);
                    AngelsharkEntity.this.setZza(0);
                }
            }
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "angelshark");
        this.entityData.define(DATA_Hiding, false);
        this.entityData.define(DATA_Burrowing, false);
        this.entityData.define(AMBUSH_COOLDOWN, 0);
    }

    public boolean isHidden() { return this.entityData.get(DATA_Hiding); }
    public void setHidden(boolean hidden) { this.entityData.set(DATA_Hiding, hidden); this.refreshDimensions(); }

    public boolean isDigging() { return this.entityData.get(DATA_Burrowing); }
    public void setDigging(boolean digging) { this.entityData.set(DATA_Burrowing, digging); }
    
    public int getAmbushCooldown() { return this.entityData.get(AMBUSH_COOLDOWN); }
    public void setAmbushCooldown(int time) { this.entityData.set(AMBUSH_COOLDOWN, time); }

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
        
        this.goalSelector.addGoal(2, new AngelsharkScavengeGoal(this));
        
        // Priority 3: Hiding (Disabled if Stomach > 0)
        this.goalSelector.addGoal(3, new AngelsharkHidingGoal(this));
        
        // Priority 5: Wandering (Takes over when Hiding is disabled)
        this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0, 10));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Predicate ensures we don't start hunting if full/cooldown
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, CookiecutterSharkEntity.class, 10, true, false, (entity) -> this.getAmbushCooldown() <= 0));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Salmon.class, 10, true, false, (entity) -> this.getAmbushCooldown() <= 0));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Cod.class, 10, true, false, (entity) -> this.getAmbushCooldown() <= 0));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, SardineEntity.class, 10, true, false, (entity) -> this.getAmbushCooldown() <= 0));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Pufferfish.class, 10, true, false, (entity) -> this.getAmbushCooldown() <= 0));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, TropicalFish.class, 10, true, false, (entity) -> this.getAmbushCooldown() <= 0));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Squid.class, 10, true, false, (entity) -> this.getAmbushCooldown() <= 0));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, GlowSquid.class, 10, true, false, (entity) -> this.getAmbushCooldown() <= 0));
        
        this.goalSelector.addGoal(10, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    // --- HITBOX & COLLISION ---
    @Override
    public boolean isPushable() { return true; }
    @Override
    public void push(Entity entity) { if (this.isHidden()) return; super.push(entity); }
    @Override
    public void doPush(Entity entity) { if (this.isHidden()) return; super.doPush(entity); }
    @Override
    public boolean canBeCollidedWith() { return false; }

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
        compound.putBoolean("Hidden", this.isHidden());
        compound.putBoolean("Digging", this.isDigging());
        compound.putInt("ScavengeCooldown", this.scavengeCooldown);
        compound.putInt("AmbushCooldown", this.getAmbushCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) this.setTexture(compound.getString("Texture"));
        if (compound.contains("Hidden")) this.setHidden(compound.getBoolean("Hidden"));
        if (compound.contains("Digging")) this.setDigging(compound.getBoolean("Digging"));
        if (compound.contains("ScavengeCooldown")) this.scavengeCooldown = compound.getInt("ScavengeCooldown");
        if (compound.contains("AmbushCooldown")) this.setAmbushCooldown(compound.getInt("AmbushCooldown"));
    }

    @Override
    public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
        ItemStack itemstack = sourceentity.getItemInHand(hand);
        
        if (itemstack.getItem() == BenssharksModItems.FISH_BUCKET.get() && this.scavengeCooldown <= 0) {
             if (!this.level().isClientSide) {
                // FIXED: Only consume item and give back a bucket if NOT in creative mode
                if (!sourceentity.getAbilities().instabuild) {
                    itemstack.shrink(1);
                    ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                    
                    if (itemstack.isEmpty()) {
                        sourceentity.setItemInHand(hand, emptyBucket);
                    } else if (!sourceentity.getInventory().add(emptyBucket)) {
                        sourceentity.drop(emptyBucket, false);
                    }
                }
                
                // Logic to trigger the Scavenge goal remains the same
                this.setDigging(true);
                this.setHidden(false); 
                this.scavengeCooldown = 20; 
                
                this.playSound(SoundEvent.createVariableRangeEvent(new ResourceLocation("entity.generic.eat")), 1.0F, 1.0F);
             }
             return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
        super.mobInteract(sourceentity, hand);
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = this;
        Level world = this.level();
        return AngelsharkRightClickedOnEntityProcedure.execute(world, x, y, z, entity, sourceentity);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.scavengeCooldown > 0) this.scavengeCooldown--;
        if (this.getAmbushCooldown() > 0) this.setAmbushCooldown(this.getAmbushCooldown() - 1);
        
        // Safety: If cooldown is active or full, never stay hidden
        if ((this.getAmbushCooldown() > 0 || this.entityData.get(AMBUSH_COOLDOWN) > 0) && this.isHidden()) {
            this.setHidden(false);
        }

        AngelsharkOnEntityTickUpdateProcedure.execute(this.level(), this);
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        if (this.isHidden()) {
            return EntityDimensions.fixed(1.0f, 0.2f);
        }
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    @Override
    public boolean canBreatheUnderwater() { return true; }
    @Override
    public boolean checkSpawnObstruction(LevelReader world) { return world.isUnobstructed(this); }
    @Override
    public boolean isPushedByFluid() { return !this.isHidden(); }

    public static void init() {}

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.8);
        builder = builder.add(Attributes.MAX_HEALTH, 20);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16); 
        builder = builder.add(ForgeMod.SWIM_SPEED.get(), 0.8); 
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.isDigging()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() < 0.01) {
                 return event.setAndContinue(RawAnimation.begin().thenLoop("burrow"));
            }
        }
        
        if (this.isHidden()) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("hide"));
        }

        if (this.animationprocedure.equals("empty")) {
            if (this.isInWaterOrBubble()) {
                if (this.hasEffect(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("benssharks", "frenzy")))) {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("sprint"));
                } else {
                	return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
                }
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("land"));
        }
        return PlayState.STOP;
    }
    
    // ... (Boilerplate unchanged) ...
    public void setTexture(String texture) { this.entityData.set(TEXTURE, texture); }
    public String getTexture() { return this.entityData.get(TEXTURE); }
    public String getSyncedAnimation() { return this.entityData.get(ANIMATION); }
    public void setAnimation(String animation) { this.entityData.set(ANIMATION, animation); }

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
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(AngelsharkEntity.RemovalReason.KILLED);
            this.dropExperience();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }
}