package me.capit.urbanization.command;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDHome implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (s.hasPermission("urbanization.group.home") && s instanceof Player){
			Player p = (Player) s;
			Group g = args.length>0
					? Urbanization.getGroupByName(args[0])
					: Urbanization.getGroupByPlayer(p.getUniqueId());
			if (g!=null){
				if (g.playerCanTele(p.getUniqueId())){
					if (g.getHome()!=null){
						p.teleport(g.getHome());
						p.sendMessage(ChatColor.YELLOW+" ♦ "+ChatColor.GRAY+g.motd());
						return CResponse.NO_RESPONSE;
					} else {
						return CResponse.FAILED_NOT_POSSIBLE;
					}
				} else {
					return CResponse.FAILED_GROUP_PERMISSION;
				}
			} else {
				return CResponse.FAILED_UNKNOWN_GROUP;
			}
		} else {
			return CResponse.FAILED_PERMISSION;
		}
	}

}
