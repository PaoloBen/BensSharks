package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.GreenlandSharkEntity;

@Mod.EventBusSubscriber
public class GreenlandSharkOnEntityTickUpdateProcedure {
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof GreenlandSharkEntity) {
            execute(entity);
        }
    }

    public static void execute(Entity entity) {
        if (entity == null) return;
        GreenlandSharkEntity shark = (GreenlandSharkEntity) entity;

        if (!entity.level().isClientSide()) {
            if (!entity.isInWaterOrBubble()) {
                double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
                entity.getPersistentData().putDouble("DryTime", dryTimer);
                if (dryTimer > 300 && dryTimer % 20 == 0) entity.hurt(entity.damageSources().dryOut(), 2.0F);
            } else {
                entity.getPersistentData().putDouble("DryTime", 0);
            }

            boolean active = false;
            if (entity.isInWaterOrBubble()) {
                LivingEntity target = shark.getTarget();
                if (target != null && target.isAlive() && shark.distanceTo(target) <= 32) active = true;
            }
            shark.getEntityData().set(GreenlandSharkEntity.DATA_Sprinting, active);
            if (active) shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 0, false, false));
        }

        if (shark.getEntityData().get(GreenlandSharkEntity.DATA_Sprinting)) {
            shark.animationprocedure = "sprint";
        } else {
            if (shark.animationprocedure.equals("sprint")) shark.animationprocedure = "empty";
        }
    }
}