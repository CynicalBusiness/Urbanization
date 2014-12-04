package me.capit.urbanization.group;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import me.capit.urbanization.Urbanization;

public class Territory implements ConfigurationSerializable{
	public final int x,z,sgid;
	public final UUID gid;
	public final String worldName;
	
	public Territory(Map<String, Object> map){
		x = (int) map.get("X");
		z = (int) map.get("Z");
		sgid = (int) map.get("SGID");
		gid = UUID.fromString((String) map.get("GID"));
		worldName = (String) map.get("WORLD");
	}
	
	public Territory(int x, int z, UUID gid, int sgid, String world){
		this.x=x; this.z=z;
		this.gid=gid;
		this.sgid=sgid;
		worldName = world;
	}
	
	public boolean playerCanBreak(UUID player){
		Group g = Urbanization.getGroupByID(gid);
		return g.playerHasPermission(player, "build.break") && g.getPlayerGroup(player).ID<=sgid;
	}
	
	public boolean playerCanPlace(UUID player){
		Group g = Urbanization.getGroupByID(gid);
		return g.playerHasPermission(player, "build.place") && g.getPlayerGroup(player).ID<=sgid;
	}
	
	public boolean playerCanUse(UUID player){
		Group g = Urbanization.getGroupByID(gid);
		return g.playerHasPermission(player, "build.interact") && g.getPlayerGroup(player).ID<=sgid;
	}
	
	public boolean playerCanAccessContainers(UUID player){
		Group g = Urbanization.getGroupByID(gid);
		return g.playerHasPermission(player, "build.container") && g.getPlayerGroup(player).ID<=sgid;
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof Territory ? ((Territory) o).x==x && ((Territory) o).z==z && ((Territory) o).worldName.equals(worldName) : false;
	}
	
	public boolean equals(int x, int z){
		return this.x==x && this.z==z;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("X", x); map.put("Z", z); map.put("GID", gid.toString());
		map.put("SGID", sgid); map.put("WORLD", worldName);
		return map;
	}
	
}
