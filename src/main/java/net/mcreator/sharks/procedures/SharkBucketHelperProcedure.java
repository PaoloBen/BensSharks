package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

public class SharkBucketHelperProcedure {

    public static InteractionResult catchFish(Entity entity, Player player, LevelAccessor world, Item resultBucketItem) {
        InteractionHand hand = null;
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.WATER_BUCKET) {
            hand = InteractionHand.MAIN_HAND;
        } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == Items.WATER_BUCKET) {
            hand = InteractionHand.OFF_HAND;
        }

        if (hand != null) {
            player.swing(hand, true);
            ItemStack resultStack = new ItemStack(resultBucketItem);
            ItemStack heldItem = player.getItemInHand(hand);

            if (entity.hasCustomName()) {
                resultStack.setHoverName(entity.getCustomName());
            }
            if (entity instanceof LivingEntity _livEnt) {
                resultStack.getOrCreateTag().putDouble("health", _livEnt.getHealth());
            }

            if (world instanceof Level _level) {
                BlockPos pos = entity.blockPosition();
                if (!_level.isClientSide()) {
                    _level.playSound(player, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bucket.fill_fish")), SoundSource.NEUTRAL, 1.0F, 1.0F);
                } else {
                    _level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bucket.fill_fish")), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
                }
            }

            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
                if (heldItem.isEmpty()) {
                    player.setItemInHand(hand, resultStack);
                } else {
                    if (!player.getInventory().add(resultStack)) {
                        player.drop(resultStack, false);
                    }
                }
            } else {
                if (!player.getInventory().contains(resultStack)) {
                    player.getInventory().add(resultStack);
                }
            }

            if (player instanceof ServerPlayer _player) {
                Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("minecraft:husbandry/tactical_fishing"));
                if (_adv != null) {
                    AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                    if (!_ap.isDone()) {
                        for (String criteria : _ap.getRemainingCriteria())
                            _player.getAdvancements().award(_adv, criteria);
                    }
                }
            }

            if (!entity.level().isClientSide()) {
                entity.discard();
            }

            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        return InteractionResult.PASS;
    }

    // UPDATED: Now returns Entity so we can modify it (e.g. set Tamed) after placement
    public static Entity placeFish(LevelAccessor world, double x, double y, double z, Direction direction, Entity entity, ItemStack itemstack, EntityType<?> entityType) {
        if (direction == null || entity == null || itemstack == null) return null;

        if (entity instanceof Player player) {
            InteractionHand usedHand = InteractionHand.MAIN_HAND;
            if (player.getItemInHand(InteractionHand.OFF_HAND) == itemstack) {
                usedHand = InteractionHand.OFF_HAND;
            }

            BlockPos clickedPos = BlockPos.containing(x, y, z);
            BlockPos targetPos = clickedPos.relative(direction);

            boolean canPlace = world.getBlockState(targetPos).canBeReplaced() || world.getBlockState(targetPos).getBlock() == Blocks.WATER;

            if (canPlace) {
                player.swing(usedHand, true);
                world.setBlock(targetPos, Blocks.WATER.defaultBlockState(), 3);

                if (world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(player, targetPos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bucket.empty_fish")), SoundSource.NEUTRAL, 1.0F, 1.0F);
                    } else {
                        _level.playLocalSound(targetPos.getX(), targetPos.getY(), targetPos.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bucket.empty_fish")), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
                    }
                }

                Entity spawnedEntity = null;
                if (!world.isClientSide() && world instanceof ServerLevel _level) {
                    spawnedEntity = entityType.spawn(_level, targetPos, MobSpawnType.BUCKET);
                    if (spawnedEntity instanceof LivingEntity living) {
                        living.setYRot(world.getRandom().nextFloat() * 360F);
                        
                        if (itemstack.hasCustomHoverName()) {
                            living.setCustomName(itemstack.getHoverName());
                        }
                        if (itemstack.hasTag() && itemstack.getTag().contains("health")) {
                            living.setHealth((float) itemstack.getTag().getDouble("health"));
                        }
                        if (spawnedEntity instanceof net.minecraft.world.entity.Mob mob) {
                             mob.setPersistenceRequired();
                        }
                    }
                }

                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                    if (itemstack.isEmpty()) {
                        player.setItemInHand(usedHand, new ItemStack(Items.BUCKET));
                    } else {
                        ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                        if (!player.getInventory().add(emptyBucket)) {
                            player.drop(emptyBucket, false);
                        }
                    }
                }
                return spawnedEntity;
            }
        }
        return null;
    }
}