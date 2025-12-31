package net.mcreator.sharks.entity.ai;

import net.mcreator.sharks.entity.AngelsharkEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.EnumSet;
import java.util.List;

public class AngelsharkScavengeGoal extends Goal {
    private final AngelsharkEntity shark;
    private BlockPos targetBlockPos;
    private int excavateTimer;
    private int searchCooldown; 
    private boolean hasArrived;

    private static final int DIG_DURATION = 100; // 5 seconds
    private static final int SEARCH_RADIUS = 32; 

    public AngelsharkScavengeGoal(AngelsharkEntity shark) {
        this.shark = shark;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.shark.isDigging();
    }

    @Override
    public boolean canContinueToUse() {
        return this.shark.isDigging() && this.excavateTimer < DIG_DURATION + 40;
    }

    @Override
    public void start() {
        this.excavateTimer = 0;
        this.searchCooldown = 0;
        this.targetBlockPos = null;
        this.hasArrived = false;
        
        // NEW: Clear animation state to start fresh
        this.shark.setAnimation("empty");
        
        if (ForgeRegistries.MOB_EFFECTS.containsKey(new ResourceLocation("benssharks", "frenzy"))) {
             this.shark.addEffect(new MobEffectInstance(
                 ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("benssharks", "frenzy")), 
                 400, 1, true, false
             ));
        }
    }

    @Override
    public void stop() {
        this.shark.setDigging(false);
        this.shark.setAnimation("empty");
        this.targetBlockPos = null;
        this.shark.getNavigation().stop();
        
        if (ForgeRegistries.MOB_EFFECTS.containsKey(new ResourceLocation("benssharks", "frenzy"))) {
            this.shark.removeEffect(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("benssharks", "frenzy")));
        }
    }

    @Override
    public void tick() {
        // --- PHASE 1: Find Target ---
        if (this.targetBlockPos == null) {
            if (this.searchCooldown <= 0) {
                this.targetBlockPos = findNearestSandOrGravel();
                this.searchCooldown = 20; 
                
                if (this.targetBlockPos != null) {
                    this.shark.getNavigation().moveTo(this.targetBlockPos.getX() + 0.5, this.targetBlockPos.getY() + 1, this.targetBlockPos.getZ() + 0.5, 1.2D);
                }
            } else {
                this.searchCooldown--;
            }
            return;
        }

        // --- PHASE 2: Check Arrival ---
        if (!hasArrived) {
            // NEW: Constantly set the animation to burrow while moving toward the goal
            this.shark.setAnimation("sprint");

            BlockPos posAt = this.shark.blockPosition();
            BlockPos posBelow = posAt.below();
            
            boolean sandAt = isSandOrGravel(this.shark.level().getBlockState(posAt));
            boolean sandBelow = isSandOrGravel(this.shark.level().getBlockState(posBelow));
            boolean closeEnoughY = Math.abs(this.shark.getY() - this.targetBlockPos.getY()) < 1.5;
    
            if ((sandAt || sandBelow) && closeEnoughY) {
                this.hasArrived = true;
                
                // NEW: Clear forced animation so the Entity's own burrow logic takes over for digging
                this.shark.setAnimation("empty");

                if (ForgeRegistries.MOB_EFFECTS.containsKey(new ResourceLocation("benssharks", "frenzy"))) {
                    this.shark.removeEffect(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("benssharks", "frenzy")));
                }
            }
        }

        // --- PHASE 3: Move or Dig ---
        if (!this.hasArrived) {
            if (this.shark.getNavigation().isDone() && this.shark.distanceToSqr(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ()) > 4.0D) {
                this.shark.getNavigation().moveTo(this.targetBlockPos.getX() + 0.5, this.targetBlockPos.getY() + 1, this.targetBlockPos.getZ() + 0.5, 1.2D);
            }
        } else {
            this.shark.getNavigation().stop();
            this.shark.setDeltaMovement(0, -0.05, 0); 
            
            this.excavateTimer++;
            
            spawnDiggingParticles();
            
            if (this.excavateTimer % 5 == 0) {
                this.shark.level().playSound(null, this.shark.blockPosition(), SoundEvents.BRUSH_SAND, SoundSource.NEUTRAL, 1.0f, 1.0f);
            }

            if (this.excavateTimer >= DIG_DURATION) {
                spawnLoot();
                this.stop(); 
            }
        }
    }

    private void spawnDiggingParticles() {
        if (this.shark.level().isClientSide) return; 
        
        BlockPos pos = this.shark.blockPosition();
        BlockState state = this.shark.level().getBlockState(pos);
        
        // Scan down 1 block max
        if (!isSandOrGravel(state)) {
            pos = pos.below();
            state = this.shark.level().getBlockState(pos);
        }

        if (isSandOrGravel(state) && this.shark.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                    this.shark.getX(), this.shark.getY() + 0.1, this.shark.getZ(),
                    5, 0.4, 0.1, 0.4, 0.05);
        }
    }

    private void spawnLoot() {
        if (this.shark.level().isClientSide) return;
        
        ServerLevel serverLevel = (ServerLevel) this.shark.level();
        LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY);
        
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, this.shark.position())
                .withParameter(LootContextParams.THIS_ENTITY, this.shark)
                .create(LootContextParamSets.CHEST);

        List<ItemStack> items = lootTable.getRandomItems(params);

        for (ItemStack stack : items) {
            if (stack.isEdible()) continue; 
            ItemEntity itemEntity = new ItemEntity(serverLevel, this.shark.getX(), this.shark.getY() + 0.5, this.shark.getZ(), stack);
            serverLevel.addFreshEntity(itemEntity);
        }
    }

    private BlockPos findNearestSandOrGravel() {
        BlockPos entityPos = this.shark.blockPosition();
        BlockPos nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(
                entityPos.offset(-SEARCH_RADIUS, -5, -SEARCH_RADIUS),
                entityPos.offset(SEARCH_RADIUS, 2, SEARCH_RADIUS))) {
            
            BlockState state = this.shark.level().getBlockState(pos);
            if (isSandOrGravel(state)) {
                if (this.shark.level().getBlockState(pos.above()).is(Blocks.WATER)) {
                    double dist = pos.distSqr(entityPos);
                    if (dist < minDistance) {
                        minDistance = dist;
                        nearest = pos.immutable();
                    }
                }
            }
        }
        return nearest;
    }

    private boolean isSandOrGravel(BlockState state) {
        return state.is(Blocks.SAND) || state.is(Blocks.GRAVEL) || 
               state.is(Blocks.RED_SAND) || state.is(Blocks.SUSPICIOUS_SAND) || 
               state.is(Blocks.SUSPICIOUS_GRAVEL);
    }
}