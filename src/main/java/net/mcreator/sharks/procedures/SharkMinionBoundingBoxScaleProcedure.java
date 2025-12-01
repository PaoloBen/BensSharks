package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;

public class SharkMinionBoundingBoxScaleProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		if ((entity.getDisplayName().getString()).equals("Gura") || (entity.getDisplayName().getString()).equals("Gawr Gura")) {
			return 1.875;
		}
		return 1;
	}
}
