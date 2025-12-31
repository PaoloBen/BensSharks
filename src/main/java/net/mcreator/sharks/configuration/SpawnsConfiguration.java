package net.mcreator.sharks.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class SpawnsConfiguration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // ==========================================
    //            PUBLIC CONFIG ENTRIES
    // ==========================================
    // These remain public static final so your other code works without changes.

    public static final ForgeConfigSpec.BooleanValue ANGELSHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue ANGELSHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue ANGELSHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue ANGELSHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> ANGELSHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> ANGELSHARK_SPAWN_TIME;
    
    public static final ForgeConfigSpec.BooleanValue AXODILE_ENABLED;
    public static final ForgeConfigSpec.IntValue AXODILE_WEIGHT;
    public static final ForgeConfigSpec.IntValue AXODILE_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue AXODILE_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> AXODILE_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> AXODILE_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue BARRACUDA_ENABLED;
    public static final ForgeConfigSpec.IntValue BARRACUDA_WEIGHT;
    public static final ForgeConfigSpec.IntValue BARRACUDA_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BARRACUDA_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BARRACUDA_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> BARRACUDA_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue BASKING_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue BASKING_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue BASKING_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BASKING_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BASKING_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> BASKING_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue BLACKTIP_REEF_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue BLACKTIP_REEF_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue BLACKTIP_REEF_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BLACKTIP_REEF_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BLACKTIP_REEF_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> BLACKTIP_REEF_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue BLUE_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue BLUE_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue BLUE_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BLUE_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BLUE_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> BLUE_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue BONNETHEAD_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue BONNETHEAD_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue BONNETHEAD_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BONNETHEAD_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BONNETHEAD_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> BONNETHEAD_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue BULL_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue BULL_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue BULL_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue BULL_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> BULL_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> BULL_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue COOKIECUTTER_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue COOKIECUTTER_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue COOKIECUTTER_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue COOKIECUTTER_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> COOKIECUTTER_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> COOKIECUTTER_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue GOBLIN_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue GOBLIN_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue GOBLIN_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue GOBLIN_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> GOBLIN_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> GOBLIN_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue GREATER_AXODILE_ENABLED;
    public static final ForgeConfigSpec.IntValue GREATER_AXODILE_WEIGHT;
    public static final ForgeConfigSpec.IntValue GREATER_AXODILE_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue GREATER_AXODILE_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> GREATER_AXODILE_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> GREATER_AXODILE_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue GREATWHITE_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue GREATWHITE_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue GREATWHITE_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue GREATWHITE_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> GREATWHITE_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> GREATWHITE_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue GREENLAND_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue GREENLAND_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue GREENLAND_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue GREENLAND_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> GREENLAND_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> GREENLAND_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue KRILL_ENABLED;
    public static final ForgeConfigSpec.IntValue KRILL_WEIGHT;
    public static final ForgeConfigSpec.IntValue KRILL_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue KRILL_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> KRILL_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> KRILL_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue LEMON_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue LEMON_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue LEMON_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue LEMON_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> LEMON_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> LEMON_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue LEOPARD_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue LEOPARD_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue LEOPARD_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue LEOPARD_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> LEOPARD_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> LEOPARD_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue MAKO_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue MAKO_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue MAKO_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue MAKO_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> MAKO_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> MAKO_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue MEGALODON_ENABLED;
    public static final ForgeConfigSpec.IntValue MEGALODON_WEIGHT;
    public static final ForgeConfigSpec.IntValue MEGALODON_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue MEGALODON_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> MEGALODON_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> MEGALODON_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue MEGAMOUTH_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue MEGAMOUTH_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue MEGAMOUTH_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue MEGAMOUTH_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> MEGAMOUTH_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> MEGAMOUTH_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue NURSE_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue NURSE_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue NURSE_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue NURSE_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> NURSE_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> NURSE_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue PILOT_FISH_ENABLED;
    public static final ForgeConfigSpec.IntValue PILOT_FISH_WEIGHT;
    public static final ForgeConfigSpec.IntValue PILOT_FISH_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue PILOT_FISH_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> PILOT_FISH_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> PILOT_FISH_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue REMORA_ENABLED;
    public static final ForgeConfigSpec.IntValue REMORA_WEIGHT;
    public static final ForgeConfigSpec.IntValue REMORA_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue REMORA_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> REMORA_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> REMORA_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue SARDINE_ENABLED;
    public static final ForgeConfigSpec.IntValue SARDINE_WEIGHT;
    public static final ForgeConfigSpec.IntValue SARDINE_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue SARDINE_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> SARDINE_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> SARDINE_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue SHOAL_ENABLED;
    public static final ForgeConfigSpec.IntValue SHOAL_WEIGHT;
    public static final ForgeConfigSpec.IntValue SHOAL_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue SHOAL_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> SHOAL_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> SHOAL_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue SAWSHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue SAWSHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue SAWSHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue SAWSHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> SAWSHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> SAWSHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue SEA_LION_ENABLED;
    public static final ForgeConfigSpec.IntValue SEA_LION_WEIGHT;
    public static final ForgeConfigSpec.IntValue SEA_LION_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue SEA_LION_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> SEA_LION_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> SEA_LION_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue THRESHER_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue THRESHER_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue THRESHER_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue THRESHER_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> THRESHER_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> THRESHER_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue TIGER_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue TIGER_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue TIGER_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue TIGER_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> TIGER_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> TIGER_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue WHALE_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue WHALE_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue WHALE_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue WHALE_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> WHALE_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> WHALE_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue WHITETIP_SHARK_ENABLED;
    public static final ForgeConfigSpec.IntValue WHITETIP_SHARK_WEIGHT;
    public static final ForgeConfigSpec.IntValue WHITETIP_SHARK_MIN_SIZE;
    public static final ForgeConfigSpec.IntValue WHITETIP_SHARK_MAX_SIZE;
    public static final ForgeConfigSpec.ConfigValue<String> WHITETIP_SHARK_BIOMES;
    public static final ForgeConfigSpec.ConfigValue<String> WHITETIP_SHARK_SPAWN_TIME;

    public static final ForgeConfigSpec.BooleanValue THALASSOGER_RAID_ENABLED;

    // ==========================================
    //            INIT & CONSTRUCTION
    // ==========================================

    static {
        BUILDER.push("Mob Spawn Settings");
        BUILDER.comment("Configuration settings for all aquatic mob spawning in Ben's Sharks.");

        // We use a temporary object to hold the builder return values to assign them cleanly
        SpawnConfigGroup group;

        // --- Angelshark ---
        group = buildMobConfig(BUILDER, "Angelshark", Defaults.ANGELSHARK);
        ANGELSHARK_ENABLED = group.enabled; ANGELSHARK_WEIGHT = group.weight; ANGELSHARK_MIN_SIZE = group.min; ANGELSHARK_MAX_SIZE = group.max; ANGELSHARK_BIOMES = group.biomes; ANGELSHARK_SPAWN_TIME = group.time;
        
        // --- Axodile ---
        group = buildMobConfig(BUILDER, "Axodile", Defaults.AXODILE);
        AXODILE_ENABLED = group.enabled; AXODILE_WEIGHT = group.weight; AXODILE_MIN_SIZE = group.min; AXODILE_MAX_SIZE = group.max; AXODILE_BIOMES = group.biomes; AXODILE_SPAWN_TIME = group.time;

        // --- Barracuda ---
        group = buildMobConfig(BUILDER, "Barracuda", Defaults.BARRACUDA);
        BARRACUDA_ENABLED = group.enabled; BARRACUDA_WEIGHT = group.weight; BARRACUDA_MIN_SIZE = group.min; BARRACUDA_MAX_SIZE = group.max; BARRACUDA_BIOMES = group.biomes; BARRACUDA_SPAWN_TIME = group.time;

        // --- Basking Shark ---
        group = buildMobConfig(BUILDER, "Basking_Shark", Defaults.BASKING_SHARK);
        BASKING_SHARK_ENABLED = group.enabled; BASKING_SHARK_WEIGHT = group.weight; BASKING_SHARK_MIN_SIZE = group.min; BASKING_SHARK_MAX_SIZE = group.max; BASKING_SHARK_BIOMES = group.biomes; BASKING_SHARK_SPAWN_TIME = group.time;

        // --- Blacktip Reef Shark ---
        group = buildMobConfig(BUILDER, "Blacktip_Reef_Shark", Defaults.BLACKTIP_REEF_SHARK);
        BLACKTIP_REEF_SHARK_ENABLED = group.enabled; BLACKTIP_REEF_SHARK_WEIGHT = group.weight; BLACKTIP_REEF_SHARK_MIN_SIZE = group.min; BLACKTIP_REEF_SHARK_MAX_SIZE = group.max; BLACKTIP_REEF_SHARK_BIOMES = group.biomes; BLACKTIP_REEF_SHARK_SPAWN_TIME = group.time;

        // --- Blue Shark ---
        group = buildMobConfig(BUILDER, "Blue_Shark", Defaults.BLUE_SHARK);
        BLUE_SHARK_ENABLED = group.enabled; BLUE_SHARK_WEIGHT = group.weight; BLUE_SHARK_MIN_SIZE = group.min; BLUE_SHARK_MAX_SIZE = group.max; BLUE_SHARK_BIOMES = group.biomes; BLUE_SHARK_SPAWN_TIME = group.time;

        // --- Bonnethead Shark ---
        group = buildMobConfig(BUILDER, "Bonnethead_Shark", Defaults.BONNETHEAD_SHARK);
        BONNETHEAD_SHARK_ENABLED = group.enabled; BONNETHEAD_SHARK_WEIGHT = group.weight; BONNETHEAD_SHARK_MIN_SIZE = group.min; BONNETHEAD_SHARK_MAX_SIZE = group.max; BONNETHEAD_SHARK_BIOMES = group.biomes; BONNETHEAD_SHARK_SPAWN_TIME = group.time;

        // --- Bull Shark ---
        group = buildMobConfig(BUILDER, "Bull_Shark", Defaults.BULL_SHARK);
        BULL_SHARK_ENABLED = group.enabled; BULL_SHARK_WEIGHT = group.weight; BULL_SHARK_MIN_SIZE = group.min; BULL_SHARK_MAX_SIZE = group.max; BULL_SHARK_BIOMES = group.biomes; BULL_SHARK_SPAWN_TIME = group.time;

        // --- Cookiecutter Shark ---
        group = buildMobConfig(BUILDER, "Cookiecutter_Shark", Defaults.COOKIECUTTER_SHARK);
        COOKIECUTTER_SHARK_ENABLED = group.enabled; COOKIECUTTER_SHARK_WEIGHT = group.weight; COOKIECUTTER_SHARK_MIN_SIZE = group.min; COOKIECUTTER_SHARK_MAX_SIZE = group.max; COOKIECUTTER_SHARK_BIOMES = group.biomes; COOKIECUTTER_SHARK_SPAWN_TIME = group.time;

        // --- Goblin Shark ---
        group = buildMobConfig(BUILDER, "Goblin_Shark", Defaults.GOBLIN_SHARK);
        GOBLIN_SHARK_ENABLED = group.enabled; GOBLIN_SHARK_WEIGHT = group.weight; GOBLIN_SHARK_MIN_SIZE = group.min; GOBLIN_SHARK_MAX_SIZE = group.max; GOBLIN_SHARK_BIOMES = group.biomes; GOBLIN_SHARK_SPAWN_TIME = group.time;

        // --- Greater Axodile ---
        group = buildMobConfig(BUILDER, "Greater_Axodile", Defaults.GREATER_AXODILE);
        GREATER_AXODILE_ENABLED = group.enabled; GREATER_AXODILE_WEIGHT = group.weight; GREATER_AXODILE_MIN_SIZE = group.min; GREATER_AXODILE_MAX_SIZE = group.max; GREATER_AXODILE_BIOMES = group.biomes; GREATER_AXODILE_SPAWN_TIME = group.time;

        // --- Great White Shark ---
        group = buildMobConfig(BUILDER, "GreatWhite_Shark", Defaults.GREATWHITE_SHARK);
        GREATWHITE_SHARK_ENABLED = group.enabled; GREATWHITE_SHARK_WEIGHT = group.weight; GREATWHITE_SHARK_MIN_SIZE = group.min; GREATWHITE_SHARK_MAX_SIZE = group.max; GREATWHITE_SHARK_BIOMES = group.biomes; GREATWHITE_SHARK_SPAWN_TIME = group.time;

        // --- Greenland Shark ---
        group = buildMobConfig(BUILDER, "Greenland_Shark", Defaults.GREENLAND_SHARK);
        GREENLAND_SHARK_ENABLED = group.enabled; GREENLAND_SHARK_WEIGHT = group.weight; GREENLAND_SHARK_MIN_SIZE = group.min; GREENLAND_SHARK_MAX_SIZE = group.max; GREENLAND_SHARK_BIOMES = group.biomes; GREENLAND_SHARK_SPAWN_TIME = group.time;

        // --- Krill ---
        group = buildMobConfig(BUILDER, "Krill", Defaults.KRILL);
        KRILL_ENABLED = group.enabled; KRILL_WEIGHT = group.weight; KRILL_MIN_SIZE = group.min; KRILL_MAX_SIZE = group.max; KRILL_BIOMES = group.biomes; KRILL_SPAWN_TIME = group.time;

        // --- Lemon Shark ---
        group = buildMobConfig(BUILDER, "Lemon_Shark", Defaults.LEMON_SHARK);
        LEMON_SHARK_ENABLED = group.enabled; LEMON_SHARK_WEIGHT = group.weight; LEMON_SHARK_MIN_SIZE = group.min; LEMON_SHARK_MAX_SIZE = group.max; LEMON_SHARK_BIOMES = group.biomes; LEMON_SHARK_SPAWN_TIME = group.time;

        // --- Leopard Shark ---
        group = buildMobConfig(BUILDER, "Leopard_Shark", Defaults.LEOPARD_SHARK);
        LEOPARD_SHARK_ENABLED = group.enabled; LEOPARD_SHARK_WEIGHT = group.weight; LEOPARD_SHARK_MIN_SIZE = group.min; LEOPARD_SHARK_MAX_SIZE = group.max; LEOPARD_SHARK_BIOMES = group.biomes; LEOPARD_SHARK_SPAWN_TIME = group.time;

        // --- Mako Shark ---
        group = buildMobConfig(BUILDER, "Mako_Shark", Defaults.MAKO_SHARK);
        MAKO_SHARK_ENABLED = group.enabled; MAKO_SHARK_WEIGHT = group.weight; MAKO_SHARK_MIN_SIZE = group.min; MAKO_SHARK_MAX_SIZE = group.max; MAKO_SHARK_BIOMES = group.biomes; MAKO_SHARK_SPAWN_TIME = group.time;

        // --- Megalodon ---
        group = buildMobConfig(BUILDER, "Megalodon", Defaults.MEGALODON);
        MEGALODON_ENABLED = group.enabled; MEGALODON_WEIGHT = group.weight; MEGALODON_MIN_SIZE = group.min; MEGALODON_MAX_SIZE = group.max; MEGALODON_BIOMES = group.biomes; MEGALODON_SPAWN_TIME = group.time;

        // --- Megamouth Shark ---
        group = buildMobConfig(BUILDER, "Megamouth_Shark", Defaults.MEGAMOUTH_SHARK);
        MEGAMOUTH_SHARK_ENABLED = group.enabled; MEGAMOUTH_SHARK_WEIGHT = group.weight; MEGAMOUTH_SHARK_MIN_SIZE = group.min; MEGAMOUTH_SHARK_MAX_SIZE = group.max; MEGAMOUTH_SHARK_BIOMES = group.biomes; MEGAMOUTH_SHARK_SPAWN_TIME = group.time;

        // --- Nurse Shark ---
        group = buildMobConfig(BUILDER, "Nurse_Shark", Defaults.NURSE_SHARK);
        NURSE_SHARK_ENABLED = group.enabled; NURSE_SHARK_WEIGHT = group.weight; NURSE_SHARK_MIN_SIZE = group.min; NURSE_SHARK_MAX_SIZE = group.max; NURSE_SHARK_BIOMES = group.biomes; NURSE_SHARK_SPAWN_TIME = group.time;

        // --- Pilot Fish ---
        group = buildMobConfig(BUILDER, "Pilot_Fish", Defaults.PILOT_FISH);
        PILOT_FISH_ENABLED = group.enabled; PILOT_FISH_WEIGHT = group.weight; PILOT_FISH_MIN_SIZE = group.min; PILOT_FISH_MAX_SIZE = group.max; PILOT_FISH_BIOMES = group.biomes; PILOT_FISH_SPAWN_TIME = group.time;

        // --- Remora ---
        group = buildMobConfig(BUILDER, "Remora", Defaults.REMORA);
        REMORA_ENABLED = group.enabled; REMORA_WEIGHT = group.weight; REMORA_MIN_SIZE = group.min; REMORA_MAX_SIZE = group.max; REMORA_BIOMES = group.biomes; REMORA_SPAWN_TIME = group.time;

        // --- Sardine ---
        group = buildMobConfig(BUILDER, "Sardine", Defaults.SARDINE);
        SARDINE_ENABLED = group.enabled; SARDINE_WEIGHT = group.weight; SARDINE_MIN_SIZE = group.min; SARDINE_MAX_SIZE = group.max; SARDINE_BIOMES = group.biomes; SARDINE_SPAWN_TIME = group.time;

        // --- Shoal ---
        group = buildMobConfig(BUILDER, "Shoal", Defaults.SHOAL);
        SHOAL_ENABLED = group.enabled; SHOAL_WEIGHT = group.weight; SHOAL_MIN_SIZE = group.min; SHOAL_MAX_SIZE = group.max; SHOAL_BIOMES = group.biomes; SHOAL_SPAWN_TIME = group.time;

        // --- Sawshark ---
        group = buildMobConfig(BUILDER, "Sawshark", Defaults.SAWSHARK);
        SAWSHARK_ENABLED = group.enabled; SAWSHARK_WEIGHT = group.weight; SAWSHARK_MIN_SIZE = group.min; SAWSHARK_MAX_SIZE = group.max; SAWSHARK_BIOMES = group.biomes; SAWSHARK_SPAWN_TIME = group.time;

        // --- Sea Lion ---
        group = buildMobConfig(BUILDER, "Sea_Lion", Defaults.SEA_LION);
        SEA_LION_ENABLED = group.enabled; SEA_LION_WEIGHT = group.weight; SEA_LION_MIN_SIZE = group.min; SEA_LION_MAX_SIZE = group.max; SEA_LION_BIOMES = group.biomes; SEA_LION_SPAWN_TIME = group.time;

        // --- Thresher Shark ---
        group = buildMobConfig(BUILDER, "Thresher_Shark", Defaults.THRESHER_SHARK);
        THRESHER_SHARK_ENABLED = group.enabled; THRESHER_SHARK_WEIGHT = group.weight; THRESHER_SHARK_MIN_SIZE = group.min; THRESHER_SHARK_MAX_SIZE = group.max; THRESHER_SHARK_BIOMES = group.biomes; THRESHER_SHARK_SPAWN_TIME = group.time;

        // --- Tiger Shark ---
        group = buildMobConfig(BUILDER, "Tiger_Shark", Defaults.TIGER_SHARK);
        TIGER_SHARK_ENABLED = group.enabled; TIGER_SHARK_WEIGHT = group.weight; TIGER_SHARK_MIN_SIZE = group.min; TIGER_SHARK_MAX_SIZE = group.max; TIGER_SHARK_BIOMES = group.biomes; TIGER_SHARK_SPAWN_TIME = group.time;

        // --- Whale Shark ---
        group = buildMobConfig(BUILDER, "Whale_Shark", Defaults.WHALE_SHARK);
        WHALE_SHARK_ENABLED = group.enabled; WHALE_SHARK_WEIGHT = group.weight; WHALE_SHARK_MIN_SIZE = group.min; WHALE_SHARK_MAX_SIZE = group.max; WHALE_SHARK_BIOMES = group.biomes; WHALE_SHARK_SPAWN_TIME = group.time;

        // --- Oceanic Whitetip Shark ---
        group = buildMobConfig(BUILDER, "Whitetip_Shark", Defaults.WHITETIP_SHARK);
        WHITETIP_SHARK_ENABLED = group.enabled; WHITETIP_SHARK_WEIGHT = group.weight; WHITETIP_SHARK_MIN_SIZE = group.min; WHITETIP_SHARK_MAX_SIZE = group.max; WHITETIP_SHARK_BIOMES = group.biomes; WHITETIP_SHARK_SPAWN_TIME = group.time;

        // --- Thalassoger (Raid) ---
        BUILDER.push("Thalassoger_Raid_Settings");
        BUILDER.comment("Settings for the Thalassoger appearing in Raids.");
        THALASSOGER_RAID_ENABLED = BUILDER.comment("If true, the Thalassoger will join Illager Raids.")
                .define("enable_raid_spawning", true);
        BUILDER.pop();

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    // ==========================================
    //            HELPER METHODS & ENUMS
    // ==========================================

    /**
     * Helper to build all config entries for a single mob using the defaults.
     */
    private static SpawnConfigGroup buildMobConfig(ForgeConfigSpec.Builder builder, String categoryName, Defaults defaults) {
        builder.push(categoryName + "_Spawns");
        builder.comment("Settings for " + categoryName.replace("_", " ") + ".");

        ForgeConfigSpec.BooleanValue enabled = builder
                .comment("If true, this mob will spawn naturally.")
                .define("enable_spawning", defaults.enabled);

        ForgeConfigSpec.IntValue weight = builder
                .comment("The weight (rarity) of this mob. Default: " + defaults.weight)
                .defineInRange("spawn_weight", defaults.weight, 0, 100);

        ForgeConfigSpec.IntValue min = builder
                .comment("Minimum group size. Default: " + defaults.min)
                .defineInRange("min_group_size", defaults.min, 1, 20);

        ForgeConfigSpec.IntValue max = builder
                .comment("Maximum group size. Default: " + defaults.max)
                .defineInRange("max_group_size", defaults.max, 1, 30);

        ForgeConfigSpec.ConfigValue<String> biomes = builder
                .comment("List of biomes (separated by commas) where this mob can spawn.")
                .define("biomes_list", defaults.biomes);

        ForgeConfigSpec.ConfigValue<String> time = builder
                .comment("Restrictions: ANY, DAY, NIGHT")
                .define("spawn_time_restriction", defaults.spawnTime);

        builder.pop();
        return new SpawnConfigGroup(enabled, weight, min, max, biomes, time);
    }

    // Simple container to pass config objects back to the static block assignment
    private static class SpawnConfigGroup {
        final ForgeConfigSpec.BooleanValue enabled;
        final ForgeConfigSpec.IntValue weight;
        final ForgeConfigSpec.IntValue min;
        final ForgeConfigSpec.IntValue max;
        final ForgeConfigSpec.ConfigValue<String> biomes;
        final ForgeConfigSpec.ConfigValue<String> time;

        SpawnConfigGroup(ForgeConfigSpec.BooleanValue enabled, ForgeConfigSpec.IntValue weight,
                         ForgeConfigSpec.IntValue min, ForgeConfigSpec.IntValue max,
                         ForgeConfigSpec.ConfigValue<String> biomes, ForgeConfigSpec.ConfigValue<String> time) {
            this.enabled = enabled;
            this.weight = weight;
            this.min = min;
            this.max = max;
            this.biomes = biomes;
            this.time = time;
        }
    }

    /**
     * CENTRALIZED DEFAULTS
     * Edit values here to change defaults without hunting through the file.
     */
    private enum Defaults {
        ANGELSHARK(true, 1, 1, 1, "deep_lukewarm_ocean,lukewarm_ocean", "ANY"),
        AXODILE(true, 10, 1, 1, "deep_cold_ocean,deep_frozen_ocean,frozen_ocean,snowy_beach", "ANY"),
        BARRACUDA(true, 1, 1, 1, "lukewarm_ocean,warm_ocean,ocean,deep_lukewarm_ocean", "ANY"),
        BASKING_SHARK(true, 1, 1, 1, "deep_cold_ocean,deep_frozen_ocean", "ANY"),
        BLACKTIP_REEF_SHARK(true, 5, 1, 2, "lukewarm_ocean,warm_ocean,beach,mangrove_swamp", "ANY"),
        BLUE_SHARK(true, 2, 1, 1, "deep_ocean,deep_cold_ocean", "ANY"),
        BONNETHEAD_SHARK(true, 3, 1, 2, "warm_ocean,lukewarm_ocean,ocean,beach,mangrove_swamp", "ANY"),
        BULL_SHARK(true, 2, 1, 2, "mangrove_swamp,swamp,lukewarm_ocean,beach,stony_shore,deep_lukewarm_ocean,warm_ocean,river", "ANY"),
        COOKIECUTTER_SHARK(true, 1, 1, 1, "deep_ocean,deep_cold_ocean,deep_frozen_ocean", "NIGHT"),
        GOBLIN_SHARK(true, 1, 1, 1, "deep_cold_ocean", "NIGHT"),
        GREATER_AXODILE(true, 1, 1, 1, "deep_frozen_ocean,frozen_ocean", "ANY"),
        GREATWHITE_SHARK(true, 1, 1, 1, "deep_ocean", "ANY"),
        GREENLAND_SHARK(true, 2, 1, 1, "deep_frozen_ocean", "ANY"),
        KRILL(true, 20, 5, 12, "cold_ocean,deep_cold_ocean,deep_frozen_ocean,deep_lukewarm_ocean,deep_ocean,frozen_ocean,lukewarm_ocean,ocean,warm_ocean", "ANY"),
        LEMON_SHARK(true, 4, 1, 2, "lukewarm_ocean,warm_ocean,beach,mangrove_swamp", "ANY"),
        LEOPARD_SHARK(true, 2, 1, 2, "beach,ocean,lukewarm_ocean,cold_ocean,stony_shore,mangrove_swamp", "ANY"),
        MAKO_SHARK(true, 2, 1, 1, "deep_ocean,ocean,deep_lukewarm_ocean,lukewarm_ocean", "ANY"),
        MEGALODON(true, 1, 1, 1, "deep_frozen_ocean", "ANY"),
        MEGAMOUTH_SHARK(true, 1, 1, 1, "deep_ocean,deep_lukewarm_ocean", "NIGHT"),
        NURSE_SHARK(true, 2, 1, 2, "beach,lukewarm_ocean,warm_ocean,ocean,mangrove_swamp", "ANY"),
        PILOT_FISH(true, 12, 3, 5, "lukewarm_ocean,ocean,warm_ocean,deep_lukewarm_ocean,beach,mangrove_swamp", "ANY"),
        REMORA(true, 10, 1, 3, "deep_lukewarm_ocean,deep_ocean,lukewarm_ocean,ocean,warm_ocean,beach", "ANY"),
        SARDINE(false, 10, 3, 7, "cold_ocean,deep_cold_ocean,deep_frozen_ocean,deep_lukewarm_ocean,deep_ocean,frozen_ocean,lukewarm_ocean,ocean,warm_ocean", "ANY"),
        SHOAL(true, 5, 3, 7, "cold_ocean,deep_cold_ocean,deep_frozen_ocean,deep_lukewarm_ocean,deep_ocean,frozen_ocean,lukewarm_ocean,ocean,warm_ocean", "ANY"),
        SAWSHARK(true, 1, 1, 1, "deep_lukewarm_ocean", "ANY"),
        SEA_LION(true, 2, 1, 3, "beach,ocean,cold_ocean,lukewarm_ocean,stony_shore", "ANY"),
        THRESHER_SHARK(true, 1, 1, 1, "lukewarm_ocean,deep_lukewarm_ocean,warm_ocean", "ANY"),
        TIGER_SHARK(true, 2, 1, 1, "deep_lukewarm_ocean,lukewarm_ocean,warm_ocean,beach", "ANY"),
        WHALE_SHARK(true, 1, 1, 1, "deep_lukewarm_ocean,lukewarm_ocean,warm_ocean", "ANY"),
        WHITETIP_SHARK(true, 4, 1, 1, "lukewarm_ocean,warm_ocean,deep_lukewarm_ocean", "ANY");

        final boolean enabled;
        final int weight;
        final int min;
        final int max;
        final String biomes;
        final String spawnTime;

        Defaults(boolean enabled, int weight, int min, int max, String biomes, String spawnTime) {
            this.enabled = enabled;
            this.weight = weight;
            this.min = min;
            this.max = max;
            this.biomes = biomes;
            this.spawnTime = spawnTime;
        }
    }
}