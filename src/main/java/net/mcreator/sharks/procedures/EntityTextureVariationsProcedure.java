package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import net.mcreator.sharks.entity.WhaleSharkEntity;
import net.mcreator.sharks.entity.ShrakEntity;
import net.mcreator.sharks.entity.SharkMinionEntity;
import net.mcreator.sharks.entity.SeaLionEntity;
import net.mcreator.sharks.entity.NurseSharkEntity;
import net.mcreator.sharks.entity.LeopardSharkEntity;
import net.mcreator.sharks.entity.LemonSharkEntity;
import net.mcreator.sharks.entity.LandSharkEntity;
import net.mcreator.sharks.entity.BonnetheadSharkEntity;
import net.mcreator.sharks.entity.BlacktipReefSharkEntity;

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
			} else if ((entity.getDisplayName().getString()).equals("Tralalero Tralala")) {
				if (entity instanceof LandSharkEntity animatable)
					animatable.setTexture("tralalero");
			} else {
				if (entity instanceof LandSharkEntity animatable)
					animatable.setTexture("land_shark");
			}
		}
		if (entity instanceof SeaLionEntity) {
			if (entity instanceof LivingEntity _livEnt11 && _livEnt11.isBaby()) {
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
		if (entity instanceof BonnetheadSharkEntity) {
			if (entity instanceof LivingEntity _livEnt20 && _livEnt20.isBaby()) {
				if (entity instanceof BonnetheadSharkEntity animatable)
					animatable.setTexture("bonnethead_baby");
			} else {
				if (entity instanceof BonnetheadSharkEntity animatable)
					animatable.setTexture("bonnet");
			}
		}
		if (entity instanceof BlacktipReefSharkEntity) {
			if (entity instanceof LivingEntity _livEnt24 && _livEnt24.isBaby()) {
				if (entity instanceof BlacktipReefSharkEntity animatable)
					animatable.setTexture("blacktip_baby");
			} else {
				if (entity instanceof BlacktipReefSharkEntity animatable)
					animatable.setTexture("blacktip");
			}
		}
		if (entity instanceof NurseSharkEntity) {
			if (entity instanceof LivingEntity _livEnt28 && _livEnt28.isBaby()) {
				if (entity instanceof NurseSharkEntity animatable)
					animatable.setTexture("nurse_baby");
			} else {
				if (entity instanceof NurseSharkEntity animatable)
					animatable.setTexture("nurse");
			}
		}
		if (entity instanceof LeopardSharkEntity) {
			if (entity instanceof LivingEntity _livEnt32 && _livEnt32.isBaby()) {
				if (entity instanceof LeopardSharkEntity animatable)
					animatable.setTexture("leopardshark_baby");
			} else {
				if (entity instanceof LeopardSharkEntity animatable)
					animatable.setTexture("leopardshark");
			}
		}
		if (entity instanceof LemonSharkEntity) {
			if (entity instanceof LivingEntity _livEnt36 && _livEnt36.isBaby()) {
				if (entity instanceof LemonSharkEntity animatable)
					animatable.setTexture("lemonshark_baby");
			} else {
				if (entity instanceof LemonSharkEntity animatable)
					animatable.setTexture("lemonshark");
			}
		}
		if (entity instanceof WhaleSharkEntity) {
			if (entity instanceof LivingEntity _livEnt40 && _livEnt40.isBaby()) {
				if (entity instanceof WhaleSharkEntity animatable)
					animatable.setTexture("whaleshark_baby");
			} else {
				if (entity instanceof WhaleSharkEntity animatable)
					animatable.setTexture("whaleshark");
			}
		}
	}
}
