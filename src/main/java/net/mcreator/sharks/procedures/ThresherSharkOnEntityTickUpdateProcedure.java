package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.ThresherSharkEntity;
import net.mcreator.sharks.BenssharksMod;

public class ThresherSharkOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof ThresherSharkEntity && !entity.isInWaterOrBubble()) {
			BenssharksMod.queueServerWork(600, () -> {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.DRYOUT_EFFECT.get(), 600, 0, true, false));
			});
		} else if (entity.isInWaterOrBubble()) {
			if (entity instanceof LivingEntity _entity)
				_entity.removeEffect(BenssharksModMobEffects.DRYOUT_EFFECT.get());
		}
		if (entity instanceof ThresherSharkEntity && entity.isInWaterOrBubble()) {
			if (entity.getPersistentData().getBoolean("Sprinting") == true) {
				if (entity instanceof ThresherSharkEntity) {
					((ThresherSharkEntity) entity).setAnimation("sprint");
				}
			} else {
				if (entity instanceof ThresherSharkEntity) {
					((ThresherSharkEntity) entity).setAnimation("empty");
				}
			}
		} // Cast to ThresherSharkEntity
		if (entity instanceof net.mcreator.sharks.entity.ThresherSharkEntity shark) {
			// --- 1. CLIENT SYNC BRIDGE ---
			String syncedAnim = shark.getSyncedAnimation();
			if ("whip".equals(syncedAnim)) {
				shark.animationprocedure = "whip";
			}
			// --- 2. SERVER LOGIC ---
			if (!world.isClientSide()) {
				// [Normal Bite Sound Logic Omitted - Keep your existing sound logic here]
				if (shark.swingTime == 1) {
					// ...
				}
				double cooldown = entity.getPersistentData().getDouble("WhipCooldown");
				double timer = entity.getPersistentData().getDouble("WhipTimer");
				if (cooldown > 0)
					entity.getPersistentData().putDouble("WhipCooldown", cooldown - 1);
				if (timer > 0) {
					entity.getPersistentData().putDouble("WhipTimer", timer - 1);
					// A. LOCK ANIMATION
					if (timer > 1) {
						if (!"whip".equals(syncedAnim))
							shark.setAnimation("whip");
						if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
							living.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 5, 10, false, false));
						}
					} else {
						shark.setAnimation("undefined");
					}
					// B. DAMAGE EVENT (Tick 5)
					if (timer == 5) {
						// --- FLAG START: THIS IS WHIP DAMAGE ---
						entity.getPersistentData().putBoolean("WhipHitFrame", true);
						double baseDamage = shark.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue();
						net.minecraft.world.damagesource.DamageSource damageSource = shark.damageSources().mobAttack(shark);
						net.minecraft.world.phys.AABB box = entity.getBoundingBox().inflate(5);
						java.util.List<net.minecraft.world.entity.LivingEntity> targets = world.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, box);
						for (net.minecraft.world.entity.LivingEntity vic : targets) {
							if (vic != entity && vic.isAlive() && !vic.isInvulnerable() && entity.distanceToSqr(vic) <= 25) {
								net.minecraft.world.phys.Vec3 lookDir = entity.getViewVector(1.0F).normalize();
								net.minecraft.world.phys.Vec3 vicDir = vic.position().subtract(entity.position()).normalize();
								if (lookDir.dot(vicDir) > 0.3) {
									vic.hurt(damageSource, (float) (baseDamage * 2));
									vic.knockback(2.5, entity.getX() - vic.getX(), entity.getZ() - vic.getZ());
									if (world instanceof net.minecraft.world.level.Level _level) {
										_level.playSound(null, vic.blockPosition(), net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS.getValue(new net.minecraft.resources.ResourceLocation("entity.player.attack.knockback")),
												net.minecraft.sounds.SoundSource.HOSTILE, 1, 1);
									}
								}
							}
						}
						// --- FLAG END: TURN IT OFF IMMEDIATELY ---
						entity.getPersistentData().putBoolean("WhipHitFrame", false);
					}
				}
				// --- TRIGGER PHASE ---
				else if (cooldown == 0 && shark.getTarget() != null) {
					net.minecraft.world.entity.LivingEntity target = shark.getTarget();
					if (target.isAlive() && entity.distanceToSqr(target) <= 16) {
						shark.setAnimation("whip");
						entity.getPersistentData().putDouble("WhipTimer", 20);
						entity.getPersistentData().putDouble("WhipCooldown", 100);
					}
				}
			}
		}
	}
}
