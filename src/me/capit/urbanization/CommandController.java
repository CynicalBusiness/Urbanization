package me.capit.urbanization;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import me.capit.urbanization.command.UrbanizationCommandParser.UrbanizationCommands;
import me.capit.urbanization.group.Group;
import me.capit.urbanization.group.GroupRelation;
import me.capit.urbanization.group.Territory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
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
	
	CommandInfo[][] help = new CommandInfo[][]{
			new CommandInfo[]{
					new CommandInfo("help [page=1]", "Displays [page] of the Urbanization help."),
					new CommandInfo("create <name>", "Creates a new group by <name>."),
					new CommandInfo("claim [rank="+Group.subgroupSize+"]", "Claims the chunk you're standing in, optionally for [rank]."),
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
					
				} else if (sc.equalsIgnoreCase("claim")){
					
				} else if (sc.equalsIgnoreCase("unclaim")){
					if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.claim")){
						Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
						Chunk chunk = p.getLocation().getChunk();
						if (g!=null){
							if (g.playerHasPermission(p.getUniqueId(), "territory.unclaim")){
								if (g.territoryBelongsToGroup(chunk.getX(), chunk.getZ())){
									g.removeTerritory(chunk.getX(), chunk.getZ());
									if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
										g.funds(g.funds()+Urbanization.CONTROLLER.getGroupData().getDouble("econ_reward_unclaim"));
									try {
										g.save();
									} catch (IOException e) {
										e.printStackTrace();
									}
									return CResponse.SUCCESS;
								} else {
									return CResponse.FAILED_NOT_IN_TERRITORY;
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
				} else if (sc.equalsIgnoreCase("modify")){
					if (args.length>=3){
						if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.modify")){
							String ssc = args[1];
							Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
							if (g!=null){
								if (ssc.equalsIgnoreCase("name")){
									if (!Urbanization.groupNameInUse(args[2])){
										if (args[2].matches(Urbanization.CONTROLLER.getGroupData().getString("name_pattern"))){
											if (g.playerHasPermission(p.getUniqueId(), "group.modify.name")){
												if (g.hasFunds(Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_name"))){
													g.name(args[2]);
													if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
														g.funds(g.funds()-Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_name"));
													return CResponse.SUCCESS;
												} else {
													return CResponse.FAILED_GROUP_FUNDS;
												}
											} else {
												return CResponse.FAILED_GROUP_PERMISSION;
											}
										} else {
											return CResponse.FAILED_FORMAT;
										} 
									} else {
										return CResponse.FAILED_IN_USE;
									}
								} else if (ssc.equalsIgnoreCase("tag")){
									if (args[2].matches(Urbanization.CONTROLLER.getGroupData().getString("tag_pattern"))){
										if (g.playerHasPermission(p.getUniqueId(), "group.modify.tag")){
											if (g.hasFunds(Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_tag"))){
												g.tag(args[2]);
												if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
													g.funds(g.funds()-Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_tag"));
												return CResponse.SUCCESS;
											} else {
												return CResponse.FAILED_GROUP_FUNDS;
											}
										} else {
											return CResponse.FAILED_GROUP_PERMISSION;
										}
									} else {
										return CResponse.FAILED_FORMAT;
									}
								} else if (ssc.equalsIgnoreCase("desc")){
									if (g.playerHasPermission(p.getUniqueId(), "group.modify.desc")){
										if (g.hasFunds(Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_desc"))){
											String desc = "";
											for (int i=2; i<args.length; i++){desc+=" "+args[i];}
											g.desc(desc.substring(1));
											if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
												g.funds(g.funds()-Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_desc"));
											return CResponse.SUCCESS;
										} else {
											return CResponse.FAILED_GROUP_FUNDS;
										}
									} else {
										return CResponse.FAILED_GROUP_PERMISSION;
									}
								} else if (ssc.equalsIgnoreCase("motd")){
									if (g.playerHasPermission(p.getUniqueId(), "group.modify.motd")){
										if (g.hasFunds(Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_motd"))){
											String motd = "";
											for (int i=2; i<args.length; i++){motd+=" "+args[i];}
											g.motd(motd.substring(1));
											if (Urbanization.CONTROLLER.getGlobals().getBoolean("enable_economy"))
												g.funds(g.funds()-Urbanization.CONTROLLER.getGroupData().getDouble("econ_cost_motd"));
											return CResponse.SUCCESS;
										} else {
											return CResponse.FAILED_GROUP_FUNDS;
										}
									} else {
										return CResponse.FAILED_GROUP_PERMISSION;
									}
								} else {
									return CResponse.FAILED_ARGUMENTS;
								}
							} else {
								return CResponse.FAILED_NOT_IN_GROUP;
							}
						} else {
							return CResponse.FAILED_PERMISSION;
						}
					} else {
						return CResponse.FAILED_ARGUMENT_COUNT;
					}
				} else if (sc.equalsIgnoreCase("info")){
					if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.info")){
						Group g = args.length>1 ? Urbanization.getGroupByName(args[1]) : Urbanization.getGroupByPlayer(p.getUniqueId());
						if (g!=null){
							String playersOnline = ""; String playersTotal = ""; String relations = "";
							for (UUID id : g.getPlayers()){
								OfflinePlayer op = Bukkit.getOfflinePlayer(id);
								playersTotal+=", "+g.getPlayerPrefix(id)+op.getName();
								if (op.isOnline()) playersOnline+=", "+g.getPlayerPrefix(id)+op.getName();
							}
							Map<String, GroupRelation> rels = g.getRelatedGroups();
							for (String id : rels.keySet()){
								relations+=ChatColor.WHITE+", "+Urbanization.getGroupByID(UUID.fromString(id)).name()+":"
										+rels.get(id).getColor()+rels.get(id).toString();
							}
							s.sendMessage(ChatColor.translateAlternateColorCodes('&', 
									"&e------ &7Group &3"+g.name()+" &f(&7"+g.tag()+"&f) &e--------------------------"));
							s.sendMessage(new String[]{
									ChatColor.GOLD+"DESC: "+ChatColor.WHITE+g.desc(),
									ChatColor.GOLD+"MOTD: "+ChatColor.WHITE+g.motd(),
									ChatColor.GOLD+"ONLINE: "+ChatColor.WHITE+playersOnline.substring(1),
									ChatColor.GOLD+"PLAYERS: "+ChatColor.WHITE+"("+g.getPlayers().size()+") "+playersTotal.substring(1),
									ChatColor.GOLD+"CLAIMS: "+ChatColor.WHITE+g.getTerritory().size(),
									ChatColor.GOLD+"RELATIONS: "+ChatColor.WHITE+relations,
									ChatColor.GOLD+"CAN TELE: "+(g.playerCanTele(p.getUniqueId()) ? ChatColor.GREEN+"Yes" : ChatColor.RED+"No")
							});
							return CResponse.NO_RESPONSE;
						} else {
							return CResponse.FAILED_UNKNOWN_GROUP;
						}
					} else {
						return CResponse.FAILED_PERMISSION;
					}
				} else if (sc.equalsIgnoreCase("home")){
					if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.home")){
						Group g = args.length==1 ? Urbanization.getGroupByPlayer(p.getUniqueId()) : Urbanization.getGroupByName(args[1]);
						if (g!=null){
							if (g.playerCanTele(p.getUniqueId())){
								if (g.getHome()!=null){
									p.teleport(g.getHome());
									p.sendMessage(ChatColor.YELLOW+" ♦ "+ChatColor.GRAY+g.motd());
									return CResponse.NO_RESPONSE;
								} else {
									return CResponse.FAILED_NOT_POSSIBLE;
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
				} else if (sc.equalsIgnoreCase("sethome")){
					if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.sethome")){
						Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
						if (g!=null){
							Location targ = p.getLocation();
							if (g.territoryBelongsToGroup(targ.getChunk().getX(), targ.getChunk().getZ())){
								if (g.playerHasPermission(p.getUniqueId(), "group.sethome")){
									g.setHome(targ);
									return CResponse.SUCCESS;
								} else {
									return CResponse.FAILED_GROUP_PERMISSION;
								}
							} else {
								return CResponse.FAILED_NOT_IN_TERRITORY;
							}
						} else {
							return CResponse.FAILED_NOT_IN_GROUP;
						}
					} else {
						return CResponse.FAILED_PERMISSION;
					}
				} else if (sc.equalsIgnoreCase("invite")){
					if (args.length==2){
						if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.invite")){
							Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
							if (g!=null){
								if (g.playerHasPermission(p.getUniqueId(), "group.invite")){
									@SuppressWarnings("deprecation")
									OfflinePlayer targ = Bukkit.getOfflinePlayer(args[1]);
									if (targ!=null){
										if (!Urbanization.invites.containsKey(p.getUniqueId()) || 
												!Urbanization.invites.get(p.getUniqueId()).equals(g.ID)){
											Urbanization.invites.put(targ.getUniqueId(), g.ID);
											p.sendMessage(ChatColor.YELLOW+" ♦ "+ChatColor.GRAY+" Invite sent to "
													+ChatColor.AQUA+targ.getName()+ChatColor.GRAY+".");
										} else {
											Urbanization.invites.remove(targ.getUniqueId());
											p.sendMessage(ChatColor.YELLOW+" ♦ "+ChatColor.GRAY+" Invite revoked from "
													+ChatColor.AQUA+targ.getName()+ChatColor.GRAY+".");
										}
										return CResponse.NO_RESPONSE;
									} else {
										return CResponse.FAILED_PLAYER_NOT_FOUND;
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
					} else {
						return CResponse.FAILED_ARGUMENT_COUNT;
					}
				} else if (sc.equalsIgnoreCase("join")){
					if (args.length==2){
						if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.join")){
							if (Urbanization.getGroupByPlayer(p.getUniqueId())==null){
								Group targ = Urbanization.getGroupByName(args[2]);
								if (targ!=null){
									if (Urbanization.invites.containsKey(p.getUniqueId())){
										Urbanization.invites.remove(p.getUniqueId());
										targ.addPlayer(p.getUniqueId());
										p.sendMessage(ChatColor.YELLOW+" ♦ "+ChatColor.GRAY+" Successfully joined "
												+ChatColor.AQUA+targ.name()+ChatColor.GRAY+".");
										return CResponse.NO_RESPONSE;
									} else {
										return CResponse.FAILED_OTHER_GROUP;
									}
								} else {
									return CResponse.FAILED_UNKNOWN_GROUP;
								}
							} else {
								return CResponse.FAILED_IN_GROUP;
							}
						} else {
							return CResponse.FAILED_PERMISSION;
						}
					} else {
						return CResponse.FAILED_ARGUMENT_COUNT;
					}
				} else if (sc.equalsIgnoreCase("leave")){
					if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.group.leave")){
						Group g = Urbanization.getGroupByPlayer(p.getUniqueId());
						if (g!=null){
							if (!g.getGroup(0).getPlayers().get(0).equals(p.getUniqueId())){
								g.removePlayer(p.getUniqueId());
							} else {
								return CResponse.FAILED_NOT_POSSIBLE;
							}
						} else {
							return CResponse.FAILED_NOT_IN_GROUP;
						}
					} else {
						return CResponse.FAILED_PERMISSION;
					}
				} else if (sc.equalsIgnoreCase("help")){
					if (s.hasPermission("urbanization.kit.player") || s.hasPermission("urbanization.help")){
						echoHelp(p);
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
