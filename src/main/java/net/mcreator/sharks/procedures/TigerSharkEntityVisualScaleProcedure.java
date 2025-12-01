package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;

public class TigerSharkEntityVisualScaleProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		if ((entity.getDisplayName().getString()).equals("Kamakai")) {
			return 1.2;
		}
		return 1;
	}
}
