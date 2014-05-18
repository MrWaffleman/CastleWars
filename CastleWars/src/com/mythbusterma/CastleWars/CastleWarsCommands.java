package com.mythbusterma.CastleWars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythbusterma.CastleWars.Events.MatchEndEvent;
import com.mythbusterma.CastleWars.Events.MatchStartEvent;
import com.mythbusterma.CastleWars.Serializables.ArenaData.Orientation;
import com.sk89q.worldedit.FilenameException;

public class CastleWarsCommands implements CommandExecutor {

	private CastleWars parent;
	private Utils util;

	public CastleWarsCommands(CastleWars _parent) {
		parent = _parent;
		util = new Utils(parent);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		// TODO Auto-generated method stub

		if (args.length == 0) {
			sender.sendMessage("Usage: (question marks denote optional with selection), a: indicates alias\n"
					+ ChatColor.AQUA+ "/cw select "+ ChatColor.YELLOW+ "<name>:"+ ChatColor.WHITE+ " select an arena by name "+ ChatColor.GREEN+ "a: sel\n"
					+ ChatColor.AQUA+ "/cw createarena "+ ChatColor.YELLOW+ "<name>:"+ ChatColor.WHITE+ " create a new arena with the current WorldEdit Selection "+ ChatColor.GREEN+ "a:create, ca\n"
					+ ChatColor.AQUA+ "/cw setbluespawn "+ ChatColor.YELLOW+ "?<name>?:"+ ChatColor.WHITE+ " set the blue spawn of an arena, "+ ChatColor.GREEN+ "a: sbs\n"
					+ ChatColor.AQUA+ "/cw setredspawn "+ ChatColor.YELLOW+ "?<name>?:"+ ChatColor.WHITE+ " set the red spawn of an arena, "+ ChatColor.GREEN+ "a: srs\n"
					+ ChatColor.AQUA+ "/cw setlobbyspawn "+ ChatColor.YELLOW+ "?<name>?:"+ ChatColor.WHITE+ " set the lobby spawn point of an arena, "+ ChatColor.GREEN+ "a: sls\n"
					+ ChatColor.AQUA+ "/cw delete "+ ChatColor.YELLOW+ "?<name>?:"+ ChatColor.WHITE+ " delete an arena, "+ ChatColor.GREEN+ "a:\n"
					+ ChatColor.AQUA+ "/cw fsave "+ ChatColor.YELLOW+ "?<name>?:"+ ChatColor.WHITE+ " force saving of an arena to config(this is done automatically at server shutdown, "
					+ " will not save changes made, use /cw save for that)\n"
					+ ChatColor.AQUA+ "/cw setmaxplayers "+ ChatColor.YELLOW+ "?<name>? <amount>:"+ ChatColor.WHITE+ " set the maximum number of players for an arena "+ ChatColor.GREEN+ "a: maxp\n"
					+ ChatColor.AQUA+ "/cw setminplayers "+ ChatColor.YELLOW+ "?<name>? <amount>:"+ ChatColor.WHITE+ " set the minimum number of players for an arena "+ ChatColor.GREEN+ "a: minp\n"
					+ ChatColor.AQUA+ "/cw save "+ ChatColor.YELLOW+ "?<name>?:"+ ChatColor.WHITE+ " save changes made to an arena, e.g. breaking blocks or making signs "+ ChatColor.GREEN+ "a: s\n"
					+ ChatColor.AQUA+ "/cw restore "+ ChatColor.YELLOW+ "?<name>?:"+ ChatColor.WHITE+ " reset an arena to the last save state\n"
					+ ChatColor.AQUA+ "/cw tp "+ ChatColor.YELLOW+ "<name>:"+ ChatColor.WHITE+ " teleport to the lobby of an arena\n"
					+ ChatColor.AQUA+ "/cw orientation "+ ChatColor.YELLOW+ "?<name>? <orientation>:"+ ChatColor.WHITE
					+ " set the way a arena is divided, valid values are: northsouth, eastwest, or horizontal "+ ChatColor.GREEN+ "a: o\n"
					+ ChatColor.AQUA+ "/cw start "+ ChatColor.YELLOW+ "<name>:"+ ChatColor.WHITE+ " force the starting of a match\n"
					+ ChatColor.AQUA+ "/cw stop "+ ChatColor.YELLOW+ "<name>:"+ ChatColor.WHITE+ " force the ending of a match "+ ChatColor.GREEN+ "a: end\n"
					+ ChatColor.AQUA+ "/cw blue:"+ ChatColor.WHITE+ " when in lobby, change to the blue team\n"
					+ ChatColor.AQUA+ "/cw spectate:"+ ChatColor.WHITE+ " when in lobby, change to spectator\n"
					+ ChatColor.AQUA+ "/cw red:"+ ChatColor.WHITE+ " when in lobby, change to the red team\n"
					+ ChatColor.AQUA+ "/cw join "+ ChatColor.YELLOW+ "<name>:"+ ChatColor.WHITE+ " join a game, puts you in the lobby\n"
					+ ChatColor.AQUA+ "/cw leave:"+ ChatColor.WHITE+ " leave an arena\n"
					+ ChatColor.AQUA+ "/cw list:"+ ChatColor.WHITE+ " list all arenas in memory\n"
					+ ChatColor.AQUA+ "/cw balance:"+ ChatColor.WHITE+ " check the balance you have for this round "+ ChatColor.GREEN + "a: b"
					);
			return false;
		}

		parent.getServer().getConsoleSender()
				.sendMessage("Command issued: " + label + args[0]);

		if (label.toString().equalsIgnoreCase("cw")
				|| label.toString().equalsIgnoreCase("castlewars")) {

			if (args[0].equalsIgnoreCase("fsave")) {
				if (parent.getPlayerSelection(sender) != null) {
					parent.saveArena(parent.getPlayerSelection(sender));
					sender.sendMessage("Forced saving of arena "
							+ parent.getPlayerSelection(sender).getName()
							+ " to configuration files");
					parent.getArenasConfig().saveConfig();
					return true;
				} else {
					if (args.length >= 2) {
						Arena a = parent.getArenaByName(args[1]);
						if (a != null) {
							parent.saveArena(a);
							sender.sendMessage("Forced saving of arena "
									+ a.getName() + " to configuration files");
							parent.getArenasConfig().saveConfig();
							return true;
						} else {
							sender.sendMessage("No arena found by the name of "
									+ args[1]);
							return true;
						}
					} else {
						sender.sendMessage("No arena specified!");
						return false;
					}
				}

			}

			if (args[0].equalsIgnoreCase("tp")) {
				if (args.length > 1) {
					if (sender instanceof Player) {
						if (sender.hasPermission("castlewars.tp")) {
							Arena sel = null;
							for (Arena a : parent.getArenas()) {
								if (a.getName().equalsIgnoreCase(args[1])) {
									sel = a;
									break;
								}
							}
							if (sel != null) {
								((Player) sender).teleport(sel.getData()
										.getLobbySpawn());
								sender.sendMessage("You have been teleported to arena "
										+ sel.getName());
							} else {
								sender.sendMessage("No arena found by the name of "
										+ args[1]);
							}
						} else {
							sender.sendMessage("You don't have permission to use this command");
						}
					} else {
						sender.sendMessage("Only players can be teleported");
					}
				} else {
					sender.sendMessage("Not enough arguments");
				}
			}

			if (args[0].equalsIgnoreCase("createarena")
					|| args[0].equalsIgnoreCase("ca")
					|| args[0].equalsIgnoreCase("create")) {
				if (sender instanceof Player) {
					if (args.length < 2) {
						sender.sendMessage("No Name specified!");
						return false;
					}
					String[] arguments = { args[1] };
					if (sender.hasPermission("castlewars.create")) {
						util.createArena(sender, arguments);
					}
				}
			}

			if (args[0].equalsIgnoreCase("setBlueSpawn")
					|| args[0].equalsIgnoreCase("sbs")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("castlewars.setspawn")) {
						if (args.length > 1) {
							try {
								util.setBlueSpawn((Player) sender, args[1]);
							} catch (NullPointerException e) {
								sender.sendMessage("Arena by the name: "
										+ args[1] + " not found!");
								return false;
							}
							sender.sendMessage("You have set the Blue spawn for: "
									+ args[1]);
							return true;
						}

						try {
							util.setBlueSpawn((Player) sender, parent
									.getPlayerSelection(sender).getName());
						} catch (NullPointerException e) {
							sender.sendMessage("You don't have any arenas currently selected!");
							return false;
						}
						sender.sendMessage("You have set the Blue spawn for: "
								+ parent.getPlayerSelection(sender).getName());
						return true;
					}
				} else {
					sender.sendMessage("You can't do this from the console!");
				}
			}

