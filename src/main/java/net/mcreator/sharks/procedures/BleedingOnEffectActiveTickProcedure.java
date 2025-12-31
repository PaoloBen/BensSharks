package net.mcreator.sharks.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.BenssharksMod;

import java.util.List;
import java.util.Comparator;

public class BleedingOnEffectActiveTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (!((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem() == Items.CHAINMAIL_HELMET)
				&& !((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem() == Items.CHAINMAIL_CHESTPLATE)
				&& !((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem() == Items.CHAINMAIL_LEGGINGS)
				&& !((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem() == Items.CHAINMAIL_BOOTS) && entity instanceof LivingEntity _livEnt8
				&& _livEnt8.hasEffect(BenssharksModMobEffects.BLEEDING.get()) && !(entity instanceof Player _plr ? _plr.getAbilities().instabuild : false)) {
			{
				final Vec3 _center = new Vec3(x, y, z);
				List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(24 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (Entity entityiterator : _entfound) {
					if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("benssharks:sharks"))) && !(entityiterator instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false) && !(entityiterator == entity)) {
						if (!((entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) instanceof LivingEntity)) {
							if (entityiterator instanceof Mob _entity && entity instanceof LivingEntity _ent)
								_entity.setTarget(_ent);
						} else if (Math.sqrt(Math.pow(entityiterator.getX() - entity.getX(), 2) + Math.pow(entityiterator.getY() - entity.getY(), 2) + Math.pow(entityiterator.getZ() - entity.getZ(), 2)) < Math
								.sqrt(Math.pow(entityiterator.getX() - (entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getX(), 2)
										+ Math.pow(entityiterator.getY() - (entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getY(), 2)
										+ Math.pow(entityiterator.getZ() - (entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getZ(), 2))) {
							if (entityiterator instanceof Mob _entity && entity instanceof LivingEntity _ent)
								_entity.setTarget(_ent);
						}
					}
				}
			}
			if (!entity.getPersistentData().getBoolean("bleed")) {
				entity.getPersistentData().putBoolean("bleed", true);
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC_KILL)), 1);
				BenssharksMod.queueServerWork(60, () -> {
					entity.getPersistentData().putBoolean("bleed", false);
				});
			}
		}
	}
}
