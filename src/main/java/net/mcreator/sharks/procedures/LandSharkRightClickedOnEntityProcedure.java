package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.AnimalTameEvent;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;

import net.mcreator.sharks.entity.LandSharkEntity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class LandSharkRightClickedOnEntityProcedure {
	@SubscribeEvent
	public static void onEntityTamed(AnimalTameEvent event) {
		execute(event, event.getAnimal(), event.getTamer());
	}

	public static InteractionResult execute(Entity entity, Entity sourceentity) {
		return execute(null, entity, sourceentity);
	}

	private static InteractionResult execute(@Nullable Event event, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return InteractionResult.PASS;
		boolean Sitting = false;
		double MovementSpeed = 0;
		if (entity instanceof TamableAnimal _tamIsTamedBy && sourceentity instanceof LivingEntity _livEnt ? _tamIsTamedBy.isOwnedBy(_livEnt) : false) {
			if (sourceentity instanceof LivingEntity _entity)
				_entity.swing(InteractionHand.MAIN_HAND, true);
			if ((entity instanceof LandSharkEntity _datEntL2 && _datEntL2.getEntityData().get(LandSharkEntity.DATA_Sitting)) == false) {
				if (entity instanceof LandSharkEntity _datEntSetL)
					_datEntSetL.getEntityData().set(LandSharkEntity.DATA_Sitting, true);
			} else if ((entity instanceof LandSharkEntity _datEntL4 && _datEntL4.getEntityData().get(LandSharkEntity.DATA_Sitting)) == true) {
				if (entity instanceof LandSharkEntity _datEntSetL)
					_datEntSetL.getEntityData().set(LandSharkEntity.DATA_Sitting, false);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
