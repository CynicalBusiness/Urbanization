package me.capit.urbanization.city;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.capit.urbanization.Urbanization;
import me.capit.urbanization.aegis.AegisDependent;
import me.capit.urbanization.aegis.AegisDisabledException;
import me.capit.urbanization.aegis.AegisEnabledException;
import me.capit.urbanization.aegis.AegisException;
import me.capit.urbanization.aegis.CityAegis;
import me.capit.urbanization.city.CityRank.RankPermission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jdom2.Element;

public class City {
	public static final short maxRanks = 128;
	
	public final UUID ID;
	private String name, tag, desc, motd;
	private int defaultRank;
	private double funds;
	private CityRank[] ranks = new CityRank[maxRanks];
	private List<CityPlayer> players = new ArrayList<CityPlayer>();
	private List<CityClaim> claims = new ArrayList<CityClaim>();
	private List<CityAegis> aegises = new ArrayList<CityAegis>();
	
	public City(Element city) throws NullPointerException, IllegalArgumentException {
		ID = UUID.fromString(city.getAttributeValue("name"));
		
		Element meta = city.getChild("meta");
			name = meta.getAttributeValue("name");
			tag = meta.getAttribute("tag")!=null ? meta.getAttributeValue("tag") : "";
			desc = meta.getAttribute("desc")!=null ? meta.getAttributeValue("desc") : "";
			motd = meta.getAttribute("motd")!=null ? meta.getAttributeValue("motd") : "";
		
		Element data = city.getChild("data");
			defaultRank = data.getAttribute("defaultRank")!=null ? Integer.parseInt(data.getAttributeValue("default_rank")) : maxRanks-1;
			funds = data.getAttribute("funds")!=null ? Double.parseDouble(data.getAttributeValue("funds")) : 0;
		
		for (Element e : city.getChild("ranks").getChildren()){
			CityRank r = new CityRank(e);
			ranks[r.rank] = r;
		}
		for (Element e : city.getChild("players").getChildren()){
			players.add(new CityPlayer(e));
		}
		if (Urbanization.AEGIS_ENABLED){
			for (Element e : city.getChild("aegises").getChildren()){
				try {
					aegises.add(new CityAegis(ID,e));
				} catch (AegisException e1) {
					Bukkit.getLogger().warning("Aegis for "+name+" failed to load. It is likely invalid:");
					Bukkit.getLogger().warning(e1.getMessage());
				}
			}
		} else {
			for (Element e : city.getChild("claims").getChildren()){
				claims.add(new CityClaim(e));
			}
		}
	}
	
	public City(String name, UUID ownerID) throws IllegalArgumentException {
		ID = UUID.randomUUID();
		if (!Urbanization.validCityName(name)) throw new IllegalArgumentException("City name is not valid!");
		this.name = name;
		tag = ""; desc = ""; motd = "";
		funds = 0;
		
		defaultRank = maxRanks-1;
		ranks[defaultRank] = new CityRank("DEFAULT", defaultRank).addPermission(RankPermission.BUILD).addPermission(RankPermission.CONTAINER)
				.addPermission(RankPermission.USE).addPermission(RankPermission.LEFT_CLICK).addPermission(RankPermission.RIGHT_CLICK);
		ranks[0] = new CityRank("ADMIN", 0).addPermission(RankPermission.BUILD).addPermission(RankPermission.CONTAINER)
				.addPermission(RankPermission.USE).addPermission(RankPermission.LEFT_CLICK).addPermission(RankPermission.RIGHT_CLICK)
				.addPermission(RankPermission.CITY).addPermission(RankPermission.PERMISSIONS).addPermission(RankPermission.RANKS)
				.addPermission(RankPermission.INVITE).addPermission(RankPermission.KICK).addPermission(RankPermission.DISBAND);
		
		players.add(new CityPlayer(ownerID, 0));
	}
	
	public Element getElement(){
		Element city = new Element("city");
		city.setAttribute("name", ID.toString());
		
		Element meta = new Element("meta");
			meta.setAttribute("name", name);
			meta.setAttribute("tag",tag);
			meta.setAttribute("desc", desc);
			meta.setAttribute("motd", motd);
		city.addContent(meta);
		
		Element data = new Element("data");
			data.setAttribute("default_rank", String.valueOf(defaultRank));
			data.setAttribute("funds", String.valueOf(funds));
		city.addContent(data);
		
		Element ranks = new Element("ranks");
		for (CityRank r : this.ranks){
			if (r!=null) ranks.addContent(r.getElement());
		}
		city.addContent(ranks);
		
		Element players = new Element("players");
		for (CityPlayer p : this.players){
			players.addContent(p.getElement());
		}
		city.addContent(players);
		
		Element aegises = new Element("aegises");
		for (CityAegis a : this.aegises){
			aegises.addContent(a.getElement());
		}
		city.addContent(aegises);
			
		Element claims = new Element("claims");
		for (CityClaim c : this.claims){
			claims.addContent(c.getElement());
		}
		city.addContent(claims);
		
		return city;
	}
	
