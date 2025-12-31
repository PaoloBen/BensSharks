package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;

import net.mcreator.sharks.init.BenssharksModEntities;

public class LeopardSharkBucketTamedRightclickedOnBlockProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, Direction direction, Entity entity, ItemStack itemstack) {
        // 1. Call Helper to place the shark
        Entity spawnedFish = SharkBucketHelperProcedure.placeFish(world, x, y, z, direction, entity, itemstack, BenssharksModEntities.LEOPARD_SHARK.get());
        
        // 2. Apply Taming Logic
        if (spawnedFish instanceof TamableAnimal tamable && entity instanceof Player player) {
            tamable.setTame(true);
            tamable.setOwnerUUID(player.getUUID());
        }
    }
}