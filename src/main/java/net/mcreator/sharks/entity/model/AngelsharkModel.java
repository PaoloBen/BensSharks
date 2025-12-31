package net.mcreator.sharks.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.sharks.entity.AngelsharkEntity;

public class AngelsharkModel extends GeoModel<AngelsharkEntity> {
	@Override
	public ResourceLocation getAnimationResource(AngelsharkEntity entity) {
		return new ResourceLocation("benssharks", "animations/angelshark.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AngelsharkEntity entity) {
		return new ResourceLocation("benssharks", "geo/angelshark.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AngelsharkEntity entity) {
		return new ResourceLocation("benssharks", "textures/entities/" + entity.getTexture() + ".png");
	}

}
