
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.sharks.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import net.mcreator.sharks.client.renderer.WhitetipSharkRenderer;
import net.mcreator.sharks.client.renderer.WhaleSharkRenderer;
import net.mcreator.sharks.client.renderer.TigerSharkRenderer;
import net.mcreator.sharks.client.renderer.ThalassogerRenderer;
import net.mcreator.sharks.client.renderer.ShrakRenderer;
import net.mcreator.sharks.client.renderer.SharkMinionRenderer;
import net.mcreator.sharks.client.renderer.SeekingArrowRenderer;
import net.mcreator.sharks.client.renderer.SeekerSharkProjectileRenderer;
import net.mcreator.sharks.client.renderer.SeaLionRenderer;
import net.mcreator.sharks.client.renderer.SawsharkRenderer;
import net.mcreator.sharks.client.renderer.RollParticleRenderer;
import net.mcreator.sharks.client.renderer.RemoraRenderer;
import net.mcreator.sharks.client.renderer.PilotFishRenderer;
import net.mcreator.sharks.client.renderer.NurseSharkRenderer;
import net.mcreator.sharks.client.renderer.MegalodonRenderer;
import net.mcreator.sharks.client.renderer.MakoSharkRenderer;
import net.mcreator.sharks.client.renderer.LeopardSharkRenderer;
import net.mcreator.sharks.client.renderer.LemonSharkRenderer;
import net.mcreator.sharks.client.renderer.LandSharkRenderer;
import net.mcreator.sharks.client.renderer.KrillRenderer;
import net.mcreator.sharks.client.renderer.GreenlandSharkRenderer;
import net.mcreator.sharks.client.renderer.GreaterAxodileRenderer;
import net.mcreator.sharks.client.renderer.GoblinSharkRenderer;
import net.mcreator.sharks.client.renderer.CookiecutterSharkRenderer;
import net.mcreator.sharks.client.renderer.BullSharkRenderer;
import net.mcreator.sharks.client.renderer.BonnetheadSharkRenderer;
import net.mcreator.sharks.client.renderer.BlueSharkRenderer;
import net.mcreator.sharks.client.renderer.BlacktipReefSharkRenderer;
import net.mcreator.sharks.client.renderer.BaskingSharkRenderer;
import net.mcreator.sharks.client.renderer.BarracudaRenderer;
import net.mcreator.sharks.client.renderer.AxodileRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BenssharksModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(BenssharksModEntities.GREATWHITE_SHARK.get(), ShrakRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.NURSE_SHARK.get(), NurseSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.REMORA.get(), RemoraRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.TIGER_SHARK.get(), TigerSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.AXODILE.get(), AxodileRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.BLUE_SHARK.get(), BlueSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.BONNETHEAD_SHARK.get(), BonnetheadSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.MAKO_SHARK.get(), MakoSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.COOKIECUTTER_SHARK.get(), CookiecutterSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.BLACKTIP_REEF_SHARK.get(), BlacktipReefSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.BASKING_SHARK.get(), BaskingSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.BULL_SHARK.get(), BullSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.MEGALODON.get(), MegalodonRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.LAND_SHARK.get(), LandSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.LEMON_SHARK.get(), LemonSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.THALASSOGER.get(), ThalassogerRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.SEEKER_SHARK_PROJECTILE.get(), SeekerSharkProjectileRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.KRILL.get(), KrillRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.WHALE_SHARK.get(), WhaleSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.SEEKING_ARROW.get(), SeekingArrowRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.PILOT_FISH.get(), PilotFishRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.GREATER_AXODILE.get(), GreaterAxodileRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.ROLL_PARTICLE.get(), RollParticleRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.GREENLAND_SHARK.get(), GreenlandSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.WHITETIP_SHARK.get(), WhitetipSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.BARRACUDA.get(), BarracudaRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.LEOPARD_SHARK.get(), LeopardSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.GOBLIN_SHARK.get(), GoblinSharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.SAWSHARK.get(), SawsharkRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.SHARK_MINION.get(), SharkMinionRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.MUTATED_EGG_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(BenssharksModEntities.SEA_LION.get(), SeaLionRenderer::new);
	}
}
