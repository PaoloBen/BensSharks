package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.BaskingSharkEntity;

@Mod.EventBusSubscriber
public class BaskingSharkOnEntityTickUpdateProcedure {
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof BaskingSharkEntity) {
            execute(entity);
        }
    }

    public static void execute(Entity entity) {
        if (entity == null) return;
        BaskingSharkEntity shark = (BaskingSharkEntity) entity;

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
                if (shark.getLastHurtByMob() != null) active = true;
            }
            shark.getEntityData().set(BaskingSharkEntity.DATA_Sprinting, active);
            if (active) shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 0, false, false));
        }

        if (shark.getEntityData().get(BaskingSharkEntity.DATA_Sprinting)) {
            shark.animationprocedure = "sprint";
        } else {
            if (shark.animationprocedure.equals("sprint")) shark.animationprocedure = "empty";
        }
    }
}