package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.MakoSharkEntity;

@Mod.EventBusSubscriber
public class MakoSharkOnEntityTickUpdateProcedure {
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof MakoSharkEntity) {
            execute(entity);
        }
    }

    public static void execute(Entity entity) {
        if (entity == null) return;
        MakoSharkEntity shark = (MakoSharkEntity) entity;

        if (!entity.level().isClientSide()) {
            // 1. Suffocation
            if (!entity.isInWaterOrBubble()) {
                double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
                entity.getPersistentData().putDouble("DryTime", dryTimer);
                if (dryTimer > 300 && dryTimer % 20 == 0) {
                    entity.hurt(entity.damageSources().dryOut(), 4.0F);
                }
            } else {
                entity.getPersistentData().putDouble("DryTime", 0);
            }

            // 2. Sprint Decision
            // [FIX] Check the new Synced Data
            boolean isBreaching = shark.getEntityData().get(MakoSharkEntity.DATA_Breaching);
            boolean active = false;
            
            // Only allow sprint if NOT breaching
            if (!isBreaching && entity.isInWaterOrBubble()) {
                LivingEntity target = shark.getTarget();
                if (target != null && target.isAlive() && shark.distanceTo(target) <= 24) {
                    active = true;
                }
            }

            // 3. Sync & Effects
            shark.getEntityData().set(MakoSharkEntity.DATA_Sprinting, active);
            if (active) {
                shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 1, false, false));
            }
        }

        // 4. Client Animation Sync
        if (shark.getEntityData().get(MakoSharkEntity.DATA_Sprinting)) {
            shark.animationprocedure = "sprint";
        } else {
            if (shark.animationprocedure.equals("sprint")) {
                shark.animationprocedure = "empty";
            }
        }
    }
}