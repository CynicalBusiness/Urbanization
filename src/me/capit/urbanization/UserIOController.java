package me.capit.urbanization;

import me.capit.urbanization.CommandController.CResponse;
import me.capit.urbanization.city.City;
import me.capit.urbanization.city.CityException;
import me.capit.urbanization.city.CityRank.RankPermission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class UserIOController implements CommandExecutor, Listener {
	public static final String
		msgNoPermission = "This command requires elevated permission or player execution.",
		msgNotEnoughMoney = "Insufficient funds.",
		msgUnknownArg = "Unknown argument(s)",
		msgNoCityPermission = "Your city does not allow you to do that!";
	
	public class CommandInfo{
		private String cmd,desc;
		public CommandInfo(String cmd, String desc){
			this.cmd=cmd;this.desc=desc;
		}
		public String toString(){
			return "&e/u "+cmd+"&7 - "+desc;
		}
	}
	
	public class CommandException extends Exception {
		private static final long serialVersionUID = -7553874292280384710L;
		
		public CommandException(){ super(); }
		public CommandException(String msg){ super(msg); }
	}
	
	Urbanization plugin;
	public UserIOController(Urbanization plugin){
		this.plugin = plugin;
	}
	
	CommandInfo[][] help = new CommandInfo[][]{
			new CommandInfo[]{
					new CommandInfo("help [page=1]", "Displays [page] of the Urbanization help."),
					new CommandInfo("create <name>", "Creates a new group by <name>."),
					new CommandInfo("claim [rank="+City.maxRanks+"]", "Claims the chunk you're standing in for [rank] and above."),
			},
			new CommandInfo[]{
					new CommandInfo("subgroup <group> addperm|takeperm <permission>", "Adds or takes <permission> from <group>."),
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
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("urbanization")){
			try {
				s.sendMessage(ChatColor.translateAlternateColorCodes('&', " "+execute(s, cmd, args)));
			} catch (CommandException | CityException e) {
				s.sendMessage(ChatColor.translateAlternateColorCodes('&', " &c&lError: &r"+e.getMessage()));
			}
			return true;
		}
		return false;
	}
	
	private String execute(CommandSender s, Command cmd, String[] args) throws CommandException, CityException {
		if (args.length>0){
			if (args[0].equalsIgnoreCase("create") && args.length==2)
				if (s instanceof Player && s.hasPermission("urbanization.group.create")) return executeCreate((Player) s, args[1]);
				else throw new CommandException(msgNoPermission);
			else if (args[0].equalsIgnoreCase("disband") && args.length==1)
				if (s instanceof Player && s.hasPermission("urbanization.group.disband")) return executeDisband((Player) s);
				else throw new CommandException(msgNoPermission);
			else
				throw new CommandException(msgUnknownArg);
		} else {
			echoHelp(s);
			return "";
		}
	}
	
	private String executeCreate(Player sender, String name) throws CommandException {
		if (Urbanization.ECONOMY_ENABLED && !Urbanization.ECONOMY.has(sender, Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_create")))
			throw new CommandException(msgNotEnoughMoney);
		if (!Urbanization.validCityName(name))
			throw new CommandException("'"+name+"' is not valid for format &o"+Urbanization.CONTROLLER.getGroupData().getString("name_pattern")+"&r.");
		if (Urbanization.getCity(name)!=null) throw new CommandException("A city by the name "+name+" already exists!");
		City city = new City(name, sender.getUniqueId());
		if (Urbanization.ECONOMY_ENABLED) 
			Urbanization.ECONOMY.withdrawPlayer(sender, Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_create"));
		Urbanization.cities.add(city);
		Urbanization.saveCities(plugin.getDataFolder());
		return "Successfully founded &e"+city.getName()+"&r.";
	}
	
	private String executeDisband(Player sender) throws CommandException, CityException {
		City city = Urbanization.getCityOf(sender.getUniqueId());
		if (city==null) throw new CommandException("You're not in a city!");
		if (!city.playerCan(sender.getUniqueId(), RankPermission.DISBAND)) throw new CommandException(msgNoCityPermission);
		if (Urbanization.ECONOMY_ENABLED) Urbanization.ECONOMY.depositPlayer(city.getOwner(), city.getFunds());
		Urbanization.disband(city);
		Urbanization.saveCities(plugin.getDataFolder());
		return "Successfully &cdisbanded &e"+city.getName()+"&r.";
	}

}
