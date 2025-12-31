
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.sharks.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.enchantment.Enchantment;

import net.mcreator.sharks.enchantment.SerratedEnchantment;
import net.mcreator.sharks.enchantment.SeekingEnchantment;
import net.mcreator.sharks.BenssharksMod;

public class BenssharksModEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, BenssharksMod.MODID);
	public static final RegistryObject<Enchantment> SERRATED = REGISTRY.register("serrated", () -> new SerratedEnchantment());
	public static final RegistryObject<Enchantment> SEEKING = REGISTRY.register("seeking", () -> new SeekingEnchantment());
}
