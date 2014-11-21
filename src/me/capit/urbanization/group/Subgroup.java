package me.capit.urbanization.group;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class Subgroup {
	private String name;
	private List<GroupPermission> perms;
	
	public Subgroup(String name){
		this.name = name;
	}
	
	public Subgroup addPermission(GroupPermission perm){
		perms.add(perm); return this;
	}
	
	public boolean hasPermission(GroupPermission perm){
		for (GroupPermission p : perms){
			if (p.matches(perm)) return true;
		}
		return false;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
	
	public void addToConfigEntry(ConfigurationSection c, int ID){
		ConfigurationSection cs = c.createSection(String.valueOf(ID));
		List<String> p = new ArrayList<String>();
		for (GroupPermission gp : perms){p.add(gp.toString());}
		cs.set("PERMISSIONS", p);
		cs.set("NAME", name);
	}
}
