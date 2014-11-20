package me.capit.urbanization.group;

public class GroupPermission {
	public final String[] KEY;
	
	public GroupPermission(String permissionKey){
		KEY = permissionKey.split(".");
	}
	
	public boolean matches(GroupPermission p){
		for (int i = 0; i<(Math.max(KEY.length, p.KEY.length)); i++){
			if (i<KEY.length && i<p.KEY.length){
				if (!KEY[i].equalsIgnoreCase(p.KEY[i]) && !(KEY[i].equals("*") || p.KEY[i].equals("*"))) return false;
			}
		}
		return true;
	}
	
}
