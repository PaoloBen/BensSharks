package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.MakoSharkEntity;
import net.mcreator.sharks.BenssharksMod;

public class MakoSharkOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof MakoSharkEntity && !entity.isInWaterOrBubble()) {
			if (entity instanceof MakoSharkEntity) {
				((MakoSharkEntity) entity).setAnimation("land");
			}
			BenssharksMod.queueServerWork(600, () -> {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.DRYOUT_EFFECT.get(), 600, 0, true, false));
			});
		} else if (entity.isInWaterOrBubble()) {
			if (entity instanceof LivingEntity _entity)
				_entity.removeEffect(BenssharksModMobEffects.DRYOUT_EFFECT.get());
		}
		if (entity instanceof MakoSharkEntity && entity.isInWaterOrBubble()) {
			if (entity.getPersistentData().getBoolean("Sprinting") == true) {
				if (entity instanceof MakoSharkEntity) {
					((MakoSharkEntity) entity).setAnimation("sprint");
				}
			} else {
				if (entity instanceof MakoSharkEntity) {
					((MakoSharkEntity) entity).setAnimation("empty");
				}
			}
		}
	}
}
