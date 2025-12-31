package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class SeekingArrowGhostLogicProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingAttackEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event);
		}
	}

	public static void execute() {
		execute(null);
	}

	private static void execute(@Nullable Event event) {
		// 1. SAFE CAST: Check if this is actually an Attack Event
		if (event instanceof net.minecraftforge.event.entity.living.LivingAttackEvent _event) {
			// 2. EXTRACT ENTITIES using the specific "_event" variable
			Entity immediatesourceentity = _event.getSource().getDirectEntity();
			Entity entity = _event.getEntity();
			// 3. NULL CHECK
			if (immediatesourceentity != null && entity != null) {
				// 4. IS THIS OUR SEEKING ARROW?
				if (immediatesourceentity.getPersistentData().getBoolean("ActivelySeeking")) {
					// 5. IS THE VICTIM *NOT* AN ENEMY?
					// We allow hits on Monsters. We BLOCK hits on everything else (Pets, Cows, Players).
					if (!(entity instanceof net.minecraft.world.entity.monster.Enemy)) {
						// 6. CANCEL THE DAMAGE
						// This makes the arrow ghost through harmlessly.
						if (event.isCancelable()) {
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
}
