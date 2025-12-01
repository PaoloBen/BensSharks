package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.sharks.entity.SharkMinionEntity;
import net.mcreator.sharks.entity.LandSharkEntity;

public class IFNOTSITTINGProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		boolean Sitting = false;
		if ((entity instanceof SharkMinionEntity _datEntL0 && _datEntL0.getEntityData().get(SharkMinionEntity.DATA_Sitting)) == true
				|| (entity instanceof LandSharkEntity _datEntL1 && _datEntL1.getEntityData().get(LandSharkEntity.DATA_Sitting)) == true) {
			if (entity instanceof SharkMinionEntity) {
				((SharkMinionEntity) entity).setAnimation("sit");
			}
			if (entity instanceof LandSharkEntity) {
				((LandSharkEntity) entity).setAnimation("sit");
			}
			return false;
		} else if ((entity instanceof SharkMinionEntity _datEntL4 && _datEntL4.getEntityData().get(SharkMinionEntity.DATA_Sitting)) == false
				|| (entity instanceof LandSharkEntity _datEntL5 && _datEntL5.getEntityData().get(LandSharkEntity.DATA_Sitting)) == false) {
			if (entity instanceof SharkMinionEntity) {
				((SharkMinionEntity) entity).setAnimation("empty");
			}
			if (entity instanceof LandSharkEntity) {
				((LandSharkEntity) entity).setAnimation("empty");
			}
			return true;
		}
		return entity instanceof SharkMinionEntity _datEntL8 && _datEntL8.getEntityData().get(SharkMinionEntity.DATA_Sitting) || entity instanceof LandSharkEntity _datEntL9 && _datEntL9.getEntityData().get(LandSharkEntity.DATA_Sitting);
	}
}
