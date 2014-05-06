package com.mythbusterma.CastleWars.Events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mythbusterma.CastleWars.Arena;
import com.mythbusterma.CastleWars.CastleWars.Team;

public class PlayerSpawnEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	
	private final Player ply;
	private final Location loc;
	private final Arena arena;
	private final Team team;
	
	private boolean cancelled;
	
	public PlayerSpawnEvent(Player p, Location l, Arena a, Team t) {
		ply = p;
		loc = l;
		arena = a;
		team = t;
	}
	
	public Arena getArena() {
		return arena;
	}


	public Team getTeam() {
		return team;
	}

	public Player getPly() {
		return ply;
	}

	public Location getLoc() {
		return loc;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}


	@Override
	public boolean isCancelled() {
		return cancelled;
	}


	@Override
	public void setCancelled(boolean arg0) {
		cancelled = arg0;
		
	}

}
