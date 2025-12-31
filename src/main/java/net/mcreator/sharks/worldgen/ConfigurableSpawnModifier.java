package net.mcreator.sharks.worldgen;

import com.mojang.serialization.Codec;
import net.mcreator.sharks.configuration.SpawnsConfiguration;
import net.mcreator.sharks.init.BenssharksModEntities;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigurableSpawnModifier implements BiomeModifier {

    public static final Codec<ConfigurableSpawnModifier> CODEC = Codec.unit(ConfigurableSpawnModifier::new);

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) {
            return;
        }

        String biomeName = biome.unwrapKey()
                .map(ResourceKey::location)
                .map(ResourceLocation::getPath)
                .orElse(null);

        if (biomeName == null) {
            return;
        }

        // --- DYNAMIC SPAWN REGISTRATION ---
        // We no longer pass MobCategory manually. The helper method extracts it from the EntityType.

        // Angelshark
        addSpawn(builder, biomeName, SpawnsConfiguration.ANGELSHARK_BIOMES.get(),
                BenssharksModEntities.ANGELSHARK.get(), SpawnsConfiguration.ANGELSHARK_WEIGHT.get(),
                SpawnsConfiguration.ANGELSHARK_MIN_SIZE.get(), SpawnsConfiguration.ANGELSHARK_MAX_SIZE.get(), SpawnsConfiguration.ANGELSHARK_ENABLED.get());
        
        // Axodile
        addSpawn(builder, biomeName, SpawnsConfiguration.AXODILE_BIOMES.get(),
                BenssharksModEntities.AXODILE.get(), SpawnsConfiguration.AXODILE_WEIGHT.get(),
                SpawnsConfiguration.AXODILE_MIN_SIZE.get(), SpawnsConfiguration.AXODILE_MAX_SIZE.get(), SpawnsConfiguration.AXODILE_ENABLED.get());

        // Barracuda
        addSpawn(builder, biomeName, SpawnsConfiguration.BARRACUDA_BIOMES.get(),
                BenssharksModEntities.BARRACUDA.get(), SpawnsConfiguration.BARRACUDA_WEIGHT.get(),
                SpawnsConfiguration.BARRACUDA_MIN_SIZE.get(), SpawnsConfiguration.BARRACUDA_MAX_SIZE.get(), SpawnsConfiguration.BARRACUDA_ENABLED.get());

        // Basking Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.BASKING_SHARK_BIOMES.get(),
                BenssharksModEntities.BASKING_SHARK.get(), SpawnsConfiguration.BASKING_SHARK_WEIGHT.get(),
                SpawnsConfiguration.BASKING_SHARK_MIN_SIZE.get(), SpawnsConfiguration.BASKING_SHARK_MAX_SIZE.get(), SpawnsConfiguration.BASKING_SHARK_ENABLED.get());

        // Blacktip Reef Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.BLACKTIP_REEF_SHARK_BIOMES.get(),
                BenssharksModEntities.BLACKTIP_REEF_SHARK.get(), SpawnsConfiguration.BLACKTIP_REEF_SHARK_WEIGHT.get(),
                SpawnsConfiguration.BLACKTIP_REEF_SHARK_MIN_SIZE.get(), SpawnsConfiguration.BLACKTIP_REEF_SHARK_MAX_SIZE.get(), SpawnsConfiguration.BLACKTIP_REEF_SHARK_ENABLED.get());

        // Blue Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.BLUE_SHARK_BIOMES.get(),
                BenssharksModEntities.BLUE_SHARK.get(), SpawnsConfiguration.BLUE_SHARK_WEIGHT.get(),
                SpawnsConfiguration.BLUE_SHARK_MIN_SIZE.get(), SpawnsConfiguration.BLUE_SHARK_MAX_SIZE.get(), SpawnsConfiguration.BLUE_SHARK_ENABLED.get());

        // Bonnethead Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.BONNETHEAD_SHARK_BIOMES.get(),
                BenssharksModEntities.BONNETHEAD_SHARK.get(), SpawnsConfiguration.BONNETHEAD_SHARK_WEIGHT.get(),
                SpawnsConfiguration.BONNETHEAD_SHARK_MIN_SIZE.get(), SpawnsConfiguration.BONNETHEAD_SHARK_MAX_SIZE.get(), SpawnsConfiguration.BONNETHEAD_SHARK_ENABLED.get());

        // Bull Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.BULL_SHARK_BIOMES.get(),
                BenssharksModEntities.BULL_SHARK.get(), SpawnsConfiguration.BULL_SHARK_WEIGHT.get(),
                SpawnsConfiguration.BULL_SHARK_MIN_SIZE.get(), SpawnsConfiguration.BULL_SHARK_MAX_SIZE.get(), SpawnsConfiguration.BULL_SHARK_ENABLED.get());

        // Cookiecutter Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.COOKIECUTTER_SHARK_BIOMES.get(),
                BenssharksModEntities.COOKIECUTTER_SHARK.get(), SpawnsConfiguration.COOKIECUTTER_SHARK_WEIGHT.get(),
                SpawnsConfiguration.COOKIECUTTER_SHARK_MIN_SIZE.get(), SpawnsConfiguration.COOKIECUTTER_SHARK_MAX_SIZE.get(), SpawnsConfiguration.COOKIECUTTER_SHARK_ENABLED.get());

        // Goblin Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.GOBLIN_SHARK_BIOMES.get(),
                BenssharksModEntities.GOBLIN_SHARK.get(), SpawnsConfiguration.GOBLIN_SHARK_WEIGHT.get(),
                SpawnsConfiguration.GOBLIN_SHARK_MIN_SIZE.get(), SpawnsConfiguration.GOBLIN_SHARK_MAX_SIZE.get(), SpawnsConfiguration.GOBLIN_SHARK_ENABLED.get());

        // Greater Axodile
        addSpawn(builder, biomeName, SpawnsConfiguration.GREATER_AXODILE_BIOMES.get(),
                BenssharksModEntities.GREATER_AXODILE.get(), SpawnsConfiguration.GREATER_AXODILE_WEIGHT.get(),
                SpawnsConfiguration.GREATER_AXODILE_MIN_SIZE.get(), SpawnsConfiguration.GREATER_AXODILE_MAX_SIZE.get(), SpawnsConfiguration.GREATER_AXODILE_ENABLED.get());

        // Great White Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.GREATWHITE_SHARK_BIOMES.get(),
                BenssharksModEntities.GREATWHITE_SHARK.get(), SpawnsConfiguration.GREATWHITE_SHARK_WEIGHT.get(),
                SpawnsConfiguration.GREATWHITE_SHARK_MIN_SIZE.get(), SpawnsConfiguration.GREATWHITE_SHARK_MAX_SIZE.get(), SpawnsConfiguration.GREATWHITE_SHARK_ENABLED.get());

        // Greenland Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.GREENLAND_SHARK_BIOMES.get(),
                BenssharksModEntities.GREENLAND_SHARK.get(), SpawnsConfiguration.GREENLAND_SHARK_WEIGHT.get(),
                SpawnsConfiguration.GREENLAND_SHARK_MIN_SIZE.get(), SpawnsConfiguration.GREENLAND_SHARK_MAX_SIZE.get(), SpawnsConfiguration.GREENLAND_SHARK_ENABLED.get());

        // Krill
        addSpawn(builder, biomeName, SpawnsConfiguration.KRILL_BIOMES.get(),
                BenssharksModEntities.KRILL.get(), SpawnsConfiguration.KRILL_WEIGHT.get(),
                SpawnsConfiguration.KRILL_MIN_SIZE.get(), SpawnsConfiguration.KRILL_MAX_SIZE.get(), SpawnsConfiguration.KRILL_ENABLED.get());

        // Lemon Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.LEMON_SHARK_BIOMES.get(),
                BenssharksModEntities.LEMON_SHARK.get(), SpawnsConfiguration.LEMON_SHARK_WEIGHT.get(),
                SpawnsConfiguration.LEMON_SHARK_MIN_SIZE.get(), SpawnsConfiguration.LEMON_SHARK_MAX_SIZE.get(), SpawnsConfiguration.LEMON_SHARK_ENABLED.get());

        // Leopard Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.LEOPARD_SHARK_BIOMES.get(),
                BenssharksModEntities.LEOPARD_SHARK.get(), SpawnsConfiguration.LEOPARD_SHARK_WEIGHT.get(),
                SpawnsConfiguration.LEOPARD_SHARK_MIN_SIZE.get(), SpawnsConfiguration.LEOPARD_SHARK_MAX_SIZE.get(), SpawnsConfiguration.LEOPARD_SHARK_ENABLED.get());

        // Mako Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.MAKO_SHARK_BIOMES.get(),
                BenssharksModEntities.MAKO_SHARK.get(), SpawnsConfiguration.MAKO_SHARK_WEIGHT.get(),
                SpawnsConfiguration.MAKO_SHARK_MIN_SIZE.get(), SpawnsConfiguration.MAKO_SHARK_MAX_SIZE.get(), SpawnsConfiguration.MAKO_SHARK_ENABLED.get());

        // Megalodon
        addSpawn(builder, biomeName, SpawnsConfiguration.MEGALODON_BIOMES.get(),
                BenssharksModEntities.MEGALODON.get(), SpawnsConfiguration.MEGALODON_WEIGHT.get(),
                SpawnsConfiguration.MEGALODON_MIN_SIZE.get(), SpawnsConfiguration.MEGALODON_MAX_SIZE.get(), SpawnsConfiguration.MEGALODON_ENABLED.get());

        // Megamouth Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.MEGAMOUTH_SHARK_BIOMES.get(),
                BenssharksModEntities.MEGAMOUTH_SHARK.get(), SpawnsConfiguration.MEGAMOUTH_SHARK_WEIGHT.get(),
                SpawnsConfiguration.MEGAMOUTH_SHARK_MIN_SIZE.get(), SpawnsConfiguration.MEGAMOUTH_SHARK_MAX_SIZE.get(), SpawnsConfiguration.MEGAMOUTH_SHARK_ENABLED.get());

        // Nurse Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.NURSE_SHARK_BIOMES.get(),
                BenssharksModEntities.NURSE_SHARK.get(), SpawnsConfiguration.NURSE_SHARK_WEIGHT.get(),
                SpawnsConfiguration.NURSE_SHARK_MIN_SIZE.get(), SpawnsConfiguration.NURSE_SHARK_MAX_SIZE.get(), SpawnsConfiguration.NURSE_SHARK_ENABLED.get());

        // Pilot Fish
        addSpawn(builder, biomeName, SpawnsConfiguration.PILOT_FISH_BIOMES.get(),
                BenssharksModEntities.PILOT_FISH.get(), SpawnsConfiguration.PILOT_FISH_WEIGHT.get(),
                SpawnsConfiguration.PILOT_FISH_MIN_SIZE.get(), SpawnsConfiguration.PILOT_FISH_MAX_SIZE.get(), SpawnsConfiguration.PILOT_FISH_ENABLED.get());

        // Remora
        addSpawn(builder, biomeName, SpawnsConfiguration.REMORA_BIOMES.get(),
                BenssharksModEntities.REMORA.get(), SpawnsConfiguration.REMORA_WEIGHT.get(),
                SpawnsConfiguration.REMORA_MIN_SIZE.get(), SpawnsConfiguration.REMORA_MAX_SIZE.get(), SpawnsConfiguration.REMORA_ENABLED.get());

        // Sardine
        addSpawn(builder, biomeName, SpawnsConfiguration.SARDINE_BIOMES.get(),
                BenssharksModEntities.SARDINE.get(), SpawnsConfiguration.SARDINE_WEIGHT.get(),
                SpawnsConfiguration.SARDINE_MIN_SIZE.get(), SpawnsConfiguration.SARDINE_MAX_SIZE.get(), SpawnsConfiguration.SARDINE_ENABLED.get());

        // Sawshark
        addSpawn(builder, biomeName, SpawnsConfiguration.SAWSHARK_BIOMES.get(),
                BenssharksModEntities.SAWSHARK.get(), SpawnsConfiguration.SAWSHARK_WEIGHT.get(),
                SpawnsConfiguration.SAWSHARK_MIN_SIZE.get(), SpawnsConfiguration.SAWSHARK_MAX_SIZE.get(), SpawnsConfiguration.SAWSHARK_ENABLED.get());

        // Sea Lion
        addSpawn(builder, biomeName, SpawnsConfiguration.SEA_LION_BIOMES.get(),
                BenssharksModEntities.SEA_LION.get(), SpawnsConfiguration.SEA_LION_WEIGHT.get(),
                SpawnsConfiguration.SEA_LION_MIN_SIZE.get(), SpawnsConfiguration.SEA_LION_MAX_SIZE.get(), SpawnsConfiguration.SEA_LION_ENABLED.get());

        // Shoal
        addSpawn(builder, biomeName, SpawnsConfiguration.SHOAL_BIOMES.get(),
                BenssharksModEntities.SHOAL.get(), SpawnsConfiguration.SHOAL_WEIGHT.get(),
                SpawnsConfiguration.SHOAL_MIN_SIZE.get(), SpawnsConfiguration.SHOAL_MAX_SIZE.get(), SpawnsConfiguration.SHOAL_ENABLED.get());

        // Thresher Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.THRESHER_SHARK_BIOMES.get(),
                BenssharksModEntities.THRESHER_SHARK.get(), SpawnsConfiguration.THRESHER_SHARK_WEIGHT.get(),
                SpawnsConfiguration.THRESHER_SHARK_MIN_SIZE.get(), SpawnsConfiguration.THRESHER_SHARK_MAX_SIZE.get(), SpawnsConfiguration.THRESHER_SHARK_ENABLED.get());

        // Tiger Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.TIGER_SHARK_BIOMES.get(),
                BenssharksModEntities.TIGER_SHARK.get(), SpawnsConfiguration.TIGER_SHARK_WEIGHT.get(),
                SpawnsConfiguration.TIGER_SHARK_MIN_SIZE.get(), SpawnsConfiguration.TIGER_SHARK_MAX_SIZE.get(), SpawnsConfiguration.TIGER_SHARK_ENABLED.get());

        // Whale Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.WHALE_SHARK_BIOMES.get(),
                BenssharksModEntities.WHALE_SHARK.get(), SpawnsConfiguration.WHALE_SHARK_WEIGHT.get(),
                SpawnsConfiguration.WHALE_SHARK_MIN_SIZE.get(), SpawnsConfiguration.WHALE_SHARK_MAX_SIZE.get(), SpawnsConfiguration.WHALE_SHARK_ENABLED.get());

        // Whitetip Shark
        addSpawn(builder, biomeName, SpawnsConfiguration.WHITETIP_SHARK_BIOMES.get(),
                BenssharksModEntities.WHITETIP_SHARK.get(), SpawnsConfiguration.WHITETIP_SHARK_WEIGHT.get(),
                SpawnsConfiguration.WHITETIP_SHARK_MIN_SIZE.get(), SpawnsConfiguration.WHITETIP_SHARK_MAX_SIZE.get(), SpawnsConfiguration.WHITETIP_SHARK_ENABLED.get());
    }

    // UPDATED HELPER METHOD: Automatically fetches category from the entityType
    private static void addSpawn(ModifiableBiomeInfo.BiomeInfo.Builder builder, String currentBiomeName,
                                 String configuredBiomeList,
                                 EntityType<?> entityType, // Removed MobCategory param
                                 int weight, int min, int max, boolean enabled) {

        if (!enabled || weight <= 0) {
            return;
        }

        // --- DYNAMICALLY GET CATEGORY ---
        // This links the spawning logic to the entity file's registered category.
        MobCategory category = entityType.getCategory();

        Set<String> biomes = new HashSet<>(Arrays.asList(configuredBiomeList.replace(" ", "").split(",")));

        if (biomes.contains("ALL_BIOMES") || biomes.contains(currentBiomeName)) {
            builder.getMobSpawnSettings().addSpawn(category,
                    new MobSpawnSettings.SpawnerData(entityType, weight, min, max));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}