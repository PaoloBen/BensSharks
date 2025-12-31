package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;

public class ConditionShowOverchargeProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity instanceof net.minecraft.world.entity.player.Player _player && (
		// CREATIVE: Show if unlocked (Charges >= 1)
		(_player.isCreative()
				&& (_player.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new net.mcreator.sharks.network.BenssharksModVariables.PlayerVariables())).ThresherRiptideCharges >= 1) ||
		// SURVIVAL: Show if Stack >= 1 (Charges >= 10)
				(!_player.isCreative()
						&& ((int) ((_player.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new net.mcreator.sharks.network.BenssharksModVariables.PlayerVariables())).ThresherRiptideCharges
								- 1) / 9) >= 1))
				&& entity.isAlive();
	}
}
