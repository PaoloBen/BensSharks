package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.init.BenssharksModEnchantments;
import net.mcreator.sharks.entity.WhitetipSharkEntity;
import net.mcreator.sharks.entity.TigerSharkEntity;
import net.mcreator.sharks.entity.ThresherSharkEntity;
import net.mcreator.sharks.entity.ShrakEntity;
import net.mcreator.sharks.entity.SharkMinionEntity;
import net.mcreator.sharks.entity.SawsharkEntity;
import net.mcreator.sharks.entity.MegalodonEntity;
import net.mcreator.sharks.entity.MakoSharkEntity;
import net.mcreator.sharks.entity.LemonSharkEntity;
import net.mcreator.sharks.entity.LandSharkEntity;
import net.mcreator.sharks.entity.GreenlandSharkEntity;
import net.mcreator.sharks.entity.GreaterAxodileEntity;
import net.mcreator.sharks.entity.GoblinSharkEntity;
import net.mcreator.sharks.entity.BullSharkEntity;
import net.mcreator.sharks.entity.BonnetheadSharkEntity;
import net.mcreator.sharks.entity.BlueSharkEntity;
import net.mcreator.sharks.entity.BlacktipReefSharkEntity;
import net.mcreator.sharks.entity.AxodileEntity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class BleedingGlobalProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingAttackEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getSource(), event.getEntity(), event.getSource().getEntity());
		}
	}

	public static void execute(LevelAccessor world, DamageSource damagesource, Entity entity, Entity sourceentity) {
		execute(null, world, damagesource, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, DamageSource damagesource, Entity entity, Entity sourceentity) {
		if (damagesource == null || entity == null || sourceentity == null)
			return;
		// Check if attacker is Thresher Shark
		if (sourceentity instanceof net.mcreator.sharks.entity.ThresherSharkEntity shark) {
			// If "WhipHitFrame" is TRUE, it means we are currently running the code inside the
			// "timer == 5" block in the tick update. This is Whip Damage.
			if (shark.getPersistentData().getBoolean("WhipHitFrame")) {
				return; // Stop. Do NOT apply bleeding.
			}
		}
		if (sourceentity instanceof ShrakEntity || sourceentity instanceof TigerSharkEntity || sourceentity instanceof BlueSharkEntity || sourceentity instanceof MakoSharkEntity || sourceentity instanceof BonnetheadSharkEntity
				|| sourceentity instanceof BlacktipReefSharkEntity || sourceentity instanceof BullSharkEntity || sourceentity instanceof MegalodonEntity || sourceentity instanceof LemonSharkEntity || sourceentity instanceof GreenlandSharkEntity
				|| sourceentity instanceof WhitetipSharkEntity || sourceentity instanceof LandSharkEntity || sourceentity instanceof GoblinSharkEntity || sourceentity instanceof SawsharkEntity || sourceentity instanceof SharkMinionEntity
				|| sourceentity instanceof ThresherSharkEntity) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.BLEEDING.get(), 200, 0, true, false));
		}
		if (EnchantmentHelper.getItemEnchantmentLevel(BenssharksModEnchantments.SERRATED.get(), (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)) != 0
				&& ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:tools/swords")))
						|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:swords")))
						|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:tools/tridents")))
						|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:tools/tridents")))
						|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof TridentItem
						|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof SwordItem
						|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:tools/axes")))
						|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:axes")))
						|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof AxeItem)) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.BLEEDING.get(), 200, 0, true, false));
		}
		if (!damagesource.isIndirect() && (entity instanceof AxodileEntity || entity instanceof GreaterAxodileEntity
				|| (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem() == BenssharksModItems.JAGGED_HELMET.get()
						&& (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem() == BenssharksModItems.JAGGED_CHESTPLATE.get()
						&& (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem() == BenssharksModItems.JAGGED_LEGGINGS.get()
						&& (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem() == BenssharksModItems.JAGGED_BOOTS.get())) {
			if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(BenssharksModMobEffects.BLEEDING.get(), 100, 0, true, false));
		}
	}
}
