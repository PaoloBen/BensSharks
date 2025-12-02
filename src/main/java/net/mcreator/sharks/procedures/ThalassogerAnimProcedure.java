package net.mcreator.sharks.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.entity.ThalassogerEntity;
import net.mcreator.sharks.entity.SeekerSharkProjectileEntity;
import net.mcreator.sharks.BenssharksMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class ThalassogerAnimProcedure {
	@SubscribeEvent
	public static void onEntitySetsAttackTarget(LivingChangeTargetEvent event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity sourceentity) {
		execute(null, world, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity sourceentity) {
		if (sourceentity == null)
			return;
		double fromZ = 0;
		double fromX = 0;
		double fromY = 0;
		BlockState clickedBlock = Blocks.AIR.defaultBlockState();
		if (!world.getEntitiesOfClass(SeekerSharkProjectileEntity.class, AABB.ofSize(new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ())), 4, 4, 4), e -> true).isEmpty()) {
			if (sourceentity instanceof LivingEntity _entity)
				_entity.swing(InteractionHand.MAIN_HAND, true);
			if (sourceentity instanceof ThalassogerEntity animatable)
				animatable.setTexture("thalassoger2");
			BenssharksMod.queueServerWork(40, () -> {
				if (sourceentity instanceof ThalassogerEntity animatable)
					animatable.setTexture("thalassoger1");
			});
		} else if (sourceentity instanceof ThalassogerEntity) {
			if (sourceentity instanceof ThalassogerEntity animatable)
				animatable.setTexture("thalassoger_glow");
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.GLOW_SQUID_INK, (sourceentity.getX()), (sourceentity.getY() + 1.5), (sourceentity.getZ()), 1, 0, 0, 0, 0.075);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.conduit.ambient.short")), SoundSource.NEUTRAL, 2, 1);
				} else {
					_level.playLocalSound((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.conduit.ambient.short")), SoundSource.NEUTRAL, 2, 1, false);
				}
			}
			BenssharksMod.queueServerWork(20, () -> {
				if (sourceentity instanceof ThalassogerEntity animatable)
					animatable.setTexture("thalassoger2");
			});
		}
	}
}
