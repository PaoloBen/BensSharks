package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;

public class ConditionShowCharge3Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity instanceof net.minecraft.world.entity.player.Player _player && (
		// CREATIVE: Show ONLY if unlocked (Charges >= 1)
		(_player.isCreative()
				&& (_player.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new net.mcreator.sharks.network.BenssharksModVariables.PlayerVariables())).ThresherRiptideCharges >= 1) ||
		// SURVIVAL: Check Cycling Math
				(!_player.isCreative()
						&& (_player.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new net.mcreator.sharks.network.BenssharksModVariables.PlayerVariables())).ThresherRiptideCharges > 0
						&& ((int) ((_player.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new net.mcreator.sharks.network.BenssharksModVariables.PlayerVariables())).ThresherRiptideCharges
								- 1) % 9) >= 2))
				&& entity.isAlive();
	}
}
