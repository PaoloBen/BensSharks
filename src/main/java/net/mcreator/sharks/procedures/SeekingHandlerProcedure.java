package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;

import net.mcreator.sharks.init.BenssharksModEnchantments;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = "benssharks", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SeekingHandlerProcedure {

    // --- REFLECTION HELPER ---
    private static Field dealtDamageField;

    private static void initDealtDamageField() {
        if (dealtDamageField == null) {
            try {
                dealtDamageField = ThrownTrident.class.getDeclaredField("dealtDamage");
            } catch (NoSuchFieldException e) {
                try {
                    dealtDamageField = ThrownTrident.class.getDeclaredField("f_37567_");
                } catch (NoSuchFieldException ex) { }
            }
            if (dealtDamageField != null) {
                dealtDamageField.setAccessible(true);
            }
        }
    }

    private static void setDealtDamage(ThrownTrident trident, boolean value) {
        try {
            initDealtDamageField();
            if (dealtDamageField != null) dealtDamageField.setBoolean(trident, value);
        } catch (Exception e) { }
    }

    private static boolean getDealtDamage(ThrownTrident trident) {
        try {
            initDealtDamageField();
            if (dealtDamageField != null) return dealtDamageField.getBoolean(trident);
        } catch (Exception e) { }
        return false;
    }

    // --- EVENT 1: IMPACT HANDLER ---
    @SubscribeEvent
    public static void onProjectileHit(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof ThrownTrident trident) {
            try {
                if (trident.level().isClientSide) return;

                if (trident.getPersistentData().getBoolean("SeekingDeactivated")) return;
                if (getDealtDamage(trident)) {
                    trident.getPersistentData().putBoolean("SeekingDeactivated", true);
                    return;
                }

                CompoundTag nbt = new CompoundTag();
                trident.addAdditionalSaveData(nbt);
                ItemStack stack = ItemStack.of(nbt.getCompound("Trident"));
                int seekingLevel = EnchantmentHelper.getItemEnchantmentLevel(BenssharksModEnchantments.SEEKING.get(), stack);

                if (seekingLevel > 0 && event.getRayTraceResult().getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHit = (EntityHitResult) event.getRayTraceResult();
                    Entity target = entityHit.getEntity();

                    List<Integer> ignored = getIgnoredIds(trident);
                    if (ignored.contains(target.getId())) {
                        if (event.isCancelable()) event.setCanceled(true);
                        return;
                    }

                    if (target instanceof TamableAnimal tamed 
                        && trident.getOwner() instanceof LivingEntity owner
                        && tamed.isOwnedBy(owner)) {
                        if (event.isCancelable()) event.setCanceled(true); 
                        return;
                    }

                    int maxTargets = (seekingLevel * 2) + 1;
                    
                    if (ignored.size() < maxTargets) {
                        if (target instanceof LivingEntity livingTarget) {
                            float damageAmount = 8.0f + (seekingLevel * 1.5f);
                            DamageSource source = trident.level().damageSources().trident(trident, trident.getOwner());
                            
                            boolean hurt = livingTarget.hurt(source, damageAmount);
                            addToIgnoreList(trident, target.getId()); 

                            // --- TRIGGER CHANNELING MANUALLY ---
                            if (hurt && trident.level() instanceof ServerLevel serverLevel) {
                                spawnLightning(serverLevel, target, trident, stack);
                            }

                            // --- AGGRESSIVE DEACTIVATION ---
                            boolean quotaReached = getIgnoredIds(trident).size() >= maxTargets;
                            boolean noTargetsLeft = false;
                            if (!quotaReached) {
                                noTargetsLeft = scanForNextTarget(trident, seekingLevel) == null;
                            }

                            if (quotaReached || noTargetsLeft) {
                                if (hurt) trident.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
                                else trident.playSound(SoundEvents.SHIELD_BLOCK, 1.0f, 1.0f);

                                forceDeactivate(trident);
                                return; 
                            }

                            if (hurt) {
                                trident.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
                            } else {
                                trident.playSound(SoundEvents.SHIELD_BLOCK, 1.0f, 1.0f);
                                trident.setDeltaMovement(trident.getDeltaMovement().scale(-0.4).add(0, 0.3, 0));
                            }
                        }

                        if (event.isCancelable()) {
                            event.setCanceled(true);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // --- EVENT 2: TICK HANDLER ---
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.level.isClientSide()) {
            if (event.level instanceof ServerLevel _level) {
                List<Entity> tridents = new ArrayList<>();
                try {
                    for (Entity entity : _level.getAllEntities()) {
                        if (entity instanceof ThrownTrident) {
                            tridents.add(entity);
                        }
                    }
                } catch (Exception e) { }
                
                for (Entity entity : tridents) {
                    try {
                        tickTrident(_level, (ThrownTrident) entity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void tickTrident(ServerLevel level, ThrownTrident trident) {
        if (trident.getPersistentData().getBoolean("SeekingDeactivated")) return;
        if (getDealtDamage(trident)) {
            trident.getPersistentData().putBoolean("SeekingDeactivated", true);
            return;
        }
        if (trident.onGround()) {
            forceDeactivate(trident);
            return;
        }

        double maintainSpeed = 0.0;
        double currentVel = trident.getDeltaMovement().length();

        if (!trident.getPersistentData().contains("SeekingBaseSpeed")) {
            if (currentVel > 0.1) {
                maintainSpeed = currentVel;
                trident.getPersistentData().putDouble("SeekingBaseSpeed", maintainSpeed);
            } else {
                maintainSpeed = 1.0; 
            }
        } else {
            maintainSpeed = trident.getPersistentData().getDouble("SeekingBaseSpeed");
        }

        CompoundTag nbt = new CompoundTag();
        trident.addAdditionalSaveData(nbt);
        
        if (nbt.contains("Trident")) {
            ItemStack stack = ItemStack.of(nbt.getCompound("Trident"));
            int seekingLevel = EnchantmentHelper.getItemEnchantmentLevel(BenssharksModEnchantments.SEEKING.get(), stack);

            if (seekingLevel > 0) {
                
                int maxTargets = (seekingLevel * 2) + 1;
                List<Integer> ignoredEntityIds = getIgnoredIds(trident);

                if (ignoredEntityIds.size() >= maxTargets) {
                    forceDeactivate(trident);
                    return; 
                }

                if (trident.getY() < level.getMinBuildHeight() || 
                   (trident.getOwner() != null && trident.distanceToSqr(trident.getOwner()) > 16384)) {
                    forceDeactivate(trident);
                    return;
                }

                if (trident.getPierceLevel() < 10) {
                    trident.setPierceLevel((byte) 127);
                }

                LivingEntity target = scanForNextTarget(trident, seekingLevel);

                if (target != null) {
                    setDealtDamage(trident, false);
                    trident.setNoGravity(false); 

                    level.sendParticles(ParticleTypes.GLOW_SQUID_INK, 
                        trident.getX(), trident.getY(), trident.getZ(), 
                        1, 0.01, 0.01, 0.01, 0.0);

                    // PROXIMITY CHECK
                    if (trident.getBoundingBox().intersects(target.getBoundingBox().inflate(0.2))) {
                        float damageAmount = 8.0f + (seekingLevel * 1.5f); 
                        DamageSource source = level.damageSources().trident(trident, trident.getOwner());
                        boolean hurt = target.hurt(source, damageAmount);
                        
                        addToIgnoreList(trident, target.getId());

                        // --- TRIGGER CHANNELING MANUALLY ---
                        if (hurt) {
                            spawnLightning(level, target, trident, stack);
                        }

                        // AGGRESSIVE DEACTIVATION
                        boolean quotaReached = getIgnoredIds(trident).size() >= maxTargets;
                        boolean noTargetsLeft = false;

                        if (!quotaReached) {
                            noTargetsLeft = scanForNextTarget(trident, seekingLevel) == null;
                        }

                        if (quotaReached || noTargetsLeft) {
                            if (hurt) trident.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
                            else trident.playSound(SoundEvents.SHIELD_BLOCK, 1.0f, 1.0f);
                            
                            forceDeactivate(trident);
                            return;
                        }
                        
                        if (hurt) {
                            trident.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
                        } else {
                            trident.playSound(SoundEvents.SHIELD_BLOCK, 1.0f, 1.0f);
                            trident.setDeltaMovement(trident.getDeltaMovement().scale(-0.4).add(0, 0.3, 0));
                            return; 
                        }
                    }

                    // Seek Movement
                    Vec3 tridentPos = trident.position(); 
                    Vec3 targetPos = target.getBoundingBox().getCenter();
                    Vec3 directionToTarget = targetPos.subtract(tridentPos).normalize();
                    Vec3 currentMotion = trident.getDeltaMovement();
                    
                    double turnStrength;
                    double distSqr = trident.distanceToSqr(target);

                    if (distSqr < 25.0) {
                        turnStrength = 1.0; 
                    } else {
                        turnStrength = Math.min(0.08 + (seekingLevel * 0.05), 0.5);
                    }

                    Vec3 newMotion = currentMotion.scale(1.0 - turnStrength)
                                                  .add(directionToTarget.scale(maintainSpeed * turnStrength));
                    
                    trident.setDeltaMovement(newMotion.normalize().scale(maintainSpeed));
                    trident.hasImpulse = true; 

                } else {
                    if (!ignoredEntityIds.isEmpty()) {
                        forceDeactivate(trident);
                    } else {
                        setDealtDamage(trident, false);
                    }
                }
            }
        }
    }

    // --- HELPER: LIGHTNING SPAWNER ---
    private static void spawnLightning(ServerLevel level, Entity target, ThrownTrident trident, ItemStack stack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.CHANNELING, stack) > 0) {
            // Check for thunderstorm
            if (level.isThundering()) {
                BlockPos pos = target.blockPosition();
                // Check if target can see sky
                if (level.canSeeSky(pos.above())) {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                    if (lightning != null) {
                        lightning.moveTo(Vec3.atBottomCenterOf(pos));
                        
                        // Set owner for damage credit
                        Entity owner = trident.getOwner();
                        if (owner instanceof net.minecraft.server.level.ServerPlayer player) {
                            lightning.setCause(player);
                        }
                        
                        level.addFreshEntity(lightning);
                    }
                }
            }
        }
    }

    private static LivingEntity scanForNextTarget(ThrownTrident trident, int seekingLevel) {
        double range = 8.0 + (seekingLevel * 2.0); 
        AABB searchBox = trident.getBoundingBox().inflate(range);
        List<Integer> ignoredEntityIds = getIgnoredIds(trident);
        
        List<LivingEntity> potentialTargets = trident.level().getEntitiesOfClass(LivingEntity.class, searchBox, (t) -> {
            if (t == trident.getOwner() || !t.isAlive() || t.isSpectator() || ignoredEntityIds.contains(t.getId())) return false;
            if (t instanceof Player p && (p.isCreative() || p.isSpectator())) return false;
            boolean isHostile = t instanceof Enemy || t instanceof Monster;
            return isHostile;
        });

        return potentialTargets.stream()
            .min(Comparator.comparingDouble(e -> e.distanceToSqr(trident)))
            .orElse(null);
    }

    private static void forceDeactivate(ThrownTrident trident) {
        trident.getPersistentData().putBoolean("SeekingDeactivated", true);
        setDealtDamage(trident, true);

        if (trident.getPierceLevel() > 0) {
            trident.setPierceLevel((byte) 0);
        }
    }

    private static List<Integer> getIgnoredIds(ThrownTrident trident) {
        List<Integer> list = new ArrayList<>();
        if (trident.getPersistentData().contains("SeekingIgnoredIDs", Tag.TAG_LIST)) {
            ListTag listTag = trident.getPersistentData().getList("SeekingIgnoredIDs", Tag.TAG_INT);
            for (Tag t : listTag) {
                list.add(((IntTag) t).getAsInt());
            }
        }
        return list;
    }

    private static void addToIgnoreList(ThrownTrident trident, int id) {
        CompoundTag data = trident.getPersistentData();
        ListTag ignoredList;
        if (data.contains("SeekingIgnoredIDs", Tag.TAG_LIST)) {
            ignoredList = data.getList("SeekingIgnoredIDs", Tag.TAG_INT);
        } else {
            ignoredList = new ListTag();
        }

        boolean alreadyListed = false;
        for (Tag t : ignoredList) {
            if (((IntTag) t).getAsInt() == id) alreadyListed = true;
        }
        
        if (!alreadyListed) {
            ignoredList.add(IntTag.valueOf(id));
            data.put("SeekingIgnoredIDs", ignoredList);
        }
    }
}