	public void breakIfAegisEnabled() throws AegisEnabledException {
		if (Urbanization.AEGIS_ENABLED) throw new AegisEnabledException();
	}
	public void breakIfAegisDisabled() throws AegisDisabledException {
		if (!Urbanization.AEGIS_ENABLED) throw new AegisDisabledException();
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof City ? equals((City) o) : false;
	}
	public boolean equals(City city){
		return city.ID.equals(ID);
	}
	
	// FUNDS
	public boolean hasFunds(double amount){
		return amount>=funds;
	}
	
	public double getFunds(){
		return funds;
	}
	
	public void addFunds(double amount){
		if (amount>0 || amount+funds>=0) funds+=amount;
	}
	
	public void takeFunds(double amount){
		addFunds(-amount);
	}
	
	// EVENT LINKS
	public boolean cancelBreakEvent(Block block, UUID breaker){
		if (!hasBlock(block)) return false; 
		try {
			return Urbanization.AEGIS_ENABLED ? damage(block)
					: playerCan(breaker, RankPermission.BUILD) && getClaim(block).getRank()>=getPlayer(breaker).getRank();
		} catch (CityException | AegisException e) {
			return false;
		}
	}
	
	// REPUTATION
	public int getReputation(){
		int total = 0;
		for (CityPlayer p : players) total+=p.getReputation();
		return total;
	}
	
	public int getMaxReputation(){
		return Urbanization.CONTROLLER.getGroupData().getInt("reputation_max")*players.size();
	}
	
	// NON-SPECIFIC TERRITORY CHECKS
	public boolean hasBlock(Block block) {
		try {
			return Urbanization.AEGIS_ENABLED ? blockUnderAegis(block) : hasClaim(block);
		} catch (AegisException e){
			return false;
		}
	}
	
	public boolean hasLocation(Location loc) {
		try {
			return Urbanization.AEGIS_ENABLED ? locationUnderAegis(loc) : hasClaim(loc);
		} catch (AegisException e){
			return false;
		}
	}
	
	// CLAIMS
	@AegisDependent(enabled=false)
	public void claim(int x, int z, String world) throws CityException, AegisEnabledException {
		breakIfAegisEnabled();
		claim(x,z,world,defaultRank);
	}
	@AegisDependent(enabled=false)
	public void claim(int x, int z, String world, int rank) throws CityException, AegisEnabledException {
		breakIfAegisEnabled();
		claims.add(new CityClaim(x,z,world,rank));
	}
	
	@AegisDependent(enabled=false)
	public void unclaim(int x, int z, String world) throws CityException, AegisEnabledException {
		breakIfAegisEnabled();
		if (!hasClaim(x,z,world)) throw new CityException("Claim does not belong to the city!");
		
	}
	
	@AegisDependent(enabled=false)
	public CityClaim getClaim(int x, int z, String world) throws AegisEnabledException {
		breakIfAegisEnabled();
		for (CityClaim claim : claims) if (claim.x==x && claim.z==z && claim.world.equals(world)) return claim;
		return null;
	}
	@AegisDependent(enabled=false)
	public CityClaim getClaim(Block block) throws AegisEnabledException {
		breakIfAegisEnabled();
		return getClaim(block.getChunk().getX(), block.getChunk().getZ(), block.getWorld().getName());
	}
	
	@AegisDependent(enabled=false)
	public boolean hasClaim(int x, int z, String world) throws AegisEnabledException {
		breakIfAegisEnabled();
		return getClaim(x,z,world)!=null;
	}
	
	@AegisDependent(enabled=false)
	public boolean hasClaim(Block block) throws AegisEnabledException {
		breakIfAegisEnabled();
		return hasClaim(block.getChunk().getX(), block.getChunk().getZ(), block.getWorld().getName());
	}
	
	@AegisDependent(enabled=false)
	public boolean hasClaim(Location location) throws AegisEnabledException {
		breakIfAegisEnabled();
		return hasClaim(location.getChunk().getX(), location.getChunk().getZ(), location.getWorld().getName());
	}
	
	@AegisDependent(enabled=false)
	public int getMaxClaims() throws AegisEnabledException {
		breakIfAegisEnabled();
		return getMaxReputation()/Urbanization.CONTROLLER.getGroupData().getInt("reputation_cost_claim");
	}
	
	@AegisDependent(enabled=false)
	public int getCurrentMaxClaims() throws AegisEnabledException {
		breakIfAegisEnabled();
		return getReputation()/Urbanization.CONTROLLER.getGroupData().getInt("reputation_cost_claim");
	}
	
