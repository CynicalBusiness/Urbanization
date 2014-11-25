package me.capit.urbanization.group;

import java.util.UUID;

import me.capit.urbanization.Urbanization;

public class Territory {
	public final int x,z,sgid;
	public final String gid;
	
	public static Territory fromString(String s){
		String[] data = s.split("|");
		if (data.length==4){
			int x = Integer.parseInt(data[0]);
			int z = Integer.parseInt(data[1]);
			String gid = data[2];
			int sgid = Integer.parseInt(data[3]);
			return new Territory(x,z,gid,sgid);
		}
		throw new IllegalArgumentException("Input string was not in correct format!");
	}
	
	public Territory(int x, int z, String gid, int sgid){
		this.x=x; this.z=z;
		this.gid=gid;
		this.sgid=sgid;
	}
	
	public boolean playerCanBreak(UUID player){
		Group g = Urbanization.getGroupByID(gid);
		return g.playerHasPermission(player, "build.break") && g.getPlayerGroup(player).ID>=sgid;
	}
	
	public boolean playerCanPlace(UUID player){
		Group g = Urbanization.getGroupByID(gid);
		return g.playerHasPermission(player, "build.place") && g.getPlayerGroup(player).ID>=sgid;
	}
	
	public boolean playerCanUse(UUID player){
		Group g = Urbanization.getGroupByID(gid);
		return g.playerHasPermission(player, "build.use") && g.getPlayerGroup(player).ID>=sgid;
	}
	
	public boolean playerCanAccessContainers(UUID player){
		Group g = Urbanization.getGroupByID(gid);
		return g.playerHasPermission(player, "build.container") && g.getPlayerGroup(player).ID>=sgid;
	}
	
	public String toString(){
		return x+"|"+z+"|"+gid+"|"+sgid;
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof Territory ? ((Territory) o).x==x && ((Territory) o).z==z : false;
	}
	
	public boolean equals(int x, int z){
		return this.x==x && this.z==z;
	}
	
}
