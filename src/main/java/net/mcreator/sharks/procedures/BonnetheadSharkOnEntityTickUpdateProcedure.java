package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.BlockPos;
import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.BonnetheadSharkEntity;

@Mod.EventBusSubscriber
public class BonnetheadSharkOnEntityTickUpdateProcedure {
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof BonnetheadSharkEntity) {
            execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
        }
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        BonnetheadSharkEntity shark = (BonnetheadSharkEntity) entity;

        if (!entity.level().isClientSide()) {
            if (!entity.isInWaterOrBubble()) {
                double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
                entity.getPersistentData().putDouble("DryTime", dryTimer);
                if (dryTimer > 300 && dryTimer % 20 == 0) entity.hurt(entity.damageSources().dryOut(), 2.0F);
            } else {
                entity.getPersistentData().putDouble("DryTime", 0);
            }

            // Seagrass Logic
            if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.TALL_SEAGRASS) {
                world.destroyBlock(BlockPos.containing(x, y, z), false);
                if (world.getBlockState(BlockPos.containing(x, y - 1, z)).canOcclude()) {
                   world.setBlock(BlockPos.containing(x, y, z), Blocks.SEAGRASS.defaultBlockState(), 3);
                }
            }

            boolean active = false;
            if (entity.isInWaterOrBubble()) {
                LivingEntity target = shark.getTarget();
                if (target != null && target.isAlive() && shark.distanceTo(target) <= 16) active = true;
            }
            shark.getEntityData().set(BonnetheadSharkEntity.DATA_Sprinting, active);
            if (active) shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 0, false, false));
        }

        if (shark.getEntityData().get(BonnetheadSharkEntity.DATA_Sprinting)) {
            shark.animationprocedure = "sprint2"; // Important
        } else {
            if (shark.animationprocedure.equals("sprint2")) shark.animationprocedure = "empty";
        }
    }
}