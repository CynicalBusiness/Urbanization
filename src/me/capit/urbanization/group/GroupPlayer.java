package me.capit.urbanization.group;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

public class GroupPlayer{
	public final UUID PID;
	public final int GID;
	
	public GroupPlayer(UUID PID, int GID){
		this.PID=PID; this.GID=GID;
	}
	
	public void addAsKey(ConfigurationSection c){
		c.set(PID.toString(), GID);
	}
}
