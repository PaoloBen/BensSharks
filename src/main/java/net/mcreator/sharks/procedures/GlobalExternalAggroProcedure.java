package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Arrays;
import java.util.function.Predicate;

@Mod.EventBusSubscriber
public class GlobalExternalAggroProcedure {

    // ================================================================= //
    //                       CONFIGURATION ZONE                          //
    // ================================================================= //

    // 1. THE AGGRESSORS: Mobs that should attack your sharks.
    private static final List<String> EXTERNAL_AGGRESSORS = Arrays.asList(
        "minecraft:guardian",
        "minecraft:elder_guardian"
    );

    // 2. THE VICTIMS: Your sharks.
    // If an entity here is NOT a living mob (e.g. an item), it will simply be ignored safely.
    private static final List<String> TARGET_SHARKS = Arrays.asList(
		"benssharks:angelshark",
        "benssharks:axodile",
        "benssharks:barracuda",
        "benssharks:basking_shark",
        "benssharks:blacktip_reef_shark",
        "benssharks:blue_shark",
        "benssharks:bonnethead_shark",
        "benssharks:bull_shark",
        "benssharks:cookiecutter_shark",
        "benssharks:goblin_shark",
        "benssharks:greater_axodile",
        "benssharks:greatwhite_shark",
        "benssharks:greenland_shark",
        "benssharks:krill",
        "benssharks:land_shark",
        "benssharks:lemon_shark",
        "benssharks:leopard_shark",
        "benssharks:mako_shark",
        "benssharks:megalodon",
        "benssharks:megamouth_shark",
        "benssharks:nurse_shark",
        "benssharks:pilot_fish",
        "benssharks:remora",
        "benssharks:sardine",
        "benssharks:sawshark",
        "benssharks:sea_lion",
        "benssharks:shark_minion",
        "benssharks:thresher_shark",
        "benssharks:tiger_shark",
        "benssharks:whale_shark",
        "benssharks:whitetip_shark",
        "benssharks:shrak"
    );

    // ================================================================= //
    //                          LOGIC ENGINE                             //
    // ================================================================= //

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        // 1. Check if the spawning entity is a Mob on the Server
        if (entity instanceof Mob mob && !entity.level().isClientSide()) {
            
            ResourceLocation mobID = ForgeRegistries.ENTITY_TYPES.getKey(mob.getType());
            
            // 2. Check if this mob is an AGGRESSOR (Guardian, Drowned, etc.)
            if (mobID != null && EXTERNAL_AGGRESSORS.contains(mobID.toString())) {
                
                // 3. Define the "Shark Filter"
                // This predicate returns TRUE only if the entity is one of your sharks.
                Predicate<LivingEntity> sharkSelector = (target) -> {
                    ResourceLocation targetID = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
                    return targetID != null && TARGET_SHARKS.contains(targetID.toString());
                };

                // 4. Inject the Single Goal
                // "Look for any LivingEntity nearby. If you find one, check the sharkSelector. If true, ATTACK."
                // Priority 2 ensures it takes precedence over wandering but yields to self-defense (HurtByTarget).
                mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                    mob, 
                    LivingEntity.class, 
                    10,     // Chance (10 is standard)
                    true,   // Must See
                    false,  // Must Reach (False allows Guardians to target from distance)
                    sharkSelector // The Filter
                ));
            }
        }
    }
    
    public static void execute() {}
}