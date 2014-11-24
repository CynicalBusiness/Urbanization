package me.capit.urbanization;

import java.io.IOException;

import me.capit.urbanization.group.Group;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandController implements CommandExecutor{
	public enum CResponse{
		SUCCESS, NO_RESPONSE, PLUGIN_ERROR,
		
		FAILED_PERMISSION, FAILED_FORMAT, FAILED_ARGUMENTS, FAILED_ARGUMENT_COUNT,
		FAILED_GROUP_PERMISSION, FAILED_NOT_POSSIBLE, FAILED_EMPTY_PAGE,
		FAILED_FUNDS, FAILED_GROUP_FUNDS;
		
		public String getMessage(){
			switch (this){
			case SUCCESS:
				return "Operation &aSucceeded&f!";
			case FAILED_PERMISSION:
				return "&cError: &rThe &oserver &rdoes not permit you to do that!";
			case FAILED_FORMAT:
				return "&cError: &rThe specified &3format &rwas invalid.";
			case FAILED_ARGUMENTS:
				return "&cError: &rInvalid or unknown &eargument &rspecified.";
			case FAILED_ARGUMENT_COUNT:
				return "&cError: &rIncorrect &eargument &rcount.";
			case FAILED_GROUP_PERMISSION:
				return "&cError: &rYour &dgroup &rdoes not permit you to do that!";
			case FAILED_NOT_POSSIBLE:
				return "&cError: &rYou can't do &othat&r!";
			case FAILED_EMPTY_PAGE:
				return "&cError: &rThat &opage &ris empty!";
			case FAILED_FUNDS:
				return "&cError: &rYou do not have enough &o"+Urbanization.ECONOMY.currencyNamePlural()+"&r!";
			case FAILED_GROUP_FUNDS:
				return "&cError: &rYour group does not have enough &o"+Urbanization.ECONOMY.currencyNamePlural()+"&r!";
			default:
				return "";
			}
		}
	}
	public class CommandInfo{
		private String cmd,desc;
		public CommandInfo(String cmd, String desc){
			this.cmd=cmd;this.desc=desc;
		}
		public String toString(){
			return "&e/u "+cmd+"&7 - "+desc;
		}
	}
	
	Urbanization plugin;
	public CommandController(Urbanization plugin){
		this.plugin=plugin;
	}
	
	CommandInfo[][] help = new CommandInfo[][]{
			new CommandInfo[]{
					new CommandInfo("create <name>", "Creates a new group by <name>."),
			},
			new CommandInfo[]{
					new CommandInfo("permission <group> add|take <permission>", "Adds or takes <permission> from <group>."),
					new CommandInfo("&cdisband", "Disbands your group."),
			},
	};
	public void echoHelp(CommandSender s){echoHelp(s,1);}
	public void echoHelp(CommandSender s, int page){
		if (page<=help.length){
			CommandInfo[] pg = help[page-1];
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', 
					"&e------ &7Urbanization &e------------------------"));
			for (CommandInfo ci : pg){
				s.sendMessage(ChatColor.translateAlternateColorCodes('&', ci.toString()));
			}
		} else {
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', CResponse.FAILED_EMPTY_PAGE.getMessage()));
		}
	}
	
	public CResponse executeCMD(CommandSender s, Command c, String l, String[] args){
		if (l.equalsIgnoreCase("urbanization")){
			if (args.length>0){
				String sc = args[0];
				if (sc.equalsIgnoreCase("create") && args.length==2){
					if (s.hasPermission("urbanizations.kit.player") || s.hasPermission("urbanization.group.create")){
						if (args[1].matches(Urbanization.CONTROLLER.getGroupData().getString("name_pattern"))){
							if (!Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy") 
									|| Urbanization.ECONOMY.has((OfflinePlayer) s, Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_create"))){
								String name = args[1];
								if (name.matches(Urbanization.CONTROLLER.getGroupData().getString("name_pattern"))){
									try {
										Urbanization.ECONOMY.withdrawPlayer((OfflinePlayer) s, 
												Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_create"));
										Group g = Group.createNewGroup(name, ((Player) s).getUniqueId());
										Urbanization.groups.add(g);
										return CResponse.SUCCESS;
									} catch (IOException e) {
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
							return CResponse.FAILED_FORMAT;
						}
					} else {
						return CResponse.FAILED_PERMISSION;
					}
				} else if (sc.equalsIgnoreCase("disband")){
					if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.disband")){
						
					} else {
						return CResponse.FAILED_PERMISSION;
					}
				} else {
					return CResponse.FAILED_ARGUMENTS;
				}
			} else {
				return executeCMD(s, c, l, new String[]{"help"});
			}
		}
		return CResponse.NO_RESPONSE;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', executeCMD(s,c,l,args).getMessage()));
		return true;
	}

}
