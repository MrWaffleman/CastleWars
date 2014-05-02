package com.mythbusterma.CastleWars;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

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
		
		try {
			boolean empty =args[0] == null;
		}
		catch (IndexOutOfBoundsException e) {
			sender.sendMessage("Usage: (question marks denote optional with selection\n"
					+ "/cw select <name>: select an arena by name\n"
					+ "/cw createarena <name>: create a new arena with the current WorldEdit Selection\n"
					+ "/cw setbluespawn ?<name>?: set the blue spawn of an arena\n"
					+ "/cw setredspawn ?<name>?: set the red spawn of an arena\n"
					+ "/cw setlobbyspawn ?<name>?: set the lobby spawn point of an arena\n"
					+ "/cw delete ?<name>?: delete an arena\n"
					+ "/cw save ?<name>?: force saving of an arena (this is done automatically at server shutdown)\n"
					+ "/cw list: list all arenas in memory");
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
			
			if(args[0].equalsIgnoreCase("list")) {
				
				sender.sendMessage("Currently registered arenas are:\n");
				for (Arena a : parent.getArenas()) {
					sender.sendMessage(a.getName() + '\n');
				}
			}
			
			/* does not work yet */
			if(args[0].equalsIgnoreCase("rename") || args[0].equalsIgnoreCase("re")) {
				if(args.length >= 3) {
					Arena sel = parent.getArenaByName(args[1]);
					if (sel != null) {
						sender.sendMessage("Arena " +sel.getName()+ " has been renamed " + args[2]);
						sel.setName(args[2]);
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
				}
			}
			
		}
		return false;
	}

}
