package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.Entity;

import net.mcreator.sharks.entity.WhitetipSharkEntity;
import net.mcreator.sharks.entity.WhaleSharkEntity;
import net.mcreator.sharks.entity.TigerSharkEntity;
import net.mcreator.sharks.entity.ThresherSharkEntity;
import net.mcreator.sharks.entity.ShrakEntity;
import net.mcreator.sharks.entity.SawsharkEntity;
import net.mcreator.sharks.entity.RemoraEntity;
import net.mcreator.sharks.entity.PilotFishEntity;
import net.mcreator.sharks.entity.NurseSharkEntity;
import net.mcreator.sharks.entity.MegalodonEntity;
import net.mcreator.sharks.entity.MakoSharkEntity;
import net.mcreator.sharks.entity.LeopardSharkEntity;
import net.mcreator.sharks.entity.LemonSharkEntity;
import net.mcreator.sharks.entity.KrillEntity;
import net.mcreator.sharks.entity.GreenlandSharkEntity;
import net.mcreator.sharks.entity.GreaterAxodileEntity;
import net.mcreator.sharks.entity.GoblinSharkEntity;
import net.mcreator.sharks.entity.CookiecutterSharkEntity;
import net.mcreator.sharks.entity.BullSharkEntity;
import net.mcreator.sharks.entity.BonnetheadSharkEntity;
import net.mcreator.sharks.entity.BlueSharkEntity;
import net.mcreator.sharks.entity.BlacktipReefSharkEntity;
import net.mcreator.sharks.entity.BaskingSharkEntity;
import net.mcreator.sharks.entity.BarracudaEntity;
import net.mcreator.sharks.entity.AxodileEntity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class StopRidingBoatProcedure {
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
		if (entity instanceof AxodileEntity || entity instanceof BlueSharkEntity || entity instanceof BonnetheadSharkEntity || entity instanceof NurseSharkEntity || entity instanceof RemoraEntity || entity instanceof ShrakEntity
				|| entity instanceof TigerSharkEntity || entity instanceof CookiecutterSharkEntity || entity instanceof MakoSharkEntity || entity instanceof BlacktipReefSharkEntity || entity instanceof BaskingSharkEntity
				|| entity instanceof BullSharkEntity || entity instanceof MegalodonEntity || entity instanceof LemonSharkEntity || entity instanceof KrillEntity || entity instanceof WhaleSharkEntity || entity instanceof PilotFishEntity
				|| entity instanceof GreaterAxodileEntity || entity instanceof GreenlandSharkEntity || entity instanceof WhitetipSharkEntity || entity instanceof BarracudaEntity || entity instanceof LeopardSharkEntity
				|| entity instanceof GoblinSharkEntity || entity instanceof SawsharkEntity || entity instanceof ThresherSharkEntity) {
			if (entity.isPassenger() && ((entity.getVehicle()) instanceof Boat || (entity.getVehicle()) instanceof ChestBoat)) {
				entity.stopRiding();
			}
		}
	}
}
