package me.capit.urbanization.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.capit.urbanization.Urbanization;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Group {
	public static final int subgroupSize = 255;
	public final String ID;
	private final YamlConfiguration instance;
	
	private String name,desc,tag,motd;
	private Subgroup[] subgroups = new Subgroup[subgroupSize+1];
	private List<String> territory = new ArrayList<String>();
	private double funds = 0;
	
	public static Group createNewGroup(String name, UUID owner) throws IOException{
		String tid = String.valueOf(System.currentTimeMillis());
		YamlConfiguration file = Urbanization.CONTROLLER.readInstance(tid);
		file.set("NAME", name); file.set("TAG", name.substring(0, 3).toUpperCase());
		file.set("DESC", "Default Group Description"); file.set("MOTD", "");
		file.set("FUNDS", 0.0); file.set("TERRITORY", new ArrayList<String>());
		
		ConfigurationSection groups = file.createSection("GROUPS");
		new Subgroup("Admin", 0).addPermission(new GroupPermission("*")).addPlayer(owner).addToConfigEntry(groups);
		new Subgroup("Default", subgroupSize).addPermission(new GroupPermission("build.*")).addToConfigEntry(groups);
		
		file.save(Urbanization.CONTROLLER.getInstanceFile(tid));
		Group g = new Group(tid);
		return g;
	}
	
	public Group(String instanceID){
		ID=instanceID;
		instance = Urbanization.CONTROLLER.readInstance(ID);
		name=instance.getString("NAME"); desc=instance.getString("DESC");
		tag=instance.getString("TAG"); motd=instance.getString("MOTD");
		funds=instance.getDouble("FUNDS"); territory=instance.getStringList("TERRITORY");
		
		for (String key : instance.getConfigurationSection("GROUPS").getKeys(false)){
			ConfigurationSection sec = instance.getConfigurationSection("GROUPS."+key);
			Subgroup g = Subgroup.buildFromConfig(sec);
			subgroups[g.ID] = g;
		}
	}
	
	public void save() throws IOException{
		instance.set("NAME", name); instance.set("DESC", desc);
		instance.set("TAG", tag); instance.set("MOTD", motd);
		instance.set("FUNDS", funds); instance.set("TERRITORY", territory);
		
		ConfigurationSection groups = instance.createSection("GROUPS");
		for (Subgroup sg : subgroups){if (sg!=null) sg.addToConfigEntry(groups);}
		
		instance.set("TERRITORY", territory);
		
		instance.save(Urbanization.CONTROLLER.getInstanceFile(ID));
	}
	
	public void delete(){
		OfflinePlayer owner = Bukkit.getServer().getPlayer(subgroups[0].getPlayers().get(0));
		if (owner.isOnline()){
			Player p = (Player) owner;
			if (Urbanization.ECONOMY!=null)
				p.sendMessage(ChatColor.YELLOW+"The "+ChatColor.RED+funds+ChatColor.YELLOW+" "+
						Urbanization.ECONOMY.currencyNamePlural()+" from the group back has been added to your account.");
		}
		if (Urbanization.ECONOMY!=null) Urbanization.ECONOMY.depositPlayer(owner, funds);
		Urbanization.CONTROLLER.getInstanceFile(ID).delete();
	}
	
	public String name(){return name;}
	public void name(String name){this.name=name;}
	
	public String desc(){return desc;}
	public void desc(String desc){this.desc=desc;}
	
	public String motd(){return motd;}
	public void motd(String motd){this.motd=motd;}
	
	public String tag(){return tag;}
	public void tag(String tag){this.tag=tag;}
	
	public double funds(){return funds;}
	public void funds(double funds){this.funds=funds;}
	public boolean hasFunds(double f){return !Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy") || funds>=f;}
	public void deposit(double f){funds+=f;}
	
	public boolean groupExists(int rank){
		return getGroup(rank)!=null;
	}
	
	public Subgroup getGroup(int rank){
		return rank<subgroups.length ? subgroups[rank] : null;
	}
	
	public Subgroup getPlayerGroup(UUID player){
		for (Subgroup g : subgroups){
			if (g!=null && g.hasPlayer(player)) return g;
		}
		return null;
	}
	
	public boolean hasPlayer(UUID player){
		return getPlayerGroup(player)!=null;
	}
	
	public boolean playerHasPermission(UUID player, String perm){
		return playerHasPermission(player, new GroupPermission(perm));
	}
	public boolean playerHasPermission(UUID player, GroupPermission perm){
		return hasPlayer(player) ? getPlayerGroup(player).hasPermission(perm) : false;
	}
	
	public Territory getTerritoryAt(int x, int z){
		for (String s : territory){
			Territory t = Territory.fromString(s);
			if (t.x==x && t.z==z) return t;
		}
		return null;
	}
	
	public void addTerritory(Territory t){
		territory.add(t.toString());
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean territoryBelongsToGroup(int x, int z){
		return getTerritoryAt(x,z)!=null;
	}
}
