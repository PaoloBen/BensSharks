package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import net.mcreator.sharks.init.BenssharksModMobEffects;

public class FrenzyEffectExpiresProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		double SwimSpeed = 0;
		double KBRes = 0;
		if (entity.getPersistentData().getBoolean("Sprinting") == true) {
			entity.getPersistentData().putBoolean("Sprinting", false);
		}
		if (entity instanceof LivingEntity _entity)
			_entity.removeEffect(BenssharksModMobEffects.FRENZY.get());
	}
}
