package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;

import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.entity.RemoraEntity;

public class RemoraRightClickedOnEntityProcedure {
    // Added x, y, z back to match MCreator's call signature
    public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return InteractionResult.PASS;
        if (!entity.isAlive()) return InteractionResult.PASS;
        
        if (entity instanceof RemoraEntity && sourceentity instanceof Player player) {
            return SharkBucketHelperProcedure.catchFish(entity, player, world, BenssharksModItems.REMORA_BUCKET.get());
        }
        return InteractionResult.PASS;
    }
}