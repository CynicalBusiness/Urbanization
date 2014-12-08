package me.capit.urbanization;

import me.capit.urbanization.command.UrbanizationCommandParser.UrbanizationCommands;
import me.capit.urbanization.group.Group;
import me.capit.urbanization.group.Territory;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class CommandController implements CommandExecutor, Listener{
	public enum CResponse{
		SUCCESS, NO_RESPONSE, PLUGIN_ERROR,
		
		FAILED_PERMISSION, FAILED_FORMAT, FAILED_ARGUMENTS, FAILED_ARGUMENT_COUNT,
		FAILED_GROUP_PERMISSION, FAILED_NOT_POSSIBLE, FAILED_EMPTY_PAGE, FAILED_NOT_IN_TERRITORY,
		FAILED_FUNDS, FAILED_GROUP_FUNDS, FAILED_NOT_IN_GROUP, FAILED_OTHER_GROUP,
		FAILED_IN_USE, FAILED_UNKNOWN_GROUP, FAILED_PLAYER_NOT_FOUND, FAILED_IN_GROUP;
		
		public String getMessage(){
			switch (this){
			case SUCCESS:
				return "&f » Operation &aSucceeded&f!";
			case FAILED_PERMISSION:
				return "&c » Error: &rThe &oserver &rdoes not permit you to do that!";
			case FAILED_FORMAT:
				return "&c » Error: &rThe specified &3format &rwas invalid.";
			case FAILED_ARGUMENTS:
				return "&c » Error: &rInvalid or unknown &eargument &rspecified.";
			case FAILED_ARGUMENT_COUNT:
				return "&c » Error: &rIncorrect &eargument &rcount.";
			case FAILED_GROUP_PERMISSION:
				return "&c » Error: &rYour &dgroup &rdoes not permit you to do that!";
			case FAILED_NOT_POSSIBLE:
				return "&c » Error: &rYou can't do &othat&r!";
			case FAILED_EMPTY_PAGE:
				return "&c » Error: &rThat &opage &ris empty!";
			case FAILED_FUNDS:
				return "&c » Error: &rYou do not have enough &o"+Urbanization.ECONOMY.currencyNamePlural()+"&r!";
			case FAILED_GROUP_FUNDS:
				return "&c » Error: &rYour group does not have enough &o"+Urbanization.ECONOMY.currencyNamePlural()+"&r!";
			case FAILED_NOT_IN_GROUP:
				return "&c » Error: &rYou're not in a group!";
			case FAILED_OTHER_GROUP:
				return "&c » Error: &rA group does not permit you to do that there.";
			case FAILED_NOT_IN_TERRITORY:
				return "&c » Error: &rYou can only do that within your territory.";
			case FAILED_IN_USE:
				return "&c » Error: &rThat is already in use by another group.";
			case FAILED_UNKNOWN_GROUP:
				return "&c » Error: &rNo group by that name exists.";
			case FAILED_PLAYER_NOT_FOUND:
				return "&c » Error: &rThat player was not found.";
			case FAILED_IN_GROUP:
				return "&c » Error: &rYou cannot do that while in a group.";
			case NO_RESPONSE:
				return "";
			default:
				return "&f » The server did &cnot &rrespond.";
			}
		}
	}
	
	
	Urbanization plugin;
	public CommandController(Urbanization plugin){
		this.plugin=plugin;
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onBlockBreakEvent(BlockBreakEvent e){
		if (!e.isCancelled()){
			Chunk c = e.getBlock().getLocation().getChunk();
			Territory t = Urbanization.getTerritory(c.getX(), c.getZ());
			if (t!=null){
				if (!t.playerCanBreak(e.getPlayer().getUniqueId())){
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CResponse.FAILED_OTHER_GROUP.getMessage()));
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onBlockPlaceEvent(BlockPlaceEvent e){
		if (!e.isCancelled()){
			Chunk c = e.getBlock().getLocation().getChunk();
			Territory t = Urbanization.getTerritory(c.getX(), c.getZ());
			if (t!=null){
				if (!t.playerCanPlace(e.getPlayer().getUniqueId())){
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CResponse.FAILED_OTHER_GROUP.getMessage()));
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		if (!e.isCancelled()){
			Action a = e.getAction();
			if (a==Action.RIGHT_CLICK_BLOCK){
				Chunk c = e.getClickedBlock().getLocation().getChunk();
				Territory t = Urbanization.getTerritory(c.getX(), c.getZ());
				if (Urbanization.CONTROLLER.getGroupData().getStringList("block_containers").contains(e.getClickedBlock().getType().toString())){
					if (!t.playerCanAccessContainers(e.getPlayer().getUniqueId())){
						e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CResponse.FAILED_OTHER_GROUP.getMessage()));
						e.setCancelled(true);
					}
				} else if (Urbanization.CONTROLLER.getGroupData().getStringList("block_interact").contains(e.getClickedBlock().getType().toString()) || 
						Urbanization.CONTROLLER.getGroupData().getStringList("block_right_click").contains(
								e.getPlayer().getItemInHand().getType().toString())) {
					if (!t.playerCanUse(e.getPlayer().getUniqueId())){
						e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CResponse.FAILED_OTHER_GROUP.getMessage()));
						e.setCancelled(true);
					}
				}
			} else if (a==Action.RIGHT_CLICK_AIR){
				Chunk c = e.getPlayer().getLocation().getChunk();
				Territory t = Urbanization.getTerritory(c.getX(), c.getZ());
				if (Urbanization.CONTROLLER.getGroupData().getStringList("block_right_click").contains(e.getPlayer().getItemInHand().getType().toString())){
					if (!t.playerCanUse(e.getPlayer().getUniqueId())){
						e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CResponse.FAILED_OTHER_GROUP.getMessage()));
						e.setCancelled(true);
					}
				}
			} else if (a==Action.LEFT_CLICK_AIR || a==Action.LEFT_CLICK_BLOCK){
				Chunk c = e.getPlayer().getLocation().getChunk();
				Territory t = Urbanization.getTerritory(c.getX(), c.getZ());
				if (Urbanization.CONTROLLER.getGroupData().getStringList("block_interact").contains(e.getClickedBlock().getType().toString()) || 
						Urbanization.CONTROLLER.getGroupData().getStringList("block_left_click").contains(
								e.getPlayer().getItemInHand().getType().toString())){
					if (!t.playerCanUse(e.getPlayer().getUniqueId())){
						e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CResponse.FAILED_OTHER_GROUP.getMessage()));
						e.setCancelled(true);
					}
				}
			} else {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		Chunk c = e.getPlayer().getLocation().getChunk();
		Group g = Urbanization.getGroupByTerritory(c.getX(), c.getZ());
		String ID = g!=null ? g.ID.toString() : "undefined";
		if (!Urbanization.trackedPlayers.containsKey(e.getPlayer().getUniqueId())
				|| !Urbanization.trackedPlayers.get(e.getPlayer().getUniqueId()).equals(ID)){
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', 
					g!=null ? "&e ♦ &f"+g.name()+" &7- &r"+g.desc() : "&e ♦ &oUnclaimed territory."));
			Urbanization.trackedPlayers.put(e.getPlayer().getUniqueId(), ID);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
		if (c.getName().equalsIgnoreCase("urbanization")){
			if (args.length>0){
				String[] newArgs = new String[args.length-1];
				for (int i=1;i<args.length;i++) newArgs[i-1] = args[i];
				s.sendMessage(ChatColor.translateAlternateColorCodes('&', UrbanizationCommands.executeCommand(args[0], newArgs, s).getMessage()));
				return true;
			} else {
				return onCommand(s,c,l,new String[]{"help"});
			}
		}
		return false;
	}

}
