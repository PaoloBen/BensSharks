package net.mcreator.sharks.potion;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

import java.util.List;

public class VortexMobEffect extends MobEffect {
    public VortexMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -10918248);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()) {
            
            // Mechanic: Player must be underwater
            if (entity.isInWater()) {
                
                // --- 1. XP ORB SPAWNING (Every 20 Ticks) ---
                if (entity.tickCount % 20 == 0) {
                    if (entity instanceof Player) {
                        // Spawn XP Orb slightly away from player so it gets "sucked in"
                        double offsetX = (Math.random() - 0.5) * 6;
                        double offsetZ = (Math.random() - 0.5) * 6;
                        double offsetY = (Math.random() - 0.5) * 2;
                        
                        ExperienceOrb xpOrb = new ExperienceOrb(
                            entity.level(), 
                            entity.getX() + offsetX, 
                            entity.getY() + 0.5 + offsetY, 
                            entity.getZ() + offsetZ, 
                            1
                        );
                        entity.level().addFreshEntity(xpOrb);
                    }
                }

                // --- 2. VORTEX MAGNET (Items & XP) ---
                // Range: Starts at 8 blocks, adds 1 block per amplifier level
                double range = 8.0 + amplifier; 
                
                // Pull ITEMS
                List<ItemEntity> items = entity.level().getEntitiesOfClass(
                    ItemEntity.class, 
                    entity.getBoundingBox().inflate(range)
                );
                
                // Pull XP ORBS
                List<ExperienceOrb> xpOrbs = entity.level().getEntitiesOfClass(
                    ExperienceOrb.class, 
                    entity.getBoundingBox().inflate(range)
                );

                // Process Items
                for (ItemEntity item : items) {
                    if (item.isInWater()) {
                        pullEntityTowards(entity, item);
                    }
                }

                // Process XP Orbs
                for (ExperienceOrb orb : xpOrbs) {
                    if (orb.isInWater()) {
                        pullEntityTowards(entity, orb);
                    }
                }
            }
        }
    }

    // Helper method: Apply motion AND spawn trail particles
    private void pullEntityTowards(LivingEntity player, net.minecraft.world.entity.Entity target) {
        Vec3 targetPos = target.position();
        Vec3 playerPos = player.position().add(0, 0.5, 0); 
        
        // 1. Move the entity (Speed reduced to 0.05 for a gentle drift)
        Vec3 pullDir = playerPos.subtract(targetPos).normalize().scale(0.05);
        target.setDeltaMovement(target.getDeltaMovement().add(pullDir));

        // 2. VISUALS: Spawn Particle Trail on the item/orb
        if (player.level() instanceof ServerLevel _level) {
            // Spawn 1 Nautilus particle at the item's location
            _level.sendParticles(ParticleTypes.NAUTILUS,
                target.getX(), target.getY() + 0.2, target.getZ(),
                1, 0.1, 0.1, 0.1, 0.0);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}