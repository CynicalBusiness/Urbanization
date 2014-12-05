package me.capit.urbanization.command;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDDisband implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (s.hasPermission("urbanization.group.disband")){
			if (args.length==0 && s instanceof Player){
				Player p = (Player) s;
				Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
				if (g!=null){
					if (g.playerHasPermission(p.getUniqueId(), "group.disband")){
						Urbanization.deleteGroupByID(g.ID);
						return CResponse.SUCCESS;
					} else {
						return CResponse.FAILED_GROUP_PERMISSION;
					}
				} else {
					return CResponse.FAILED_NOT_IN_GROUP;
				}
			} else if (s.hasPermission("urbanization.admin.disbandother")){
				Group g = Urbanization.getGroupByName(args[0]);
				if (g!=null){
					Urbanization.deleteGroupByID(g.ID);
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
