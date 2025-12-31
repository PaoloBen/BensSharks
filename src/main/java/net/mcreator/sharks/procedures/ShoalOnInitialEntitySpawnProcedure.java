package net.mcreator.sharks.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.init.BenssharksModEntities;

public class ShoalOnInitialEntitySpawnProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = BenssharksModEntities.SARDINE.get().spawn(_level, BlockPos.containing(entity.getX() + 1, entity.getY(), entity.getZ()), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setDeltaMovement(0, 0, 0);
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = BenssharksModEntities.SARDINE.get().spawn(_level, BlockPos.containing(entity.getX(), entity.getY() + 1, entity.getZ()), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setDeltaMovement(0, 0, 0);
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = BenssharksModEntities.SARDINE.get().spawn(_level, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ() + 1), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setDeltaMovement(0, 0, 0);
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = BenssharksModEntities.SARDINE.get().spawn(_level, BlockPos.containing(entity.getX() + 1, entity.getY() + 1, entity.getZ()), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setDeltaMovement(0, 0, 0);
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = BenssharksModEntities.SARDINE.get().spawn(_level, BlockPos.containing(entity.getX(), entity.getY() + 1, entity.getZ() + 1), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setDeltaMovement(0, 0, 0);
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = BenssharksModEntities.SARDINE.get().spawn(_level, BlockPos.containing(entity.getX() + 1, entity.getY(), entity.getZ() + 1), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setDeltaMovement(0, 0, 0);
			}
		}
		if (!entity.level().isClientSide())
			entity.discard();
	}
}
