package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;

import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.entity.CookiecutterSharkEntity;

public class CookiecutterSharkRightClickedOnEntityProcedure {
    // Added x, y, z back to match MCreator's call signature
    public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return InteractionResult.PASS;
        if (!entity.isAlive()) return InteractionResult.PASS;

        if (entity instanceof CookiecutterSharkEntity && sourceentity instanceof Player player) {
            // Check for Baby status
            if (entity instanceof LivingEntity _livEnt && _livEnt.isBaby()) {
                return InteractionResult.PASS;
            }
            // Use the Helper Procedure
            return SharkBucketHelperProcedure.catchFish(entity, player, world, BenssharksModItems.COOKIECUTTER_SHARK_BUCKET.get());
        }
        return InteractionResult.PASS;
    }
}