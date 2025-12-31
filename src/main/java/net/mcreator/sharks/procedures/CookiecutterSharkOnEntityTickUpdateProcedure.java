package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.CookiecutterSharkEntity;
import net.mcreator.sharks.BenssharksMod;

public class CookiecutterSharkOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof CookiecutterSharkEntity && !entity.isInWaterOrBubble()) {
			if (entity instanceof CookiecutterSharkEntity) {
				((CookiecutterSharkEntity) entity).setAnimation("flop");
			}
			BenssharksMod.queueServerWork(600, () -> {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.DRYOUT_EFFECT.get(), 600, 0, true, false));
			});
		} else if (entity.isInWaterOrBubble()) {
			if (entity instanceof LivingEntity _entity)
				_entity.removeEffect(BenssharksModMobEffects.DRYOUT_EFFECT.get());
		}
	}
}
