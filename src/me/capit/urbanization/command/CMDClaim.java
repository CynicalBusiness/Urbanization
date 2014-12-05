package me.capit.urbanization.command;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;
import me.capit.urbanization.group.Territory;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDClaim implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (s.hasPermission("urbanization.group.claim") && s instanceof Player){
			Player p = (Player) s;
			Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
			if (g!=null){
				if (g.playerHasPermission(p.getUniqueId(), "territory.claim")){
					if (g.hasFunds(Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_claim"))){
						Group atPos = Urbanization.getGroupByTerritory(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ());
						if (atPos==null){
							int rank = args.length>=1 ? Integer.parseInt(args[0]) : Group.subgroupSize;
							Territory t = new Territory(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ(),g.ID,
									rank, p.getLocation().getWorld().getName());
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
									"&e » Registered new territory at &c"+t.x+"&e,&c"+t.z+"&e for &3"+g.name()+"&f:&c"+rank+"&e."));
							g.addTerritory(t);
							if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
								g.funds(g.funds()-Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_claim"));
							return CResponse.SUCCESS;
						} else {
							return CResponse.FAILED_OTHER_GROUP;
						}
					} else {
						return CResponse.FAILED_FUNDS;
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
