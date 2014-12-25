<<<<<<< HEAD
package me.capit.urbanization.command;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDLeave implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (s.hasPermission("urbanization.group.leave") && s instanceof Player){
			Player p = (Player) s;
			Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
			if (g!=null){
				if (!g.getGroup(0).getPlayers().get(0).equals(p.getUniqueId())){
					g.removePlayer(p.getUniqueId());
					return CResponse.SUCCESS;
				} else {
					return CResponse.FAILED_NOT_POSSIBLE;
				}
			} else {
				return CResponse.FAILED_NOT_IN_GROUP;
			}
		} else {
			return CResponse.FAILED_PERMISSION;
		}
	}

}
=======
package me.capit.urbanization.command;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDLeave implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (s.hasPermission("urbanization.group.leave") && s instanceof Player){
			Player p = (Player) s;
			Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
			if (g!=null){
				if (!g.getGroup(0).getPlayers().get(0).equals(p.getUniqueId())){
					g.removePlayer(p.getUniqueId());
					return CResponse.SUCCESS;
				} else {
					return CResponse.FAILED_NOT_POSSIBLE;
				}
			} else {
				return CResponse.FAILED_NOT_IN_GROUP;
			}
		} else {
			return CResponse.FAILED_PERMISSION;
		}
	}

}
>>>>>>> branch 'master' of https://github.com/Wehttam664/Urbanization.git
