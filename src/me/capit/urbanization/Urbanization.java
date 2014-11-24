package me.capit.urbanization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	
}
