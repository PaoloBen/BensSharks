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
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.init.BenssharksModMobEffects; // Added Import for Effects
import net.mcreator.sharks.network.BenssharksModVariables;

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
            if (anim == UseAnim.BOW || anim == UseAnim.CROSSBOW || anim == UseAnim.SPEAR || 
                anim == UseAnim.SPYGLASS || anim == UseAnim.BLOCK || 
                anim == UseAnim.EAT || anim == UseAnim.DRINK) {
                return;
            }

            // --- 3. COMPATIBILITY CHECK: PROPERTIES ---
            if (mainHand.isEdible()) {
                return;
            }

            // --- 4. COMPATIBILITY CHECK: TAGS ---
            if (mainHand.is(ItemTags.create(new ResourceLocation("minecraft", "bows"))) || 
                mainHand.is(ItemTags.create(new ResourceLocation("minecraft", "boats"))) ||
                mainHand.is(ItemTags.create(new ResourceLocation("forge", "foods")))) {
                return;
            }

            // --- 5. COMPATIBILITY CHECK: CLASSES ---
            if (mainHand.getItem() instanceof ProjectileWeaponItem ||  
                mainHand.getItem() instanceof FishingRodItem ||      
                mainHand.getItem() instanceof SnowballItem ||        
                mainHand.getItem() instanceof EggItem ||             
                mainHand.getItem() instanceof EnderpearlItem ||      
                mainHand.getItem() instanceof ExperienceBottleItem || 
                mainHand.getItem() instanceof SplashPotionItem ||    
                mainHand.getItem() instanceof LingeringPotionItem ||
                mainHand.getItem() instanceof PotionItem) {
                return;
            }

            // --- 6. SAFETY CHECK: ENTITY INTERACTION ---
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

            // --- 7. ENVIRONMENT CHECK (RIPTIDE RESTRICTION) ---
            // Allow if: (Wet OR Rain OR Bubble) OR (Has Frenzy Effect)
            boolean isWet = entity.isInWaterRainOrBubble();
            boolean hasFrenzy = player.hasEffect(BenssharksModMobEffects.FRENZY.get());

            if (!isWet && !hasFrenzy) {
                return;
            }

            // --- 8. RIPTIDE LOGIC (Server Side Only) ---
            if (!world.isClientSide()) {
                
                double charges = (entity.getCapability(BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                        .orElse(new BenssharksModVariables.PlayerVariables())).ThresherRiptideCharges;

                if (charges > 0) {
                    
                    if (!player.isCreative()) {
                         {
                            double _setval = charges - 1;
                            final net.minecraft.world.entity.Entity _ent = entity;
                            entity.getCapability(BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                                capability.ThresherRiptideCharges = _setval;
                                capability.syncPlayerVariables(_ent);
                            });
                        }
                    }

                    net.minecraft.world.phys.Vec3 launchDir = entity.getViewVector(1.0f);
                    entity.setDeltaMovement(launchDir.x * 3.0, launchDir.y * 3.0, launchDir.z * 3.0);
                    
                    entity.hasImpulse = true; 
                    player.hurtMarked = true; 
                    entity.fallDistance = 0;

                    player.startAutoSpinAttack(20);

                    if (world instanceof net.minecraft.world.level.Level _level) {
                        _level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), 
                            net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS.getValue(new net.minecraft.resources.ResourceLocation("item.trident.riptide_3")), 
                            net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
                    }
                }
            }
        }
    }
}