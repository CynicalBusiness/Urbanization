package me.capit.urbanization.city;

import java.util.UUID;

import me.capit.urbanization.Urbanization;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jdom2.Element;

public class CityPlayer {
	public final UUID ID;
	private int rank,reputation=0;
	
	public CityPlayer(Element player) throws NullPointerException, IllegalArgumentException {
		ID = UUID.fromString(player.getAttributeValue("name"));
		rank = Integer.parseInt(player.getAttributeValue("rank"));
		reputation = Integer.parseInt(player.getAttributeValue("reputation"));
	}
	
	public CityPlayer(UUID ID, int rank){
		this.ID = ID;
		this.rank = rank;
	}
	
	public int getRank(){
		return rank;
	}
	
	public int getReputation(){
		return reputation;
	}
	
	public void setRank(int rank){
		this.rank = rank;
	}
	
	public void increaseReputation(){
		if (reputation<Urbanization.CONTROLLER.getGroupData().getInt("reputation_max")) reputation++;
	}
	
	public void decreaseReputation(){
		int by = Urbanization.CONTROLLER.getGroupData().getInt("reputation_on_death");
		int max = Urbanization.CONTROLLER.getGroupData().getInt("reputation_max");
		int min = Urbanization.CONTROLLER.getGroupData().getInt("reputation_min");
		if (reputation+by>=min && reputation+by<=max) reputation+=by;
	}
	
	public Element getElement(){
		Element player = new Element("player");
		player.setAttribute("name", ID.toString());
		player.setAttribute("rank", String.valueOf(rank));
		player.setAttribute("reputation", String.valueOf(reputation));
		return player;
	}
	
	public Player getPlayer(){
		return Bukkit.getServer().getPlayer(ID);
	}
	
}
