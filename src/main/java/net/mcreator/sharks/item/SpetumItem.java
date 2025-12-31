package net.mcreator.sharks.item;

import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoItem;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

import net.mcreator.sharks.procedures.SpetumSpecialInformationProcedure;
import net.mcreator.sharks.procedures.SpetumLivingEntityIsHitWithItemProcedure;
import net.mcreator.sharks.item.renderer.SpetumItemRenderer;

import java.util.function.Consumer;
import java.util.UUID;
import java.util.List;

import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;

public class SpetumItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	private static final UUID REACH_MODIFIER_UUID = UUID.fromString("c07b0682-f548-4e32-8411-18e3c153327d");

	public SpetumItem() {
		super(new Item.Properties().durability(750).rarity(Rarity.UNCOMMON));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new SpetumItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment.category == EnchantmentCategory.WEAPON) {
			return true;
		}
		if (enchantment == Enchantments.RIPTIDE || enchantment == Enchantments.IMPALING) {
			return true;
		}
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			
			// Attack Damage (7)
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Item modifier", 7d, AttributeModifier.Operation.ADDITION));
			
			// Attack Speed (-2.4)
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Item modifier", -2.4, AttributeModifier.Operation.ADDITION));
			
			// REACH INCREASED to 2.5 blocks (Total ~5.5 reach in Survival)
			builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(REACH_MODIFIER_UUID, "Reach modifier", 2.5, AttributeModifier.Operation.ADDITION));
			
			return builder.build();
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
			return InteractionResultHolder.fail(itemstack);
		} else {
			player.startUsingItem(hand);
			return InteractionResultHolder.consume(itemstack);
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof Player player) {
			int i = this.getUseDuration(stack) - timeLeft;
			
			if (i >= 10) { 
				int riptideLevel = EnchantmentHelper.getRiptide(stack);
				
				// --- RIPTIDE LOGIC ---
				if (riptideLevel > 0 && player.isInWaterOrRain()) {
					float f = player.getYRot();
					float f1 = player.getXRot();
					float f2 = -Mth.sin(f * ((float) Math.PI / 180F)) * Mth.cos(f1 * ((float) Math.PI / 180F));
					float f3 = -Mth.sin(f1 * ((float) Math.PI / 180F));
					float f4 = Mth.cos(f * ((float) Math.PI / 180F)) * Mth.cos(f1 * ((float) Math.PI / 180F));
					float f5 = Mth.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
					float f6 = 3.0F * ((1.0F + (float) riptideLevel) / 4.0F);
					f2 *= f6 / f5;
					f3 *= f6 / f5;
					f4 *= f6 / f5;
					player.push((double) f2, (double) f3, (double) f4);
					player.startAutoSpinAttack(20);
					
					if (player.onGround()) {
						player.move(MoverType.SELF, new Vec3(0.0D, 1.1999999F, 0.0D));
					}
					
					SoundEvent soundevent = (riptideLevel >= 3) ? SoundEvents.TRIDENT_RIPTIDE_3 : 
											(riptideLevel == 2 ? SoundEvents.TRIDENT_RIPTIDE_2 : SoundEvents.TRIDENT_RIPTIDE_1);
					level.playSound(null, player, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
					
					if (!player.getAbilities().instabuild) {
						stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(entityLiving.getUsedItemHand()));
					}

				// --- SWEEP ATTACK LOGIC ---
				} else {
					// 1. LUNGE
					Vec3 lookVec = player.getLookAngle();
					player.push(lookVec.x * 0.8, 0.1, lookVec.z * 0.8);
					
					// 2. SERVER LOGIC
					if (!level.isClientSide) {
						int sweepingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, stack);
						
						level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
						
						double partX = player.getX() + lookVec.x;
						double partY = player.getY() + player.getEyeHeight() * 0.5;
						double partZ = player.getZ() + lookVec.z;
						
						if (level instanceof ServerLevel serverLevel) {
							serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, partX, partY, partZ, 1, 0, 0, 0, 0);
						}

						double range = 1.5 + (sweepingLevel * 0.5);
						Vec3 sweepCenter = player.position().add(lookVec.scale(1.5));
						AABB sweepArea = new AABB(sweepCenter, sweepCenter).inflate(range, 1.0, range);
						
						List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, sweepArea);
						
						for (LivingEntity target : list) {
							if (target != player && !target.isAlliedTo(player) && target instanceof LivingEntity) {
								
								float damage = ((float)player.getAttributeValue(Attributes.ATTACK_DAMAGE)) * 1.25F;
								
								if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.IMPALING, stack) > 0 && target.isInWaterOrRain()) {
									damage += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.IMPALING, stack) * 2.5F;
								}

								boolean successfullyHit = target.hurt(level.damageSources().playerAttack(player), damage);
								
								if (successfullyHit) {
									target.knockback(0.4F, Mth.sin(player.getYRot() * ((float)Math.PI / 180F)), -Mth.cos(player.getYRot() * ((float)Math.PI / 180F)));
								}
							}
						}
						
						stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
						player.resetAttackStrengthTicker();
						
						// CHANGED: 30 ticks = 1.5 seconds cooldown
						player.getCooldowns().addCooldown(this, 30);
					}
				}
			}
		}
	}

	private PlayState idlePredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("1"));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (this.animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		AnimationController procedureController = new AnimationController(this, "procedureController", 0, this::procedurePredicate);
		data.add(procedureController);
		AnimationController idleController = new AnimationController(this, "idleController", 0, this::idlePredicate);
		data.add(idleController);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.SPEAR;
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		Entity entity = itemstack.getEntityRepresentation();
		String hoverText = SpetumSpecialInformationProcedure.execute();
		if (hoverText != null) {
			for (String line : hoverText.split("\n")) {
				list.add(Component.literal(line));
			}
		}
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		itemstack.hurtAndBreak(1, sourceentity, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		SpetumLivingEntityIsHitWithItemProcedure.execute(entity.level(), entity, sourceentity);
		return retval;
	}
}