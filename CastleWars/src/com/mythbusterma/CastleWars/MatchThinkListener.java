package com.mythbusterma.CastleWars;

import java.util.logging.Level;

import org.bukkit.event.*;

import com.mythbusterma.CastleWars.Events.MatchThinkEvent;

public class MatchThinkListener implements Listener {

	private CastleWars parent;
	
	public MatchThinkListener (CastleWars _parent){
		parent = _parent;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onThink (MatchThinkEvent e) {
		for(Arena a:parent.getArenas()) {
			a.getCurrentMatch().think();
		}
		parent.getLogger().log(Level.INFO,"Thinks");
	}
}
