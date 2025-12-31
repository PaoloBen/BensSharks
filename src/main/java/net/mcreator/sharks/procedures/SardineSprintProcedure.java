package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.mcreator.sharks.entity.SardineEntity;

@Mod.EventBusSubscriber
public class SardineSprintProcedure {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		if (event.getEntity() instanceof SardineEntity) {
			execute(event.getEntity());
		}
	}

	public static void execute(Object entity) {
		// Logic is centralized in SmoothTurns4Procedure now.
		// We call it here if needed, but it's already called in baseTick of the entity.
	}
}