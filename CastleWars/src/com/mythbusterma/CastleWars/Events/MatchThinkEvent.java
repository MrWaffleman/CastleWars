package com.mythbusterma.CastleWars.Events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mythbusterma.CastleWars.Arena;

public class MatchThinkEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	
	public MatchThinkEvent () {

	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}

}
