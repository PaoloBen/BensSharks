package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

@Mod.EventBusSubscriber
public class MobDeathParticleProcedure {

	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		if (event != null && event.getEntity() != null) {
			// In LivingTickEvent, getEntity() is already a LivingEntity, so we just use it.
			LivingEntity entity = event.getEntity();
			
			// 1. Check if the entity is dead and has reached the last tick of its 20-tick delay
			if (entity.isDeadOrDying() && entity.deathTime == 19) {
				
				// 2. Check if the entity belongs to your mod's mob tag
				if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("benssharks", "mobs")))) {
					execute(entity);
				}
			}
		}
	}

	public static void execute(LivingEntity entity) {
		if (entity == null || entity.level().isClientSide()) return;

		if (entity.level() instanceof ServerLevel _level) {
			double x = entity.getX();
			double y = entity.getY() + (entity.getBbHeight() / 2);
			double z = entity.getZ();

			// 3. Balanced Particle Count (Scaled by hitbox width)
			int particleCount = (int) (8 * Math.max(1, entity.getBbWidth()));
			
			_level.sendParticles(ParticleTypes.POOF, 
				x, y, z, 
				particleCount, 
				entity.getBbWidth() / 4, 
				entity.getBbHeight() / 4, 
				entity.getBbWidth() / 4, 
				0.02
			);

			// 4. Extra burst for massive sharks
			if (entity.getBbWidth() > 2.0) {
				_level.sendParticles(ParticleTypes.LARGE_SMOKE, 
					x, y, z, 
					3, 
					entity.getBbWidth() / 3, 
					0.2, 
					entity.getBbWidth() / 3, 
					0.02
				);
			}
		}
	}
}