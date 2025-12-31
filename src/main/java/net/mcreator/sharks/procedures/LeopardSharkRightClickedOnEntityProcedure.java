package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;

import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.entity.LeopardSharkEntity;

public class LeopardSharkRightClickedOnEntityProcedure {
    public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null) return InteractionResult.PASS;
        if (!entity.isAlive()) return InteractionResult.PASS;

        if (entity instanceof LeopardSharkEntity && sourceentity instanceof Player player) {
            
            // --- PRIORITY CHECK: WATER BUCKET ---
            boolean hasBucket = player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.WATER_BUCKET 
                             || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == Items.WATER_BUCKET;

            if (hasBucket) {
                // 1. Check if baby (cannot catch babies)
                if (entity instanceof LivingEntity _livEnt && _livEnt.isBaby()) {
                    return InteractionResult.PASS;
                }

                // 2. Select Bucket (Tamed vs Untamed)
                Item bucketToGive = BenssharksModItems.LEOPARD_SHARK_BUCKET.get();
                if (entity instanceof TamableAnimal _tamEnt && _tamEnt.isTame()) {
                    bucketToGive = BenssharksModItems.LEOPARD_SHARK_BUCKET_TAMED.get();
                }

                // 3. Catch!
                // We return the result immediately. If SUCCESS, the vanilla "Sit" logic will NOT run.
                return SharkBucketHelperProcedure.catchFish(entity, player, world, bucketToGive);
            }
            
            // If we are NOT holding a bucket, we return PASS.
            // This allows the standard Vanilla behavior (Toggling Sit) to happen automatically.
        }
        return InteractionResult.PASS;
    }
}