package com.mythbusterma.CastleWars;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.mythbusterma.CastleWars.CastleWars.Team;
import com.mythbusterma.CastleWars.Events.MatchEndEvent;
import com.mythbusterma.CastleWars.Events.MatchPrestartEvent;
import com.mythbusterma.CastleWars.Events.MatchStartEvent;
import com.mythbusterma.CastleWars.Events.MatchTransitionEvent;
import com.mythbusterma.CastleWars.Serializables.ArenaData;

public class Match {
	
	private Arena parent;
	/**
	 * Players in the lobby or spectating scheduled to be part of the red team
	 */
	private List<String> redLobby;
	/**
	 * Players in the lobby or spectating scheduled to be part of the blue team
	 */
	private List<String> blueLobby;
	private List<String> spectatorLobby;
	/**
	 * Players in the lobby that are as of yet unassigned
	 */
	private List<String> unassignedLobby;
	
	private List<String> redTeam;
	private List<String> blueTeam;
	private List<String> deadPlayers;
	private List<String> spectators;
	private List<String> lobbists;
	
	private Map<String, SimpleEntry<ItemStack[], ItemStack[]>> inventories;
	private Map<String, Location> locations;
	
	private int redScore;
	private int blueScore;

	private MatchState state;
	
	/**
	 *  In seconds
	 */
	private int timeTillStart;
	/**
	 *  In seconds
	 */
	private int timeTillNextStage;
	/**
	 *  In seconds
	 */
	private int timeTillMatchEnd;
	
	private int minNumberOfPlayers;
	
	private boolean arenaComplete;
	
	
	public enum MatchState 
	{
		/**
		 * Match cannot be joined
		 */
		NONE,
		/**
		 * Match can be joined but is not counting down to start
		 */
		PRESTART,
		/**
		 * Match is counting down to start
		 */
		STARTING,
		/**
		 * First stage of match, players can break blocks and obtain supplies, wall is up
		 */
		STAGE1,
		/**
		 * Second Stage of match, wall is down, players fight
		 */
		STAGE2;
		
		public int toInteger (MatchState ms) {
			if (ms == NONE) {
				return 1;
			}
			else if (ms == PRESTART) {
				return 2;
			}
			else if (ms==STARTING) {
				return 3;
			}
			else if (ms==STAGE1) {
				return 4;
			}
			else if (ms==STAGE2) {
				return 5;
			}
			
			else return -1;
		}
		
		public MatchState fromInteger(int ms) {
			if (ms == 1) {
				return NONE;
			}
			else if (ms == 2) {
				return PRESTART;
			}
			else if (ms == 3) {
				return STARTING;
			}
			else if (ms == 4) {
				return STAGE1;
			}
			else if (ms == 5) {
				return STAGE2;
			}
			else return null;
		}
	}
	
	public Match (Arena _parent) {
		parent = _parent;
		inventories = new HashMap<String,SimpleEntry<ItemStack[], ItemStack[]>> (parent.getParent().getConfig().getInt("maxplayers"));
		locations = new HashMap<String,Location> (parent.getParent().getConfig().getInt("maxplayers"));
		
		boolean complete = true;
		ArenaData data = parent.getData();
		if (data.getRedSpawn() == null) {
			complete = false;
		}
		if (data.getBlueSpawn() == null) {
			complete = false;
		}
		if (data.getLobbySpawn() == null) {
			complete = false;
		}
		if (data.getOrientation() == null) {
			complete = false;
		}
		arenaComplete = complete;
	}
	
	public void initilize (CastleWars cw) {
		setTimeTillStart(cw.getConfig().getInt("matchcountdown"));
		setTimeTillNextStage(cw.getConfig().getInt("firststagelength"));
		setTimeTillMatchEnd(cw.getConfig().getInt("secondstagelength"));
		minNumberOfPlayers = cw.getConfig().getInt("minplayers");
	}
	
	public void tryComplete () {
		boolean complete = true;
		ArenaData data = parent.getData();
		if (data.getRedSpawn() == null) {
			complete = false;
		}
		if (data.getBlueSpawn() == null) {
			complete = false;
		}
		if (data.getLobbySpawn() == null) {
			complete = false;
		}
		if (data.getOrientation() == null) {
			complete = false;
		}
		arenaComplete = complete;
	}

	public int getTimeTillStart() {
		return timeTillStart;
	}

	public void setTimeTillStart(int timeTillStart) {
		this.timeTillStart = timeTillStart;
	}

	public int getTimeTillNextStage() {
		return timeTillNextStage;
	}

	public void setTimeTillNextStage(int timeTillNextStage) {
		this.timeTillNextStage = timeTillNextStage;
	}

