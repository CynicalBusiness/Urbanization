package me.capit.urbanization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.capit.urbanization.group.Group;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Urbanization extends JavaPlugin {
	public static ConsoleCommandSender CONSOLE;
	public static Permission PERMISSION;
	public static Economy ECONOMY; public static Chat CHAT;
	public static DataController CONTROLLER;
	public static CommandController COMMANDS;
	public static List<Group> groups = new ArrayList<Group>();
	
	
	public void onEnable(){
		CONSOLE = getServer().getConsoleSender();
		CONTROLLER = new DataController(this);
		
		CONSOLE.sendMessage(ChatColor.WHITE+"---- "+ChatColor.AQUA+"Urbanization"+ChatColor.WHITE+" -------------------");
		CONTROLLER.createEconomyProvider();
		CONSOLE.sendMessage(ChatColor.WHITE+"Loading groups...");
		for (File gfile : getDataFolder().listFiles()){
			Group g = new Group(gfile.getName().replaceFirst("[.][^.]+$", ""));
			CONSOLE.sendMessage(ChatColor.WHITE+"    INIT "+ChatColor.LIGHT_PURPLE+g.name());
			groups.add(g);
		}
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Hooking data...");
		COMMANDS = new CommandController(this);
		getCommand("urbanization").setExecutor(COMMANDS);
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Finished!");
		CONSOLE.sendMessage(ChatColor.WHITE+"-------------------------------------");
	}
	
	public void onDisable(){
		// TODO
		
		for (Group g : groups){
			try {
				g.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
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
	
	public static Group getGroupByID(String ID){
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
	public static boolean territoryClaimed(int x, int z){
		return getGroupByTerritory(x,z)!=null;
	}
	
}
