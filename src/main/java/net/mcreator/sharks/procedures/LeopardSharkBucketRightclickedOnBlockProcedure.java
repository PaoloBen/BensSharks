package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;

import net.mcreator.sharks.init.BenssharksModEntities;

public class LeopardSharkBucketRightclickedOnBlockProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, Direction direction, Entity entity, ItemStack itemstack) {
        SharkBucketHelperProcedure.placeFish(world, x, y, z, direction, entity, itemstack, BenssharksModEntities.LEOPARD_SHARK.get());
    }
}