package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.entity.ThresherSharkEntity;
import net.mcreator.sharks.BenssharksMod;

public class ThresherSharkRightClickedOnEntityProcedure {
	public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return InteractionResult.PASS;
		if (sourceentity instanceof Player && entity instanceof ThresherSharkEntity) {
			if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BenssharksModItems.FISH_BUCKET.get()) {
				if (!entity.getPersistentData().getBoolean("canBeMilked")) {
					if (entity instanceof LivingEntity _entity)
						_entity.swing(InteractionHand.MAIN_HAND, true);
					if (sourceentity instanceof LivingEntity _entity)
						_entity.swing(InteractionHand.MAIN_HAND, true);
					if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getCount() > 0 && !(sourceentity instanceof Player _plr ? _plr.getAbilities().instabuild : false)) {
						if (sourceentity instanceof Player _player) {
							ItemStack _stktoremove = new ItemStack(BenssharksModItems.FISH_BUCKET.get());
							_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
						}
						if (sourceentity instanceof Player _player) {
							ItemStack _setstack = new ItemStack(Items.BUCKET).copy();
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
						}
					}
					entity.getPersistentData().putBoolean("canBeMilked", true);// 1. ADD 3 CHARGES TO VARIABLE
					{
						double _setval = (sourceentity.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null)
								.orElse(new net.mcreator.sharks.network.BenssharksModVariables.PlayerVariables())).ThresherRiptideCharges + 3;
						sourceentity.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.ThresherRiptideCharges = _setval;
							capability.syncPlayerVariables(sourceentity);
						});
					}
					// 2. VISUALS
					if (world instanceof net.minecraft.server.level.ServerLevel _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, sourceentity.blockPosition(), net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS.getValue(new net.minecraft.resources.ResourceLocation("block.conduit.activate")),
									net.minecraft.sounds.SoundSource.PLAYERS, 1, 1);
						}
						for (int i = 0; i < 20; i++) {
							double offsetX = (Math.random() - 0.5) * 3.0;
							double offsetY = (Math.random() - 0.5) * 3.0 + 1.0;
							double offsetZ = (Math.random() - 0.5) * 3.0;
							_level.sendParticles(net.minecraft.core.particles.ParticleTypes.NAUTILUS, sourceentity.getX() + offsetX, sourceentity.getY() + offsetY, sourceentity.getZ() + offsetZ, 0, -offsetX * 0.1, -offsetY * 0.1, -offsetZ * 0.1,
									1.0);
						}
					}
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.bite")), SoundSource.NEUTRAL, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.bite")), SoundSource.NEUTRAL, 1, 1, false);
						}
					}
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1, true, false));
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.CRIT, x, y, z, 5, 1, 1, 1, 1);
					BenssharksMod.queueServerWork(20, () -> {
						entity.getPersistentData().putBoolean("canBeMilked", false);
					});
				}
			} else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == BenssharksModItems.FISH_BUCKET.get()) {
				if (!entity.getPersistentData().getBoolean("canBeMilked")) {
					if (entity instanceof LivingEntity _entity)
						_entity.swing(InteractionHand.MAIN_HAND, true);
					if (sourceentity instanceof LivingEntity _entity)
						_entity.swing(InteractionHand.OFF_HAND, true);
					if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getCount() > 0 && !(sourceentity instanceof Player _plr ? _plr.getAbilities().instabuild : false)) {
						if (sourceentity instanceof Player _player) {
							ItemStack _stktoremove = new ItemStack(BenssharksModItems.FISH_BUCKET.get());
							_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
						}
						if (sourceentity instanceof Player _player) {
							ItemStack _setstack = new ItemStack(Items.BUCKET).copy();
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
						}
					}
					entity.getPersistentData().putBoolean("canBeMilked", true);// 1. ADD 3 CHARGES TO VARIABLE
					{
						double _setval = (sourceentity.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null)
								.orElse(new net.mcreator.sharks.network.BenssharksModVariables.PlayerVariables())).ThresherRiptideCharges + 3;
						sourceentity.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.ThresherRiptideCharges = _setval;
							capability.syncPlayerVariables(sourceentity);
						});
					}
					// 2. VISUALS
					if (world instanceof net.minecraft.server.level.ServerLevel _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, sourceentity.blockPosition(), net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS.getValue(new net.minecraft.resources.ResourceLocation("block.conduit.activate")),
									net.minecraft.sounds.SoundSource.PLAYERS, 1, 1);
						}
						for (int i = 0; i < 20; i++) {
							double offsetX = (Math.random() - 0.5) * 3.0;
							double offsetY = (Math.random() - 0.5) * 3.0 + 1.0;
							double offsetZ = (Math.random() - 0.5) * 3.0;
							_level.sendParticles(net.minecraft.core.particles.ParticleTypes.NAUTILUS, sourceentity.getX() + offsetX, sourceentity.getY() + offsetY, sourceentity.getZ() + offsetZ, 0, -offsetX * 0.1, -offsetY * 0.1, -offsetZ * 0.1,
									1.0);
						}
					}
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.bite")), SoundSource.NEUTRAL, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.bite")), SoundSource.NEUTRAL, 1, 1, false);
						}
					}
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1, true, false));
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.CRIT, x, y, z, 5, 1, 1, 1, 1);
					BenssharksMod.queueServerWork(20, () -> {
						entity.getPersistentData().putBoolean("canBeMilked", false);
					});
				}
			}
		}
		return InteractionResult.PASS;
	}
}