	public int getTimeTillMatchEnd() {
		return timeTillMatchEnd;
	}

	public void setTimeTillMatchEnd(int timeTillMatchEnd) {
		this.timeTillMatchEnd = timeTillMatchEnd;
	}
	
	public void endMatch() {
		for (String s: spectators) {
			spectators.remove(s);
			parent.getParent().getServer().getPlayerExact(s).teleport(parent.getData().getLobbySpawn());
			lobbists.add(s);
			parent.getParent().getServer().getPlayerExact(s).getInventory().clear();
		}
		for (String s: blueTeam) {
			blueTeam.remove(s);
			parent.getParent().getServer().getPlayerExact(s).teleport(parent.getData().getLobbySpawn());
			lobbists.add(s);
			parent.getParent().getServer().getPlayerExact(s).getInventory().clear();

		}
		for (String s: redTeam) {
			redTeam.remove(s);
			parent.getParent().getServer().getPlayerExact(s).teleport(parent.getData().getLobbySpawn());
			lobbists.add(s);
			parent.getParent().getServer().getPlayerExact(s).getInventory().clear();
		}
		deadPlayers.clear();
	}
	
	public void startMatch() {
	
	}
	
	public void think () {
		
		List<String> players = new LinkedList<String>();
		players.addAll(redTeam);
		players.addAll(blueTeam);
		players.addAll(spectators);
		
		if(state == MatchState.STARTING) {
			if (timeTillStart <= 0) {
				MatchStartEvent event = new MatchStartEvent (this, lobbists);
				state = MatchState.STAGE1;
				Bukkit.getPluginManager().callEvent(event);
			}
			else {
				timeTillStart--;
			}
		}
		else if(state == MatchState.STAGE1) {
			if(timeTillNextStage <= 0 ) {
				
				state = MatchState.STAGE2;
				MatchTransitionEvent event = new MatchTransitionEvent(this,players);
				Bukkit.getPluginManager().callEvent(event);
			}
			else {
				timeTillNextStage--;
			}
		}
		else if(state == MatchState.STAGE2){
			if (timeTillMatchEnd <= 0 ) {
				
				state = MatchState.PRESTART;
				MatchEndEvent event = new MatchEndEvent(this,players);
				Bukkit.getPluginManager().callEvent(event);
				
				endMatch();
			}
			else {
				timeTillMatchEnd--;
			}
		}
		
		if(state == MatchState.PRESTART) {
			if (this.getParticipants().size() >= minNumberOfPlayers) {
				state = MatchState.STARTING;
				MatchPrestartEvent event = new MatchPrestartEvent(this,players);
				Bukkit.getPluginManager().callEvent(event);
			}
		}
	}
	
	/**
	 * Adds the player to the game, putting them in unassigned. Also teleports them to the lobby.
	 * 
	 * @param ply
	 * @return The success of the operation, 1 for successfully joining a match, -100 for already in this match, -101 for already in another match, -102 for an unfinished arena, -1 for generic error
	 */
	public int join (final String ply) {
		if(!arenaComplete) {
			return -102;
		}
		for (Arena a: parent.getParent().getArenas()) {
			for(String s : a.getCurrentMatch().getParticipants()) {
				if(s.equalsIgnoreCase(ply)) {
					if (a == parent) {
						return -100;
					}
					else {
						return -101;
					}
				}
			}
			
		}
		
		BukkitScheduler sche = parent.getParent().getServer().getScheduler();
		sche.scheduleSyncDelayedTask(parent.getParent(), new Runnable () {
			@Override
			public void run() {
				addPlayerToLobby(ply, Bukkit.getPlayer(ply).getLocation());
				Bukkit.getPlayer(ply).teleport(parent.getData().getLobbySpawn());
			}
		}, parent.getParent().getConfig().getInt("teleportdelay")*20);
		return 1;
		
	}
	
	/**
	 * @param ply
	 * @return The success of leaving, -100 if they are alive and cannot leave, -101 if they are not in this arena, 1 for success
	 */
	public int leave (String ply) {
		if (getParticipants().contains(ply)) {
			if(livingPlayers().contains(ply)) {
				return -100;
			}
			else {
				spectators.remove(ply);
				redLobby.remove(ply);
				blueLobby.remove(ply);
				spectatorLobby.remove(ply);
				unassignedLobby.remove(ply);
				lobbists.remove(ply);
				Player player = Bukkit.getServer().getPlayerExact(ply);
				player.getInventory().clear();
				player.getInventory().setArmorContents(inventories.get(ply).getValue());
				int i = 0;
				for (ItemStack is : inventories.get(ply).getKey()) {
					player.getInventory().setItem(i, is);
					i++;
				}
				inventories.remove(ply);
				parent.getParent().getServer().getPlayerExact(ply).teleport(locations.get(ply));
				locations.remove(ply);
			}
		}
		else {
			return -101;
		}
		return -1;
	}
	
