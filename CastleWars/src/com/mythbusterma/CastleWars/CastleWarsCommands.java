package com.mythbusterma.CastleWars;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import com.mythbusterma.CastleWars.Serializables.ArenaData.Orientation;
import com.sk89q.worldedit.FilenameException;

public class CastleWarsCommands implements CommandExecutor {
	
	private CastleWars parent;
	private Utils util;

	public CastleWarsCommands (CastleWars _parent) {
		parent = _parent;
		util = new Utils(parent);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		
		if(args.length == 0) {
			sender.sendMessage("Usage: (question marks denote optional with selection), a: indicates alias\n"
					+ "/cw select <name>: select an arena by name a: sel\n"
					+ "/cw createarena <name>: create a new arena with the current WorldEdit Selection a:create, ca\n"
					+ "/cw setbluespawn ?<name>?: set the blue spawn of an arena, a: sbs\n"
					+ "/cw setredspawn ?<name>?: set the red spawn of an arena, a: srs\n"
					+ "/cw setlobbyspawn ?<name>?: set the lobby spawn point of an arena, a: sls\n"
					+ "/cw delete ?<name>?: delete an arena, a:\n"
					+ "/cw fsave ?<name>?: force saving of an arena (this is done automatically at server shutdown, "
					+ " will not save changes made, use /cw save for that)\n"
					+ "/cw list: list all arenas in memory\n"
					+ "/cw save: save changes made to an arena, e.g. breaking blocks or making signs a: s\n"
					+ "/cw restore ?<name>?: reset an arena to the last save state\n"
					+ "/cw blue: when in lobby, change to the blue team\n"
					+ "/cw spectate: when in lobby, change to spectator\n"
					+ "/cw red: when in lobby, change to the red team\n"
					+ "/cw join <name>: join a game, puts you in the lobby\n"
					+ "/cw orientation ?<name>? <orientation>: set the way a arena is divided, valid values are: northsouth, eastwest, or horizontal a: o");
			return false;
		}
		
		parent.getServer().getConsoleSender().sendMessage("Command issued: " +label + args[0]);
		
		if(label.toString().equalsIgnoreCase("cw") || label.toString().equalsIgnoreCase("castlewars")) {
		
			if(args[0].equalsIgnoreCase("createarena") || args[0].equalsIgnoreCase("ca") || args[0].equalsIgnoreCase("create")) {
				if(sender instanceof Player) {
					if (args.length <2) {
						sender.sendMessage("No Name specified!");
						return false;
					}
					String [] arguments = {args[1]};
					if (sender.hasPermission("castlewars.create")) {
						util.createArena(sender, arguments);
					}
				}
			}
			
			if(args[0].equalsIgnoreCase("setBlueSpawn") || args[0].equalsIgnoreCase("sbs")) {
				if (sender instanceof Player) {
					if(sender.hasPermission("castlewars.setspawn")) {
						if(args.length > 1) {
							try {
								util.setBlueSpawn ((Player)sender,args[1]);
							}
							catch (NullPointerException e) {
								sender.sendMessage("Arena by the name: " + args[1] + " not found!");
								return false;
							}
							sender.sendMessage("You have set the Blue spawn for: "  + args[1]);
							return true;
						}
						
						try {
							util.setBlueSpawn((Player)sender,parent.getPlayerSelection(sender).getName());
						}
						catch (NullPointerException e) {
							sender.sendMessage("You don't have any arenas currently selected!");
							return false;
						}
						sender.sendMessage("You have set the Blue spawn for: " 
								+ parent.getPlayerSelection(sender).getName());
						return true;
					}
				}
				else {
					sender.sendMessage("You can't do this from the console!");
				}
			}
			
			if(args[0].equalsIgnoreCase("setRedSpwan") || args[0].equalsIgnoreCase("srs")) {
				if (sender instanceof Player) {
					if(sender.hasPermission("castlewars.setspawn")) {
						if(args.length > 1) {
							try {
								util.setRedSpawn ((Player)sender,args[1]);
							}
							catch (NullPointerException e) {
								sender.sendMessage("Arena by the name: " + args[1] + " not found!");
								return false;
							}
							sender.sendMessage("You have set the Red spawn for: "  + args[1]);
							return true;
						}
						
						try {
							util.setRedSpawn((Player)sender,parent.getPlayerSelection(sender).getName());
						}
						catch (NullPointerException e) {
							sender.sendMessage("You don't have any arenas currently selected!");
							return false;
						}
						sender.sendMessage("You have set the Red spawn for: " 
								+ parent.getPlayerSelection(sender).getName());
						return true;
					}
				}
				else {
					sender.sendMessage("You can't do this from the console!");
				}
			}
			
			if(args[0].equalsIgnoreCase("orientation") || args[0].equalsIgnoreCase("o")) {
				if(sender.hasPermission("castlewars.orientation")) {
					if (args.length == 1) {
						sender.sendMessage("Not enough arguments!");
						return false;
					}
					else if (args.length == 2) {
						Arena sel = parent.getPlayerSelection(sender);
						if (sel != null) {
							Orientation o = null;
							if (args[1].startsWith("n")) {
								o = Orientation.NORTHSOUTH;
							}
							else if (args[1].startsWith("e")) {
								o = Orientation.EASTWEST;
							}
							else if (args[1].startsWith("h")) {
								o = Orientation.HORIZONTAL;
							}
							
							if (o != null) {
								util.setOrientation(sel.getName(), o);
								sender.sendMessage("Orientation of " +sel.getName() + " changed to " + o.toString());
								return true;
							}
							else {
								sender.sendMessage("Orientation incorrect!");
								return true;
							}
						}
						else {
							sender.sendMessage("No arena selected!");
						}
					}
					else if (args.length >=3) {
						Arena sel = parent.getArenaByName(args[1]);
						if (sel == null) {
							sender.sendMessage("Arena by name: " + args[1] + " not found");
							return true;
						}
						else {
							Orientation o = null;
							if (args[2].startsWith("n")) {
								o = Orientation.NORTHSOUTH;
							}
							else if (args[2].startsWith("e")) {
								o = Orientation.EASTWEST;
							}
							else if (args[2].startsWith("h")) {
								o = Orientation.HORIZONTAL;
							}
							
							if (o != null) {
								util.setOrientation(sel.getName(), o);
								sender.sendMessage("Orientation of " +sel.getName() + " changed to " + o.toString());
								return true;
							}
							else {
								sender.sendMessage("Orientation incorrect!");
								return true;
							}
						}
					}
 				}
			}
			
			if(args[0].equalsIgnoreCase("setLobbySpawn") || args[0].equalsIgnoreCase("sls")) {
				if (sender instanceof Player) {
					if(sender.hasPermission("castlewars.setlobby")) {
						if(args.length > 1) {
							try {
								util.setLobbySpawn ((Player)sender,args[1]);
							}
							catch (NullPointerException e) {
								sender.sendMessage("Arena by the name: " + args[1] + " not found!");
								return false;
							}
							sender.sendMessage("You have set the lobby spawn for: "  + args[1]);
							return true;
						}
						
						try {
							util.setLobbySpawn((Player)sender,parent.getPlayerSelection(sender).getName());
						}
						catch (NullPointerException e) {
							sender.sendMessage("You don't have any arenas currently selected!");
							return false;
						}
						sender.sendMessage("You have set the lobby spawn for: " 
								+ parent.getPlayerSelection(sender).getName());
						return true;
					}
				}
				else {
					sender.sendMessage("You can't do this from the console!");
				}
			}
			
			if(args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("sel")) {
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
				}
				else {
					sender.sendMessage("No arena found by that name!");
					return false;
				}
				
			}
			
			
			if(args[0].equalsIgnoreCase("restore") || args[0].equalsIgnoreCase("res")) {
				if (args.length <= 1) {
					Arena sel = parent.getPlayerSelection(sender);
					if (sel == null) {
						sender.sendMessage("Not enough arguments!");
						return false;
					}
					sender.sendMessage("Restored selection: "+parent.getPlayerSelection(sender));
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
				}
				else {
					sender.sendMessage("No arena found by that name!");
					return false;
				}
				
			}
			

			if(args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("s")) {
				if (args.length <= 1) {
					Arena sel = parent.getPlayerSelection(sender);
					if (sel == null) {
						sender.sendMessage("Not enough arguments!");
						return false;
					}
					sender.sendMessage("Saved selection: "+parent.getPlayerSelection(sender));
					sel.save();
					return true;
				}
				
				Arena sel = parent.getArenaByName(args[1]);
				if (sel != null) {
					sel.save();
					sender.sendMessage("Saved arena: " + sel.getName());
					return true;
				}
				else {
					sender.sendMessage("No arena found by that name!");
					return false;
				}
				
			}
			
			if(args[0].equalsIgnoreCase("list")) {
				
				sender.sendMessage("Currently registered arenas are:\n");
				for (Arena a : parent.getArenas()) {
					sender.sendMessage(a.getName() + '\n');
				}
				return true;
			}
			
			
			/*
			 *
			 /////////////////////////////////////////////////////////////////////////////////////////////
			 * 
			 * Normal player commands
			 */
			if (args[0].equalsIgnoreCase("join")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("You must be a player to join a match!");
					return false;
				}
				if(args.length < 2) {
					sender.sendMessage("Not enough arguments!");
					return false;
				}
				
				Arena a = parent.getArenaByName(args[1]);
				
				if (a == null) {
					sender.sendMessage("No arena by the name of: "  + args[1]);
					return true;
				}
				else {
					int ret = a.getCurrentMatch().join(sender.getName());
					if (ret == 1) {
						sender.sendMessage("You will be teleported to the arena after " + parent.getConfig().getInt("teleportdelay") + " second/s");
					}
					else if (ret == -100) {
						sender.sendMessage("You are already in this arena!");
					}
					else if (ret == -101) {
						sender.sendMessage("You are already in an arena, leave before trying to join this one! (/cw leave)");
					}
					else if (ret == -102) {
						sender.sendMessage("The administrator has not finished setting up the arena, contact them about finishing it before trying to join");
					}
					return true;
				}
			}
			
