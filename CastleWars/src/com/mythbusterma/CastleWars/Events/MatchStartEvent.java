package com.mythbusterma.CastleWars.Events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mythbusterma.CastleWars.Arena;
import com.mythbusterma.CastleWars.Match;

public class MatchStartEvent extends Event implements Cancellable {

	
	private static final HandlerList handlers = new HandlerList();
	
	private final Match match;
	private final List<String> participants;
	private boolean cancelled;
	
	
	public MatchStartEvent (Match _match, List<String> _participants) {
		match = _match;
		participants = _participants;
	}
	
	public Match getMatch() {
		return match;
	}

	public List<String> getParticipants() {
		return participants;
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
