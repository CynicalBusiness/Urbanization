package me.capit.urbanization;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class SerialLocation implements ConfigurationSerializable {
	private double x,y,z,pitch,yaw;
	private String world;
	
	public SerialLocation(Map<String, Object> map){
		x = (double) map.get("X");
		y = (double) map.get("Y");
		z = (double) map.get("Z");
		pitch = (double) map.get("PITCH");
		yaw = (double) map.get("YAW");
		world = (String) map.get("WORLD");
	}
	
	public SerialLocation(Location loc){
		x = loc.getX(); y = loc.getY(); z = loc.getZ();
		pitch = loc.getPitch();
		yaw = loc.getYaw();
		world = loc.getWorld().getName();
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("X", x); map.put("Y", y); map.put("Z", z);
		map.put("PITCH", pitch); map.put("YAW", yaw);
		map.put("WORLD", world);
		return map;
	}
	
	public Location getLocation(){
		return new Location(Bukkit.getWorld(world),x,y,z,(float) yaw,(float) pitch);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public double getYaw() {
		return yaw;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

}
