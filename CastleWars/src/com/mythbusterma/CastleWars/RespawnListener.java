package com.mythbusterma.CastleWars;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

	private Map<String, Location> players;

	public RespawnListener(CastleWars _parent) {
		players = new HashMap<String, Location>();
	}

	public void addPlayer(String player, Location respawnLocation) {
		if (player != null && respawnLocation != null) {
			players.put(player, respawnLocation);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent e) {
		String player = e.getPlayer().getName();
		List<String> toRemove = new LinkedList<>();
		for (Entry<String, Location> entry : players.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(player)) {
				e.setRespawnLocation(entry.getValue());
				toRemove.add(entry.getKey());
				break;
			}
		}
		for (String s : toRemove) {
			players.remove(s);
		}
		toRemove.clear();
	}

}
