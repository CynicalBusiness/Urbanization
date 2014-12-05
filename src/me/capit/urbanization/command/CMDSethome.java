package me.capit.urbanization.command;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDSethome implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (s.hasPermission("urbanization.group.sethome") && s instanceof Player){
			if (args.length==0){
				Player p = (Player) s;
				Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
				if (g!=null){
					Location targ = p.getLocation();
					if (g.territoryBelongsToGroup(targ.getChunk().getX(), targ.getChunk().getZ())){
						if (g.playerHasPermission(p.getUniqueId(), "group.sethome")){
							g.setHome(targ);
							return CResponse.SUCCESS;
						} else {
							return CResponse.FAILED_GROUP_PERMISSION;
						}
					} else {
						return CResponse.FAILED_NOT_IN_TERRITORY;
					}
				} else {
					return CResponse.FAILED_NOT_IN_GROUP;
				}
			} else if (s.hasPermission("urbanization.admin.sethomeother") && s instanceof Player){
				Group g = Urbanization.getGroupByName(args[0]);
				Player p = (Player) s;
				if (g!=null){
					g.setHome(p.getLocation());
					return CResponse.SUCCESS;
				} else {
					return CResponse.FAILED_UNKNOWN_GROUP;
				}
			} else {
				return CResponse.FAILED_NOT_POSSIBLE;
			}
		} else {
			return CResponse.FAILED_PERMISSION;
		}
	}

}
