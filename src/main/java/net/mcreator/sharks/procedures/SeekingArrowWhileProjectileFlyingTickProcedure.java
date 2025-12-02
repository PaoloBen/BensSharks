package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class SeekingArrowWhileProjectileFlyingTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity immediatesourceentity) {
		if (entity == null || immediatesourceentity == null)
			return;
		double dis = 0;
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(30 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (immediatesourceentity.isOnFire()) {
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.FLAME, (immediatesourceentity.getX()), (immediatesourceentity.getY()), (immediatesourceentity.getZ()), 5, 0.05, 0.05, 0.05, 0.01);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.FLAME, (immediatesourceentity.getX()), (immediatesourceentity.getY()), (immediatesourceentity.getZ()), 3, 0.1, 0.1, 0.1, 0.1);
				} else {
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.GLOW_SQUID_INK, (immediatesourceentity.getX()), (immediatesourceentity.getY()), (immediatesourceentity.getZ()), 1, 0.01, 0.01, 0.01, 0);
				}
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(immediatesourceentity.getX(), immediatesourceentity.getY(), immediatesourceentity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.conduit.deactivate")),
								SoundSource.NEUTRAL, (float) 0.025, 2);
					} else {
						_level.playLocalSound((immediatesourceentity.getX()), (immediatesourceentity.getY()), (immediatesourceentity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.conduit.deactivate")),
								SoundSource.NEUTRAL, (float) 0.025, 2, false);
					}
				}
				if (((Entity) world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(x, y, z), 30, 30, 30), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null)) == entityiterator) {
					if (!(entityiterator == entity || entityiterator == immediatesourceentity || (entityiterator instanceof Player _plr ? _plr.getAbilities().instabuild : false)
							|| (entityiterator instanceof TamableAnimal _tamIsTamedBy && entity instanceof LivingEntity _livEnt ? _tamIsTamedBy.isOwnedBy(_livEnt) : false))) {
						dis = Math.sqrt(Math.pow(entityiterator.getX() - immediatesourceentity.getX(), 2) + Math.pow(entityiterator.getY() - immediatesourceentity.getY(), 2) + Math.pow(entityiterator.getZ() - immediatesourceentity.getZ(), 2));
						immediatesourceentity.setDeltaMovement(
								new Vec3(((entityiterator.getX() - immediatesourceentity.getX()) / dis), ((entityiterator.getY() - immediatesourceentity.getY()) / dis), ((entityiterator.getZ() - immediatesourceentity.getZ()) / dis)));
					}
				}
			}
		}
	}
}
