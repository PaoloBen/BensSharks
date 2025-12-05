package net.mcreator.sharks.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.sharks.entity.ThresherSharkEntity;

public class ThresherSharkModel extends GeoModel<ThresherSharkEntity> {
	@Override
	public ResourceLocation getAnimationResource(ThresherSharkEntity entity) {
		return new ResourceLocation("benssharks", "animations/threshershark.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ThresherSharkEntity entity) {
		return new ResourceLocation("benssharks", "geo/threshershark.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ThresherSharkEntity entity) {
		return new ResourceLocation("benssharks", "textures/entities/" + entity.getTexture() + ".png");
	}

}
