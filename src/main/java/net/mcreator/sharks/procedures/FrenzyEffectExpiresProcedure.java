package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;

public class FrenzyEffectExpiresProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		double SwimSpeed = 0;
		double KBRes = 0;
		if (entity.getPersistentData().getBoolean("Sprinting") == true) {
			entity.getPersistentData().putBoolean("Sprinting", false);
		}
	}
}
