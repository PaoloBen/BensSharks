package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.sharks.init.BenssharksModItems;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class ThresherRiptideAbilityProcedure {

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() != event.getEntity().getUsedItemHand())
            return;
        execute(event);
    }

    public static void execute() {
        execute(null);
    }

    private static void execute(@Nullable Event event) {
        net.minecraft.world.entity.Entity entity = null;
        if (event instanceof PlayerInteractEvent _event) {
            entity = _event.getEntity();
        }
        
        if (entity == null) return;
        net.minecraft.world.level.LevelAccessor world = entity.level();

        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            
            ItemStack mainHand = player.getMainHandItem();

            // --- 1. COMPATIBILITY CHECK: SPECIFIC MOD ITEMS ---
            if (mainHand.getItem() == BenssharksModItems.MAELSTROM_BOW.get() || 
                mainHand.getItem() == BenssharksModItems.MUTATED_EGG.get()) {
                return;
            }

            // --- 2. COMPATIBILITY CHECK: ANIMATIONS ---
            UseAnim anim = mainHand.getUseAnimation();
            if (anim == UseAnim.BOW || anim == UseAnim.CROSSBOW || anim == UseAnim.SPEAR || anim == UseAnim.SPYGLASS || anim == UseAnim.BLOCK) {
                return;
            }

            // --- 3. COMPATIBILITY CHECK: TAGS ---
            if (mainHand.is(ItemTags.create(new ResourceLocation("minecraft", "bows"))) || 
                mainHand.is(ItemTags.create(new ResourceLocation("minecraft", "boats")))) {
                return;
            }

            // --- 4. COMPATIBILITY CHECK: CLASSES ---
            if (mainHand.getItem() instanceof ProjectileWeaponItem ||  
                mainHand.getItem() instanceof FishingRodItem ||      
                mainHand.getItem() instanceof SnowballItem ||        
                mainHand.getItem() instanceof EggItem ||             
                mainHand.getItem() instanceof EnderpearlItem ||      
                mainHand.getItem() instanceof ExperienceBottleItem || 
                mainHand.getItem() instanceof SplashPotionItem ||    
                mainHand.getItem() instanceof LingeringPotionItem) { 
                return;
            }

            // --- 5. SAFETY CHECK: ENTITY INTERACTION ---
            Vec3 eyePos = entity.getEyePosition(1.0F);
            Vec3 viewVec = entity.getViewVector(1.0F);
            Vec3 targetVec = eyePos.add(viewVec.scale(5.0d));
            AABB box = entity.getBoundingBox().expandTowards(viewVec.scale(5.0d)).inflate(1.0D);
            
            EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                entity, 
                eyePos, 
                targetVec, 
                box, 
                (e) -> !e.isSpectator() && e.isPickable(), 
                0
            );
            
            if (hitResult != null) {
                return;
            }

            // --- 6. RIPTIDE LOGIC (Server Side Only) ---
            if (!world.isClientSide()) {
                double charges = entity.getPersistentData().getDouble("ThresherRiptideCharges");

                // REQUIREMENT: Must have at least 1 charge (Even in Creative)
                if (charges > 0) {
                    
                    // CONSUMPTION: Only remove charge if NOT Creative
                    if (!player.isCreative()) {
                        entity.getPersistentData().putDouble("ThresherRiptideCharges", charges - 1);
                    }

                    // Physics Launch
                    net.minecraft.world.phys.Vec3 launchDir = entity.getViewVector(1.0f);
                    entity.setDeltaMovement(launchDir.x * 3.0, launchDir.y * 3.0, launchDir.z * 3.0);
                    
                    // Force Sync
                    entity.hasImpulse = true; 
                    player.hurtMarked = true; 
                    entity.fallDistance = 0;

                    // Animation & Sound
                    player.startAutoSpinAttack(20);

                    if (world instanceof net.minecraft.world.level.Level _level) {
                        _level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), 
                            net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS.getValue(new net.minecraft.resources.ResourceLocation("item.trident.riptide_3")), 
                            net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
                    }

                    // Status Message Logic
                    if (!player.isCreative()) {
                        // Survival: Show remaining count
                        player.displayClientMessage(net.minecraft.network.chat.Component.literal("\u00A7bThresher Charges: " + (int) (charges - 1)), true);
                    } else {
                        // Creative: Show Infinity Symbol (\u221E)
                        player.displayClientMessage(net.minecraft.network.chat.Component.literal("\u00A7bThresher Charges: \u221E"), true);
                    }
                }
            }
        }
    }
}