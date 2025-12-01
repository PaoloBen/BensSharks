package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import net.mcreator.sharks.entity.ShrakEntity;
import net.mcreator.sharks.entity.SharkMinionEntity;
import net.mcreator.sharks.entity.SeaLionEntity;
import net.mcreator.sharks.entity.LandSharkEntity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class EntityTextureVariationsProcedure {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof ShrakEntity) {
			if ((entity.getDisplayName().getString()).equals("Deep Blue")) {
				if (entity instanceof ShrakEntity animatable)
					animatable.setTexture("deepblue");
			} else {
				if (entity instanceof ShrakEntity animatable)
					animatable.setTexture("greatwhite");
			}
		}
		if (entity instanceof LandSharkEntity) {
			if ((entity.getDisplayName().getString()).equals("Jeff")) {
				if (entity instanceof LandSharkEntity animatable)
					animatable.setTexture("jeff");
			} else {
				if (entity instanceof LandSharkEntity animatable)
					animatable.setTexture("land_shark");
			}
		}
		if (entity instanceof SeaLionEntity) {
			if (entity instanceof LivingEntity _livEnt9 && _livEnt9.isBaby()) {
				if (entity instanceof SeaLionEntity animatable)
					animatable.setTexture("sea_lion_baby");
			} else {
				if (entity instanceof SeaLionEntity animatable)
					animatable.setTexture("sea_lion");
			}
		}
		if (entity instanceof SharkMinionEntity) {
			if ((entity.getDisplayName().getString()).equals("Gura") || (entity.getDisplayName().getString()).equals("Gawr Gura")) {
				if (entity instanceof SharkMinionEntity animatable)
					animatable.setTexture("gura");
			} else {
				if (entity instanceof SharkMinionEntity animatable)
					animatable.setTexture("sharkminion");
			}
		}
	}
}
