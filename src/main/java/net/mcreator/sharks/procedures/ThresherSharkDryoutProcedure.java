//package net.mcreator.sharks.procedures;

//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.event.entity.living.LivingEvent;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.mcreator.sharks.init.BenssharksModMobEffects;
//import net.mcreator.sharks.entity.ThresherSharkEntity;

//@Mod.EventBusSubscriber
//public class ThresherSharkDryoutProcedure {
    //@SubscribeEvent
    //public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        //Entity entity = event.getEntity();
        //if (entity instanceof ThresherSharkEntity) {
            //execute(entity);
        //}
    //}

    //public static void execute(Entity entity) {
        //if (entity == null) return;

        //if (!entity.isInWaterOrBubble()) {
            //double dryTimer = entity.getPersistentData().getDouble("DryTime") + 1;
            //entity.getPersistentData().putDouble("DryTime", dryTimer);

            //if (dryTimer > 300) {
                //if (dryTimer % 20 == 0) {
                    //entity.hurt(entity.damageSources().dryOut(), 2.0F);
                    //if (entity instanceof LivingEntity _entity)
                        //_entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.DRYOUT_EFFECT.get(), 40, 0, false, false));
                //}
            //}
        //} else {
            //if (entity.getPersistentData().getDouble("DryTime") > 0)
                //entity.getPersistentData().putDouble("DryTime", 0);
            //if (entity instanceof LivingEntity _entity)
                //_entity.removeEffect(BenssharksModMobEffects.DRYOUT_EFFECT.get());
        //}
    //}
//}