	private void addPlayerToLobby (String ply,Location loc) {
		lobbists.add(ply);
		unassignedLobby.add(ply);
		
		locations.put(ply, loc);
		ItemStack[] inventory = Bukkit.getServer().getPlayer(ply).getInventory().getContents();
		ItemStack[] saveInventory = new ItemStack[inventory.length];
		
		ItemStack[] armor = Bukkit.getServer().getPlayer(ply).getInventory().getArmorContents();
		ItemStack[] saveArmor = new ItemStack[armor.length];
		
		for(int i = 0; i < inventory.length; i++)
		{
		    if(inventory[i] != null)
		    {
		        saveInventory[i] = inventory[i].clone();
		    }
		}
		for (int i = 0; i <inventory.length; i++) {
			if (armor[i]!= null) {
				saveArmor[i] = armor[i].clone();
			}
		}
		SimpleEntry<ItemStack[],ItemStack[]> pair = new SimpleEntry<ItemStack[],ItemStack[]> (saveInventory, saveArmor);
		
		inventories.put(ply, pair);
	}
	
	
	
	/**
	 * Adds a player to the red team if they are eligible to be queued
	 * @param ply
	 * @return The success of the operation, 1 for successfully adding to red team, -100 if they are already playing in this arena, -101 if they are already playing in another arena
	 *  -102 if they are not in the lobby or spectator area of this arena
	 */
	public int joinRed (String ply) {
		for (Arena a: parent.getParent().getArenas()) {
			for (String s: a.getCurrentMatch().livingPlayers()) {
				if (s.equalsIgnoreCase(ply)) {
					if(a == parent) {
						return -100;
					}
					else {
						return -101;
					}
				}
			}
		}
		
		if (lobbists.contains(ply) || spectators.contains(ply)) {
			redLobby.add(ply);
			return 1;
		}
		else {
			return -102;
		}
	}
	
	/**
	 * Adds a player to the blue team if they are eligible to be queued
	 * @param ply
	 * @return The success of the operation, 1 for successfully adding to red team, -100 if they are already playing in this arena, -101 if they are already playing in another arena
	 *  -102 if they are not in the lobby or spectator area of this arena
	 */
	public int joinBlue (String ply) {
		for (Arena a: parent.getParent().getArenas()) {
			for (String s: a.getCurrentMatch().livingPlayers()) {
				if (s.equalsIgnoreCase(ply)) {
					if(a == parent) {
						return -100;
					}
					else {
						return -101;
					}
				}
			}
		}
		
		if (lobbists.contains(ply) || spectators.contains(ply)) {
			blueLobby.add(ply);
			return 1;
		}
		else {
			return -102;
		}
	}
	
	public int joinSpectators (String ply) {
		for (Arena a: parent.getParent().getArenas()) {
			for (String s: a.getCurrentMatch().livingPlayers()) {
				if (s.equalsIgnoreCase(ply)) {
					if(a == parent) {
						return -100;
					}
					else {
						return -101;
					}
				}
			}
		}
		
		if (lobbists.contains(ply) || spectators.contains(ply)) {
			spectatorLobby.add(ply);
			return 1;
		}
		else {
			return -102;
		}
	}
	
 	public void messageBlue (String message) {
		Iterator<String> iter = blueTeam.iterator();
		while(iter.hasNext()) {
			Player ply = Bukkit.getServer().getPlayerExact((String) iter.next());
			if(ply!=null) {
				ply.sendMessage(message);
			}
		}
	}
	
	public void messageRed (String message) {
		Iterator<String> iter = redTeam.iterator();
		while(iter.hasNext()) {
			Player ply = Bukkit.getServer().getPlayerExact((String) iter.next());
			if(ply!=null) {
				ply.sendMessage(message);
			}
		}
	}
	
	public void messageLobby (String message) {
		Iterator<String> iter = lobbists.iterator();
		while(iter.hasNext()) {
			Player ply = Bukkit.getServer().getPlayerExact((String) iter.next());
			if(ply!=null) {
				ply.sendMessage(message);
			}
		}
	}
	
	public void messageSpectators (String message) {
		Iterator<String> iter = spectators.iterator();
		while(iter.hasNext()) {
			Player ply = Bukkit.getServer().getPlayerExact((String) iter.next());
			if(ply!=null) {
				ply.sendMessage(message);
			}
		}
	}
	
	public void messageParticipants (String message) {
		messageBlue(message);
		messageRed(message);
		messageLobby(message);
		messageSpectators(message);
	}
	
