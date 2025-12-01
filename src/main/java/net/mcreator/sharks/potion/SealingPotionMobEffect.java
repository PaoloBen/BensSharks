
package net.mcreator.sharks.potion;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.mcreator.sharks.procedures.SealingPotionOnEffectActiveTickProcedure;
import net.mcreator.sharks.procedures.SealingPotionEffectExpiresProcedure;

public class SealingPotionMobEffect extends MobEffect {
	public SealingPotionMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -3230104);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		SealingPotionOnEffectActiveTickProcedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		SealingPotionEffectExpiresProcedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
