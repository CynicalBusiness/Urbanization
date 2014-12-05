package me.capit.urbanization.command;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDModify implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (args.length>=2){
			if (s.hasPermission("urbanization.group.modify") && s instanceof Player){
				Player p = (Player) s;
				String ssc = args[1];
				Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
				if (g!=null){
					if (ssc.equalsIgnoreCase("name")){
						if (!Urbanization.groupNameInUse(args[2])){
							if (args[2].matches(Urbanization.CONTROLLER.getGroupData().getString("name_pattern"))){
								if (g.playerHasPermission(p.getUniqueId(), "group.modify.name")){
									if (g.hasFunds(Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_name"))){
										g.name(args[2]);
										if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
											g.funds(g.funds()-Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_name"));
										return CResponse.SUCCESS;
									} else {
										return CResponse.FAILED_GROUP_FUNDS;
									}
								} else {
									return CResponse.FAILED_GROUP_PERMISSION;
								}
							} else {
								return CResponse.FAILED_FORMAT;
							} 
						} else {
							return CResponse.FAILED_IN_USE;
						}
					} else if (ssc.equalsIgnoreCase("tag")){
						if (args[2].matches(Urbanization.CONTROLLER.getGroupData().getString("tag_pattern"))){
							if (g.playerHasPermission(p.getUniqueId(), "group.modify.tag")){
								if (g.hasFunds(Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_tag"))){
									g.tag(args[2]);
									if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
										g.funds(g.funds()-Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_tag"));
									return CResponse.SUCCESS;
								} else {
									return CResponse.FAILED_GROUP_FUNDS;
								}
							} else {
								return CResponse.FAILED_GROUP_PERMISSION;
							}
						} else {
							return CResponse.FAILED_FORMAT;
						}
					} else if (ssc.equalsIgnoreCase("desc")){
						if (g.playerHasPermission(p.getUniqueId(), "group.modify.desc")){
							if (g.hasFunds(Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_desc"))){
								String desc = "";
								for (int i=2; i<args.length; i++){desc+=" "+args[i];}
								g.desc(desc.substring(1));
								if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
									g.funds(g.funds()-Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_desc"));
								return CResponse.SUCCESS;
							} else {
								return CResponse.FAILED_GROUP_FUNDS;
							}
						} else {
							return CResponse.FAILED_GROUP_PERMISSION;
						}
					} else if (ssc.equalsIgnoreCase("motd")){
						if (g.playerHasPermission(p.getUniqueId(), "group.modify.motd")){
							if (g.hasFunds(Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_motd"))){
								String motd = "";
								for (int i=2; i<args.length; i++){motd+=" "+args[i];}
								g.motd(motd.substring(1));
								if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
									g.funds(g.funds()-Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_motd"));
								return CResponse.SUCCESS;
							} else {
								return CResponse.FAILED_GROUP_FUNDS;
							}
						} else {
							return CResponse.FAILED_GROUP_PERMISSION;
						}
					} else {
						return CResponse.FAILED_ARGUMENTS;
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
