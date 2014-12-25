package me.capit.urbanization.city;

import org.jdom2.Element;

public class CityClaim {
	public final int x,z;
	public final String world;
	private int rank;
	
	public CityClaim (Element claim) throws NullPointerException, IllegalArgumentException {
		x = Integer.parseInt(claim.getAttributeValue("x"));
		z = Integer.parseInt(claim.getAttributeValue("z"));
		rank = Integer.parseInt(claim.getAttributeValue("rank"));
		world = claim.getAttributeValue("world");
	}
	
	public CityClaim (int chunkX, int chunkZ, String world, int rank){
		x = chunkX; z = chunkZ;
		this.rank = rank;
		this.world = world;
	}
	
	public Element getElement(){
		Element claim = new Element("claim");
			claim.setAttribute("x", String.valueOf(x));
			claim.setAttribute("z",String.valueOf(z));
			claim.setAttribute("rank", String.valueOf(rank));
			claim.setAttribute("world", world);
		return claim;
	}
	
	public int getRank(){
		return rank;
	}
	
	public void setRank(int rank){
		if (rank>=0 && rank<City.maxRanks) this.rank = rank;
	}
	
}
