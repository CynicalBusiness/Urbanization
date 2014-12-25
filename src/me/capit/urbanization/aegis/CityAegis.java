package me.capit.urbanization.aegis;

import java.util.UUID;

import me.capit.urbanization.Urbanization;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.jdom2.Element;

public class CityAegis {
	public static final int getValueOf(Material mat){
		return Urbanization.CONTROLLER.getGroupData().getInt("aegis_materials."+mat.toString());
	}
	
	public final UUID cityID;
	private final Location loc;
	private Block chest,signal;
	private int radius=0,height=0,power=0;
	
	public CityAegis(UUID cityID, Element aegis) throws NullPointerException, IllegalArgumentException, AegisException {
		this.cityID = cityID;
		loc = new Location(
				Bukkit.getWorld(aegis.getAttributeValue("world")),
				Double.parseDouble(aegis.getAttributeValue("x")),
				Double.parseDouble(aegis.getAttributeValue("y")),
				Double.parseDouble(aegis.getAttributeValue("z")));
		validate();
	}
	
	public CityAegis(UUID cityID, Location loc) throws AegisException {
		this.cityID = cityID;
		this.loc = loc;
		validate();
	}
	
	public void validate() throws AegisException {
		try {
			Material base = Material.valueOf(Urbanization.CONTROLLER.getGroupData().getString("aegis_base"));
			Material chest = Material.CHEST;
			Material antenna = Material.valueOf(Urbanization.CONTROLLER.getGroupData().getString("aegis_antenna"));
			Material topper = Material.valueOf(Urbanization.CONTROLLER.getGroupData().getString("aegis_topper"));
			Material signal_off = Material.valueOf(Urbanization.CONTROLLER.getGroupData().getString("aegis_signal_off"));
			Material signal_on = Material.valueOf(Urbanization.CONTROLLER.getGroupData().getString("aegis_signal_on"));
			
			if (loc.getBlock().getType()!=base) throw new AegisException("Invalid base block!");
			
			Block chestBlock = loc.getBlock().getRelative(BlockFace.UP);
			if (chestBlock.getType()!=chest) throw new AegisException("Invalid chest!");
			this.chest = chestBlock;
			
			Block antennaBlock = chestBlock.getRelative(BlockFace.UP);
			while (antennaBlock.getType()==antenna){
				height++;
				antennaBlock = antennaBlock.getRelative(BlockFace.UP);
			}
			int antennaMin = Integer.parseInt(Urbanization.CONTROLLER.getGroupData().getString("aegis_antenna_min"));
			if (height<=antennaMin) throw new AegisException("Antenna too small! Minimum size is "+antennaMin+"!");
			if (antennaBlock.getType()!=topper) throw new AegisException("Invalid topper block!");
			
			Block signalBlock = antennaBlock.getRelative(BlockFace.UP);
			if (signalBlock.getType()!=signal_off && signalBlock.getType()!=signal_on) throw new AegisException("Invalid signal block!");
			this.signal = signalBlock;
			
			radius = height*Integer.parseInt(Urbanization.CONTROLLER.getGroupData().getString("aegis_raidus_per_height"));
			setActive(true);
		} catch (NullPointerException | IllegalArgumentException e) {
			throw new AegisException("Invalid Aegis configuration!");
		}
	}
	
	public Element getElement(){
		Element aegis = new Element("aegis");
		
		aegis.setAttribute("world", loc.getWorld().getName());
		aegis.setAttribute("x", String.valueOf(loc.getBlockX()));
		aegis.setAttribute("y", String.valueOf(loc.getBlockY()));
		aegis.setAttribute("z", String.valueOf(loc.getBlockZ()));
		
		return aegis;
	}
	
	public int getRadius(){
		return radius;
	}
	
	public int getHeight(){
		return height;
	}
	
	public Chest getChest(){
		return (Chest) chest.getState();
	}
	
	public double getX(){
		return loc.getX();
	}
	public double getY(){
		return loc.getY();
	}
	public double getZ(){
		return loc.getZ();
	}
	
	public void setActive(boolean active){
		Material signal = active
				? Material.valueOf(Urbanization.CONTROLLER.getGroupData().getString("aegis_signal_on"))
				: Material.valueOf(Urbanization.CONTROLLER.getGroupData().getString("aegis_signal_off"));
		this.signal.setType(signal);
	}
	
	public boolean useChestedItem(){
		Chest c = getChest();
		for (ItemStack is : c.getBlockInventory().getContents()){
			if (is!=null && getValueOf(is.getType())>0){
				power = getValueOf(is.getType());
				return true;
			}
		}
		return false;
	}
	
	public boolean damage(){
		if (power>0) { power--; return true; }
		else return useChestedItem();
	}
	
	public int getPower(){
		return power;
	}
	
	public boolean locationCovered(Location block){
		try {
			block.setY(loc.getY());
			return Math.abs(block.distance(loc))<=radius;
		} catch (IllegalArgumentException e){
			return false;
		}
	}
	public boolean blockCovered(Block block){
		return locationCovered(block.getLocation());
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof CityAegis ? equals((CityAegis) o) : false;
	}
	
	public boolean equals(CityAegis a){
		return a.cityID.equals(cityID) && a.getX()==getX() && a.getY()==getY() && a.getZ()==getZ();
	}
}
