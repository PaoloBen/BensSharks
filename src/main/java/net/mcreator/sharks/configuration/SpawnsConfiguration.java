package net.mcreator.sharks.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class SpawnsConfiguration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // --- Mob-Specific Configuration Entries (25 Mobs) ---
    
    // Axodile
    public static final ForgeConfigSpec.IntValue AXODILE_WEIGHT;
    public static final ForgeConfigSpec.IntValue AXODILE_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue AXODILE_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> AXODILE_BIOMES;
    
    // Barracuda
    public static final ForgeConfigSpec.IntValue BARRACUDA_WEIGHT;
    public static final ForgeConfigSpec.IntValue BARRACUDA_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BARRACUDA_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BARRACUDA_BIOMES;

    // Basking Shark
    public static final ForgeConfigSpec.IntValue BASKING_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue BASKING_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BASKING_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BASKING_SHARK_BIOMES;

    // Blacktip Reef Shark
    public static final ForgeConfigSpec.IntValue BLACKTIP_REEF_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue BLACKTIP_REEF_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BLACKTIP_REEF_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BLACKTIP_REEF_SHARK_BIOMES;
    
    // Blue Shark
    public static final ForgeConfigSpec.IntValue BLUE_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue BLUE_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BLUE_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BLUE_SHARK_BIOMES;
    
    // Bonnethead Shark
    public static final ForgeConfigSpec.IntValue BONNETHEAD_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue BONNETHEAD_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BONNETHEAD_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BONNETHEAD_SHARK_BIOMES;
    
    // Bull Shark
    public static final ForgeConfigSpec.IntValue BULL_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue BULL_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BULL_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BULL_SHARK_BIOMES;

    // Cookiecutter Shark
    public static final ForgeConfigSpec.IntValue COOKIECUTTER_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue COOKIECUTTER_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue COOKIECUTTER_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> COOKIECUTTER_SHARK_BIOMES;
    
    // Goblin Shark
    public static final ForgeConfigSpec.IntValue GOBLIN_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue GOBLIN_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue GOBLIN_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> GOBLIN_SHARK_BIOMES;

    // Greater Axodile
    public static final ForgeConfigSpec.IntValue GREATER_AXODILE_WEIGHT;
    public static final ForgeConfigSpec.IntValue GREATER_AXODILE_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue GREATER_AXODILE_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> GREATER_AXODILE_BIOMES;

    // Great White Shark
    public static final ForgeConfigSpec.IntValue GREATWHITE_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue GREATWHITE_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue GREATWHITE_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> GREATWHITE_SHARK_BIOMES;
    
    // Greenland Shark
    public static final ForgeConfigSpec.IntValue GREENLAND_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue GREENLAND_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue GREENLAND_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> GREENLAND_SHARK_BIOMES;
    
    // Krill
    public static final ForgeConfigSpec.IntValue KRILL_WEIGHT;
    public static final ForgeConfigSpec.IntValue KRILL_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue KRILL_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> KRILL_BIOMES;
    
    // Lemon Shark
    public static final ForgeConfigSpec.IntValue LEMON_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue LEMON_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue LEMON_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> LEMON_SHARK_BIOMES;
    
    // Leopard Shark
    public static final ForgeConfigSpec.IntValue LEOPARD_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue LEOPARD_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue LEOPARD_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> LEOPARD_SHARK_BIOMES;

    // Mako Shark
    public static final ForgeConfigSpec.IntValue MAKO_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue MAKO_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue MAKO_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> MAKO_SHARK_BIOMES;

    // Megalodon
    public static final ForgeConfigSpec.IntValue MEGALODON_WEIGHT;
    public static final ForgeConfigSpec.IntValue MEGALODON_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue MEGALODON_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> MEGALODON_BIOMES;

    // Nurse Shark
    public static final ForgeConfigSpec.IntValue NURSE_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue NURSE_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue NURSE_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> NURSE_SHARK_BIOMES;

    // Pilot Fish
    public static final ForgeConfigSpec.IntValue PILOT_FISH_WEIGHT;
    public static final ForgeConfigSpec.IntValue PILOT_FISH_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue PILOT_FISH_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> PILOT_FISH_BIOMES;

    // Remora
    public static final ForgeConfigSpec.IntValue REMORA_WEIGHT;
    public static final ForgeConfigSpec.IntValue REMORA_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue REMORA_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> REMORA_BIOMES;
    
    // Sawshark
    public static final ForgeConfigSpec.IntValue SAWSHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue SAWSHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue SAWSHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> SAWSHARK_BIOMES;

    // Sea Lion
    public static final ForgeConfigSpec.IntValue SEA_LION_WEIGHT;
    public static final ForgeConfigSpec.IntValue SEA_LION_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue SEA_LION_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> SEA_LION_BIOMES;

    // Tiger Shark
    public static final ForgeConfigSpec.IntValue TIGER_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue TIGER_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue TIGER_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> TIGER_SHARK_BIOMES;

    // Whale Shark
    public static final ForgeConfigSpec.IntValue WHALE_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue WHALE_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue WHALE_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> WHALE_SHARK_BIOMES;
    
    // Oceanic Whitetip Shark
    public static final ForgeConfigSpec.IntValue WHITETIP_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue WHITETIP_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue WHITETIP_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> WHITETIP_SHARK_BIOMES;

    static {
        BUILDER.push("Mob Spawn Settings");
        BUILDER.comment("Configuration settings for all aquatic mob spawning in Ben's Sharks.");

        // --- Axodile ---
        BUILDER.push("Axodile_Spawns");
        BUILDER.comment("Settings for the Axodile.");
        
        AXODILE_WEIGHT = BUILDER
                .comment("The weight (rarity) of Axodiles. Default: 10 (Mid-range).")
                .defineInRange("spawn_weight", 10, 0, 100);
        
        AXODILE_MIN_SIZE = BUILDER
                .comment("Minimum group size when an Axodile spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        AXODILE_MAX_SIZE = BUILDER
                .comment("Maximum group size when an Axodile spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        AXODILE_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Axodile can spawn.")
                .define("biomes_list", "deep_cold_ocean,deep_frozen_ocean,frozen_ocean,snowy_beach");
        
        BUILDER.pop();

        // --- Barracuda ---
        BUILDER.push("Barracuda_Spawns");
        BUILDER.comment("Settings for the Barracuda.");
        
        BARRACUDA_WEIGHT = BUILDER
                .comment("The weight (rarity) of Barracudas. Default: 1 (Very Rare).")
                .defineInRange("spawn_weight", 1, 0, 100);
        
        BARRACUDA_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Barracuda spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        BARRACUDA_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Barracuda spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        BARRACUDA_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where Barracudas can spawn.")
                .define("biomes_list", "lukewarm_ocean,warm_ocean,ocean,deep_lukewarm_ocean");
        
        BUILDER.pop();

        // --- Basking Shark ---
        BUILDER.push("Basking_Shark_Spawns");
        BUILDER.comment("Settings for the Basking Shark.");
        
        BASKING_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Basking Sharks. Default: 1 (Very Rare).")
                .defineInRange("spawn_weight", 1, 0, 100);
        
        BASKING_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Basking Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        BASKING_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Basking Shark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        BASKING_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Basking Shark can spawn.")
                .define("biomes_list", "deep_cold_ocean,deep_frozen_ocean");
        
        BUILDER.pop();

        // --- Blacktip Reef Shark ---
        BUILDER.push("Blacktip_Reef_Shark_Spawns");
        BUILDER.comment("Settings for the Blacktip Reef Shark.");
        
        BLACKTIP_REEF_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Blacktip Reef Sharks. Default: 5 (Uncommon).")
                .defineInRange("spawn_weight", 5, 0, 100);
        
        BLACKTIP_REEF_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Blacktip Reef Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        BLACKTIP_REEF_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Blacktip Reef Shark spawns. Default: 2")
                .defineInRange("max_group_size", 2, 1, 10);

        BLACKTIP_REEF_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Blacktip Reef Shark can spawn.")
                .define("biomes_list", "lukewarm_ocean,warm_ocean,beach,mangrove_swamp");
        
        BUILDER.pop();

        // --- Blue Shark ---
        BUILDER.push("Blue_Shark_Spawns");
        BUILDER.comment("Settings for the Blue Shark.");
        
        BLUE_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Blue Sharks. Default: 2 (Rare).")
                .defineInRange("spawn_weight", 2, 0, 100);
        
        BLUE_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Blue Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        BLUE_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Blue Shark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        BLUE_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Blue Shark can spawn.")
                .define("biomes_list", "deep_ocean,deep_cold_ocean");
        
        BUILDER.pop();

        // --- Bonnethead Shark ---
        BUILDER.push("Bonnethead_Shark_Spawns");
        BUILDER.comment("Settings for the Bonnethead Shark.");
        
        BONNETHEAD_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Bonnethead Sharks. Default: 3 (Uncommon).")
                .defineInRange("spawn_weight", 3, 0, 100);
        
        BONNETHEAD_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Bonnethead Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        BONNETHEAD_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Bonnethead Shark spawns. Default: 2")
                .defineInRange("max_group_size", 2, 1, 10);

        BONNETHEAD_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Bonnethead Shark can spawn.")
                .define("biomes_list", "warm_ocean,lukewarm_ocean,ocean,beach,mangrove_swamp");
        
        BUILDER.pop();

        // --- Bull Shark ---
        BUILDER.push("Bull_Shark_Spawns");
        BUILDER.comment("Settings for the Bull Shark.");
        
        BULL_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Bull Sharks. Default: 2 (Rare).")
                .defineInRange("spawn_weight", 2, 0, 100);
        
        BULL_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Bull Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        BULL_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Bull Shark spawns. Default: 2")
                .defineInRange("max_group_size", 2, 1, 10);

        BULL_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Bull Shark can spawn.")
                .define("biomes_list", "mangrove_swamp,swamp,lukewarm_ocean,beach,stony_shore,deep_lukewarm_ocean,warm_ocean");
        
        BUILDER.pop();

        // --- Cookiecutter Shark ---
        BUILDER.push("Cookiecutter_Shark_Spawns");
        BUILDER.comment("Settings for the Cookiecutter Shark.");
        
        COOKIECUTTER_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Cookiecutter Sharks. Default: 1 (Very rare).")
                .defineInRange("spawn_weight", 1, 0, 100);
        
        COOKIECUTTER_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Cookiecutter Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        COOKIECUTTER_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Cookiecutter Shark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        COOKIECUTTER_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Cookiecutter Shark can spawn.")
                .define("biomes_list", "deep_ocean,deep_cold_ocean,deep_frozen_ocean");
        
        BUILDER.pop();

        // --- Goblin Shark ---
        BUILDER.push("Goblin_Shark_Spawns");
        BUILDER.comment("Settings for the Goblin Shark.");
        
        GOBLIN_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Goblin Sharks. Default: 1 (Extremely rare).")
                .defineInRange("spawn_weight", 1, 0, 100);
        
        GOBLIN_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Goblin Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        GOBLIN_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Goblin Shark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        GOBLIN_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Goblin Shark can spawn.")
                .define("biomes_list", "deep_cold_ocean");
        
        BUILDER.pop();

        // --- Greater Axodile ---
        BUILDER.push("Greater_Axodile_Spawns");
        BUILDER.comment("Settings for the Greater Axodile.");
        
        GREATER_AXODILE_WEIGHT = BUILDER
                .comment("The weight (rarity) of Greater Axodiles. Default: 1 (Rare).")
                .defineInRange("spawn_weight", 1, 0, 100);
        
        GREATER_AXODILE_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Greater Axodile spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        GREATER_AXODILE_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Greater Axodile spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        GREATER_AXODILE_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Greater Axodile can spawn.")
                .define("biomes_list", "deep_frozen_ocean,frozen_ocean");
        
        BUILDER.pop();

        // --- Great White Shark ---
        BUILDER.push("GreatWhite_Shark_Spawns");
        BUILDER.comment("Settings for the Great White Shark.");
        
        GREATWHITE_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Great White Sharks. Default: 1 (Very rare).")
                .defineInRange("spawn_weight", 1, 0, 100);
        
        GREATWHITE_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Great White Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        GREATWHITE_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Great White Shark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        GREATWHITE_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Great White can spawn. Use 'ALL_BIOMES' for every biome.")
                .define("biomes_list", "deep_ocean");
        
        BUILDER.pop();
        
        // --- Greenland Shark ---
        BUILDER.push("Greenland_Shark_Spawns");
        BUILDER.comment("Settings for the Greenland Shark.");
        
        GREENLAND_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Greenland Sharks. Default: 2 (Very Rare).")
                .defineInRange("spawn_weight", 2, 0, 100);
        
        GREENLAND_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Greenland Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        GREENLAND_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Greenland Shark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        GREENLAND_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Greenland Shark can spawn.")
                .define("biomes_list", "deep_frozen_ocean");
        
        BUILDER.pop();

        // --- Krill ---
        BUILDER.push("Krill_Spawns");
        BUILDER.comment("Settings for Krill.");
        
        KRILL_WEIGHT = BUILDER
                .comment("The weight (rarity) of Krill. Default: 20 (Common).")
                .defineInRange("spawn_weight", 20, 0, 100);
        
        KRILL_MIN_SIZE = BUILDER
                .comment("Minimum group size when Krill spawns. Default: 5")
                .defineInRange("min_group_size", 5, 1, 20);
        
        KRILL_MAX_SIZE = BUILDER
                .comment("Maximum group size when Krill spawns. Default: 12")
                .defineInRange("max_group_size", 12, 1, 20);

        KRILL_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where Krill can spawn.")
                .define("biomes_list", "cold_ocean,deep_cold_ocean,deep_frozen_ocean,deep_lukewarm_ocean,deep_ocean,frozen_ocean,lukewarm_ocean,ocean,warm_ocean");
        
        BUILDER.pop();
        
        // --- Lemon Shark ---
        BUILDER.push("Lemon_Shark_Spawns");
        BUILDER.comment("Settings for the Lemon Shark.");
        
        LEMON_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Lemon Sharks. Default: 4 (Uncommon).")
                .defineInRange("spawn_weight", 4, 0, 100);
        
        LEMON_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Lemon Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        LEMON_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Lemon Shark spawns. Default: 2")
                .defineInRange("max_group_size", 2, 1, 10);

        LEMON_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Lemon Shark can spawn.")
                .define("biomes_list", "lukewarm_ocean,warm_ocean,beach,mangrove_swamp");
        
        BUILDER.pop();
        
        // --- Leopard Shark ---
        BUILDER.push("Leopard_Shark_Spawns");
        BUILDER.comment("Settings for the Leopard Shark.");
        
        LEOPARD_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Leopard Sharks. Default: 2 (Rare).")
                .defineInRange("spawn_weight", 2, 0, 100);
        
        LEOPARD_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Leopard Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        LEOPARD_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Leopard Shark spawns. Default: 2")
                .defineInRange("max_group_size", 2, 1, 10);

        LEOPARD_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Leopard Shark can spawn.")
                .define("biomes_list", "beach,ocean,lukewarm_ocean,cold_ocean,stony_shore,mangrove_swamp");
        
        BUILDER.pop();

        // --- Mako Shark ---
        BUILDER.push("Mako_Shark_Spawns");
        BUILDER.comment("Settings for the Mako Shark.");
        
        MAKO_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Mako Sharks. Default: 2 (Rare).")
                .defineInRange("spawn_weight", 2, 0, 100);
        
        MAKO_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Mako Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        MAKO_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Mako Shark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        MAKO_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Mako Shark can spawn.")
                .define("biomes_list", "deep_ocean,ocean,deep_lukewarm_ocean,lukewarm_ocean");
        
        BUILDER.pop();

        // --- Megalodon ---
        BUILDER.push("Megalodon_Spawns");
        BUILDER.comment("Settings for the Megalodon.");
        
        MEGALODON_WEIGHT = BUILDER
                .comment("The weight (rarity) of Megalodons. Default: 1 (Ultra Rare). Set to 0 to disable regular spawning.")
                .defineInRange("spawn_weight", 1, 0, 10);

        MEGALODON_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Megalodon spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        MEGALODON_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Megalodon spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        MEGALODON_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Megalodon can spawn.")
                .define("biomes_list", "deep_frozen_ocean");
        
        BUILDER.pop();

        // --- Nurse Shark ---
        BUILDER.push("Nurse_Shark_Spawns");
        BUILDER.comment("Settings for the Nurse Shark.");
        
        NURSE_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Nurse Sharks. Default: 2 (Rare).")
                .defineInRange("spawn_weight", 2, 0, 100);
        
        NURSE_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Nurse Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        NURSE_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Nurse Shark spawns. Default: 2")
                .defineInRange("max_group_size", 2, 1, 10);

        NURSE_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Nurse Shark can spawn.")
                .define("biomes_list", "beach,lukewarm_ocean,warm_ocean,ocean,mangrove_swamp");
        
        BUILDER.pop();
        
        // --- Pilot Fish ---
        BUILDER.push("Pilot_Fish_Spawns");
        BUILDER.comment("Settings for the Pilot Fish.");
        
        PILOT_FISH_WEIGHT = BUILDER
                .comment("The weight (rarity) of Pilot Fish. Default: 12 (Common).")
                .defineInRange("spawn_weight", 12, 0, 100);
        
        PILOT_FISH_MIN_SIZE = BUILDER
                .comment("Minimum group size when Pilot Fish spawn. Default: 3")
                .defineInRange("min_group_size", 3, 1, 20);
        
        PILOT_FISH_MAX_SIZE = BUILDER
                .comment("Maximum group size when Pilot Fish spawn. Default: 5")
                .defineInRange("max_group_size", 5, 1, 20);

        PILOT_FISH_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where Pilot Fish can spawn.")
                .define("biomes_list", "lukewarm_ocean,ocean,warm_ocean,deep_lukewarm_ocean,beach,mangrove_swamp");
        
        BUILDER.pop();

        // --- Remora ---
        BUILDER.push("Remora_Spawns");
        BUILDER.comment("Settings for the Remora.");
        
        REMORA_WEIGHT = BUILDER
                .comment("The weight (rarity) of Remora. Default: 10 (Common).")
                .defineInRange("spawn_weight", 10, 0, 100);
        
        REMORA_MIN_SIZE = BUILDER
                .comment("Minimum group size when Remora spawn. Default: 1")
                .defineInRange("min_group_size", 1, 1, 20);
        
        REMORA_MAX_SIZE = BUILDER
                .comment("Maximum group size when Remora spawn. Default: 3")
                .defineInRange("max_group_size", 3, 1, 20);

        REMORA_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where Remora can spawn.")
                .define("biomes_list", "deep_lukewarm_ocean,deep_ocean,lukewarm_ocean,ocean,warm_ocean,beach");
        
        BUILDER.pop();

        // --- Sawshark ---
        BUILDER.push("Sawshark_Spawns");
        BUILDER.comment("Settings for the Sawshark.");
        
        SAWSHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Sawsharks. Default: 1 (Very Rare).")
                .defineInRange("spawn_weight", 1, 0, 100);
        
        SAWSHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Sawshark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        SAWSHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Sawshark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        SAWSHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Sawshark can spawn.")
                .define("biomes_list", "deep_lukewarm_ocean");
        
        BUILDER.pop();

        // --- Sea Lion ---
        BUILDER.push("Sea_Lion_Spawns");
        BUILDER.comment("Settings for the Sea Lion.");
        
        SEA_LION_WEIGHT = BUILDER
                .comment("The weight (rarity) of Sea Lion. Default: 5 (Uncommon).")
                .defineInRange("spawn_weight", 5, 0, 100);
        
        SEA_LION_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Sea Lion spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        SEA_LION_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Sea Lion spawns. Default: 3")
                .defineInRange("max_group_size", 3, 1, 10);

        SEA_LION_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Sea Lion can spawn.")
                .define("biomes_list", "beach,ocean,cold_ocean,lukewarm_ocean,stony_shore");
        
        BUILDER.pop();

        // --- Tiger Shark ---
        BUILDER.push("Tiger_Shark_Spawns");
        BUILDER.comment("Settings for the Tiger Shark.");
        
        TIGER_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Tiger Sharks. Default: 2 (Rare).")
                .defineInRange("spawn_weight", 2, 0, 100);
        
        TIGER_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Tiger Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        TIGER_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Tiger Shark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        TIGER_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Tiger Shark can spawn.")
                .define("biomes_list", "deep_lukewarm_ocean,lukewarm_ocean,warm_ocean,beach");
        
        BUILDER.pop();

        // --- Whale Shark ---
        BUILDER.push("Whale_Shark_Spawns");
        BUILDER.comment("Settings for the Whale Shark.");
        
        WHALE_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Whale Sharks. Default: 1 (Very Rare).")
                .defineInRange("spawn_weight", 1, 0, 100);
        
        WHALE_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Whale Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        WHALE_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Whale Shark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        WHALE_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Whale Shark can spawn.")
                .define("biomes_list", "deep_lukewarm_ocean,lukewarm_ocean,warm_ocean");
        
        BUILDER.pop();

        // --- Oceanic Whitetip Shark ---
        BUILDER.push("Whitetip_Shark_Spawns");
        BUILDER.comment("Settings for the Oceanic Whitetip Shark.");
        
        WHITETIP_SHARK_WEIGHT = BUILDER
                .comment("The weight (rarity) of Oceanic Whitetip Sharks. Default: 4 (Uncommon).")
                .defineInRange("spawn_weight", 4, 0, 100);
        
        WHITETIP_SHARK_MIN_SIZE = BUILDER
                .comment("Minimum group size when a Oceanic Whitetip Shark spawns. Default: 1")
                .defineInRange("min_group_size", 1, 1, 10);
        
        WHITETIP_SHARK_MAX_SIZE = BUILDER
                .comment("Maximum group size when a Oceanic Whitetip Shark spawns. Default: 1")
                .defineInRange("max_group_size", 1, 1, 10);

        WHITETIP_SHARK_BIOMES = BUILDER
                .comment("List of biomes (separated by commas) where the Oceanic Whitetip Shark can spawn.")
                .define("biomes_list", "lukewarm_ocean,warm_ocean,deep_lukewarm_ocean");
        
        BUILDER.pop();
        
        // --- End of main section ---
        BUILDER.pop();
        
        // This MUST be the last line in the static block
        SPEC = BUILDER.build();
    }
}