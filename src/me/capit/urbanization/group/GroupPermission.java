package me.capit.urbanization.group;

public class GroupPermission {
	public final String[] KEY;
	
	public GroupPermission(String permissionKey){
		KEY = permissionKey.split("\\.");
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof GroupPermission ? equals((GroupPermission) o) : false;
	}
	
	public boolean equals(GroupPermission p){
		int i = 0;
		while (i<(Math.min(p.KEY.length, KEY.length))){
			if (!p.KEY[i].equalsIgnoreCase(KEY[i]) && !(p.KEY[i].equals("*") || KEY[i].equals("*"))) return false;
			i++;
		}
		return true;
	}
	
	@Override
	public String toString(){
		String out = "";
		for (String s : KEY){out+="."+s;}
		return out.substring(1);
	}
	
	
}
