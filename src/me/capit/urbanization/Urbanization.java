package me.capit.urbanization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import me.capit.urbanization.command.*;
import me.capit.urbanization.command.UrbanizationCommandParser.UrbanizationCommands;
import me.capit.urbanization.group.Group;
import me.capit.urbanization.group.Subgroup;
import me.capit.urbanization.group.Territory;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class Urbanization extends JavaPlugin {
	public static ConsoleCommandSender CONSOLE;
	public static Logger LOGGER;
	public static Permission PERMISSION; public static boolean AEIGS_ENABLED = false;
	public static Economy ECONOMY; public static Chat CHAT;
	public static DataController CONTROLLER;
	public static CommandController COMMANDS;
	public static List<Group> groups = new ArrayList<Group>();
	public static HashMap<UUID, String> trackedPlayers = new HashMap<UUID, String>();
	public static HashMap<UUID, UUID> invites = new HashMap<UUID, UUID>();
	
	static {
		ConfigurationSerialization.registerClass(Group.class);
		ConfigurationSerialization.registerClass(Territory.class);
		ConfigurationSerialization.registerClass(Subgroup.class);
		ConfigurationSerialization.registerClass(SerialLocation.class);
		
		UrbanizationCommands.registerCommandParser("claim", CMDClaim.class);
		UrbanizationCommands.registerCommandParser("create", CMDCreate.class);
		UrbanizationCommands.registerCommandParser("disband", CMDDisband.class);
		UrbanizationCommands.registerCommandParser("home", CMDHome.class);
		UrbanizationCommands.registerCommandParser("info", CMDInfo.class);
		UrbanizationCommands.registerCommandParser("invite", CMDInvite.class);
		UrbanizationCommands.registerCommandParser("join", CMDJoin.class);
		UrbanizationCommands.registerCommandParser("leave", CMDLeave.class);
		UrbanizationCommands.registerCommandParser("modify", CMDModify.class);
		UrbanizationCommands.registerCommandParser("sethome", CMDSethome.class);
		UrbanizationCommands.registerCommandParser("unclaim", CMDUnclaim.class);
	}
	
	public void onEnable(){
		CONSOLE = getServer().getConsoleSender();
		LOGGER = getLogger();
		CONTROLLER = new DataController(this);
		
		CONSOLE.sendMessage(ChatColor.WHITE+"---- "+ChatColor.AQUA+"Urbanization"+ChatColor.WHITE+" -------------------");
		CONTROLLER.createEconomyProvider();
		
		//LOGGER.setLevel(CONTROLLER.getGlobals().getBoolean("enable_debug") ? Level.FINE : Level.INFO);
		if (CONTROLLER.getGlobals().getBoolean("enable_debug")) 
			CONSOLE.sendMessage(ChatColor.YELLOW+"Debug"+ChatColor.WHITE+" mode is "+ChatColor.BLUE+"enabled"+ChatColor.WHITE+".");
		saveDefaultConfig();
		
		if (ECONOMY==null){
			CONTROLLER.getGlobals().set("enable_economy", false);
			CONSOLE.sendMessage(ChatColor.YELLOW+"Economy"+ChatColor.WHITE+" is "+ChatColor.RED+"disabled"+ChatColor.WHITE+", no economy plugin found.");
		}
		
		File gFolder = new File(getDataFolder().getPath()+File.separator+"groups");
		if (!gFolder.exists()) gFolder.mkdir();
		CONSOLE.sendMessage(ChatColor.WHITE+"Loading groups...");
		for (File gfile : gFolder.listFiles()){
			FileConfiguration c = YamlConfiguration.loadConfiguration(gfile);
			Group g = (Group) c.get("DATA");
			CONSOLE.sendMessage(ChatColor.WHITE+"    INIT called for "+ChatColor.LIGHT_PURPLE+g.name());
			groups.add(g);
		}
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Hooking data...");
		COMMANDS = new CommandController(this);
		getCommand("urbanization").setExecutor(COMMANDS);
		getServer().getPluginManager().registerEvents(COMMANDS, this);
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Finished!");
		CONSOLE.sendMessage(ChatColor.WHITE+"-------------------------------------");
	}
	
	public void onDisable(){
		CONSOLE.sendMessage(ChatColor.WHITE+"---- "+ChatColor.AQUA+"Urbanization"+ChatColor.WHITE+" -------------------");
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Saving groups...");
		for (Group g : groups){
			try {
				g.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Finished!");
		CONSOLE.sendMessage(ChatColor.WHITE+"-------------------------------------");
	}
	
	public boolean playerInGroup(UUID player){
		return getGroupByPlayer(player)!=null;
	}
	
	public static Group getGroupByPlayer(UUID player){
		for (Group g : groups){
			if (g.hasPlayer(player)) return g;
		}
		return null;
	}
	
	public static Group getGroupByID(UUID ID){
		for (Group g : groups){
			if (g.ID.equals(ID)) return g;
		}
		return null;
	}
	
	public static Group getGroupByTerritory(int x, int z){
		for (Group g : groups){
			if (g.territoryBelongsToGroup(x, z)) return g;
		}
		return null;
	}
	
	public static Group getGroupByName(String name){
		for (Group g : groups){
			if (g.name().equalsIgnoreCase(name)) return g;
		}
		return null;
	}
	
	public static boolean groupNameInUse(String name){
		return getGroupByName(name)!=null;
	}
	
	public static boolean territoryClaimed(int x, int z){
		return getGroupByTerritory(x,z)!=null;
	}
	
	public static void deleteGroupByID(UUID ID){
		for (int i=0; i<groups.size(); i++){
			Group g = groups.get(i);
			if (g.ID.equals(ID)){
				groups.remove(i);
				g.delete();
			}
		}
	}
	
	public static Territory getTerritory(int x, int z){
		Group g = getGroupByTerritory(x,z);
		return g!=null ? g.getTerritoryAt(x, z) : null;
	}
	
}
