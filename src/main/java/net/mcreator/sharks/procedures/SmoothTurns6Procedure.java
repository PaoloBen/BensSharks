package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

@Mod.EventBusSubscriber(modid = "benssharks")
public class SmoothTurns6Procedure {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        // Logic removed to prevent interference with Angelshark hiding/lunging.
    }
}