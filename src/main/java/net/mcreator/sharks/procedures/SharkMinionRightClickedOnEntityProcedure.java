package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.AnimalTameEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.entity.SharkMinionEntity;
import net.mcreator.sharks.BenssharksMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class SharkMinionRightClickedOnEntityProcedure {
	@SubscribeEvent
	public static void onEntityTamed(AnimalTameEvent event) {
		execute(event, event.getAnimal().level(), event.getAnimal(), event.getTamer());
	}

	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
		execute(null, world, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		boolean Sitting = false;
		if (entity instanceof TamableAnimal _tamIsTamedBy && sourceentity instanceof LivingEntity _livEnt ? _tamIsTamedBy.isOwnedBy(_livEnt) : false) {
			if (!((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.BUCKET
					|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.BUCKET)) {
				if (sourceentity instanceof LivingEntity _entity)
					_entity.swing(InteractionHand.MAIN_HAND, true);
				if ((entity instanceof SharkMinionEntity _datEntL6 && _datEntL6.getEntityData().get(SharkMinionEntity.DATA_Sitting)) == true) {
					if (entity instanceof SharkMinionEntity _datEntSetL)
						_datEntSetL.getEntityData().set(SharkMinionEntity.DATA_Sitting, false);
				} else if ((entity instanceof SharkMinionEntity _datEntL8 && _datEntL8.getEntityData().get(SharkMinionEntity.DATA_Sitting)) == false) {
					if (entity instanceof SharkMinionEntity _datEntSetL)
						_datEntSetL.getEntityData().set(SharkMinionEntity.DATA_Sitting, true);
				}
			}
		}
		if (sourceentity instanceof Player && entity instanceof SharkMinionEntity && (entity instanceof TamableAnimal _tamIsTamedBy && sourceentity instanceof LivingEntity _livEnt ? _tamIsTamedBy.isOwnedBy(_livEnt) : false)) {
			if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.BUCKET) {
				if (entity.getPersistentData().getDouble("SharkStorage") > 0) {
					if (!entity.getPersistentData().getBoolean("canBeMilked")) {
						if (entity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.MAIN_HAND, true);
						if (sourceentity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.MAIN_HAND, true);
						if (sourceentity instanceof Player _player) {
							ItemStack _stktoremove = new ItemStack(Items.BUCKET);
							_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
						}
						entity.getPersistentData().putBoolean("canBeMilked", true);
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1);
							} else {
								_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1, false);
							}
						}
						entity.getPersistentData().putDouble("Timer", 60);
						if (entity.getPersistentData().getDouble("Timer") > 40) {
							BenssharksMod.queueServerWork(60, () -> {
								if (entity instanceof LivingEntity _entity)
									_entity.swing(InteractionHand.MAIN_HAND, true);
								if (world instanceof Level _level) {
									if (!_level.isClientSide()) {
										_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.burp")), SoundSource.NEUTRAL, 1, 1);
									} else {
										_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.burp")), SoundSource.NEUTRAL, 1, 1, false);
									}
								}
								entity.getPersistentData().putDouble("SharkStorage", (entity.getPersistentData().getDouble("SharkStorage") - 1));
								if (world instanceof ServerLevel _level) {
									ItemEntity entityToSpawn = new ItemEntity(_level, (entity.getX()), (entity.getY()), (entity.getZ()), new ItemStack(BenssharksModItems.FISH_BUCKET.get()));
									entityToSpawn.setPickUpDelay(10);
									_level.addFreshEntity(entityToSpawn);
								}
								entity.getPersistentData().putBoolean("canBeMilked", false);
							});
						}
					}
				} else if (entity.getPersistentData().getDouble("SharkStorage") < 1) {
					if (!entity.getPersistentData().getBoolean("canBeMilked")) {
						if (entity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.MAIN_HAND, true);
						if (sourceentity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.MAIN_HAND, true);
						if (sourceentity instanceof Player _player) {
							ItemStack _stktoremove = new ItemStack(Items.BUCKET);
							_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
						}
						entity.getPersistentData().putBoolean("canBeMilked", true);
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1);
							} else {
								_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1, false);
							}
						}
						BenssharksMod.queueServerWork(60, () -> {
							if (entity instanceof LivingEntity _entity)
								_entity.swing(InteractionHand.MAIN_HAND, true);
							if (world instanceof Level _level) {
								if (!_level.isClientSide()) {
									_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.burp")), SoundSource.NEUTRAL, 1, 1);
								} else {
									_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.burp")), SoundSource.NEUTRAL, 1, 1, false);
								}
							}
							if (world instanceof ServerLevel _level) {
								ItemEntity entityToSpawn = new ItemEntity(_level, (entity.getX()), (entity.getY()), (entity.getZ()), new ItemStack(Items.BUCKET));
								entityToSpawn.setPickUpDelay(10);
								_level.addFreshEntity(entityToSpawn);
							}
							if (sourceentity instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("Throw it some Fish"), true);
							entity.getPersistentData().putBoolean("canBeMilked", false);
						});
					}
				}
			} else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.BUCKET) {
				if (entity.getPersistentData().getDouble("SharkStorage") > 0) {
					if (!entity.getPersistentData().getBoolean("canBeMilked")) {
						if (entity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.MAIN_HAND, true);
						if (sourceentity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.OFF_HAND, true);
						if (sourceentity instanceof Player _player) {
							ItemStack _stktoremove = new ItemStack(Items.BUCKET);
							_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
						}
						entity.getPersistentData().putBoolean("canBeMilked", true);
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1);
							} else {
								_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1, false);
							}
						}
						BenssharksMod.queueServerWork(60, () -> {
							if (entity instanceof LivingEntity _entity)
								_entity.swing(InteractionHand.MAIN_HAND, true);
							if (world instanceof Level _level) {
								if (!_level.isClientSide()) {
									_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.burp")), SoundSource.NEUTRAL, 1, 1);
								} else {
									_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.burp")), SoundSource.NEUTRAL, 1, 1, false);
								}
							}
							entity.getPersistentData().putDouble("SharkStorage", (entity.getPersistentData().getDouble("SharkStorage") - 1));
							if (world instanceof ServerLevel _level) {
								ItemEntity entityToSpawn = new ItemEntity(_level, (entity.getX()), (entity.getY()), (entity.getZ()), new ItemStack(BenssharksModItems.FISH_BUCKET.get()));
								entityToSpawn.setPickUpDelay(10);
								_level.addFreshEntity(entityToSpawn);
							}
							entity.getPersistentData().putBoolean("canBeMilked", false);
						});
					}
				} else if (entity.getPersistentData().getDouble("SharkStorage") < 1) {
					if (!entity.getPersistentData().getBoolean("canBeMilked")) {
						if (entity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.MAIN_HAND, true);
						if (sourceentity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.OFF_HAND, true);
						if (sourceentity instanceof Player _player) {
							ItemStack _stktoremove = new ItemStack(Items.BUCKET);
							_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
						}
						entity.getPersistentData().putBoolean("canBeMilked", true);
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1);
							} else {
								_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.eat")), SoundSource.NEUTRAL, 1, 1, false);
							}
						}
						BenssharksMod.queueServerWork(60, () -> {
							if (entity instanceof LivingEntity _entity)
								_entity.swing(InteractionHand.MAIN_HAND, true);
							if (world instanceof Level _level) {
								if (!_level.isClientSide()) {
									_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.burp")), SoundSource.NEUTRAL, 1, 1);
								} else {
									_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.burp")), SoundSource.NEUTRAL, 1, 1, false);
								}
							}
							if (world instanceof ServerLevel _level) {
								ItemEntity entityToSpawn = new ItemEntity(_level, (entity.getX()), (entity.getY()), (entity.getZ()), new ItemStack(Items.BUCKET));
								entityToSpawn.setPickUpDelay(10);
								_level.addFreshEntity(entityToSpawn);
							}
							if (sourceentity instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("Throw it some Fish"), true);
							entity.getPersistentData().putBoolean("canBeMilked", false);
						});
					}
				}
			}
			if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.BUCKET) {
				if (sourceentity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("Feeding on Cooldown"), true);
			} else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.BUCKET) {
				if (sourceentity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("Feeding on Cooldown"), true);
			}
		}
	}
}
