package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.mcreator.sharks.entity.AngelsharkEntity;

public class AngelsharkOnEntityTickUpdateProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null) return;

        // --- SURVIVAL LOGIC: DRYING OUT ---
        // This handles hurting the shark if it stays out of water too long.
        if (!entity.isInWaterOrBubble()) {
            double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
            entity.getPersistentData().putDouble("DryTime", dryTimer);
            
            // After 15 seconds (300 ticks), start taking damage
            if (dryTimer > 300 && dryTimer % 20 == 0) {
                entity.hurt(entity.damageSources().dryOut(), 4.0F);
            }
        } else {
            // Reset timer instantly if back in water
            if (entity.getPersistentData().getDouble("DryTime") > 0) {
                entity.getPersistentData().putDouble("DryTime", 0);
            }
        }

        // Note: The Feeding, Burrowing, and Loot Spawning logic 
        // has been moved to 'AngelsharkScavengeGoal.java'
    }
}