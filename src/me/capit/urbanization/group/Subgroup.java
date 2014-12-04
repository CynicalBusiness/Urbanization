package me.capit.urbanization.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Subgroup implements ConfigurationSerializable {
	private String name; public final int ID; private char prefix = ' ';
	private List<GroupPermission> perms = new ArrayList<GroupPermission>();
	private List<UUID> players = new ArrayList<UUID>();
	
	public Subgroup(Map<String, Object> map){
		name = (String) map.get("NAME");
		ID = (int) map.get("ID");
		prefix = ((String) map.get("PREFIX")).charAt(0);
		for (Object o : (List<?>) map.get("PLAYERS")) players.add(UUID.fromString((String) o));
		for (Object o : (List<?>) map.get("PERMISSIONS")) perms.add(new GroupPermission((String) o));
	}
	
	@Override
	public Map<String, Object> serialize(){
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("PREFIX", String.valueOf(prefix));
		map.put("ID", ID); map.put("NAME", name);
		List<String> u = new ArrayList<String>();
		List<String> p = new ArrayList<String>();
		for (GroupPermission perm : perms) p.add(perm.toString());
		for (UUID player : players) u.add(player.toString());
		map.put("PLAYERS", u); map.put("PERMISSIONS", p);
		return map;
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
