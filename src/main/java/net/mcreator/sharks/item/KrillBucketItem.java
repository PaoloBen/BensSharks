
package net.mcreator.sharks.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class KrillBucketItem extends Item {
	public KrillBucketItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
	}
}
