package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;

import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.entity.SawsharkEntity;

public class SawsharkRightClickedOnEntityProcedure {
    public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return InteractionResult.PASS;
        if (!entity.isAlive()) return InteractionResult.PASS;

        if (entity instanceof SawsharkEntity && sourceentity instanceof Player player) {
            // No baby check here, matching your original file
            return SharkBucketHelperProcedure.catchFish(entity, player, world, BenssharksModItems.SAWSHARK_BUCKET.get());
        }
        return InteractionResult.PASS;
    }
}