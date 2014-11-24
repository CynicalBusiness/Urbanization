package me.capit.urbanization.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import me.capit.urbanization.Urbanization;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Group {
	public static final int subgroupSize = 255;
	public final String ID;
	private final YamlConfiguration instance;
	
	private String name,desc,tag,motd;
	private List<GroupPlayer> players = new ArrayList<GroupPlayer>();
	private Subgroup[] subgroups = new Subgroup[subgroupSize+1];
	private double funds = 0;
	
	public static Group createNewGroup(String name, UUID owner) throws IOException{
		String tid = String.valueOf(System.currentTimeMillis());
		YamlConfiguration file = Urbanization.CONTROLLER.readInstance(tid);
		file.set("NAME", name); file.set("TAG", name.substring(0, 3).toUpperCase());
		file.set("DESC", "Default Group Description"); file.set("MOTD", "");
		file.set("FUNDS", 0);
		
		ConfigurationSection groups = file.createSection("GROUPS");
		new Subgroup("Admin").addPermission(new GroupPermission("group.*")).addToConfigEntry(groups, 0);
		new Subgroup("Default").addPermission(new GroupPermission("group.build.*")).addToConfigEntry(groups, subgroupSize-1);
		new Subgroup("Guest").addPermission(new GroupPermission("")).addToConfigEntry(groups, subgroupSize);
		
		file.set("PLAYERS", Arrays.asList(new GroupPlayer(owner, 0)));
		
		
		file.save(Urbanization.CONTROLLER.getInstanceFile(tid));
		Group g = new Group(tid);
		return g;
	}
	
	public Group(String instanceID){
		ID=instanceID;
		instance = Urbanization.CONTROLLER.readInstance(ID);
		name=instance.getString("NAME"); desc=instance.getString("DESC");
		tag=instance.getString("TAG"); motd=instance.getString("MOTD");
		funds=instance.getDouble("FUNDS");
		
		for (String key : instance.getConfigurationSection("PLAYERS").getKeys(false)){
			players.add(new GroupPlayer(UUID.fromString(key), instance.getInt("PLAYERS."+key)));
		}
	}
	
	public void save() throws IOException{
		instance.save(Urbanization.CONTROLLER.getInstanceFile(ID));
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
	public boolean hasFunds(double f){return funds>=f;}
	public void deposit(double f){funds+=f;}
	
	public boolean groupExistsAtRank(int rank){
		return getGroupAt(rank)!=null;
	}
	
	public Subgroup getGroupAt(int rank){
		return rank<subgroups.length ? subgroups[rank] : null;
	}
}
