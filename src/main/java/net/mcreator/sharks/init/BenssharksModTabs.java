
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.sharks.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.sharks.BenssharksMod;

public class BenssharksModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BenssharksMod.MODID);
	public static final RegistryObject<CreativeModeTab> BENS_SHARKS = REGISTRY.register("bens_sharks",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.benssharks.bens_sharks")).icon(() -> new ItemStack(BenssharksModBlocks.SHARK_PLUSH_BLOCK.get())).displayItems((parameters, tabData) -> {
				tabData.accept(BenssharksModItems.AXODILE_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.BASKING_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.BARRACUDA_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.BLACKTIP_REEF_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.BLUE_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.BONNETHEAD_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.BULL_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.COOKIECUTTER_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.GOBLIN_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.GREATER_AXODILE_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.GREATWHITE_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.GREENLAND_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.KRILL_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.LAND_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.LEMON_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.LEOPARD_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.MAKO_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.MEGALODON_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.NURSE_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.WHITETIP_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.PILOT_FISH_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.REMORA_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.SAWSHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.SEA_LION_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.SHARK_MINION_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.THRESHER_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.TIGER_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.THALASSOGER_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.WHALE_SHARK_SPAWN_EGG.get());
				tabData.accept(BenssharksModItems.AXOLEATHER.get());
				tabData.accept(BenssharksModItems.AXOSCUTE.get());
				tabData.accept(BenssharksModItems.BARRACUDA_BUCKET.get());
				tabData.accept(BenssharksModItems.BLACKTIP_REEF_SHARK_BUCKET.get());
				tabData.accept(BenssharksModItems.BONNETHEAD_SHARK_BUCKET.get());
				tabData.accept(BenssharksModItems.COOKIECUTTER_SHARK_BUCKET.get());
				tabData.accept(BenssharksModItems.LEOPARD_SHARK_BUCKET.get());
				tabData.accept(BenssharksModItems.PILOT_FISH_BUCKET.get());
				tabData.accept(BenssharksModItems.REMORA_BUCKET.get());
				tabData.accept(BenssharksModItems.SAWSHARK_BUCKET.get());
				tabData.accept(BenssharksModItems.CELLOPHANE_NOODLES.get());
				tabData.accept(BenssharksModItems.COOKIECUTTER_SHARK_LIVE.get());
				tabData.accept(BenssharksModItems.FISH_BROTH.get());
				tabData.accept(BenssharksModItems.FISH_BUCKET.get());
				tabData.accept(BenssharksModItems.JAGGED_HELMET.get());
				tabData.accept(BenssharksModItems.JAGGED_CHESTPLATE.get());
				tabData.accept(BenssharksModItems.JAGGED_LEGGINGS.get());
				tabData.accept(BenssharksModItems.JAGGED_BOOTS.get());
				tabData.accept(BenssharksModItems.KRILL_ITEM.get());
				tabData.accept(BenssharksModItems.KRILL_BUCKET.get());
				tabData.accept(BenssharksModItems.KRILL_CAKE.get());
				tabData.accept(BenssharksModItems.KRILL_NOODLES.get());
				tabData.accept(BenssharksModBlocks.LAND_MINE.get().asItem());
				tabData.accept(BenssharksModItems.DENT.get());
				tabData.accept(BenssharksModItems.MAELSTROM.get());
				tabData.accept(BenssharksModItems.MAELSTROM_BOW.get());
				tabData.accept(BenssharksModItems.SHARK_TOOTH_CLUB.get());
				tabData.accept(BenssharksModItems.SPETUM.get());
				tabData.accept(BenssharksModItems.RAW_BARRACUDA.get());
				tabData.accept(BenssharksModItems.COOKED_BARRACUDA.get());
				tabData.accept(BenssharksModItems.RAW_PILOT_FISH.get());
				tabData.accept(BenssharksModItems.COOKED_PILOT_FISH.get());
				tabData.accept(BenssharksModItems.SHARK_FIN.get());
				tabData.accept(BenssharksModBlocks.SHARK_PLUSH_BLOCK.get().asItem());
				tabData.accept(BenssharksModItems.SHARK_TOOTH.get());
				tabData.accept(BenssharksModItems.MEGALODON_TOOTH.get());
				tabData.accept(BenssharksModItems.SHARK_FIN_SOUP.get());
				tabData.accept(BenssharksModItems.SOUP_SHARKFIN.get());
				tabData.accept(BenssharksModItems.STARCH.get());
				tabData.accept(BenssharksModItems.SUCKER.get());
				tabData.accept(BenssharksModItems.MUTATED_EGG.get());
				tabData.accept(BenssharksModItems.EGG_CAPSULE.get());
			}).build());
}
