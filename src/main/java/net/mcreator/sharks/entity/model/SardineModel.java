package net.mcreator.sharks.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.sharks.entity.SardineEntity;

public class SardineModel extends GeoModel<SardineEntity> {
	@Override
	public ResourceLocation getAnimationResource(SardineEntity entity) {
		return new ResourceLocation("benssharks", "animations/sardine.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SardineEntity entity) {
		return new ResourceLocation("benssharks", "geo/sardine.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SardineEntity entity) {
		return new ResourceLocation("benssharks", "textures/entities/" + entity.getTexture() + ".png");
	}

}
