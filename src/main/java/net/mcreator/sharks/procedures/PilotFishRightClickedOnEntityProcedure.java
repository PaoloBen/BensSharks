package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;

import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.entity.PilotFishEntity;

public class PilotFishRightClickedOnEntityProcedure {
    // Added x, y, z back to match MCreator's call signature
    public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return InteractionResult.PASS;
        if (!entity.isAlive()) return InteractionResult.PASS;

        if (entity instanceof PilotFishEntity && sourceentity instanceof Player player) {
            return SharkBucketHelperProcedure.catchFish(entity, player, world, BenssharksModItems.PILOT_FISH_BUCKET.get());
        }
        return InteractionResult.PASS;
    }
}