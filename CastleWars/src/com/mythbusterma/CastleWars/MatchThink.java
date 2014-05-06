package com.mythbusterma.CastleWars;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.mythbusterma.CastleWars.Events.MatchThinkEvent;

public class MatchThink extends BukkitRunnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		MatchThinkEvent event = new MatchThinkEvent();
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

}
