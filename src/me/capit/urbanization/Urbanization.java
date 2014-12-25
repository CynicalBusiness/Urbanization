package me.capit.urbanization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import me.capit.urbanization.aegis.AegisDisabledException;
import me.capit.urbanization.aegis.AegisEnabledException;
import me.capit.urbanization.aegis.CityAegis;
import me.capit.urbanization.city.City;
import me.capit.urbanization.city.CityClaim;
import me.capit.urbanization.group.Group;
import me.capit.urbanization.group.Territory;
import me.capit.xmlapi.XMLPlugin;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;

public class Urbanization extends JavaPlugin {
	public static final String citiesFileName = "cities.xml";
	
	public static ConsoleCommandSender CONSOLE;
	public static Logger LOGGER; public static Economy ECONOMY;
	public static boolean AEGIS_ENABLED = false;
	public static boolean ECONOMY_ENABLED = false;
	public static DataController CONTROLLER;
	public static UserIOController USER_IO;
	public static FileConfiguration CONFIG;
	public static Document CITIES_XML;
	
	public static final List<City> cities = new ArrayList<City>();
	public static final List<Group> groups = new ArrayList<Group>();
	public static final HashMap<UUID, String> trackedPlayers = new HashMap<UUID, String>();
	public static final HashMap<UUID, UUID> invites = new HashMap<UUID, UUID>();
	
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
		CONFIG = getConfig();
		if (!new File(getDataFolder(), citiesFileName).exists()) saveResource(citiesFileName, false);
		
		if (ECONOMY==null){
			CONTROLLER.getGlobals().set("enable_economy", false);
			CONSOLE.sendMessage(ChatColor.YELLOW+"Economy"+ChatColor.WHITE+" is "+ChatColor.RED+"disabled"+ChatColor.WHITE+", no economy plugin found.");
		}
		ECONOMY_ENABLED = CONTROLLER.getGlobals().getBoolean("enable_economy");
		
		AEGIS_ENABLED = CONTROLLER.getGlobals().getBoolean("use_aegis_system");
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Loading groups...");
		try {
			CITIES_XML = XMLPlugin.read(new File(getDataFolder(), "cities.xml"));
			for (Element city : CITIES_XML.getRootElement().getChildren()){
				try {
					City c = new City(city);
					cities.add(c);
					CONSOLE.sendMessage(ChatColor.YELLOW+"  "+c.getName()+ChatColor.WHITE+" loaded.");
				} catch (NullPointerException | IllegalArgumentException e){
					CONSOLE.sendMessage(ChatColor.RED+"  Error "+ChatColor.WHITE+" loading city for "+city.getAttributeValue("name"));
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		
		saveCities(getDataFolder());
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Hooking data...");
		USER_IO = new UserIOController(this);
		getCommand("urbanization").setExecutor(USER_IO);
		getServer().getPluginManager().registerEvents(USER_IO, this);
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Finished!");
		CONSOLE.sendMessage(ChatColor.WHITE+"-------------------------------------");
	}
	
	public void onDisable(){
		CONSOLE.sendMessage(ChatColor.WHITE+"---- "+ChatColor.AQUA+"Urbanization"+ChatColor.WHITE+" -------------------");
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Saving cities...");
		saveCities(getDataFolder());
		
		CONSOLE.sendMessage(ChatColor.WHITE+"Finished!");
		CONSOLE.sendMessage(ChatColor.WHITE+"-------------------------------------");
	}
	
	public static void saveCities(File folder){
		try {
			Element root = CITIES_XML.getRootElement();
			root.removeContent();
			for (City city : cities){
				root.addContent(city.getElement());
			}
			Format format = Format.getPrettyFormat();
			format.setTextMode(Format.TextMode.TRIM);
			format.setIndent("    ");
			format.setLineSeparator(LineSeparator.CRNL);
			XMLOutputter output = new XMLOutputter(format);
			output.output(CITIES_XML, new FileOutputStream(new File(folder, "cities.xml")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean validCityName(String name){
		return name!=null && name.matches(CONFIG.getString("groups.name_pattern")) && getCity(name)==null;
	}
	public static boolean validCityTag(String tag){
		return tag==null || tag.matches(CONFIG.getString("groups.tag_pattern"));
	}
	
	public static City getCity(UUID id){
		for (City c : cities) if (c.ID.equals(id)) return c; return null;
	}
	public static City getCityOf(UUID id){
		for (City c : cities) if (c.hasPlayer(id)) return c; return null;
	}
	public static City getCity(String name){
		for (City c : cities) if (c.getName().equalsIgnoreCase(name)) return c; return null;
	}
	public static City getCity(int x, int z, String world) throws AegisEnabledException {
		for (City c : cities) if (c.hasClaim(x, z, world)) return c; return null;
	}
	public static City getCity(CityClaim claim) throws AegisEnabledException {
		return getCity(claim.x, claim.z, claim.world);
	}
	public static City getCity(CityAegis aegis) throws AegisDisabledException {
		for (City c : cities) if (c.hasAegis(aegis)) return c; return null;
	}
	public static City getCity(Location loc) {
		for (City c : cities) if (c.hasLocation(loc)) return c; return null;
	}
	public static City getCity(Block block) {
		for (City c : cities) if (c.hasBlock(block)) return c; return null;
	}
	
	public static void disband(City city){
		for (int i=0; i<cities.size(); i++){
			if (cities.get(i).equals(city)){ cities.remove(i); return; }
		}
	}
	
	// OLD STUFF! TODO REMOVE THIS ALL LATER!
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
