<<<<<<< HEAD
package me.capit.urbanization.command;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDJoin implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (args.length==1){
			if (s.hasPermission("urbanization.group.join") && s instanceof Player){
				Player p = (Player) s;
				if (Urbanization.getGroupByPlayer(p.getUniqueId())==null){
					Group targ = Urbanization.getGroupByName(args[1]);
					if (targ!=null){
						if (Urbanization.invites.containsKey(p.getUniqueId()) || targ.isOpen()){
							if (!targ.isOpen()) Urbanization.invites.remove(p.getUniqueId());
							targ.addPlayer(p.getUniqueId());
							p.sendMessage(ChatColor.YELLOW+" ♦ "+ChatColor.GRAY+" Successfully joined "
									+ChatColor.AQUA+targ.name()+ChatColor.GRAY+".");
							return CResponse.NO_RESPONSE;
						} else {
							return CResponse.FAILED_OTHER_GROUP;
						}
					} else {
						return CResponse.FAILED_UNKNOWN_GROUP;
					}
				} else {
					return CResponse.FAILED_IN_GROUP;
				}
			} else {
				return CResponse.FAILED_PERMISSION;
			}
		} else {
			return CResponse.FAILED_ARGUMENT_COUNT;
		}
	}

}
=======
package me.capit.urbanization.command;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDJoin implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (args.length==1){
			if (s.hasPermission("urbanization.group.join") && s instanceof Player){
				Player p = (Player) s;
				if (Urbanization.getGroupByPlayer(p.getUniqueId())==null){
					Group targ = Urbanization.getGroupByName(args[1]);
					if (targ!=null){
						if (Urbanization.invites.containsKey(p.getUniqueId()) || targ.isOpen()){
							if (!targ.isOpen()) Urbanization.invites.remove(p.getUniqueId());
							targ.addPlayer(p.getUniqueId());
							p.sendMessage(ChatColor.YELLOW+" ♦ "+ChatColor.GRAY+" Successfully joined "
									+ChatColor.AQUA+targ.name()+ChatColor.GRAY+".");
							return CResponse.NO_RESPONSE;
						} else {
							return CResponse.FAILED_OTHER_GROUP;
						}
					} else {
						return CResponse.FAILED_UNKNOWN_GROUP;
					}
				} else {
					return CResponse.FAILED_IN_GROUP;
				}
			} else {
				return CResponse.FAILED_PERMISSION;
			}
		} else {
			return CResponse.FAILED_ARGUMENT_COUNT;
		}
	}

}
>>>>>>> branch 'master' of https://github.com/Wehttam664/Urbanization.git
