package com.mythbusterma.CastleWars.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mythbusterma.CastleWars.Match;

public class MatchPrestartEvent extends Event  implements Cancellable {

private static final HandlerList handlers = new HandlerList();
	
	private final Match match;
	private boolean cancelled;
	
	
	public MatchPrestartEvent (Match _match) {
		match = _match;
	}
	
	public Match getMatch() {
		return match;
	}

	public static HandlerList getHandlerList() {
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

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

}
