package com.mythbusterma.CastleWars.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mythbusterma.CastleWars.Match;

public class MatchEndEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	
	private final Match match;
	private final int reason;
	private boolean cancelled;
	
	
	public MatchEndEvent (Match _match, int _reason) {
		match = _match;
		reason = _reason;
	}
	
	public Match getMatch() {
		return match;
	}

	public int getReason() {
		return reason;
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
