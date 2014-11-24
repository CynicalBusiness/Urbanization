package me.capit.urbanization;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class DataController {
	Urbanization plugin;
	public DataController(Urbanization plugin){
		this.plugin=plugin;
	}
	
	public DataController createPermissionProvider(){
		RegisteredServiceProvider<Permission> permProvider = plugin.getServer()
				.getServicesManager().getRegistration(Permission.class);
		if (permProvider!=null){
			Urbanization.PERMISSION=permProvider.getProvider();
		}
		return this;
	}
	
	public DataController createChatProvider(){
		RegisteredServiceProvider<Chat> chatProvider = plugin.getServer()
				.getServicesManager().getRegistration(Chat.class);
		if (chatProvider!=null){
			Urbanization.CHAT=chatProvider.getProvider();
		}
		return this;
	}
	
	public DataController createEconomyProvider(){
		RegisteredServiceProvider<Economy> econProvider = plugin.getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (econProvider!=null){
			Urbanization.ECONOMY=econProvider.getProvider();
		}
		return this;
	}
	
	public ConfigurationSection getGlobals(){
		return plugin.getConfig().getConfigurationSection("globals");
	}
	
	public ConfigurationSection getGroupData(){
		return plugin.getConfig().getConfigurationSection("groups");
	}
	
	public File getInstanceFile(String instanceID){
		File f = new File(plugin.getDataFolder().getPath()+File.separator+instanceID+".yml");
		if (!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return f;
	}
	
	public YamlConfiguration readInstance(File instance){
		return YamlConfiguration.loadConfiguration(instance);
	}
	
	public YamlConfiguration readInstance(String instanceID){
		return readInstance(getInstanceFile(instanceID));
	}
	
	public void writeInstance(String instanceID, String key, Object value) throws IOException{
		YamlConfiguration c = readInstance(instanceID);
		c.set(key, value);
		c.save(getInstanceFile(instanceID));
	}
	
}