			if (args[0].equalsIgnoreCase("setRedSpwan")
					|| args[0].equalsIgnoreCase("srs")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("castlewars.setspawn")) {
						if (args.length > 1) {
							try {
								util.setRedSpawn((Player) sender, args[1]);
							} catch (NullPointerException e) {
								sender.sendMessage("Arena by the name: "
										+ args[1] + " not found!");
								return false;
							}
							sender.sendMessage("You have set the Red spawn for: "
									+ args[1]);
							return true;
						}

						try {
							util.setRedSpawn((Player) sender, parent
									.getPlayerSelection(sender).getName());
						} catch (NullPointerException e) {
							sender.sendMessage("You don't have any arenas currently selected!");
							return false;
						}
						sender.sendMessage("You have set the Red spawn for: "
								+ parent.getPlayerSelection(sender).getName());
						return true;
					}
				} else {
					sender.sendMessage("You can't do this from the console!");
				}
			}

			if (args[0].equalsIgnoreCase("orientation")
					|| args[0].equalsIgnoreCase("o")) {
				if (sender.hasPermission("castlewars.orientation")) {
					if (args.length == 1) {
						sender.sendMessage("Not enough arguments!");
						return false;
					} else if (args.length == 2) {
						Arena sel = parent.getPlayerSelection(sender);
						if (sel != null) {
							Orientation o = null;
							if (args[1].startsWith("n")) {
								o = Orientation.NORTHSOUTH;
							} else if (args[1].startsWith("e")) {
								o = Orientation.EASTWEST;
							} else if (args[1].startsWith("h")) {
								o = Orientation.HORIZONTAL;
							}

							if (o != null) {
								util.setOrientation(sel.getName(), o, sender);
								sender.sendMessage("Orientation of "
										+ sel.getName() + " changed to "
										+ o.toString());
								return true;
							} else {
								sender.sendMessage("Orientation incorrect!");
								return true;
							}
						} else {
							sender.sendMessage("No arena selected!");
						}
					} else if (args.length >= 3) {
						Arena sel = parent.getArenaByName(args[1]);
						if (sel == null) {
							sender.sendMessage("Arena by name: " + args[1]
									+ " not found");
							return true;
						} else {
							Orientation o = null;
							if (args[2].startsWith("n")) {
								o = Orientation.NORTHSOUTH;
							} else if (args[2].startsWith("e")) {
								o = Orientation.EASTWEST;
							} else if (args[2].startsWith("h")) {
								o = Orientation.HORIZONTAL;
							}

							if (o != null) {
								util.setOrientation(sel.getName(), o, sender);
								sender.sendMessage("Orientation of "
										+ sel.getName() + " changed to "
										+ o.toString());
								return true;
							} else {
								sender.sendMessage("Orientation incorrect!");
								return true;
							}
						}
					}
				}
			}

			if (args[0].equalsIgnoreCase("setminplayers")
					|| args[0].equalsIgnoreCase("minp")) {
				if (sender.hasPermission("castlewars.setplayers")) {
					if (args.length == 1) {
						sender.sendMessage("Not enough arguments!");
						return false;
					} else if (args.length == 2) {
						Arena sel = parent.getPlayerSelection(sender);
						try {
							Integer.valueOf(args[1]);
						} catch (Exception e) {
							sender.sendMessage(args[1]
									+ " is not a valid number!");
							return true;
						}
						if (sel != null) {
							sel.getData().setMinPlayers(
									Integer.valueOf(args[1]));
							parent.logToFile(
									sender
											+ " set the min number of players on "
											+ sel.getName() + " to "
											+ Integer.valueOf(args[1]),
									"modify");
							return true;
						} else {
							sender.sendMessage("No arena selected!");
							return true;
						}
					} else if (args.length >= 3) {
						Arena sel = parent.getArenaByName(args[1]);
						if (sel == null) {
							sender.sendMessage("Arena by name: " + args[1]
									+ " not found");
							return true;
						} else {
							try {
								Integer.valueOf(args[2]);
							} catch (Exception e) {
								sender.sendMessage(args[2]
										+ " is not a valid number!");
								return true;
							}
							sel.getData().setMinPlayers(
									Integer.valueOf(args[2]));
							parent.logToFile(
									sender
											+ " set the min number of players on "
											+ sel.getName() + " to "
											+ Integer.valueOf(args[2]),
									"modify");
							return true;
						}
					}
				}
			}

			if (args[0].equalsIgnoreCase("setmaxplayers")
					|| args[0].equalsIgnoreCase("maxp")) {
				if (sender.hasPermission("castlewars.setplayers")) {
					if (args.length == 1) {
						sender.sendMessage("Not enough arguments!");
						return false;
					} else if (args.length == 2) {
						Arena sel = parent.getPlayerSelection(sender);
						try {
							Integer.valueOf(args[1]);
						} catch (Exception e) {
							sender.sendMessage(args[1]
									+ " is not a valid number!");
							return true;
						}
						if (sel != null) {
							sel.getData().setMaxPlayers(
									Integer.valueOf(args[1]));
							parent.logToFile(
									sender
											+ " set the max number of players on "
											+ sel.getName() + " to "
											+ Integer.valueOf(args[1]),
									"modify");
							return true;
						} else {
							sender.sendMessage("No arena selected!");
						}
					} else if (args.length >= 3) {
						Arena sel = parent.getArenaByName(args[1]);
						if (sel == null) {
							sender.sendMessage("Arena by name: " + args[1]
									+ " not found");
							return true;
						} else {
							try {
								Integer.valueOf(args[2]);
							} catch (Exception e) {
								sender.sendMessage(args[2]
										+ " is not a valid number!");
								return true;
							}
							sel.getData().setMaxPlayers(
									Integer.valueOf(args[2]));
							parent.logToFile(
									sender
											+ " set the max number of players on "
											+ sel.getName() + " to "
											+ Integer.valueOf(args[2]),
									"modify");
							return true;
						}
					}
				}
			}

			if (args[0].equalsIgnoreCase("setLobbySpawn")
					|| args[0].equalsIgnoreCase("sls")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("castlewars.setlobby")) {
						if (args.length > 1) {
							try {
								util.setLobbySpawn((Player) sender, args[1]);
							} catch (NullPointerException e) {
								sender.sendMessage("Arena by the name: "
										+ args[1] + " not found!");
								return false;
							}
							sender.sendMessage("You have set the lobby spawn for: "
									+ args[1]);
							return true;
						}

						try {
							util.setLobbySpawn((Player) sender, parent
									.getPlayerSelection(sender).getName());
						} catch (NullPointerException e) {
							sender.sendMessage("You don't have any arenas currently selected!");
							return false;
						}
						sender.sendMessage("You have set the lobby spawn for: "
								+ parent.getPlayerSelection(sender).getName());
						return true;
					}
				} else {
					sender.sendMessage("You can't do this from the console!");
				}
			}

			if (args[0].equalsIgnoreCase("select")
					|| args[0].equalsIgnoreCase("sel")) {
				if (args.length <= 1) {
					sender.sendMessage("Selection Cleared!");
					parent.setPlayerSelection(sender, null);
					return true;
				}

				Arena sel = parent.getArenaByName(args[1]);
				if (sel != null) {
					parent.setPlayerSelection(sender, sel);
					sender.sendMessage("Selected arena: " + sel.getName());
					return true;
				} else {
					sender.sendMessage("No arena found by that name!");
					return false;
				}

			}

			if (args[0].equalsIgnoreCase("restore")
					|| args[0].equalsIgnoreCase("res")) {
				if (args.length <= 1) {
					Arena sel = parent.getPlayerSelection(sender);
					if (sel == null) {
						sender.sendMessage("Not enough arguments!");
						return false;
					}
					sender.sendMessage("Restored selection: "
							+ parent.getPlayerSelection(sender));
					try {
						sel.restore();
					} catch (NullPointerException e) {
						// TODO Auto-generated catch block
						sender.sendMessage("error");
						e.printStackTrace();
					} catch (FilenameException e) {
						// TODO Auto-generated catch block
						sender.sendMessage("error");
						e.printStackTrace();
					}
					return true;
				}

				Arena sel = parent.getArenaByName(args[1]);
				if (sel != null) {
					try {
						sel.restore();
					} catch (NullPointerException | FilenameException e) {
						// TODO Auto-generated catch block
						sender.sendMessage("error");
						e.printStackTrace();
					}
					sender.sendMessage("Restored arena: " + sel.getName());
					return true;
				} else {
					sender.sendMessage("No arena found by that name!");
					return false;
				}

			}

			if (args[0].equalsIgnoreCase("save")
					|| args[0].equalsIgnoreCase("s")) {
				if (sender.hasPermission("castlewars.save")) {
					if (args.length <= 1) {
						Arena sel = parent.getPlayerSelection(sender);
						if (sel == null) {
							sender.sendMessage("Not enough arguments!");
							return false;
						}
						sender.sendMessage("Saved selection: "
								+ parent.getPlayerSelection(sender));
						sel.save();
						return true;
					}

					Arena sel = parent.getArenaByName(args[1]);
					if (sel != null) {
						sel.save();
						sender.sendMessage("Saved arena: " + sel.getName());
						return true;
					} else {
						sender.sendMessage("No arena found by that name!");
						return false;
					}
				}
				sender.sendMessage("You don't have permission to use this command");
				return false;

			}

			if (args[0].equalsIgnoreCase("kick")) {
				if (sender.hasPermission("castlewars.kick")) {
					if (args.length > 1) {
						if (args.length > 2) {
							for (Arena a : parent.getArenas()) {
								for (String s : a.getCurrentMatch()
										.getParticipants()) {
									if (s.equalsIgnoreCase(args[1])) {
										StringBuilder sb = new StringBuilder();
										for (int i = 2; i < args.length; i++) {
											sb.append(args[i] + " ");
										}
										a.getCurrentMatch()
												.kick(s, sender.getName(),
														sb.toString());
									}
								}
							}
						}
						for (Arena a : parent.getArenas()) {
							for (String s : a.getCurrentMatch()
									.getParticipants()) {
								if (s.equalsIgnoreCase(args[1])) {
									a.getCurrentMatch().kick(s,
											sender.getName(), null);
								}
							}
						}
					} else {
						sender.sendMessage("No player specified!");
					}
				} else {
					sender.sendMessage("You don't have permission to kick players");
				}
			}

			if (args[0].equalsIgnoreCase("list")) {

				sender.sendMessage("Currently registered arenas are:\n");
				for (Arena a : parent.getArenas()) {
					sender.sendMessage(a.getName() + '\n');
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("start")) {
				if (sender.hasPermission("castlewars.start")) {
					if (args.length > 1) {
						Arena a = parent.getArenaByName(args[1]);
						if (a == null) {
							sender.sendMessage("No arena by that name found.");
							return true;
						}
						Bukkit.getPluginManager().callEvent(
								new MatchStartEvent(a.getCurrentMatch()));
						sender.sendMessage("You force started the match on arena: "
								+ a.getName());
						return true;
					} else {
						sender.sendMessage("Not enough arguments!");
						return false;
					}
				}
			}

			if (args[0].equalsIgnoreCase("stop")
					|| args[0].equalsIgnoreCase("end")) {
				if (sender.hasPermission("castlewars.stop")) {
					if (args.length > 1) {
						Arena a = parent.getArenaByName(args[1]);
						if (a == null) {
							sender.sendMessage("No arena by that name found.");
							return true;
						}
						Bukkit.getPluginManager().callEvent(
								new MatchEndEvent(a.getCurrentMatch(), 2));
						sender.sendMessage("You force stopped the match on arena: "
								+ a.getName());
						return true;
					} else {
						sender.sendMessage("Not enough arguments!");
						return false;
					}
				}
			}

			if (args[0].equalsIgnoreCase("balance")
					|| args[0].equalsIgnoreCase("b")) {
				if (sender instanceof Player) {
					for (Arena a : parent.getArenas()) {
						for (String s : a.getCurrentMatch().getParticipants()) {
							if (s.equalsIgnoreCase(sender.getName())) {
								sender.sendMessage("You have "
										+ a.getCurrentMatch().getBalance(
												sender.getName())
										+ " "
										+ parent.getConfig().getString(
												"currencyunit"));
								return true;
							}
						}
					}
					sender.sendMessage("You only have a balance if you are in an arena!");
					return true;
				} else {
					sender.sendMessage("Only players can have balances");
					return true;
				}
			}

			/*
			 * 
			 * //////////////////////////////////////////////////////////////////
			 * ///////////////////////////
			 * 
			 * Normal player commands
			 */
			if (args[0].equalsIgnoreCase("join")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("You must be a player to join a match!");
					return false;
				}
				if (args.length < 2) {
					sender.sendMessage("Not enough arguments!");
					return false;
				}

				Arena a = parent.getArenaByName(args[1]);

				if (a == null) {
					sender.sendMessage("No arena by the name of: " + args[1]);
					return true;
				} else {
					int ret = a.getCurrentMatch().join(sender.getName());
					if (ret == 1) {
						sender.sendMessage("You will be teleported to the arena after "
								+ parent.getConfig().getInt("teleportdelay")
								+ " second/s");
					} else if (ret == -100) {
						sender.sendMessage("You are already in this arena!");
					} else if (ret == -101) {
						sender.sendMessage("You are already in an arena, leave before trying to join this one! (/cw leave)");
					} else if (ret == -102) {
						sender.sendMessage("The administrator has not finished setting up the arena, contact them about finishing it before trying to join");
					}
					return true;
				}
			}

			if (args[0].equalsIgnoreCase("red")
					|| args[0].equalsIgnoreCase("redteam")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("You must be a player to join a team!");
					return false;
				}

				Arena arena = null;

				for (Arena a : parent.getArenas()) {
					for (String s : a.getCurrentMatch().getParticipants()) {
						if (s.equalsIgnoreCase(sender.getName())) {
							arena = a;
						}
					}
				}

				if (arena == null) {
					sender.sendMessage("You must be in an arena to join a team!");
				} else {
					int ret = arena.getCurrentMatch().joinRed(sender.getName());
					if (ret == 1) {
						sender.sendMessage("You are scheduled to join the red team in the next match!");
					} else if (ret == -100) {
						sender.sendMessage("You are already playing in this arena!");
					} else if (ret == -101) {
						sender.sendMessage("You are already playing in an arena, leave before trying to join this one! (/cw leave)");
					}
					return true;
				}
			}

			if (args[0].equalsIgnoreCase("blue")
					|| args[0].equalsIgnoreCase("blueteam")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("You must be a player to join a team!");
					return false;
				}

				Arena arena = null;

				for (Arena a : parent.getArenas()) {
					for (String s : a.getCurrentMatch().getParticipants()) {
						if (s.equalsIgnoreCase(sender.getName())) {
							arena = a;
						}
					}
				}

				if (arena == null) {
					sender.sendMessage("You must be in an arena to join a team!");
				} else {
					int ret = arena.getCurrentMatch()
							.joinBlue(sender.getName());
					if (ret == 1) {
						sender.sendMessage("You are scheduled to join the blue team in the next match!");
					} else if (ret == -100) {
						sender.sendMessage("You are already playing in this arena!");
					} else if (ret == -101) {
						sender.sendMessage("You are already playing in an arena, leave before trying to join this one! (/cw leave)");
					}
					return true;
				}
			}

			if (args[0].equalsIgnoreCase("leave")
					|| args[0].equalsIgnoreCase("l")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("You must be a player to leave!");
					return false;
				}

				Arena arena = null;

				for (Arena a : parent.getArenas()) {
					for (String s : a.getCurrentMatch().getParticipants()) {
						if (s.equalsIgnoreCase(sender.getName())) {
							arena = a;
						}
					}
				}

				if (arena == null) {
					sender.sendMessage("You must be in an arena to leave!");
				} else {
					int ret = arena.getCurrentMatch().leave(sender.getName(),
							false);
					if (ret == 1) {
						sender.sendMessage("You left the arena!");
					} else if (ret == -100) {
						sender.sendMessage("You can't leave while still playing!");
					} else if (ret == -101) {
						sender.sendMessage("Internal Error while trying to leave");
					}
					return true;
				}
			}

			/* does not work yet */
			if (args[0].equalsIgnoreCase("rename")
					|| args[0].equalsIgnoreCase("re")) {
				return false;
				/*
				 * if(args.length >= 3) { Arena sel =
				 * parent.getArenaByName(args[1]); parent.deleteArena(sel); if
				 * (sel != null) { sender.sendMessage("Arena " +sel.getName()+
				 * " has been renamed " + args[2]); //Arena temp = new
				 * Arena(parent,args[2],sel.getData().get); return true; } }
				 * else if(parent.getPlayerSelection(sender)!= null &&
				 * args.length >= 2) { sender.sendMessage ("Arena " +
				 * parent.getPlayerSelection(sender).getName() +
				 * " has been renamed " +args[1]);
				 * parent.getPlayerSelection(sender).setName(args[1]); return
				 * true; } else if (args.length == 1) {
				 * sender.sendMessage("Too few arguments!"); return false; }
				 * else {
				 * sender.sendMessage("No arena selected, and none specified!");
				 * return false; }
				 */
			}

		}
		return false;
	}

}
