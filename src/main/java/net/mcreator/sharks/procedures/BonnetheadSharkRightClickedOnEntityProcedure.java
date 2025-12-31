package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;

import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.entity.BonnetheadSharkEntity;

public class BonnetheadSharkRightClickedOnEntityProcedure {
    public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return InteractionResult.PASS;
        if (!entity.isAlive()) return InteractionResult.PASS;

        if (entity instanceof BonnetheadSharkEntity && sourceentity instanceof Player player) {
            // Unique Logic: Check for Baby status (cannot catch babies)
            if (entity instanceof LivingEntity _livEnt && _livEnt.isBaby()) {
                return InteractionResult.PASS;
            }

            // Call the Helper Procedure
            return SharkBucketHelperProcedure.catchFish(entity, player, world, BenssharksModItems.BONNETHEAD_SHARK_BUCKET.get());
        }
        return InteractionResult.PASS;
    }
}