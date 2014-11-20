package me.capit.urbanization.group;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import me.capit.urbanization.Urbanization;

import org.bukkit.configuration.file.YamlConfiguration;

public class Group {
	public final String ID;
	private final YamlConfiguration instance;
	
	private String name,desc,tag;
	private List<UUID> players;
	private Subgroup[] subgroups = new Subgroup[64];
	
	public Group(String instanceID){
		ID=instanceID;
		instance = Urbanization.CONTROLLER.readInstance(ID);
	}
	
	public Group(){
		this(String.valueOf(new Date().getTime()));
	}
	
}
