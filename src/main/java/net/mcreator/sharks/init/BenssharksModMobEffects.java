
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.sharks.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

import net.mcreator.sharks.potion.VortexMobEffect;
import net.mcreator.sharks.potion.SealingPotionMobEffect;
import net.mcreator.sharks.potion.PreyMobEffect;
import net.mcreator.sharks.potion.ParasiteMobEffect;
import net.mcreator.sharks.potion.MegaMouthMobEffect;
import net.mcreator.sharks.potion.JawsMobEffect;
import net.mcreator.sharks.potion.FrenzyMobEffect;
import net.mcreator.sharks.potion.FertilizedMobEffect;
import net.mcreator.sharks.potion.DryoutEffectMobEffect;
import net.mcreator.sharks.potion.DetachedMobEffect;
import net.mcreator.sharks.potion.BleedingMobEffect;
import net.mcreator.sharks.BenssharksMod;

public class BenssharksModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BenssharksMod.MODID);
	public static final RegistryObject<MobEffect> BLEEDING = REGISTRY.register("bleeding", () -> new BleedingMobEffect());
	public static final RegistryObject<MobEffect> JAWS = REGISTRY.register("jaws", () -> new JawsMobEffect());
	public static final RegistryObject<MobEffect> DRYOUT_EFFECT = REGISTRY.register("dryout_effect", () -> new DryoutEffectMobEffect());
	public static final RegistryObject<MobEffect> SEALING = REGISTRY.register("sealing", () -> new SealingPotionMobEffect());
	public static final RegistryObject<MobEffect> PARASITE = REGISTRY.register("parasite", () -> new ParasiteMobEffect());
	public static final RegistryObject<MobEffect> DETACHED = REGISTRY.register("detached", () -> new DetachedMobEffect());
	public static final RegistryObject<MobEffect> PREY = REGISTRY.register("prey", () -> new PreyMobEffect());
	public static final RegistryObject<MobEffect> FRENZY = REGISTRY.register("frenzy", () -> new FrenzyMobEffect());
	public static final RegistryObject<MobEffect> FERTILIZED = REGISTRY.register("fertilized", () -> new FertilizedMobEffect());
	public static final RegistryObject<MobEffect> VORTEX = REGISTRY.register("vortex", () -> new VortexMobEffect());
	public static final RegistryObject<MobEffect> MEGA_MOUTH = REGISTRY.register("mega_mouth", () -> new MegaMouthMobEffect());
}
