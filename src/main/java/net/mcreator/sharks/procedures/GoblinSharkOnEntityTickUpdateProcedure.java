package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.GoblinSharkEntity;

@Mod.EventBusSubscriber
public class GoblinSharkOnEntityTickUpdateProcedure {
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof GoblinSharkEntity) {
            execute(entity);
        }
    }

    public static void execute(Entity entity) {
        if (entity == null) return;
        GoblinSharkEntity shark = (GoblinSharkEntity) entity;

        // --- SERVER SIDE LOGIC ---
        if (!entity.level().isClientSide()) {
            // 1. Suffocation (NBT Timer)
            if (!entity.isInWaterOrBubble()) {
                double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
                entity.getPersistentData().putDouble("DryTime", dryTimer);

                if (dryTimer > 300) {
                    if (dryTimer % 20 == 0) {
                        entity.hurt(entity.damageSources().dryOut(), 2.0F);
                        if (entity instanceof LivingEntity _entity)
                            _entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.DRYOUT_EFFECT.get(), 40, 0, false, false));
                    }
                }
            } else {
                if (entity.getPersistentData().getDouble("DryTime") > 0)
                    entity.getPersistentData().putDouble("DryTime", 0);
                if (entity instanceof LivingEntity _entity)
                    _entity.removeEffect(BenssharksModMobEffects.DRYOUT_EFFECT.get());
            }

            // 2. Sprint Logic (Aggressive)
            boolean shouldSprint = false;
            if (entity.isInWaterOrBubble()) {
                LivingEntity target = shark.getTarget();
                // Range: 32 Blocks (Matches Entity Attributes)
                if (target != null && target.isAlive() && shark.distanceTo(target) <= 32) {
                    shouldSprint = true;
                }
            }

            // Sync to Client
            shark.getEntityData().set(GoblinSharkEntity.DATA_Sprinting, shouldSprint);

            // Apply Frenzy Effect
            if (shouldSprint) {
                shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 0, false, false));
            }
        }

        // --- CLIENT SIDE ANIMATION OVERRIDE ---
        if (shark.getEntityData().get(GoblinSharkEntity.DATA_Sprinting)) {
            shark.animationprocedure = "sprint";
        } else {
            // Reset to empty to allow default animations (swim/idle)
            if (shark.animationprocedure.equals("sprint")) {
                shark.animationprocedure = "empty";
            }
        }
    }
}