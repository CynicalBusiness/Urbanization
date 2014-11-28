package me.capit.urbanization.group;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

public class Subgroup {
	private String name; public final int ID; private char prefix;
	private List<GroupPermission> perms = new ArrayList<GroupPermission>();
	private List<UUID> players = new ArrayList<UUID>();
	
	public static Subgroup buildFromConfig(ConfigurationSection c){
		Subgroup s = new Subgroup(c.getString("NAME"),Integer.parseInt(c.getName().substring(2)));
		char pr = (char) c.getInt("PREFIX");
		List<String> u = c.getStringList("PLAYERS");
		List<String> p = c.getStringList("PERMISSIONS");
		for (String su : u){s.addPlayer(UUID.fromString(su));}
		for (String sp : p){s.addPermission(new GroupPermission(sp));}
		s.setPrefix(pr);
		return s;
	}
	
	public Subgroup(String name, int ID){
		this.name = name; this.ID = ID;
	}
	
	public Subgroup setPrefix(char prefix){
		this.prefix = prefix; return this;
	}
	
	public char getPrefix(){
		return prefix;
	}
	
	public Subgroup addPermission(GroupPermission perm){
		perms.add(perm); return this;
	}
	
	public Subgroup removePermission(GroupPermission perm){
		perms.remove(perm); return this;
	}
	
	public Subgroup addPlayer(UUID player){
		players.add(player); return this;
	}
	
	public boolean hasPermission(GroupPermission perm){
		for (GroupPermission p : perms){
			if (p.equals(perm)) return true;
		}
		return false;
	}
	
	public boolean hasPlayer(UUID player){
		return players.contains(player);
	}
	
	public Subgroup removePlayer(UUID id){
		players.remove(id); return this;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
	
	public List<UUID> getPlayers(){
		return players;
	}
	
	public void addToConfigEntry(ConfigurationSection c){
		ConfigurationSection cs = c.createSection("SG"+String.valueOf(ID));
		List<String> p = new ArrayList<String>();
		List<String> u = new ArrayList<String>();
		for (GroupPermission gp : perms){p.add(gp.toString());}
		for (UUID id : players){u.add(id.toString());}
		cs.set("PERMISSIONS", p);
		cs.set("PLAYERS", u);
		cs.set("NAME", name);
		cs.set("PREFIX", (int) prefix);
	}
}
