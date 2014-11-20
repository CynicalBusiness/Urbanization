package me.capit.urbanization.group;

import java.util.List;

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
	
}
