package net.mcreator.sharks.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments; // Import needed
import net.minecraft.world.item.enchantment.EnchantmentHelper; // Import needed
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;

import net.mcreator.sharks.procedures.MaelstromBowRightclickedProcedure;
import net.mcreator.sharks.procedures.MaelstromBowRangedItemShootsProjectileProcedure;
import net.mcreator.sharks.procedures.MaelstromBowItemInHandTickProcedure;
import net.mcreator.sharks.procedures.MaelstromBowEntitySwingsItemProcedure;
import net.mcreator.sharks.procedures.MaelstromBowCanUseRangedItemProcedure;
import net.mcreator.sharks.entity.SeekingArrowEntity;

public class MaelstromBowItem extends BowItem {
    public MaelstromBowItem() {
        super(new Item.Properties().durability(500).rarity(Rarity.RARE));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.BOW;
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 99999;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
        InteractionResultHolder<ItemStack> ar = InteractionResultHolder.fail(entity.getItemInHand(hand));
        if (MaelstromBowCanUseRangedItemProcedure.execute(world, entity))
            if (entity.getAbilities().instabuild || findAmmo(entity) != ItemStack.EMPTY) {
                ar = InteractionResultHolder.pass(entity.getItemInHand(hand));
                entity.startUsingItem(hand);
            }
        MaelstromBowRightclickedProcedure.execute();
        return ar;
    }

    @Override
    public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
        boolean retval = super.onEntitySwing(itemstack, entity);
        MaelstromBowEntitySwingsItemProcedure.execute();
        return retval;
    }

    @Override
    public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);
        if (selected)
            MaelstromBowItemInHandTickProcedure.execute();
    }

    @Override
    public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
        if (!world.isClientSide() && entity instanceof ServerPlayer player) {
            float pullingPower = BowItem.getPowerForTime(this.getUseDuration(itemstack) - time);
            if (pullingPower < 0.1)
                return;
            
            ItemStack stack = findAmmo(player);
            
            // LOGIC FIX: Check for Infinity Enchantment
            boolean isInfinite = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemstack) > 0;

            if (isInfinite || stack != ItemStack.EMPTY) {
                SeekingArrowEntity projectile = SeekingArrowEntity.shoot(world, entity, world.getRandom(), pullingPower);

                // --- ENCHANTMENT LOGIC START ---
                
                // 1. POWER (Increases Damage)
                int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemstack);
                if (powerLevel > 0) {
                    projectile.setBaseDamage(projectile.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
                }

                // 2. PUNCH (Increases Knockback)
                int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemstack);
                if (punchLevel > 0) {
                    projectile.setKnockback(punchLevel);
                }

                // 3. FLAME (Sets entity on fire)
                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemstack) > 0) {
                    projectile.setSecondsOnFire(100);
                }
                
                // --- ENCHANTMENT LOGIC END ---

                itemstack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(entity.getUsedItemHand()));
                
                // AMMO CONSUMPTION LOGIC
                if (isInfinite) {
                    // If infinite, the arrow cannot be picked up (prevents farming arrows)
                    projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                } else {
                    if (stack.isDamageableItem()) {
                        if (stack.hurt(1, world.getRandom(), player)) {
                            stack.shrink(1);
                            stack.setDamageValue(0);
                            if (stack.isEmpty())
                                player.getInventory().removeItem(stack);
                        }
                    } else {
                        // Consume the arrow if NOT infinite
                        stack.shrink(1);
                        if (stack.isEmpty())
                            player.getInventory().removeItem(stack);
                    }
                }
                
                MaelstromBowRangedItemShootsProjectileProcedure.execute(world, entity);
            }
        }
    }

    private ItemStack findAmmo(Player player) {
        ItemStack stack = ProjectileWeaponItem.getHeldProjectile(player, e -> e.getItem() == SeekingArrowEntity.PROJECTILE_ITEM.getItem());
        if (stack == ItemStack.EMPTY) {
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack teststack = player.getInventory().items.get(i);
                if (teststack != null && teststack.getItem() == SeekingArrowEntity.PROJECTILE_ITEM.getItem()) {
                    stack = teststack;
                    break;
                }
            }
        }
        return stack;
    }
}