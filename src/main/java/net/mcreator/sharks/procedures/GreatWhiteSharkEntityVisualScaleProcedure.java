package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;

public class GreatWhiteSharkEntityVisualScaleProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		if ((entity.getDisplayName().getString()).equals("Deep Blue")) {
			return 1.33;
		}
		return 1;
	}
}
