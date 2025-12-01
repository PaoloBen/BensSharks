package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity;

import net.mcreator.sharks.entity.SharkMinionEntity;
import net.mcreator.sharks.entity.LandSharkEntity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class SittingSneakingSaveProcedure {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		double dis = 0;
		boolean Sitting = false;
		boolean LandSsit = false;
		if (entity instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false) {
			if (entity instanceof SharkMinionEntity) {
				if ((entity instanceof SharkMinionEntity _datEntL2 && _datEntL2.getEntityData().get(SharkMinionEntity.DATA_Sitting)) == true) {
					if (entity instanceof SharkMinionEntity) {
						((SharkMinionEntity) entity).setAnimation("sit");
					}
				} else if ((entity instanceof SharkMinionEntity _datEntL4 && _datEntL4.getEntityData().get(SharkMinionEntity.DATA_Sitting)) == false) {
					if (entity instanceof SharkMinionEntity) {
						((SharkMinionEntity) entity).setAnimation("empty");
					}
				}
			} else if (entity instanceof LandSharkEntity) {
				if ((entity instanceof LandSharkEntity _datEntL7 && _datEntL7.getEntityData().get(LandSharkEntity.DATA_Sitting)) == true) {
					if (entity instanceof LandSharkEntity) {
						((LandSharkEntity) entity).setAnimation("sit");
					}
				} else if ((entity instanceof LandSharkEntity _datEntL9 && _datEntL9.getEntityData().get(LandSharkEntity.DATA_Sitting)) == false) {
					if (entity instanceof LandSharkEntity) {
						((LandSharkEntity) entity).setAnimation("empty");
					}
				}
			}
		} else {
			if (entity instanceof SharkMinionEntity) {
				if (entity instanceof SharkMinionEntity _datEntSetL)
					_datEntSetL.getEntityData().set(SharkMinionEntity.DATA_Sitting, false);
			} else if (entity instanceof LandSharkEntity) {
				if (entity instanceof LandSharkEntity _datEntSetL)
					_datEntSetL.getEntityData().set(LandSharkEntity.DATA_Sitting, false);
			}
		}
	}
}
