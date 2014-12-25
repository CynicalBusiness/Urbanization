package me.capit.urbanization.command;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDInvite implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (args.length==1){
			if (s.hasPermission("urbanization.group.invite") && s instanceof Player){
				Player p = (Player) s;
				Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
				if (g!=null){
					if (g.playerHasPermission(p.getUniqueId(), "group.invite")){
						@SuppressWarnings("deprecation")
						OfflinePlayer targ = Bukkit.getOfflinePlayer(args[0]);
						if (targ!=null){
							if (!Urbanization.invites.containsKey(p.getUniqueId()) || 
									!Urbanization.invites.get(p.getUniqueId()).equals(g.ID)){
								Urbanization.invites.put(targ.getUniqueId(), g.ID);
								p.sendMessage(ChatColor.YELLOW+" ♦ "+ChatColor.GRAY+" Invite sent to "
										+ChatColor.AQUA+targ.getName()+ChatColor.GRAY+".");
							} else {
								Urbanization.invites.remove(targ.getUniqueId());
								p.sendMessage(ChatColor.YELLOW+" ♦ "+ChatColor.GRAY+" Invite revoked from "
										+ChatColor.AQUA+targ.getName()+ChatColor.GRAY+".");
							}
							return CResponse.NO_RESPONSE;
						} else {
							return CResponse.FAILED_PLAYER_NOT_FOUND;
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
		} else {
			return CResponse.FAILED_ARGUMENT_COUNT;
		}
	}

}
