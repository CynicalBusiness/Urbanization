package me.capit.urbanization.command;

import java.io.IOException;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDUnclaim implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (s.hasPermission("urbanization.group.claim") && s instanceof Player){
			Player p = (Player) s;
			Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
			Chunk chunk = p.getLocation().getChunk();
			if (g!=null){
				if (g.playerHasPermission(p.getUniqueId(), "territory.unclaim")){
					if (g.territoryBelongsToGroup(chunk.getX(), chunk.getZ())){
						g.removeTerritory(chunk.getX(), chunk.getZ());
						if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
							g.funds(g.funds()+Urbanization.CONTROLLER.getGroupData().getDouble("econ_reward_unclaim"));
						try {
							g.save();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return CResponse.SUCCESS;
					} else {
						return CResponse.FAILED_NOT_IN_TERRITORY;
					}
				} else {
					return CResponse.FAILED_GROUP_PERMISSION;
				}
			} else {
				return CResponse.FAILED_NOT_IN_GROUP;
			}
		} else {
			return CResponse.FAILED_PERMISSION;
		}
	}

}
