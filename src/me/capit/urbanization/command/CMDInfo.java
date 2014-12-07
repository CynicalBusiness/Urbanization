package me.capit.urbanization.command;

import java.util.Map;
import java.util.UUID;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;
import me.capit.urbanization.group.GroupRelation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDInfo implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (s.hasPermission("urbanization.group.info")){
			Group g = args.length==0 && s instanceof Player
					? Urbanization.getGroupByPlayer(((Player) s).getUniqueId())
					: Urbanization.getGroupByName(args[0]);
			if (g!=null){
				String playersOnline = ""; String playersTotal = ""; String relations = "";
				for (UUID id : g.getPlayers()){
					OfflinePlayer op = Bukkit.getOfflinePlayer(id);
					playersTotal+=", "+g.getPlayerPrefix(id)+op.getName();
					if (op.isOnline()) playersOnline+=", "+g.getPlayerPrefix(id)+op.getName();
				}
				Map<String, GroupRelation> rels = g.getRelatedGroups();
				for (String id : rels.keySet()){
					relations+=ChatColor.WHITE+", "+Urbanization.getGroupByID(UUID.fromString(id)).name()+":"
							+rels.get(id).getColor()+rels.get(id).toString();
				}
				s.sendMessage(ChatColor.translateAlternateColorCodes('&', 
						"&e------ &7Group &3"+g.name()+" &f(&7"+g.tag()+"&f) &e--------------------------"));
				s.sendMessage(new String[]{
						ChatColor.GOLD+"DESC: "+ChatColor.WHITE+g.desc(),
						ChatColor.GOLD+"MOTD: "+ChatColor.WHITE+g.motd(),
						ChatColor.GOLD+"ONLINE: "+ChatColor.WHITE+playersOnline.substring(1),
						ChatColor.GOLD+"PLAYERS: "+ChatColor.WHITE+"("+g.getPlayers().size()+") "+playersTotal.substring(1),
						ChatColor.GOLD+"CLAIMS: "+ChatColor.WHITE+g.getTerritory().size(),
						ChatColor.GOLD+"RELATIONS: "+ChatColor.WHITE+relations,
						ChatColor.GOLD+"CAN TELE: "+(s instanceof Player
								? (g.playerCanTele(((Player) s).getUniqueId()) 
										? ChatColor.GREEN+"Yes" 
										: ChatColor.RED+"No")
								: ChatColor.RED+"No")
				});
				return CResponse.NO_RESPONSE;
			} else {
				return CResponse.FAILED_UNKNOWN_GROUP;
			}
		} else {
			return CResponse.FAILED_PERMISSION;
		}
	}

}