			if (args[0].equalsIgnoreCase("red") || args[0].equalsIgnoreCase("redteam")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("You must be a player to join a team!");
					return false;
				}
				
				Arena arena = null;
				
				for (Arena a : parent.getArenas()) {
					for (String s : a.getCurrentMatch().getParticipants()) {
						if(s.equalsIgnoreCase(sender.getName())) {
							arena = a;
						}
					}
				}
				
				if (arena == null) {
					sender.sendMessage("You must be in an arena to join a team!");
				}
				else {
					int ret = arena.getCurrentMatch().joinRed(sender.getName());
					if (ret == 1) {
						sender.sendMessage("You are scheduled to join the red team in the next match!");
					}
					else if (ret == -100) {
						sender.sendMessage("You are already playing in this arena!");
					}
					else if (ret == -101) {
						sender.sendMessage("You are already playing in an arena, leave before trying to join this one! (/cw leave)");
					}
					return true;
				}
			}
			
			
			/* does not work yet */
			if(args[0].equalsIgnoreCase("rename") || args[0].equalsIgnoreCase("re")) {
				return false;
				/*if(args.length >= 3) {
					Arena sel = parent.getArenaByName(args[1]);
					parent.deleteArena(sel);
					if (sel != null) {
						sender.sendMessage("Arena " +sel.getName()+ " has been renamed " + args[2]);
						//Arena temp = new Arena(parent,args[2],sel.getData().get);
						return true;
					}
				}
				else if(parent.getPlayerSelection(sender)!= null && args.length >= 2) {
					sender.sendMessage ("Arena " + parent.getPlayerSelection(sender).getName() + " has been renamed " +args[1]);
					parent.getPlayerSelection(sender).setName(args[1]);
					return true;
				}
				else if (args.length == 1) {
					sender.sendMessage("Too few arguments!");
					return false;
				}
				else {
					sender.sendMessage("No arena selected, and none specified!");
					return false;
				}*/
			}
			
		}
		return false;
	}

}
