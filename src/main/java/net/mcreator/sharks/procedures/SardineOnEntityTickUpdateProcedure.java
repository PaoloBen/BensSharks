package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;
import net.mcreator.sharks.procedures.SmoothTurns4Procedure;

public class SardineOnEntityTickUpdateProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;

		// 1. DRY OUT LOGIC (Suffocation)
		if (!entity.isInWaterOrBubble()) {
			double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
			entity.getPersistentData().putDouble("DryTime", dryTimer);
			if (dryTimer > 300 && dryTimer % 20 == 0) {
				entity.hurt(entity.damageSources().dryOut(), 2.0F);
			}
		} else {
			entity.getPersistentData().putDouble("DryTime", 0);
		}

		// 2. MOVEMENT & BEHAVIOR
		SmoothTurns4Procedure.execute(entity);
	}
}