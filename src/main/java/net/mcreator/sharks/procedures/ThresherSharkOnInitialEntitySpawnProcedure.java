package net.mcreator.sharks.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import net.mcreator.sharks.entity.ThresherSharkEntity;

import java.util.List;
import java.util.Comparator;

public class ThresherSharkOnInitialEntitySpawnProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof ThresherSharkEntity) {
			{
				final Vec3 _center = new Vec3(x, y, z);
				List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(50 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (Entity entityiterator : _entfound) {
					if (entityiterator instanceof ElderGuardian || entityiterator instanceof Guardian) {
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
		}
	}
}
