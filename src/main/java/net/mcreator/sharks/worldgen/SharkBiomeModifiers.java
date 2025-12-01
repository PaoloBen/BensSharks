package net.mcreator.sharks.worldgen;

import com.mojang.serialization.Codec;
import net.mcreator.sharks.BenssharksMod;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SharkBiomeModifiers {
    // Create a DeferredRegister for Biome Modifier Serializers
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, BenssharksMod.MODID);

    // Register our new modifier's serializer
    public static final RegistryObject<Codec<ConfigurableSpawnModifier>> CONFIGURABLE_SPAWNS =
            BIOME_MODIFIER_SERIALIZERS.register("configurable_spawns", () -> ConfigurableSpawnModifier.CODEC);

    // Helper method to register this DeferredRegister to the mod event bus
    public static void register(IEventBus eventBus) {
        BIOME_MODIFIER_SERIALIZERS.register(eventBus);
    }
}