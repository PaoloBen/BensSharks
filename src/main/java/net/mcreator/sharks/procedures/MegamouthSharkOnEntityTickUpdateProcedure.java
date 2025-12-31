package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.entity.MegamouthSharkEntity;

@Mod.EventBusSubscriber
public class MegamouthSharkOnEntityTickUpdateProcedure {
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof MegamouthSharkEntity) {
            execute(entity);
        }
    }

    public static void execute(Entity entity) {
        if (entity == null) return;
        
        // Cast the entity to MegamouthSharkEntity
        MegamouthSharkEntity shark = (MegamouthSharkEntity) entity;

        if (!entity.level().isClientSide()) {
            // --- Dry Out Mechanics ---
            if (!entity.isInWaterOrBubble()) {
                double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
                entity.getPersistentData().putDouble("DryTime", dryTimer);
                // Damage the shark if it has been out of water too long (300 ticks)
                if (dryTimer > 300 && dryTimer % 20 == 0) entity.hurt(entity.damageSources().dryOut(), 2.0F);
            } else {
                entity.getPersistentData().putDouble("DryTime", 0);
            }

            // --- Frenzy/Sprinting Mechanics ---
            boolean active = false;
            if (entity.isInWaterOrBubble()) {
                // If the shark was hurt by a mob, set active to true
                if (shark.getLastHurtByMob() != null) active = true;
            }
            
            // Set the entity data for sprinting
            shark.getEntityData().set(MegamouthSharkEntity.DATA_Sprinting, active);
            
            // Apply Frenzy effect if active
            if (active) shark.addEffect(new MobEffectInstance(BenssharksModMobEffects.FRENZY.get(), 10, 0, false, false));
        }

        // --- Animation Sync ---
        if (shark.getEntityData().get(MegamouthSharkEntity.DATA_Sprinting)) {
            shark.animationprocedure = "sprint";
        } else {
            if (shark.animationprocedure.equals("sprint")) shark.animationprocedure = "empty";
        }
    }
}