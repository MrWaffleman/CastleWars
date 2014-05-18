package com.mythbusterma.CastleWars.Events;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mythbusterma.CastleWars.Match;

public class MatchTransitionEvent extends Event {

private static final HandlerList handlers = new HandlerList();
	
	private final Match match;
	private final List<String> participants;
	
	
	public MatchTransitionEvent (Match _match, List<String> _participants) {
		match = _match;
		participants = _participants;
	}
	
	public Match getArena() {
		return match;
	}

	public List<String> getParticipants() {
		return participants;
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
