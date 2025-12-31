package net.mcreator.sharks.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.sharks.entity.ShoalEntity;

public class ShoalModel extends GeoModel<ShoalEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShoalEntity entity) {
		return new ResourceLocation("benssharks", "animations/sardine.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShoalEntity entity) {
		return new ResourceLocation("benssharks", "geo/sardine.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShoalEntity entity) {
		return new ResourceLocation("benssharks", "textures/entities/" + entity.getTexture() + ".png");
	}

}
