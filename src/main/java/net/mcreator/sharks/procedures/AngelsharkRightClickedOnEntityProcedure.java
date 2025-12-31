package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;

public class AngelsharkRightClickedOnEntityProcedure {
    public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        // The "Fish Bucket" logic is now handled natively in AngelsharkEntity.java (mobInteract method).
        // This procedure is kept empty to prevent compilation errors in case other parts of the mod try to reference it.
        return InteractionResult.PASS;
    }
}