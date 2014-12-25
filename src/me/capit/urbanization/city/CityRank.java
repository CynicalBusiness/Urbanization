package me.capit.urbanization.city;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

public class CityRank {
	public enum RankPermission{
		BUILD, CONTAINER, RIGHT_CLICK, LEFT_CLICK, USE, PERMISSIONS, CITY, RANKS, INVITE, KICK, DISBAND
	}
	
	public final String name;
	public final int rank;
	private final List<RankPermission> permissions = new ArrayList<RankPermission>();
	
	public CityRank (Element rank) throws NullPointerException, IllegalArgumentException {
		name = rank.getAttributeValue("name");
		this.rank = Integer.parseInt(rank.getAttributeValue("rank"));
		for (String perm : rank.getAttributeValue("permissions").trim().split(",")){
			perm = perm.trim();
			permissions.add(RankPermission.valueOf(perm.toUpperCase()));
		}
	}
	
	public CityRank (String name, int rank){
		this.name = name;
		this.rank = rank;
	}
	
	public CityRank addPermission(RankPermission perm){
		if (!permissions.contains(perm)) permissions.add(perm);
		return this;
	}
	
	public boolean removePermission(RankPermission perm){
		boolean contains = permissions.contains(perm);
		if (contains) permissions.remove(perm);
		return contains;
	}
	
	public boolean hasPermission(RankPermission perm){
		for (RankPermission p : permissions){
			if (p.equals(perm)) return true;
		}
		return false;
	}
	
	public String getPermissionString(){
		if (permissions.size()==0) return "";
		String str = "";
		for (RankPermission perm : permissions){
			str+=","+perm.toString().toLowerCase();
		}
		return str.substring(1);
	}
	
	public Element getElement(){
		Element rank = new Element("rank");
		rank.setAttribute("name", name);
		rank.setAttribute("permissions", getPermissionString());
		rank.setAttribute("rank", String.valueOf(this.rank));
		return rank;
	}
}
