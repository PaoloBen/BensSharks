
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.sharks.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.sharks.BenssharksMod;

public class BenssharksModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BenssharksMod.MODID);
	public static final RegistryObject<SoundEvent> AXODILE_BITE = REGISTRY.register("axodile.bite", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("benssharks", "axodile.bite")));
	public static final RegistryObject<SoundEvent> SHARK_CHOMP = REGISTRY.register("shark.chomp", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("benssharks", "shark.chomp")));
	public static final RegistryObject<SoundEvent> SQUEAK = REGISTRY.register("squeak", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("benssharks", "squeak")));
	public static final RegistryObject<SoundEvent> CLUB_HIT = REGISTRY.register("club_hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("benssharks", "club_hit")));
	public static final RegistryObject<SoundEvent> SEA_LION_AMBIENT = REGISTRY.register("sea_lion.ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("benssharks", "sea_lion.ambient")));
	public static final RegistryObject<SoundEvent> SEA_LION_HURT = REGISTRY.register("sea_lion.hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("benssharks", "sea_lion.hurt")));
	public static final RegistryObject<SoundEvent> SEA_LION_DEATH = REGISTRY.register("sea_lion.death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("benssharks", "sea_lion.death")));
}
