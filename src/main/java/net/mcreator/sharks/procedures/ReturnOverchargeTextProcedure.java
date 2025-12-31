package net.mcreator.sharks.procedures;

import net.minecraft.world.entity.Entity;

public class ReturnOverchargeTextProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		if (entity.isAlive()) {
			return (entity instanceof net.minecraft.world.entity.player.Player _player && _player.isCreative()
					&& (_player.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new net.mcreator.sharks.network.BenssharksModVariables.PlayerVariables())).ThresherRiptideCharges >= 1)
							? "\u221E"
							: ("" + (int) (((entity.getCapability(net.mcreator.sharks.network.BenssharksModVariables.PLAYER_VARIABLES_CAPABILITY, null)
									.orElse(new net.mcreator.sharks.network.BenssharksModVariables.PlayerVariables())).ThresherRiptideCharges - 1) / 9));
		}
		return "";
	}
}
