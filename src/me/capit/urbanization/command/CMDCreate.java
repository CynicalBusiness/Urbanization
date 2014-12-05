package me.capit.urbanization.command;

import java.io.IOException;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDCreate implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (s.hasPermission("urbanization.group.create") && s instanceof Player){
			Player p = (Player) s;
			String name = args[0];
			if (!Urbanization.groupNameInUse(name)){
				if (name.matches(Urbanization.CONTROLLER.getGroupData().getString("name_pattern"))){
					if (Urbanization.getGroupByPlayer(p.getUniqueId())==null){
						if (!Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy") 
								|| Urbanization.ECONOMY.has((OfflinePlayer) s, Urbanization.CONTROLLER.getGroupData().
										getDouble("econ_cost_create"))){
							if (name.matches(Urbanization.CONTROLLER.getGroupData().getString("name_pattern"))){
								try {
									if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
										Urbanization.ECONOMY.withdrawPlayer((OfflinePlayer) s, 
												Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_create"));
									Group g = Group.createNewGroup(name, ((Player) s).getUniqueId());
									Urbanization.groups.add(g);
									return CResponse.SUCCESS;
								} catch (IOException | NullPointerException e) {
									e.printStackTrace();
									return CResponse.PLUGIN_ERROR;
								}
							} else {
								return CResponse.FAILED_FORMAT;
							}
						} else {
							return CResponse.FAILED_FUNDS;
						}
					} else {
						return CResponse.FAILED_NOT_POSSIBLE;
					}
				} else {
					return CResponse.FAILED_FORMAT;
				}
			} else {
				return CResponse.FAILED_IN_USE;
			}
		} else {
			return CResponse.FAILED_PERMISSION;
		}
	}

}
