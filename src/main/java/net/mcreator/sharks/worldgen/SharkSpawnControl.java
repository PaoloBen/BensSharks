package net.mcreator.sharks.worldgen;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.mcreator.sharks.BenssharksMod;
import net.mcreator.sharks.configuration.SpawnsConfiguration;
import net.mcreator.sharks.init.BenssharksModEntities;

import java.util.Random;

@Mod.EventBusSubscriber(modid = BenssharksMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SharkSpawnControl {

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Register all mobs with the custom water-safe predicate
            register(BenssharksModEntities.ANGELSHARK.get(), SpawnsConfiguration.ANGELSHARK_SPAWN_TIME);
            register(BenssharksModEntities.AXODILE.get(), SpawnsConfiguration.AXODILE_SPAWN_TIME);
            register(BenssharksModEntities.BARRACUDA.get(), SpawnsConfiguration.BARRACUDA_SPAWN_TIME);
            register(BenssharksModEntities.BASKING_SHARK.get(), SpawnsConfiguration.BASKING_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.BLACKTIP_REEF_SHARK.get(), SpawnsConfiguration.BLACKTIP_REEF_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.BLUE_SHARK.get(), SpawnsConfiguration.BLUE_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.BONNETHEAD_SHARK.get(), SpawnsConfiguration.BONNETHEAD_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.BULL_SHARK.get(), SpawnsConfiguration.BULL_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.COOKIECUTTER_SHARK.get(), SpawnsConfiguration.COOKIECUTTER_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.GOBLIN_SHARK.get(), SpawnsConfiguration.GOBLIN_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.GREATER_AXODILE.get(), SpawnsConfiguration.GREATER_AXODILE_SPAWN_TIME);
            register(BenssharksModEntities.GREATWHITE_SHARK.get(), SpawnsConfiguration.GREATWHITE_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.GREENLAND_SHARK.get(), SpawnsConfiguration.GREENLAND_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.KRILL.get(), SpawnsConfiguration.KRILL_SPAWN_TIME);
            register(BenssharksModEntities.LEMON_SHARK.get(), SpawnsConfiguration.LEMON_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.LEOPARD_SHARK.get(), SpawnsConfiguration.LEOPARD_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.MAKO_SHARK.get(), SpawnsConfiguration.MAKO_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.MEGALODON.get(), SpawnsConfiguration.MEGALODON_SPAWN_TIME);
            register(BenssharksModEntities.MEGAMOUTH_SHARK.get(), SpawnsConfiguration.MEGAMOUTH_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.NURSE_SHARK.get(), SpawnsConfiguration.NURSE_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.PILOT_FISH.get(), SpawnsConfiguration.PILOT_FISH_SPAWN_TIME);
            register(BenssharksModEntities.REMORA.get(), SpawnsConfiguration.REMORA_SPAWN_TIME);
            register(BenssharksModEntities.SARDINE.get(), SpawnsConfiguration.SARDINE_SPAWN_TIME);
            register(BenssharksModEntities.SHOAL.get(), SpawnsConfiguration.SHOAL_SPAWN_TIME);
            register(BenssharksModEntities.SAWSHARK.get(), SpawnsConfiguration.SAWSHARK_SPAWN_TIME);
            register(BenssharksModEntities.SEA_LION.get(), SpawnsConfiguration.SEA_LION_SPAWN_TIME);
            register(BenssharksModEntities.THRESHER_SHARK.get(), SpawnsConfiguration.THRESHER_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.TIGER_SHARK.get(), SpawnsConfiguration.TIGER_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.WHALE_SHARK.get(), SpawnsConfiguration.WHALE_SHARK_SPAWN_TIME);
            register(BenssharksModEntities.WHITETIP_SHARK.get(), SpawnsConfiguration.WHITETIP_SHARK_SPAWN_TIME);
        });
    }

    private static <T extends Mob> void register(EntityType<T> entity, ForgeConfigSpec.ConfigValue<String> timeConfig) {
        SpawnPlacements.register(entity,
                SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> {
                    // --- 1. Time Restriction Check ---
                    if (world instanceof Level level) {
                        String mode = timeConfig.get();
                        boolean isDay = level.isDay();

                        if (mode.equalsIgnoreCase("NIGHT") && isDay) return false;
                        if (mode.equalsIgnoreCase("DAY") && !isDay) return false;
                    }

                    // --- 2. Correct Water Check ---
                    // We DO NOT use Mob.checkMobSpawnRules() because it requires solid ground.
                    // Instead, we manually check if the spawn block is water.
                    
                    return world.getFluidState(pos).is(FluidTags.WATER) 
                        && world.getFluidState(pos.below()).is(FluidTags.WATER); 
                });
    }
}