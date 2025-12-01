package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;

public class ShrakBoundingBoxScaleProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		if ((entity.getDisplayName().getString()).equals("Deep Blue")) {
			return 2.66;
		}
		return 2;
	}
}