	// AEGIS
	@AegisDependent(enabled=true)
	public boolean locationUnderAegis(Location loc) throws AegisDisabledException {
		breakIfAegisDisabled();
		for (CityAegis a : aegises){
			if (a.locationCovered(loc)) return true;
		}
		return false;
	}
	
	@AegisDependent(enabled=true)
	public boolean blockUnderAegis(Block b) throws AegisDisabledException {
		breakIfAegisDisabled();
		return locationUnderAegis(b.getLocation());
	}
	
	@AegisDependent(enabled=true)
	public boolean damage(Block block) throws AegisDisabledException {
		breakIfAegisDisabled();
		for (CityAegis a : aegises) if (a.blockCovered(block)) return a.damage(); return false;
	}
	
	@AegisDependent(enabled=true)
	public boolean hasAegis(CityAegis a) throws AegisDisabledException {
		breakIfAegisDisabled();
		for (CityAegis ca : aegises) if (ca.equals(a)) return true; return false; 
	}
	
	@AegisDependent(enabled=true)
	public int getMaxAegises(CityAegis a) throws AegisDisabledException {
		breakIfAegisDisabled();
		return getMaxReputation()/Urbanization.CONTROLLER.getGroupData().getInt("reputation_cost_aegis");
	}
	
	// PLAYERS
	public void updatePlayers(){
		for (CityPlayer player : players){
			if (!hasRank(player.getRank())) player.setRank(getDefaultRank().rank);
		}
	}
	
	public CityPlayer getPlayer(UUID id){
		for (CityPlayer player : players){
			if (player.ID.equals(id)) return player;
		}
		return null;
	}
	
	public List<UUID> getPlayers(){
		List<UUID> ids = new ArrayList<UUID>();
		for (CityPlayer cp : players) ids.add(cp.ID);
		return ids;
	}
	
	public boolean hasPlayer(UUID id){
		return getPlayer(id)!=null;
	}
	
	public int getPlayerRank(UUID id) throws CityException {
		if (!hasPlayer(id)) throw new CityException("Player is not in group!");
		for (CityPlayer player : players){
			if (player.ID.equals(id)) return player.getRank();
		}
		return defaultRank;
	}
	
	public void setPlayerRank(UUID id, int rank) throws CityException {
		if (rank==0) throw new CityException("Cannot add player to rank 0! Use &oowner &rinstead!");
		if (!hasRank(rank)) throw new CityException("That rank does not exist!");
		if (!hasPlayer(id)) throw new CityException("Player is not in group!");
		getPlayer(id).setRank(rank);
	}
	public void setPlayerRank(UUID id, String rank) throws CityException {
		setPlayerRank(id, getRankID(rank));
	}
	
	public boolean playerCan(UUID id, RankPermission permission) throws CityException {
		CityRank rank = getRank(getPlayerRank(id));
		if (rank==null){ updatePlayers(); return playerCan(id, permission); }
		return rank.hasPermission(permission);
	}
	
	public UUID getOwnerID(){
		return getOwner()!=null ? getOwner().getUniqueId() : null;
	}
	public Player getOwner(){
		for (CityPlayer player : players){
			if (player.getRank()==0) return player.getPlayer();
		}
		return null;
	}
	
	// RANKS
	public String getRankName(int rank){
		return hasRank(rank) ? getRank(rank).name : null;
	}
	public int getRankID(String name){
		return hasRank(name) ? getRank(name).rank : -1;
	}
	
	public void deleteRank(int rank) throws CityException {
		if (!hasRank(rank)) throw new CityException("That rank does not exist.");
		if (rank==defaultRank || rank==0) throw new CityException("That rank cannot be deleted.");
		ranks[rank] = null;
	}
	public void deleteRank(String name) throws CityException {
		deleteRank(getRankID(name));
	}
	
	public boolean hasRank(int rank){
		return rank<maxRanks && rank>=0 ? ranks[rank]!=null : false;
	}
	public boolean hasRank(String name){
		return getRank(name)!=null;
	}
	
	public CityRank getRank(int rank){
		return rank<maxRanks && rank>=0 ? ranks[rank] : null;
	}
	public CityRank getRank(String name) {
		return getRank(getRankID(name));
	}
	
	public CityRank getDefaultRank(){
		return getRank(defaultRank);
	}
	
	// Getters and Setters
	public String getName(){ return name; }
	public void setName(String name){ if (Urbanization.validCityName(name)) this.name = name; }
	
	public String getTag(){ return tag!=null ? tag : getDefaultTag(); }
	public void setTag(String tag){ if (Urbanization.validCityTag(tag)) this.tag = tag; }
	public String getDefaultTag(){ return name.substring(0, 3).toUpperCase(); }
	
	public String getDesc(){ return desc!=null ? desc : "Default city description."; }
	public void setDesc(String desc){ this.desc = desc; }
	
	public String getMotd(){ return motd!=null ? motd : ""; }
	public void setMotd(String motd){ this.motd = motd; }
}
