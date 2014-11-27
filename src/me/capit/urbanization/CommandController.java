package me.capit.urbanization;

import java.io.IOException;

import me.capit.urbanization.group.Group;
import me.capit.urbanization.group.Territory;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
		FAILED_GROUP_PERMISSION, FAILED_NOT_POSSIBLE, FAILED_EMPTY_PAGE,
		FAILED_FUNDS, FAILED_GROUP_FUNDS, FAILED_NOT_IN_GROUP, FAILED_OTHER_GROUP;
		
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
			default:
				return "&f » The server did &cnot &rrespond.";
			}
		}
	}
	public class CommandInfo{
		private String cmd,desc;
		public CommandInfo(String cmd, String desc){
			this.cmd=cmd;this.desc=desc;
		}
		public String toString(){
			return "&e/u "+cmd+"&7 - "+desc;
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
						Urbanization.CONTROLLER.getGroupData().getStringList("block_right_click").contains(e.getPlayer().getItemInHand().getType().toString())) {
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
						Urbanization.CONTROLLER.getGroupData().getStringList("block_left_click").contains(e.getPlayer().getItemInHand().getType().toString())){
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
		String ID = g!=null ? g.ID : "undefined";
		if (!Urbanization.trackedPlayers.containsKey(e.getPlayer().getUniqueId())
				|| !Urbanization.trackedPlayers.get(e.getPlayer().getUniqueId()).equals(ID)){
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', 
					g!=null ? "&e » &f"+g.name()+" &7- &r"+g.desc() : "&e » &fUnclaimed &eterritory."));
			Urbanization.trackedPlayers.put(e.getPlayer().getUniqueId(), ID);
		}
	}
	
	CommandInfo[][] help = new CommandInfo[][]{
			new CommandInfo[]{
					new CommandInfo("help [page=1]", "Displays [page] of the Urbanization help."),
					new CommandInfo("create <name>", "Creates a new group by <name>."),
					new CommandInfo("claim [rank="+Group.subgroupSize+"] [apothem=0]", "Claims the chunk(s) you're standing in, "
							+ "optionally with [apothem] under [rank]."),
			},
			new CommandInfo[]{
					new CommandInfo("subgroup <group> addperm|takeperm <permission>", "Adds or takes <permission> from <group>."),
					new CommandInfo("&cdisband", "Disbands your group."),
			},
	};
	public void echoHelp(CommandSender s){echoHelp(s,1);}
	public void echoHelp(CommandSender s, int page){
		if (page<=help.length){
			CommandInfo[] pg = help[page-1];
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', 
					"&e------ &7Urbanization &f- Page &3"+page+" &fof &3"+help.length+" &e--------------------"));
			for (CommandInfo ci : pg){
				s.sendMessage(ChatColor.translateAlternateColorCodes('&', ci.toString()));
			}
		} else {
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', CResponse.FAILED_EMPTY_PAGE.getMessage()));
		}
	}
	
	public CResponse executeCMD(CommandSender s, Command c, String l, String[] args){
		if (c.getName().equalsIgnoreCase("urbanization")){
			if (args.length>0){
				String sc = args[0];
				Player p = (Player) s;
				if (sc.equalsIgnoreCase("create") && args.length==2){
					if (s.hasPermission("urbanizations.kit.player") || s.hasPermission("urbanization.group.create")){
						if (args[1].matches(Urbanization.CONTROLLER.getGroupData().getString("name_pattern"))){
							if (Urbanization.getGroupByPlayer(p.getUniqueId())==null){
								if (!Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy") 
										|| Urbanization.ECONOMY.has((OfflinePlayer) s, Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_create"))){
									String name = args[1];
									if (name.matches(Urbanization.CONTROLLER.getGroupData().getString("name_pattern"))){
										try {
											if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
												Urbanization.ECONOMY.withdrawPlayer((OfflinePlayer) s, 
														Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_create"));
											Group g = Group.createNewGroup(name, ((Player) s).getUniqueId());
											Urbanization.groups.add(g);
											return CResponse.SUCCESS;
										} catch (IOException | NullPointerException e) {
											e.printStackTrace();
											return CResponse.PLUGIN_ERROR;
										}
									} else {
										return CResponse.FAILED_FORMAT;
									}
								} else {
									return CResponse.FAILED_FUNDS;
								}
							} else {
								return CResponse.FAILED_NOT_POSSIBLE;
							}
						} else {
							return CResponse.FAILED_FORMAT;
						}
					} else {
						return CResponse.FAILED_PERMISSION;
					}
				} else if (sc.equalsIgnoreCase("claim")){
					Urbanization.LOGGER.fine(p.getName()+" issued 'claim' command.");
					if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.claim")){
						Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
						if (g!=null){
							if (g.playerHasPermission(p.getUniqueId(), "territory.claim")){
								if (!Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy") 
										|| g.hasFunds(Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_claim"))){
									Group atPos = Urbanization.getGroupByTerritory(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ());
									if (atPos==null){
										int rank = args.length>=2 ? Integer.parseInt(args[1]) : Group.subgroupSize;
										Territory t = new Territory(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ(),g.ID,rank);
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
												"&e » Registered new territory at &c"+t.x+"&e,&c"+t.z+"&e for &3"+g.name()+"&f:&c"+rank+"&e."));
										g.addTerritory(t);
										if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
											g.funds(g.funds()-Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_claim"));
										return CResponse.SUCCESS;
									} else {
										return CResponse.FAILED_OTHER_GROUP;
									}
								} else {
									return CResponse.FAILED_FUNDS;
								}
							} else {
								return CResponse.FAILED_GROUP_PERMISSION;
							}
						} else {
							return CResponse.FAILED_NOT_IN_GROUP;
						}
					} else {
						return CResponse.FAILED_PERMISSION;
					}
				} else if (sc.equalsIgnoreCase("disband")){
					if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.disband")){
						Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
						if (g!=null){
							if (g.playerHasPermission(p.getUniqueId(), "group.disband")){
								Urbanization.deleteGroupByID(g.ID);
								return CResponse.SUCCESS;
							} else {
								return CResponse.FAILED_GROUP_PERMISSION;
							}
						} else {
							return CResponse.FAILED_NOT_IN_GROUP;
						}
					} else {
						return CResponse.FAILED_PERMISSION;
					}
				} else {
					return CResponse.FAILED_ARGUMENTS;
				}
			} else {
				return executeCMD(s, c, l, new String[]{"help"});
			}
		}
		return CResponse.NO_RESPONSE;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', executeCMD(s,c,l,args).getMessage()));
		return true;
	}

}
