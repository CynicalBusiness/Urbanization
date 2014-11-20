package me.capit.urbanization;

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
	
	
	
	public void onEnable(){
		CONSOLE = getServer().getConsoleSender();
		CONTROLLER = new DataController(this);
		
		CONSOLE.sendMessage(ChatColor.WHITE+"+--- "+ChatColor.AQUA+"Urbanization"+ChatColor.WHITE+" ---------------+");
		
		CONTROLLER.createEconomyProvider().createPermissionProvider();
	}
	
	
}
