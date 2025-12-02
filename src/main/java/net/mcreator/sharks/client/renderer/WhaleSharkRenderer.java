
package net.mcreator.sharks.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.sharks.procedures.WhaleSharkEntityVisualScaleProcedure;
import net.mcreator.sharks.entity.model.WhaleSharkModel;
import net.mcreator.sharks.entity.WhaleSharkEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class WhaleSharkRenderer extends GeoEntityRenderer<WhaleSharkEntity> {
	public WhaleSharkRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new WhaleSharkModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	public RenderType getRenderType(WhaleSharkEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, WhaleSharkEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		float scale = (float) WhaleSharkEntityVisualScaleProcedure.execute(entity);
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
