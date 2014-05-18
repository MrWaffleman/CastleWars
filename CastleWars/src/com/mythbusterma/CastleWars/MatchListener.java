package com.mythbusterma.CastleWars;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.mythbusterma.CastleWars.Events.MatchEndEvent;
import com.mythbusterma.CastleWars.Events.MatchStartEvent;
import com.mythbusterma.CastleWars.Events.MatchThinkEvent;

public class MatchListener implements Listener {

	private CastleWars parent;

	public MatchListener(CastleWars _parent) {
		parent = _parent;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onMatchEnd(MatchEndEvent e) {
		if (e.isCancelled()) {
			return;
		} else {
			e.getMatch().endMatch(e.getReason());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMatchStart(MatchStartEvent e) {
		if (e.isCancelled()) {
			return;
		} else {
			e.getMatch().startMatch();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onThink(MatchThinkEvent e) {
		parent.getSignManager().updateSigns();
		for (Arena a : parent.getArenas()) {
			a.getCurrentMatch().think();
		}
	}
}
