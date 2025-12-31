package net.mcreator.sharks.procedures;

import net.mcreator.sharks.configuration.SharkAggroConfig; // <--- NEW IMPORT

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Arrays;
import java.util.function.Predicate;

@Mod.EventBusSubscriber
public class VanillaMobsAggroToFishProcedure {

    // ================================================================= //
    //                       CONFIGURATION ZONE                          //
    // ================================================================= //

    private static final List<String> AGGRESSORS = Arrays.asList(
        "minecraft:dolphin"
    );

    private static final List<String> TARGET_FISH = Arrays.asList(
        "benssharks:sardine"
    );

    // ================================================================= //
    //                          LOGIC ENGINE                             //
    // ================================================================= //

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        
        // --- CONFIG CHECK ---
        // If config is disabled, stop immediately.
        try {
            if (!SharkAggroConfig.COMMON.enableDolphinAggro.get()) {
                return;
            }
        } catch (Exception e) {
            // If config isn't loaded yet, catch the error and proceed safely.
        }

        Entity entity = event.getEntity();

        // MeleeAttackGoal requires the entity to be a PathfinderMob to work.
        if (entity instanceof PathfinderMob mob && !entity.level().isClientSide()) {
            
            ResourceLocation mobID = ForgeRegistries.ENTITY_TYPES.getKey(mob.getType());
            
            if (mobID != null && AGGRESSORS.contains(mobID.toString())) {
                
                Predicate<LivingEntity> fishSelector = (target) -> {
                    ResourceLocation targetID = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
                    return targetID != null && TARGET_FISH.contains(targetID.toString());
                };

                // 1. TARGETING (Who to chase)
                mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                    mob, 
                    LivingEntity.class, 
                    10,     
                    true,   
                    false,  
                    fishSelector 
                ));

                // 2. ATTACKING (Actually biting)
                mob.goalSelector.addGoal(1, new MeleeAttackGoal(mob, 1.4D, true));
            }
        }
    }
    
    public static void execute() {}
}