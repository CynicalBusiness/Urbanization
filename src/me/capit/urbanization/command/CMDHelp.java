package me.capit.urbanization.command;

import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.group.Group;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CMDHelp implements UrbanizationCommandParser {
	public class CommandInfo{
		private String cmd,desc;
		public CommandInfo(String cmd, String desc){
			this.cmd=cmd;this.desc=desc;
		}
		public String toString(){
			return "&e/u "+cmd+"&7 - "+desc;
		}
	}
	
	CommandInfo[][] help = new CommandInfo[][]{
			new CommandInfo[]{
					new CommandInfo("help [page=1]", "Displays [page] of the Urbanization help."),
					new CommandInfo("create <name>", "Creates a new group by <name>."),
					new CommandInfo("claim [rank="+Group.subgroupSize+"]", "Claims the chunk you're standing in, optionally for [rank]."),
					new CommandInfo("info [group=YOURS]", "Gets the info on the specified group."),
					new CommandInfo("home [group=YOURS]", "Teleports to the specified group, given that you have permission to."),
					new CommandInfo("join <group>", "Joins the group given that you were invited or it was open."),
			},
			new CommandInfo[]{
					new CommandInfo("subgroup <group> addperm|takeperm <permission>", "Adds or takes <permission> from <group>."),
					new CommandInfo("sethome", "Sets your group's home."),
					new CommandInfo("invite <player>", "Invites the specified player."),
					new CommandInfo("leave", "Leaves your current group. The group owner cannot leave, try 'disband' instead."),
					new CommandInfo("modify <key> [value=CLEAR]", "Modifies or clears a value."),
					new CommandInfo("&cdisband", "Disbands your group."),
			},
	};
	public void echoHelp(CommandSender s){echoHelp(s,1);}
	public void echoHelp(CommandSender s, int page){
		if (page<=help.length){
			CommandInfo[] pg = help[page-1];
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', 
					"&e------ &7Urbanization &f- Page &3"+page+" &fof &3"+help.length+" &e--------------------"));
			for (CommandInfo ci : pg){
				s.sendMessage(ChatColor.translateAlternateColorCodes('&', ci.toString()));
			}
		} else {
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', CResponse.FAILED_EMPTY_PAGE.getMessage()));
		}
	}
	
	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		if (s.hasPermission("urbanization.help")){
			echoHelp(s);
			return CResponse.NO_RESPONSE;
		} else {
			return CResponse.FAILED_PERMISSION;
		}
	}

}