	public boolean checkFriendlyFire (Player ply1, Player ply2) {
		if (redTeam.contains(ply1.getName())) {
			if(redTeam.contains(ply2.getName())){
				return true;
			}
		}
		else if(blueTeam.contains(ply1.getName())) {
			if(blueTeam.contains(ply2.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Find if the match has this player, and what team they're on, or if they're dead
	 * 
	 * @param ply The player to look for
	 * @return The team the player is on, null if not in this match
	 */
	public CastleWars.Team playerTeam (String ply) {
		if(blueTeam.contains(ply)) return Team.BLUE;
		else if (redTeam.contains(ply)) return Team.RED;
		else if (spectators.contains(ply)) return Team.SPECTATE;
		else if (lobbists.contains(ply)) return Team.LOBBY;
		else if (deadPlayers.contains(ply)) return Team.DEAD;
		
		return null;
	}
	
	/**
	 * @param ply The player to look for
	 * @return 1 if the player is dead, 0 if the player is alive, -1 if the player isn't in the arena
	 */
	public int playerDead (String ply) {
	
		if(this.playerTeam(ply)!=null) {
			if (this.deadPlayers.contains(ply)) {
				return 1;
			}
			else return 0;
		}
		
		return -1;
	}

	public void triggerDeath(Player ply, PlayerDeathEvent e) {
		// if the player is on the read team
		if(deadPlayers.contains(ply.getName())) {
			ply.teleport(parent.getData().getSpectatorSpawn().toLocation());
		}
		
		else if(redTeam.contains(ply.getName())) {
			blueScore++;
			ply.teleport(parent.getData().getSpectatorSpawn().toLocation());
			if(ply.getLastDamageCause().getEntity() instanceof Player) {
				ply.sendMessage("You died in combat, you were killed by: " + ((Player)ply.getLastDamageCause().getEntity()).getName());
				messageParticipants(ply.getName() + " has perished at the hands of " + ((Player)ply.getLastDamageCause().getEntity()).getName());
			}
			else {
				ply.sendMessage("You died in combat, you were killed by: " + ply.getLastDamageCause().getEntityType().toString());
				messageParticipants(ply.getName() + " has perished at the hands of " + ply.getLastDamageCause().getEntityType().toString());
			}
			deadPlayers.add(ply.getName());
		}
		// if the player is on the blue team
		else if (blueTeam.contains(ply.getName())) {
			redScore ++;
			ply.teleport(parent.getData().getSpectatorSpawn().toLocation());
			if(ply.getLastDamageCause().getEntity() instanceof Player) {
				ply.sendMessage("You died in combat, you were killed by: " + ((Player)ply.getLastDamageCause().getEntity()).getName());
				messageParticipants(ply.getName() + " has perished at the hands of " + ((Player)ply.getLastDamageCause().getEntity()).getName());
			}
			else {
				ply.sendMessage("You died in combat, you were killed by: " + ply.getLastDamageCause().getEntityType().toString());
				messageParticipants(ply.getName() + " has perished at the hands of " + ply.getLastDamageCause().getEntityType().toString());
			}
			deadPlayers.add(ply.getName());
		}
		else if (spectators.contains(ply.getName())) {
			ply.teleport(parent.getData().getSpectatorSpawn().toLocation());
		}
		else if (lobbists.contains(ply.getName())) {
			ply.teleport(parent.getData().getSpectatorSpawn().toLocation());
		}
		else return;
	}
	
	/**
	 * @return All players involved with this match
	 */
	public List<String> getParticipants () {
		LinkedList<String> ll = new LinkedList<String>();
		ll.addAll(blueTeam);
		ll.addAll(redTeam);
		ll.addAll(spectators);
		ll.addAll(lobbists);
		return ll;
	}
	
	/**
	 * @return All the players still on the field
	 */
	public List<String> livingPlayers () {
		LinkedList<String> ll = new LinkedList<String>();
		ll.addAll(blueTeam);
		ll.addAll(redTeam);
		ll.removeAll(deadPlayers);
		return ll;
	}
	
	/**
	 * Get the Spectators
	 * 
	 * @return The spectators to this match, including dead players
	 */
	public List<String> getSpectators () {
		LinkedList<String> ll = new LinkedList<String>();
		ll.addAll(deadPlayers);
		ll.addAll(spectators);	
		return ll;
	}
	
	public int getRedScore() {
		return redScore;
	}

	public void setRedScore(int redScore) {
		this.redScore = redScore;
	}

	public int getBlueScore() {
		return blueScore;
	}

	public void setBlueScore(int blueScore) {
		this.blueScore = blueScore;
	}
	
	public List<String> getLobbists () {
		return lobbists;
	}

	public boolean isArenaComplete() {
		return arenaComplete;
	}
}
