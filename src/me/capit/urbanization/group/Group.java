package me.capit.urbanization.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.capit.urbanization.Urbanization;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Group {
	public static final int subgroupSize = 255;
	public final String ID;
	private final YamlConfiguration instance;
	
	private String name,desc,tag,motd;
	private Subgroup[] subgroups = new Subgroup[subgroupSize+1];
	private List<String> territory = new ArrayList<String>();
	private HashMap<String, GroupRelation> relations = new HashMap<String, GroupRelation>();
	private double funds = 0; private GroupRelation teleportRelation = GroupRelation.NONE;
	private Location home;
	
	public static Group createNewGroup(String name, UUID owner) throws IOException{
		String tid = String.valueOf(System.currentTimeMillis());
		YamlConfiguration file = Urbanization.CONTROLLER.readInstance(tid);
		file.set("NAME", name); file.set("TAG", name.substring(0, 3).toUpperCase());
		file.set("DESC", "Default Group Description"); file.set("MOTD", "");
		file.set("FUNDS", 0.0); file.set("TERRITORY", new ArrayList<String>());
		file.set("RELATIONS", new ArrayList<String>()); file.set("TELEPORT_RELATION", "NEUTRAL");
		file.set("HOME", "");
		
		ConfigurationSection groups = file.createSection("GROUPS");
		new Subgroup("Admin", 0).addPermission(new GroupPermission("*")).setPrefix('♦').addPlayer(owner).addToConfigEntry(groups);
		new Subgroup("Default", subgroupSize).addPermission(new GroupPermission("build.*"))
			.addPermission(new GroupPermission("group.home")).setPrefix(' ').addToConfigEntry(groups);
		
		file.save(Urbanization.CONTROLLER.getInstanceFile(tid));
		Group g = new Group(tid);
		return g;
	}
	
	public Group(String instanceID){
		ID=instanceID;
		instance = Urbanization.CONTROLLER.readInstance(ID);
		name=instance.getString("NAME"); desc=instance.getString("DESC");
		tag=instance.getString("TAG"); motd=instance.getString("MOTD");
		funds=instance.getDouble("FUNDS"); territory=instance.getStringList("TERRITORY");
		teleportRelation=GroupRelation.valueOf(instance.getString("TELEPORT_RELATION"));
		
		String[] homeData = instance.getString("HOME")!=null ? instance.getString("HOME").split(",") : new String[]{};
		home=instance.getString("HOME")!=null ? new Location(Bukkit.getWorld(homeData[0]),
				Double.valueOf(homeData[1]),Double.valueOf(homeData[2]),Double.valueOf(homeData[3]),
				Float.valueOf(homeData[4]), Float.valueOf(homeData[5])) : null;
		
		for (String rel : instance.getStringList("RELATIONS")){
			String id = rel.split(":")[0];
			GroupRelation r = GroupRelation.valueOf(rel.split(":")[1]);
			relations.put(id, r);
		}
		
		for (String key : instance.getConfigurationSection("GROUPS").getKeys(false)){
			ConfigurationSection sec = instance.getConfigurationSection("GROUPS."+key);
			Subgroup g = Subgroup.buildFromConfig(sec);
			subgroups[g.ID] = g;
		}
	}
	
	public void save() throws IOException{
		instance.set("NAME", name); instance.set("DESC", desc);
		instance.set("TAG", tag); instance.set("MOTD", motd);
		instance.set("FUNDS", funds); instance.set("TERRITORY", territory);
		
		ConfigurationSection groups = instance.createSection("GROUPS");
		for (Subgroup sg : subgroups){if (sg!=null) sg.addToConfigEntry(groups);}
		
		validateRelations();
		List<String> rels = new ArrayList<String>();
		for (String id : relations.keySet()){
			rels.add(id+":"+relations.get(id));
		}
		instance.set("RELATIONS", rels);
		instance.set("TELEPORT_RELATION", teleportRelation.toString());
		
		instance.set("TERRITORY", territory);
		instance.set("HOME", home.getWorld().getName()+","+home.getX()+","+home.getY()+","+home.getZ()+","+home.getYaw()+","+home.getPitch());
		
		instance.save(Urbanization.CONTROLLER.getInstanceFile(ID));
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
		home=loc; 
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Location getHome(){
		return home;
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
		for (String s : territory){
			Territory t = Territory.fromString(s);
			if (t.x==x && t.z==z) return t;
		}
		return null;
	}
	
	public void addTerritory(Territory t){
		territory.add(t.toString());
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void removeTerritory(int x, int z){
		for (int i = 0; i<territory.size(); i++){
			Territory t = Territory.fromString(territory.get(i));
			if (t.x==x && t.z==z){ territory.remove(i); break; }
		}
	}
	
	public boolean territoryBelongsToGroup(int x, int z){
		return getTerritoryAt(x,z)!=null;
	}
	
	public List<String> getTerritory(){
		return territory;
	}
	
	public void validateRelations(){
		for (String s : relations.keySet()){
			if (Urbanization.getGroupByID(s)==null) relations.remove(s);
		}
	}
	
	public HashMap<String, GroupRelation> getRelatedGroups(){
		validateRelations();
		return relations;
	}
	
	public List<String> getGroupsWithRelation(){
		validateRelations();
		List<String> gs = new ArrayList<String>();
		for (String s : relations.keySet()){
			gs.add(ChatColor.AQUA+Urbanization.getGroupByID(s).name()+ChatColor.WHITE+":"+relations.get(s).getColor()+relations.get(s).toString());
		}
		return gs;
	}
	
	public void setRelationTo(Group g, GroupRelation gr){
		setRelationTo(g.ID, gr);
	}
	public void setRelationTo(String id, GroupRelation gr){
		relations.put(id, gr);
	}
	
	public GroupRelation getRelationOfGroup(Group g){
		return getRelationOfGroup(g.ID);
	}
	
	public GroupRelation getRelationOfGroup(String ID){
		return relations.get(ID)!=null ? relations.get(ID) : GroupRelation.NEUTRAL;
	}
	
	public boolean playerCanTele(UUID player){
		if (hasPlayer(player)) return playerHasPermission(player, "group.home");
		Group g = Urbanization.getGroupByPlayer(player);
		if (g==null || g.getHome()==null) return false;
		return canTeleportWithRelation(g.getRelationOfGroup(ID));
	}
}
