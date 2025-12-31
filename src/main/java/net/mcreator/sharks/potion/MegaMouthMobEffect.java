package net.mcreator.sharks.potion;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.mcreator.sharks.init.BenssharksModMobEffects;

@Mod.EventBusSubscriber
public class MegaMouthMobEffect extends MobEffect {
    public MegaMouthMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -8231846);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @SubscribeEvent
    public static void onItemUseTick(LivingEntityUseItemEvent.Tick event) {
        LivingEntity entity = event.getEntity();
        
        // 1. Check if player has the Mega Mouth effect
        if (entity.hasEffect(BenssharksModMobEffects.MEGA_MOUTH.get())) {
            
            ItemStack itemStack = event.getItem();
            UseAnim anim = itemStack.getUseAnimation();

            // 2. Check if the item is Food (Edible) OR a Drink (Potion/Milk)
            // This prevents it from instantly charging Bows, Tridents, or Shields
            if (itemStack.isEdible() || anim == UseAnim.EAT || anim == UseAnim.DRINK) {
                
                // 3. Make it instant
                event.setDuration(1);
            }
        }
    }
}