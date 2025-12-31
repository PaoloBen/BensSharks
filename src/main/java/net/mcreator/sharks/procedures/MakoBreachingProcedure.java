package net.mcreator.sharks.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents; 
import net.mcreator.sharks.entity.MakoSharkEntity;

@Mod.EventBusSubscriber
public class MakoBreachingProcedure {

    private static final double JUMP_STRENGTH = 1.3;     
    private static final double SPEED_BOOST = 2.0;       
    private static final double COOLDOWN_SECONDS = 15.0; 
    private static final double DETECTION_RANGE = 24.0;

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof MakoSharkEntity) {
            executeBreachLogic(entity);
        }
    }

    private static void executeBreachLogic(Entity entity) {
        MakoSharkEntity shark = (MakoSharkEntity) entity;
        if (entity.level().isClientSide()) return;

        // Use Synced Data
        boolean isBreaching = shark.getEntityData().get(MakoSharkEntity.DATA_Breaching);
        LivingEntity target = shark.getTarget();
        
        // ================= 1. ACTIVE BREACH LOGIC (Mid-Air) =================
        if (isBreaching) {
            
            // A. Stop if Landed or Falling into Water
            // If we are RISING (launching), do not cancel even if touching water blocks.
            boolean isRising = shark.getDeltaMovement().y > 0.1;

            if (shark.onGround() || (shark.isInWater() && !isRising)) {
                shark.getEntityData().set(MakoSharkEntity.DATA_Breaching, false);
                return; // Reset complete
            }

            // B. Stop if Target is Invalid (Dead or Null)
            if (target == null || !target.isAlive()) {
                shark.getEntityData().set(MakoSharkEntity.DATA_Breaching, false);
                return; 
            }

            // [REMOVED] "Maintain Physics" Block
            // This was the cause of the mid-air boost. 
            // Now the shark will purely rely on the momentum it got from the launch.
            
            // Force Sprint OFF
            shark.getEntityData().set(MakoSharkEntity.DATA_Sprinting, false);
            return; 
        }

        // ================= 2. START BREACH LOGIC (In Water) =================
        
        double cooldown = shark.getPersistentData().getDouble("BreachCooldown");
        if (cooldown > 0) {
            shark.getPersistentData().putDouble("BreachCooldown", cooldown - 1);
        }

        if (cooldown <= 0 && shark.isInWater()) {
            
            Vec3 motion = shark.getDeltaMovement();
            
            if (motion.horizontalDistanceSqr() > 0.01) {
                if (target != null && target.isAlive() && shark.distanceTo(target) <= DETECTION_RANGE) {
                    
                    double heightDifference = target.getY() - shark.getY();
                    
                    if (heightDifference >= 1.0 && heightDifference <= 8.0) {
                        
                        BlockPos surfaceCheck = BlockPos.containing(shark.getX(), shark.getY() + 1.2, shark.getZ());
                        
                        if (shark.level().getBlockState(surfaceCheck).is(Blocks.AIR)) {
                            
                            // --- LAUNCH ACTION ---
                            // This is the only time we add velocity.
                            shark.setDeltaMovement(motion.x * SPEED_BOOST, JUMP_STRENGTH, motion.z * SPEED_BOOST);
                            
                            shark.getPersistentData().putDouble("BreachCooldown", COOLDOWN_SECONDS * 20);
                            
                            // SET SYNCED STATE TO TRUE
                            shark.getEntityData().set(MakoSharkEntity.DATA_Breaching, true);
                            
                            shark.getEntityData().set(MakoSharkEntity.DATA_Sprinting, false);
                            shark.playSound(SoundEvents.DOLPHIN_JUMP, 1.0f, 1.0f);
                        }
                    }
                }
            }
        }
    }
}