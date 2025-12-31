package net.mcreator.sharks.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.sharks.entity.MegamouthSharkEntity;

public class MegamouthSharkModel extends GeoModel<MegamouthSharkEntity> {
	@Override
	public ResourceLocation getAnimationResource(MegamouthSharkEntity entity) {
		return new ResourceLocation("benssharks", "animations/megamouth_shark.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MegamouthSharkEntity entity) {
		return new ResourceLocation("benssharks", "geo/megamouth_shark.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MegamouthSharkEntity entity) {
		return new ResourceLocation("benssharks", "textures/entities/" + entity.getTexture() + ".png");
	}

}
