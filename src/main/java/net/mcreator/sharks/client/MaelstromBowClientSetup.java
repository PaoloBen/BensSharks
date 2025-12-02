package net.mcreator.sharks.client;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

// IMPORTANT: Replace "sharks" below with your actual MOD ID if it is different.
// Based on your package name, it seems to be "sharks".
@Mod.EventBusSubscriber(modid = "benssharks", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MaelstromBowClientSetup {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 1. Locate the item. 
            // We use the Registry lookup to avoid crashing if you don't have a static reference handy.
            // NOTE: Check if your item ID is "maelstrom_bow" or just "maelstrombow". 
            // I am assuming "maelstrom_bow" based on standard MCreator naming conventions.
            // If the game crashes with a NullPointerException here, change "maelstrom_bow" to the correct registry name.
            Item bowItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("benssharks", "maelstrom_bow"));

            if (bowItem != null) {
                // 2. Register the "pull" property (How far back the string is drawn)
                ItemProperties.register(bowItem, new ResourceLocation("pull"), (stack, level, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }
                    // If not using this specific bow, pull is 0.
                    if (entity.getUseItem() != stack) {
                        return 0.0F;
                    }
                    // Calculate pull progress based on how long it's been used
                    return (float)(stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                });

                // 3. Register the "pulling" property (Is the user currently holding right-click?)
                ItemProperties.register(bowItem, new ResourceLocation("pulling"), (stack, level, entity, seed) -> {
                    return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                });
            }
        });
    }
}