package me.capit.urbanization.group;

import org.bukkit.ChatColor;

public enum GroupRelation {
	WARRING, HOSTILE,
	NEUTRAL, 
	FRIENDLY, ALLIED,
	NONE;
	
	public boolean isNeutral(){
		return this==GroupRelation.NEUTRAL ? true : false;
	}
	
	public ChatColor getColor(){
		switch(this){
		case ALLIED:
			return ChatColor.GREEN;
		case WARRING:
			return ChatColor.RED;
		default:
			return ChatColor.WHITE;
		}
	}
}
