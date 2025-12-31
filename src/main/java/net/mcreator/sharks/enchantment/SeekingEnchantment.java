package net.mcreator.sharks.enchantment;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.EquipmentSlot;

public class SeekingEnchantment extends Enchantment {

    public SeekingEnchantment() {
        // CHANGED: Use standard TRIDENT category instead of a custom one.
        // CHANGED: Rarity.RARE makes it harder to get (better balance for a homing missile).
        super(Enchantment.Rarity.RARE, EnchantmentCategory.TRIDENT, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinCost(int level) {
        // Standard cost scaling for mid-tier enchantments
        return 10 + (level - 1) * 8;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        // You can keep this at 4, but 3 is usually standard for Trident enchants.
        return 3;
    }

    @Override
    public boolean checkCompatibility(Enchantment other) {
        // CHANGED: Simplified check.
        // This ensures you cannot have Riptide AND Seeking on the same trident.
        return super.checkCompatibility(other) && other != Enchantments.RIPTIDE;
    }
}