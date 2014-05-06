package com.mythbusterma.CastleWars;

import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerListener implements Listener { 
	private CastleWars parent;                    
                                                  
	public PlayerListener(CastleWars parent) {    
		this.parent = parent;                     
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {		
		for(Arena a: parent.getArenas()) {
			a.getCurrentMatch().triggerDeath(e.getEntity());
		}
	}
}                                                 
                                                  