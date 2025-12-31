package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;

import java.util.List;

public class SeekerSharkHomingProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity immediatesourceentity) {
        if (immediatesourceentity == null)
            return;

        // 1. IDENTIFY OWNER
        Entity owner = null;
        if (immediatesourceentity instanceof Projectile _proj) {
            owner = _proj.getOwner();
        }

        // 2. DETERMINE HOMING TARGET
        LivingEntity steeringTarget = null;
        if (owner instanceof Mob _mobOwner) {
            steeringTarget = _mobOwner.getTarget();
        } else if (owner instanceof LivingEntity _livingOwner) {
            steeringTarget = _livingOwner.getLastHurtMob();
        }

        // --- VISUAL TRAIL ---
        if (world instanceof ServerLevel _level)
            _level.sendParticles(ParticleTypes.GLOW_SQUID_INK, immediatesourceentity.getX(), immediatesourceentity.getY(), immediatesourceentity.getZ(), 1, 0.01, 0.01, 0.01, 0.01);

        // --- 3. HOMING MOVEMENT ---
        if (steeringTarget != null && steeringTarget.isAlive()) {
            double dX = steeringTarget.getX() - immediatesourceentity.getX();
            double dY = steeringTarget.getBoundingBox().getCenter().y - immediatesourceentity.getY();
            double dZ = steeringTarget.getZ() - immediatesourceentity.getZ();
            double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

            if (dist > 0 && dist <= 64.0) { 
                double currentSpeed = immediatesourceentity.getDeltaMovement().length();
                if (currentSpeed < 0.5) currentSpeed = 0.5;

                net.minecraft.world.phys.Vec3 currentVel = immediatesourceentity.getDeltaMovement();
                net.minecraft.world.phys.Vec3 desiredVel = new net.minecraft.world.phys.Vec3(dX, dY, dZ).normalize();
                double turnSpeed = 0.3; 
                net.minecraft.world.phys.Vec3 newVel = currentVel.normalize().scale(1.0 - turnSpeed).add(desiredVel.scale(turnSpeed));

                immediatesourceentity.setDeltaMovement(newVel.normalize().scale(currentSpeed));
                immediatesourceentity.getPersistentData().putBoolean("ActivelySeeking", true);
            }
        }

        // --- 4. COLLISION DETECTION (GROUND/WALL) ---
        if (immediatesourceentity.onGround() || immediatesourceentity.verticalCollision || immediatesourceentity.horizontalCollision) {
            detonate(world, immediatesourceentity, owner, null);
            return;
        }

        // --- 5. COLLISION DETECTION (ENTITY) ---
        List<Entity> collisions = world.getEntitiesOfClass(Entity.class, immediatesourceentity.getBoundingBox().inflate(2.0), e -> true);
        
        for (Entity hitEntity : collisions) {
            if (hitEntity == immediatesourceentity) continue;
            if (hitEntity == owner) continue;
            
            // FILTER: Only collide with LivingEntities (Mobs/Players). 
            // Ignore Items, XP, Arrows, etc.
            if (!(hitEntity instanceof LivingEntity)) continue;

            // Safety Checks
            if (hitEntity instanceof TamableAnimal _tamable && owner instanceof LivingEntity _ownerLiving && _tamable.isOwnedBy(_ownerLiving)) continue;
            if (hitEntity instanceof Player _plr && _plr.getAbilities().instabuild) continue; 

            // Exclusions
            TagKey<EntityType<?>> illagerTag = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("minecraft", "illager"));
            if (hitEntity.getType().is(illagerTag)) continue; 
            if (hitEntity instanceof net.minecraft.world.entity.monster.Witch) continue;
            if (hitEntity instanceof net.mcreator.sharks.entity.ThalassogerEntity) continue;

