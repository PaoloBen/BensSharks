package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.init.BenssharksModItems;

public class MaelstromBowRangedItemShootsProjectileProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BenssharksModItems.MAELSTROM_BOW.get()
				&& !(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)) != 0)
				|| (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == BenssharksModItems.MAELSTROM_BOW.get()
						&& !(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY)) != 0)) {
			if (!(entity instanceof Player _plr ? _plr.getAbilities().instabuild : false)) {
				if (entity instanceof Player _playerHasItem ? _playerHasItem.getInventory().contains(new ItemStack(Items.ARROW)) : false) {
					if (entity instanceof Player _player) {
						ItemStack _stktoremove = new ItemStack(Items.ARROW);
						_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
					}
				} else if (entity instanceof Player _playerHasItem ? _playerHasItem.getInventory().contains(new ItemStack(Items.SPECTRAL_ARROW)) : false) {
					if (entity instanceof Player _player) {
						ItemStack _stktoremove = new ItemStack(Items.SPECTRAL_ARROW);
						_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
					}
				} else if (entity instanceof Player _playerHasItem ? _playerHasItem.getInventory().contains(new ItemStack(Items.TIPPED_ARROW)) : false) {
					if (entity instanceof Player _player) {
						ItemStack _stktoremove = new ItemStack(Items.TIPPED_ARROW);
						_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
					}
				}
			}
		}
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BenssharksModItems.MAELSTROM_BOW.get()
				&& EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)) != 0
				|| (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == BenssharksModItems.MAELSTROM_BOW.get()
						&& EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY)) != 0) {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.FLAME, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 5, 0.25, 0.25, 0.25, 0.25);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.FLAME, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 5, 1, 1, 1, 0.5);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.FLASH, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 1, 0, 0, 0, 0.075);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.shoot")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.shoot")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.firework_rocket.large_blast")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.firework_rocket.large_blast")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.elder_guardian.curse")), SoundSource.NEUTRAL, (float) 0.25, 2);
				} else {
					_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.elder_guardian.curse")), SoundSource.NEUTRAL, (float) 0.25, 2, false);
				}
			}
		} else {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.GLOW_SQUID_INK, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 3, 0.25, 0.25, 0.25, 0.25);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.GLOW_SQUID_INK, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 3, 1, 1, 1, 0.5);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.GLOW_SQUID_INK, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 1, 0, 0, 0, 0.075);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.conduit.ambient.short")), SoundSource.NEUTRAL, 2, 1);
				} else {
					_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.conduit.ambient.short")), SoundSource.NEUTRAL, 2, 1, false);
				}
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.elder_guardian.curse")), SoundSource.NEUTRAL, (float) 0.25, 2);
				} else {
					_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.elder_guardian.curse")), SoundSource.NEUTRAL, (float) 0.25, 2, false);
				}
			}
		}
	}
}
