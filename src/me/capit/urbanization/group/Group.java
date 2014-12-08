package me.capit.urbanization.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import me.capit.urbanization.SerialLocation;
import me.capit.urbanization.Urbanization;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class Group implements ConfigurationSerializable {
	public static final int subgroupSize = 255;
	public final UUID ID;
	
	private String name,desc,tag,motd;
	private Subgroup[] subgroups = new Subgroup[subgroupSize+1];
	private List<Territory> territory = new ArrayList<Territory>();
	private Map<String, GroupRelation> relations = new TreeMap<String, GroupRelation>();
	private double funds = 0; private GroupRelation teleportRelation = GroupRelation.NONE;
	private SerialLocation home; private boolean open; private int defaultGroup;
	
	public static Group createNewGroup(String name, UUID owner) throws IOException{
		Map<String, Object> file = new TreeMap<String, Object>();
		file.put("ID", UUID.randomUUID().toString());
		file.put("NAME", name); file.put("TAG", name.substring(0, 3).toUpperCase());
		file.put("DESC", "Default Group Description"); file.put("MOTD", ""); file.put("FUNDS", 0.0);
		file.put("TELEPORT_RELATION", GroupRelation.NEUTRAL.toString());
		file.put("HOME", null); file.put("OPEN", false);
		file.put("DEFAULT_GROUP",subgroupSize);
		
		Map<String, Object> terr = new TreeMap<String, Object>();
		file.put("TERRITORY", terr);
		
		Map<String, Object> rel = new TreeMap<String, Object>();
		file.put("RELATIONS", rel);
		
		Map<String, Object> sg = new TreeMap<String, Object>();
		sg.put("S0", new Subgroup("Admin", 0).addPermission(new GroupPermission("*")).setPrefix('♦').addPlayer(owner));
		sg.put("S"+subgroupSize, new Subgroup("Default", subgroupSize).addPermission(new GroupPermission("build.*"))
			.addPermission(new GroupPermission("group.home")).setPrefix(' '));
		file.put("SUBGROUPS", sg);
		
		Group g = new Group(file);
		g.save();
		return g;
	}
	
	@SuppressWarnings("unchecked")
	public Group(Map<String, Object> map){
		ID = UUID.fromString((String) map.get("ID"));
		name = (String) map.get("NAME"); desc = (String) map.get("DESC");
		tag = (String) map.get("TAG"); motd = (String) map.get("MOTD");
		funds = (double) map.get("FUNDS"); home = (SerialLocation) map.get("HOME");
		open = (boolean) map.get("OPEN"); defaultGroup = (int) map.get("DEFAULT_GROUP");
		teleportRelation = GroupRelation.valueOf((String) map.get("TELEPORT_RELATION"));
		
		Map<String, Object> terr = (Map<String, Object>) map.get("TERRITORY");
		for (String key : terr.keySet()) territory.add((Territory) terr.get(key));
		
		Map<String, Object> sg = (Map<String, Object>) map.get("SUBGROUPS");
		for (String key : sg.keySet()) subgroups[Integer.parseInt(key.substring(1))]=(Subgroup) sg.get(key);
		
		Map<String, Object> rel = (Map<String, Object>) map.get("RELATIONS");
		for (String key : rel.keySet()) relations.put(key, GroupRelation.valueOf((String) rel.get(key)));
	}
	
	public void save() throws IOException{
		Urbanization.CONTROLLER.writeInstance(ID, this);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("ID", ID.toString());
		map.put("NAME", name); map.put("DESC", desc);
		map.put("MOTD", motd); map.put("TAG", tag);
		map.put("FUNDS", funds); map.put("HOME", home);
		map.put("TELEPORT_RELATION", teleportRelation.toString());
		map.put("OPEN", open); map.put("DEFAULT_GROUP", defaultGroup);
		
		Map<String, Object> terr = new TreeMap<String, Object>();
		for (int i = 0; i<territory.size(); i++) terr.put("T"+i, territory.get(i));
		map.put("TERRITORY", terr);
		
		Map<String, Object> sg = new TreeMap<String, Object>();
		for (int i = 0; i<subgroups.length; i++) if (subgroups[i]!=null) sg.put("S"+i, subgroups[i]);
		map.put("SUBGROUPS", sg);
		
		Map<String, Object> rel = new TreeMap<String, Object>();
		for (String key : relations.keySet()) rel.put(key, relations.get(key));
		map.put("RELATIONS", rel);
		
		return map;
	}
	
	public void delete(){
		OfflinePlayer owner = Bukkit.getServer().getPlayer(subgroups[0].getPlayers().get(0));
		if (owner.isOnline()){
			Player p = (Player) owner;
			if (Urbanization.ECONOMY!=null)
				p.sendMessage(ChatColor.YELLOW+"The "+ChatColor.RED+funds+ChatColor.YELLOW+" "+
						Urbanization.ECONOMY.currencyNamePlural()+" from the group back has been added to your account.");
		}
		if (Urbanization.ECONOMY!=null) Urbanization.ECONOMY.depositPlayer(owner, funds);
		Urbanization.CONTROLLER.getInstanceFile(ID).delete();
	}
	
	public String name(){return name;}
	public void name(String name){this.name=name;}
	
	public String desc(){return desc;}
	public void desc(String desc){this.desc=desc;}
	
	public String motd(){return motd;}
	public void motd(String motd){this.motd=motd;}
	
	public String tag(){return tag;}
	public void tag(String tag){this.tag=tag;}
	
	public double funds(){return funds;}
	public void funds(double funds){this.funds=funds;}
	public boolean hasFunds(double f){return !Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy") || funds>=f;}
	public void deposit(double f){funds+=f;}
	
	public boolean groupExists(int rank){
		return getGroup(rank)!=null;
	}
	
	public GroupRelation getTeleportRelation(){
		return teleportRelation;
	}
	
	public boolean canTeleportWithRelation(GroupRelation rel){
		return rel.ordinal()>=teleportRelation.ordinal();
	}
	
	public List<UUID> getPlayers(){
		List<UUID> players = new ArrayList<UUID>();
		for (Subgroup sg : subgroups){if (sg!=null){for (UUID id : sg.getPlayers()){players.add(id);}}}
		return players;
	}
	
	public char getPlayerPrefix(UUID player){
		return hasPlayer(player) ? getPlayerGroup(player).getPrefix() : ' ';
	}
	
	public Subgroup getGroup(int rank){
		return rank<subgroups.length ? subgroups[rank] : null;
	}
	
	public Subgroup getPlayerGroup(UUID player){
		for (Subgroup g : subgroups){
			if (g!=null && g.hasPlayer(player)) return g;
		}
		return null;
	}
	
	public void setHome(Location loc){
		home=new SerialLocation(loc); 
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Location getHome(){
		return home.getLocation();
	}
	
	public void addPlayer(UUID player){
		addPlayer(player, defaultGroup);
	}
	
	public void addPlayer(UUID player, int sgid){
		subgroups[sgid].addPlayer(player);
	}
	
	public void removePlayer(UUID player){
		Subgroup sg = getPlayerGroup(player);
		sg.removePlayer(player);
	}
	
	public boolean hasPlayer(UUID player){
		return getPlayerGroup(player)!=null;
	}
	
	public boolean playerHasPermission(UUID player, String perm){
		return playerHasPermission(player, new GroupPermission(perm));
	}
	public boolean playerHasPermission(UUID player, GroupPermission perm){
		return hasPlayer(player) ? getPlayerGroup(player).hasPermission(perm) : false;
	}
	
	public Territory getTerritoryAt(int x, int z){
		for (Territory t : territory){
			if (t.x==x && t.z==z) return t;
		}
		return null;
	}
	
	public void addTerritory(Territory t){
		territory.add(t);
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void removeTerritory(int x, int z){
		for (int i = 0; i<territory.size(); i++){
			Territory t = territory.get(i);
			if (t.x==x && t.z==z){ territory.remove(i); break; }
		}
	}
	
	public boolean territoryBelongsToGroup(int x, int z){
		return getTerritoryAt(x,z)!=null;
	}
	
	public List<Territory> getTerritory(){
		return territory;
	}
	
	public void validateRelations(){
		for (String s : relations.keySet()){
			if (Urbanization.getGroupByID(UUID.fromString(s))==null) relations.remove(s);
		}
	}
	
	public Map<String, GroupRelation> getRelatedGroups(){
		validateRelations();
		return relations;
	}
	
	public List<String> getGroupsWithRelation(){
		validateRelations();
		List<String> gs = new ArrayList<String>();
		for (String s : relations.keySet()){
			gs.add(ChatColor.AQUA+Urbanization.getGroupByID(UUID.fromString(s)).name()+
					ChatColor.WHITE+":"+relations.get(s).getColor()+relations.get(s).toString());
		}
		return gs;
	}
	
	public void setRelationTo(Group g, GroupRelation gr){
		setRelationTo(g.ID, gr);
	}
	public void setRelationTo(UUID id, GroupRelation gr){
		relations.put(id.toString(), gr);
	}
	
	public GroupRelation getRelationOfGroup(Group g){
		return getRelationOfGroup(g.ID);
	}
	
	public GroupRelation getRelationOfGroup(UUID ID){
		return relations.get(ID)!=null ? relations.get(ID.toString()) : GroupRelation.NEUTRAL;
	}
	
	public boolean playerCanTele(UUID player){
		if (hasPlayer(player)) return playerHasPermission(player, "group.home");
		Group g = Urbanization.getGroupByPlayer(player);
		if (g==null || g.getHome()==null) return false;
		return canTeleportWithRelation(g.getRelationOfGroup(ID));
	}
	
	public boolean isOpen(){
		return open;
	}
	
	public void setOpen(boolean open){
		this.open=open;
	}
}