            // HIT!
            detonate(world, immediatesourceentity, owner, hitEntity);
            return;
        }
    }

    // --- SHARED DETONATION LOGIC ---
    private static void detonate(LevelAccessor world, Entity shark, Entity owner, Entity directHitTarget) {
        if (world instanceof Level _lvl) {
            
            // 1. DEAL DIRECT DAMAGE
            if (directHitTarget instanceof LivingEntity livingTarget) {
                DamageSource projectileSource = _lvl.damageSources().thrown(shark, owner);
                
                livingTarget.invulnerableTime = 0; 
                float damageAmount = 18.0f;
                float startHealth = livingTarget.getHealth();

                // --- SHIELD CHECK ---
                if (livingTarget.isDamageSourceBlocked(projectileSource)) {
                    // Shield Sound
                    _lvl.playSound(null, BlockPos.containing(livingTarget.getX(), livingTarget.getY(), livingTarget.getZ()), 
                        ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.shield.block")), SoundSource.PLAYERS, 1, 1);
                    
                    // Damage Shield
                    if (livingTarget.isUsingItem()) {
                        ItemStack useItem = livingTarget.getUseItem();
                        useItem.hurtAndBreak(25, livingTarget, (p_222) -> p_222.broadcastBreakEvent(p_222.getUsedItemHand()));
                    }
                    
                    // Force Knockback
                    Vec3 knockVec = livingTarget.position().subtract(shark.position()).normalize().scale(1.2);
                    livingTarget.setDeltaMovement(livingTarget.getDeltaMovement().add(knockVec.x, 0.6, knockVec.z));
                    livingTarget.hasImpulse = true;

                } else {
                    // --- DIRECT HIT ---
                    boolean success = livingTarget.hurt(projectileSource, damageAmount);
                    
                    // Double Tap (Armor Pierce if 0 dmg taken)
                    if (!success || livingTarget.getHealth() >= startHealth) {
                        livingTarget.invulnerableTime = 0; 
                        livingTarget.hurt(_lvl.damageSources().generic(), damageAmount);
                    }

                    // Knockback
                    Vec3 knockVec = livingTarget.position().subtract(shark.position()).normalize().scale(0.8);
                    livingTarget.setDeltaMovement(livingTarget.getDeltaMovement().add(knockVec.x, 0.4, knockVec.z));
                    livingTarget.hasImpulse = true;
                }
            }

            // 2. AUDIO FX
            BlockPos pos = BlockPos.containing(shark.getX(), shark.getY(), shark.getZ());
            if (!_lvl.isClientSide()) {
                _lvl.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.conduit.attack.target")), SoundSource.HOSTILE, 2, 1);
                _lvl.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.firework_rocket.large_blast")), SoundSource.HOSTILE, 1, 1);
                _lvl.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), SoundSource.HOSTILE, 1, 2);
            }

            // 3. VISUAL FX
            if (world instanceof ServerLevel _serverLvl) {
                _serverLvl.sendParticles(ParticleTypes.FLASH, shark.getX(), shark.getY(), shark.getZ(), 1, 0, 0, 0, 0);
                _serverLvl.sendParticles(ParticleTypes.GLOW_SQUID_INK, shark.getX(), shark.getY() + 0.5, shark.getZ(), 25, 0.5, 0.5, 0.5, 0.15);
            }

            // 4. SPLASH DAMAGE (Exclude Items/Owner)
            List<Entity> nearbyEntities = _lvl.getEntitiesOfClass(Entity.class, shark.getBoundingBox().inflate(3.0), e -> true);
            for (Entity victim : nearbyEntities) {
                if (victim == shark) continue;
                if (victim == owner) continue;
                if (victim == directHitTarget) continue; 

                // FILTER: Only affect LivingEntities with splash!
                if (!(victim instanceof LivingEntity)) continue;

                double dx = victim.getX() - shark.getX();
                double dz = victim.getZ() - shark.getZ();
                victim.push(dx * 0.5, 0.5, dz * 0.5);
                
                if (victim instanceof LivingEntity _livingVictim) {
                     _livingVictim.hurt(_lvl.damageSources().explosion(null, owner), 5.0f);
                }
            }
        }
        
        shark.discard();
    }
}