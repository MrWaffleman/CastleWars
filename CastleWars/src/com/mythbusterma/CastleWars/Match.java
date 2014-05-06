package com.mythbusterma.CastleWars;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mythbusterma.CastleWars.Events.MatchEndEvent;
import com.mythbusterma.CastleWars.Events.MatchPrestartEvent;
import com.mythbusterma.CastleWars.Events.MatchStartEvent;
import com.mythbusterma.CastleWars.Events.MatchTransitionEvent;

public class Match {
	
	private Arena parent;
	private List<String> redTeam;
	private List<String> blueTeam;
	private List<String> deadPlayers;
	private List<String> spectators;
	private List<String> lobbists;
	
	private int redScore;
	private int blueScore;

	private boolean hasPlayers;
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
	}
	
	public void initilize (CastleWars cw) {
		setTimeTillStart(cw.getConfig().getInt("matchcountdown"));
		setTimeTillNextStage(cw.getConfig().getInt("firststagelength"));
		setTimeTillMatchEnd(cw.getConfig().getInt("secondstagelength"));
		minNumberOfPlayers = cw.getConfig().getInt("minplayers");
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
		}
		for (String s: blueTeam) {
			blueTeam.remove(s);
			parent.getParent().getServer().getPlayerExact(s).teleport(parent.getData().getLobbySpawn());
			lobbists.add(s);
		}
		for (String s: redTeam) {
			redTeam.remove(s);
			parent.getParent().getServer().getPlayerExact(s).teleport(parent.getData().getLobbySpawn());
			lobbists.add(s);
		}
		deadPlayers.clear();
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


	public void triggerDeath(Player ply) {
		if(redTeam.contains(ply.getName())) {
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
	
	public List<String> getParticipants () {
		LinkedList<String> ll = new LinkedList<String>();
		ll.addAll(blueTeam);
		ll.addAll(redTeam);
		ll.addAll(spectators);
		ll.addAll(lobbists);
		return ll;
	}
	
	public List<String> livingPlayers () {
		LinkedList<String> ll = new LinkedList<String>();
		ll.addAll(blueTeam);
		ll.addAll(redTeam);
		ll.removeAll(deadPlayers);
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
}
