
package net.mcreator.sharks.client.screens;

import org.checkerframework.checker.units.qual.h;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;

import net.mcreator.sharks.procedures.ReturnOverchargeTextProcedure;
import net.mcreator.sharks.procedures.ConditionShowOverchargeProcedure;
import net.mcreator.sharks.procedures.ConditionShowCharge9Procedure;
import net.mcreator.sharks.procedures.ConditionShowCharge8Procedure;
import net.mcreator.sharks.procedures.ConditionShowCharge7Procedure;
import net.mcreator.sharks.procedures.ConditionShowCharge6Procedure;
import net.mcreator.sharks.procedures.ConditionShowCharge5Procedure;
import net.mcreator.sharks.procedures.ConditionShowCharge4Procedure;
import net.mcreator.sharks.procedures.ConditionShowCharge3Procedure;
import net.mcreator.sharks.procedures.ConditionShowCharge2Procedure;
import net.mcreator.sharks.procedures.ConditionShowCharge1Procedure;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class ThresherChargeHUDOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		if (ConditionShowCharge1Procedure.execute(entity)) {
			if (ConditionShowCharge1Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("benssharks:textures/screens/thresher_charge_icon5.png"), w / 2 + 102, h - 16, 0, 0, 198, 16, 198, 16);
			}
			if (ConditionShowCharge9Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("benssharks:textures/screens/thresher_charge_icon3.png"), w / 2 + 187, h - 16, 0, 0, 16, 16, 16, 16);
			}
			if (ConditionShowCharge8Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("benssharks:textures/screens/thresher_charge_icon2.png"), w / 2 + 177, h - 16, 0, 0, 16, 16, 16, 16);
			}
			if (ConditionShowCharge7Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("benssharks:textures/screens/thresher_charge_icon2.png"), w / 2 + 167, h - 16, 0, 0, 16, 16, 16, 16);
			}
			if (ConditionShowCharge6Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("benssharks:textures/screens/thresher_charge_icon2.png"), w / 2 + 157, h - 16, 0, 0, 16, 16, 16, 16);
			}
			if (ConditionShowCharge5Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("benssharks:textures/screens/thresher_charge_icon2.png"), w / 2 + 147, h - 16, 0, 0, 16, 16, 16, 16);
			}
			if (ConditionShowCharge4Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("benssharks:textures/screens/thresher_charge_icon2.png"), w / 2 + 137, h - 16, 0, 0, 16, 16, 16, 16);
			}
			if (ConditionShowCharge3Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("benssharks:textures/screens/thresher_charge_icon2.png"), w / 2 + 127, h - 16, 0, 0, 16, 16, 16, 16);
			}
			if (ConditionShowCharge2Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("benssharks:textures/screens/thresher_charge_icon2.png"), w / 2 + 117, h - 16, 0, 0, 16, 16, 16, 16);
			}
			if (ConditionShowCharge1Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("benssharks:textures/screens/thresher_charge_icon.png"), w / 2 + 107, h - 16, 0, 0, 16, 16, 16, 16);
			}
			if (ConditionShowOverchargeProcedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,

						ReturnOverchargeTextProcedure.execute(entity), w / 2 + 152, h - 17, -16777216, false);
			if (ConditionShowOverchargeProcedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,

						ReturnOverchargeTextProcedure.execute(entity), w / 2 + 152, h - 15, -16777216, false);
			if (ConditionShowOverchargeProcedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,

						ReturnOverchargeTextProcedure.execute(entity), w / 2 + 151, h - 16, -16777216, false);
			if (ConditionShowOverchargeProcedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,

						ReturnOverchargeTextProcedure.execute(entity), w / 2 + 153, h - 16, -16777216, false);
			if (ConditionShowOverchargeProcedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,

						ReturnOverchargeTextProcedure.execute(entity), w / 2 + 152, h - 16, -13988097, false);
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
