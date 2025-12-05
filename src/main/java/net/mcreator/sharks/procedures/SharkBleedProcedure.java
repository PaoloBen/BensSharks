package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.registries.Registries;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.WhitetipSharkEntity;
import net.mcreator.sharks.entity.TigerSharkEntity;
import net.mcreator.sharks.entity.ThresherSharkEntity;
import net.mcreator.sharks.entity.ShrakEntity;
import net.mcreator.sharks.entity.SawsharkEntity;
import net.mcreator.sharks.entity.MegalodonEntity;
import net.mcreator.sharks.entity.MakoSharkEntity;
import net.mcreator.sharks.entity.LemonSharkEntity;
import net.mcreator.sharks.entity.LandSharkEntity;
import net.mcreator.sharks.entity.GreenlandSharkEntity;
import net.mcreator.sharks.entity.GoblinSharkEntity;
import net.mcreator.sharks.entity.BullSharkEntity;
import net.mcreator.sharks.entity.BonnetheadSharkEntity;
import net.mcreator.sharks.entity.BlueSharkEntity;
import net.mcreator.sharks.entity.BlacktipReefSharkEntity;
import net.mcreator.sharks.entity.BaskingSharkEntity;
import net.mcreator.sharks.BenssharksMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class SharkBleedProcedure {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof ShrakEntity || entity instanceof TigerSharkEntity || entity instanceof BlueSharkEntity || entity instanceof MakoSharkEntity || entity instanceof BonnetheadSharkEntity || entity instanceof BlacktipReefSharkEntity
				|| entity instanceof BullSharkEntity || entity instanceof MegalodonEntity || entity instanceof LemonSharkEntity || entity instanceof BaskingSharkEntity || entity instanceof GreenlandSharkEntity || entity instanceof WhitetipSharkEntity
				|| entity instanceof LandSharkEntity || entity instanceof GoblinSharkEntity || entity instanceof SawsharkEntity || entity instanceof ThresherSharkEntity) && entity instanceof LivingEntity _livEnt16
				&& _livEnt16.hasEffect(BenssharksModMobEffects.BLEEDING.get())) {
			if (!entity.getPersistentData().getBoolean("bleed")) {
				entity.getPersistentData().putBoolean("bleed", true);
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC_KILL)), 1);
				BenssharksMod.queueServerWork(60, () -> {
					entity.getPersistentData().putBoolean("bleed", false);
				});
			}
		}
	}
}
