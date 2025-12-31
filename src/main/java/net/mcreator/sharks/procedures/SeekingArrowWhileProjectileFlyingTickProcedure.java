package net.mcreator.sharks.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

import java.util.List;
import java.util.Comparator;

public class SeekingArrowWhileProjectileFlyingTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity immediatesourceentity) {
		if (immediatesourceentity == null)
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
				} // 0. GET OWNER
				net.minecraft.world.entity.Entity owner = null;
				if (immediatesourceentity instanceof net.minecraft.world.entity.projectile.Projectile proj) {
					owner = proj.getOwner();
				}
				// 1. RESET SEEKING STATE
				// Default to "Solid Arrow" (Not Seeking)
				immediatesourceentity.getPersistentData().putBoolean("ActivelySeeking", false);
				if (immediatesourceentity instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow) {
					arrow.setPierceLevel((byte) 0);
				}
				// 2. CHECK FOR ENEMY
				if (entityiterator instanceof net.minecraft.world.entity.monster.Enemy && entityiterator != owner && entityiterator != immediatesourceentity) {
					// --- A. SMOOTH STEERING LOGIC ---
					double currentSpeed = immediatesourceentity.getDeltaMovement().length();
					// Keep minimum speed to prevent floating arrows
					if (currentSpeed < 0.1)
						currentSpeed = 0.5;
					// Calculate direction
					double dX = entityiterator.getX() - immediatesourceentity.getX();
					double dY = entityiterator.getBoundingBox().getCenter().y - immediatesourceentity.getY();
					double dZ = entityiterator.getZ() - immediatesourceentity.getZ();
					double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
					if (dist > 0) {
						net.minecraft.world.phys.Vec3 currentVelocity = immediatesourceentity.getDeltaMovement();
						net.minecraft.world.phys.Vec3 desiredDir = new net.minecraft.world.phys.Vec3(dX, dY, dZ).normalize();
						// Blend directions (0.3 = Smooth turn)
						double turnSpeed = 0.3;
						net.minecraft.world.phys.Vec3 newDir = currentVelocity.normalize().scale(1.0 - turnSpeed).add(desiredDir.scale(turnSpeed));
						// Apply new velocity
						immediatesourceentity.setDeltaMovement(newDir.normalize().scale(currentSpeed));
					}
					// --- B. ACTIVATE GHOST MODE ---
					if (immediatesourceentity instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow) {
						arrow.setPierceLevel((byte) 5);
						immediatesourceentity.getPersistentData().putBoolean("ActivelySeeking", true);
					}
					// --- C. MANUAL IMPACT TRIGGER (Max 18 Damage) ---
					// Deals damage based on velocity.
					if (immediatesourceentity.getBoundingBox().intersects(entityiterator.getBoundingBox().inflate(0.3))) {
						// CALCULATE DAMAGE: Speed * 6.0
						// Speed 3.0 (Full Charge) * 6.0 = 18.0 Damage
						float calculatedDamage = (float) (currentSpeed * 6.0);
						// Safety: Ensure it deals at least 6 damage (3 hearts)
						if (calculatedDamage < 6.0f)
							calculatedDamage = 6.0f;
						// 1. Deal Damage
						if (world instanceof net.minecraft.world.level.Level _lvl) {
							entityiterator.hurt(_lvl.damageSources().arrow((net.minecraft.world.entity.projectile.AbstractArrow) immediatesourceentity, owner), calculatedDamage);
						}
						// 2. Kill the Arrow
						immediatesourceentity.discard();
					}
					break;
				}
			}
		}
	}
}
