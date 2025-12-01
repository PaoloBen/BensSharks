package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;

public class TigerSharkBoundingBoxScaleProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		if ((entity.getDisplayName().getString()).equals("Kamakai")) {
			return 2.1;
		}
		return 1.75;
	}
}
