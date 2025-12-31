package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.WhitetipSharkEntity;
import net.mcreator.sharks.entity.TigerSharkEntity;
import net.mcreator.sharks.entity.ThresherSharkEntity;
import net.mcreator.sharks.entity.ShrakEntity;
import net.mcreator.sharks.entity.SeaLionEntity;
import net.mcreator.sharks.entity.MakoSharkEntity;
import net.mcreator.sharks.entity.LemonSharkEntity;
import net.mcreator.sharks.entity.GreaterAxodileEntity;
import net.mcreator.sharks.entity.BullSharkEntity;
import net.mcreator.sharks.entity.BlueSharkEntity;
import net.mcreator.sharks.entity.BarracudaEntity;
import net.mcreator.sharks.entity.AxodileEntity;
import net.mcreator.sharks.BenssharksMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class BarracudaSprintProcedure {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof BarracudaEntity) {
			if (entity.isInWaterOrBubble()) {
				if ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) instanceof LivingEntity) {
					if (!world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(x, y, z), 16, 16, 16), e -> true).isEmpty() && (entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).isAlive()
							&& !((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) instanceof Player _plr ? _plr.getAbilities().instabuild : false)) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 60, 2, true, false));
						BenssharksMod.queueServerWork(60, () -> {
							if (entity instanceof LivingEntity _entity)
								_entity.removeEffect(BenssharksModMobEffects.FRENZY.get());
						});
					} else {
						if (entity instanceof LivingEntity _entity)
							_entity.removeEffect(BenssharksModMobEffects.FRENZY.get());
					}
				} else if (!world.getEntitiesOfClass(MakoSharkEntity.class, AABB.ofSize(new Vec3(x, y, z), 12, 12, 12), e -> true).isEmpty()
						|| !world.getEntitiesOfClass(TigerSharkEntity.class, AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), e -> true).isEmpty()
						|| !world.getEntitiesOfClass(BullSharkEntity.class, AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), e -> true).isEmpty() || !world.getEntitiesOfClass(AxodileEntity.class, AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), e -> true).isEmpty()
						|| !world.getEntitiesOfClass(WhitetipSharkEntity.class, AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), e -> true).isEmpty()
						|| !world.getEntitiesOfClass(GreaterAxodileEntity.class, AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), e -> true).isEmpty()
						|| !world.getEntitiesOfClass(LemonSharkEntity.class, AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), e -> true).isEmpty() || !world.getEntitiesOfClass(ShrakEntity.class, AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), e -> true).isEmpty()
						|| !world.getEntitiesOfClass(BlueSharkEntity.class, AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), e -> true).isEmpty() || !world.getEntitiesOfClass(SeaLionEntity.class, AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), e -> true).isEmpty()
						|| !world.getEntitiesOfClass(ThresherSharkEntity.class, AABB.ofSize(new Vec3(x, y, z), 6, 6, 6), e -> true).isEmpty()) {
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 60, 2, true, false));
					BenssharksMod.queueServerWork(60, () -> {
						if (entity instanceof LivingEntity _entity)
							_entity.removeEffect(BenssharksModMobEffects.FRENZY.get());
					});
				} else {
					if (entity instanceof LivingEntity _entity)
						_entity.removeEffect(BenssharksModMobEffects.FRENZY.get());
				}
			}
		}
	}
}
