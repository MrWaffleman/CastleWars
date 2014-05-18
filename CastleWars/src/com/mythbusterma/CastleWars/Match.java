package com.mythbusterma.CastleWars;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.mythbusterma.CastleWars.CastleWars.Team;
import com.mythbusterma.CastleWars.Events.MatchEndEvent;
import com.mythbusterma.CastleWars.Events.MatchPrestartEvent;
import com.mythbusterma.CastleWars.Events.MatchStartEvent;
import com.mythbusterma.CastleWars.Events.MatchTransitionEvent;
import com.mythbusterma.CastleWars.Serializables.ArenaData;
import com.sk89q.worldedit.FilenameException;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.MobDisguise;

public class Match {

	public enum MatchState {
		/**
		 * Match cannot be joined
		 */
		NONE,
		/**
		 * Match can be joined but is not counting down to start
		 */
		PRESTART,
		/**
		 * First stage of match, players can break blocks and obtain supplies,
		 * wall is up
		 */
		STAGE1,
		/**
		 * Second Stage of match, wall is down, players fight
		 */
		STAGE2,
		/**
		 * Match is counting down to start
		 */
		STARTING;

		public MatchState fromInteger(int ms) {
			if (ms == 1) {
				return NONE;
			} else if (ms == 2) {
				return PRESTART;
			} else if (ms == 3) {
				return STARTING;
			} else if (ms == 4) {
				return STAGE1;
			} else if (ms == 5) {
				return STAGE2;
			} else {
				return null;
			}
		}

		public int toInteger(MatchState ms) {
			if (ms == NONE) {
				return 1;
			} else if (ms == PRESTART) {
				return 2;
			} else if (ms == STARTING) {
				return 3;
			} else if (ms == STAGE1) {
				return 4;
			} else if (ms == STAGE2) {
				return 5;
			} else {
				return -1;
			}
		}
	}

	private boolean arenaComplete;
	private Map<String, Integer> balance;
	/**
	 * Players in the lobby or spectating scheduled to be part of the blue team
	 */
	private List<String> blueLobby;
	private int blueScore;

	private List<String> blueTeam;
	private List<String> deadPlayers;
	private Map<String, Integer> exitAttempts;
	private Map<String, SimpleEntry<ItemStack[], ItemStack[]>> inventories;
	private Team lastWinner;

	private List<String> lobbists;
	private Map<String, Location> locations;
	private Arena parent;
	/**
	 * Players in the lobby or spectating scheduled to be part of the red team
	 */
	private List<String> redLobby;
	private int redScore;

	private List<String> redTeam;

	private List<String> spectatorLobby;
	private List<String> spectators;

	private MatchState state;

	private Map<String, Integer> technology;
	/**
	 * In seconds
	 */
	private int timeTillMatchEnd;
	/**
	 * In seconds
	 */
	private int timeTillNextStage;

	/**
	 * In seconds
	 */
	private int timeTillStart;

	/**
	 * Players in the lobby that are as of yet unassigned
	 */
	private List<String> unassignedLobby;

	public Match(Arena _parent) {
		parent = _parent;
		ArenaData data = parent.getData();
		balance = new HashMap<String, Integer>(data.getMaxPlayers());

		redTeam = new LinkedList<String>();
		blueTeam = new LinkedList<String>();
		deadPlayers = new LinkedList<String>();
		spectators = new LinkedList<String>();
		lobbists = new LinkedList<String>();
		redLobby = new LinkedList<String>();
		blueLobby = new LinkedList<String>();
		spectatorLobby = new LinkedList<String>();
		unassignedLobby = new LinkedList<String>();
		inventories = new HashMap<String, SimpleEntry<ItemStack[], ItemStack[]>>(
				parent.getData().getMaxPlayers());
		locations = new HashMap<String, Location>(parent.getData()
				.getMaxPlayers());
		exitAttempts = new HashMap<String, Integer>(parent.getData()
				.getMaxPlayers());
		technology = new HashMap<String, Integer>(parent.getData()
				.getMaxPlayers());
		lastWinner = Team.DEAD;

		state = MatchState.PRESTART;

		boolean complete = true;
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

	@SuppressWarnings("deprecation")
	private void addPlayerToLobby(String ply, Location loc) {
		if (getParticipants().contains(ply)) {
			return;
		}

		Bukkit.getPlayer(ply).teleport(parent.getData().getLobbySpawn());
		lobbists.add(ply);
		unassignedLobby.add(ply);

		locations.put(ply, loc);
		ItemStack[] inventory = Bukkit.getServer().getPlayer(ply)
				.getInventory().getContents();
		ItemStack[] saveInventory = new ItemStack[inventory.length];

		ItemStack[] armor = Bukkit.getServer().getPlayer(ply).getInventory()
				.getArmorContents();
		ItemStack[] saveArmor = new ItemStack[armor.length];

		Bukkit.getServer().getPlayer(ply).getInventory().clear();

		balance.put(ply, 0);

		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null) {
				saveInventory[i] = inventory[i].clone();
			}
		}
		for (int i = 0; i < armor.length; i++) {
			if (armor[i] != null) {
				saveArmor[i] = armor[i].clone();
			}
		}
		SimpleEntry<ItemStack[], ItemStack[]> pair = new SimpleEntry<ItemStack[], ItemStack[]>(
				saveInventory, saveArmor);

		parent.getParent().getServer().getPlayer(ply).updateInventory();
		parent.getParent().logToFile(
				ply + " joined the lobby of " + parent.getName(), null);

		inventories.put(ply, pair);
		technology.put(ply, 0);

		exitAttempts.put(ply, 0);
	}

	public void addTechnology(String ply, ItemStack items) {
		int tech = technology.get(ply);
		tech = tech + Technology.getTechnology(items);
		technology.put(ply, tech);
	}

	private void buildWall() {
		parent.buildWall();
	}

	public boolean checkFriendlyFire(Player ply1, Player ply2) {
		if (redTeam.contains(ply1.getName())) {
			if (redTeam.contains(ply2.getName())) {
				return true;
			}
		} else if (blueTeam.contains(ply1.getName())) {
			if (blueTeam.contains(ply2.getName())) {
				return true;
			}
		}
		return false;
	}

	public Team checkWin() {
		List<String> blueClone = new LinkedList<String>(blueTeam);
		List<String> redClone = new LinkedList<String>(redTeam);
		blueClone.removeAll(deadPlayers);
		redClone.removeAll(deadPlayers);

		if (blueClone.size() == 0) {
			return Team.RED;
		}
		if (redClone.size() == 0) {
			return Team.BLUE;
		}

		return null;
	}

	public boolean deduct(String ply, int amount) {
		if (balance.get(ply) >= amount) {
			balance.put(ply, balance.get(ply) - amount);
			return true;
		} else {
			return false;
		}
	}

	private void destroyWall() {
		parent.destroyWall();
	}

	/**
	 * DO NOT CALL, FIRE A MATCHENDEVENT INSTEAD
	 * 
	 * @param cause
	 */
	@Deprecated
	public void endMatch(int cause) {
		state = MatchState.PRESTART;

		try {
			parent.restore();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (FilenameException e) {
			e.printStackTrace();
		}
		CastleWars superParent = parent.getParent();

		Team win = checkWin();
		if (cause == 1) {
			if (win != null) {
				String lastAlive = deadPlayers.get(deadPlayers.size() - 1);

				List<String> stillStanding = livingPlayers();
				for (String s : stillStanding) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("lastmanbonus"),
							"staying alive until the end of the match!");
				}

				superParent.payPlayer(lastAlive, superParent.getConfig()
						.getDouble("lastmanlose"),
						"being the last hope of the losing team!");

				if (win == Team.RED) {
					for (String s : redTeam) {
						superParent.payPlayer(s, superParent.getConfig()
								.getDouble("winpayout"), "winning the match!");
					}
					for (String s : blueTeam) {
						superParent.payPlayer(s, superParent.getConfig()
								.getDouble("losepayout"),
								"a consolation for losing");
					}
				} else {
					for (String s : blueTeam) {
						superParent.payPlayer(s, superParent.getConfig()
								.getDouble("winpayout"), "winning the match!");
					}
					for (String s : redTeam) {
						superParent.payPlayer(s, superParent.getConfig()
								.getDouble("losepayout"),
								"a consolation for losing");
					}
				}
			}
		}

		if (cause == 0) {
			List<String> blueClone = new LinkedList<String>(blueTeam);
			List<String> redClone = new LinkedList<String>(redTeam);

			blueClone.removeAll(deadPlayers);
			redClone.removeAll(deadPlayers);

			if (redClone.size() > blueClone.size()) {
				// red win
				for (String s : redClone) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("lastmanbonus"),
							"staying alive until the end of the match!");
				}

				for (String s : blueClone) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("lastmanlose"),
							"being the last hope of the losing team!");
				}

				for (String s : redTeam) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("winpayout"),
							"winning the match!");
				}
				for (String s : blueTeam) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("losepayout"),
							"a consolation for losing");
				}
			} else if (redClone.size() == blueClone.size()) {
				// tie
				for (String s : livingPlayers()) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("lastmanbonus"),
							"staying alive until the end of the match!");
				}
				LinkedList<String> ll = new LinkedList<String>(blueTeam);
				ll.addAll(redTeam);
				for (String s : ll) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("tiepayout"),
							"playing in a tied match!");
				}
			} else {
				// blue win
				for (String s : blueClone) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("lastmanbonus"),
							"staying alive until the end of the match!");
				}

				for (String s : redClone) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("lastmanlose"),
							"being the last hope of the losing team!");
				}

				for (String s : blueTeam) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("winpayout"),
							"winning the match!");
				}
				for (String s : redTeam) {
					superParent.payPlayer(s,
							superParent.getConfig().getDouble("losepayout"),
							"a consolation for losing");
				}
			}
		}

		DisguiseAPI api = superParent.getDisguise();

		for (String s : spectators) {
			spectators.remove(s);
			Player p = parent.getParent().getServer().getPlayerExact(s);
			p.teleport(parent.getData().getLobbySpawn());
			lobbists.add(s);
			p.getInventory().clear();

			p.setHealth(20D);
			spectatorLobby.add(s);
		}
		for (String s : blueTeam) {
			Player p = parent.getParent().getServer().getPlayerExact(s);
			p.teleport(parent.getData().getLobbySpawn());
			lobbists.add(s);
			p.getInventory().clear();
			p.setHealth(20D);
			unassignedLobby.add(s);
			if (api != null) {
				api.undisguiseToAll(superParent.getServer().getPlayer(s));
			}
			parent.getParent().getScores().addTechnology(s, technology.get(s));
			technology.put(s, 0);
		}
		for (String s : redTeam) {
			Player p = parent.getParent().getServer().getPlayerExact(s);
			p.teleport(parent.getData().getLobbySpawn());
			lobbists.add(s);
			p.getInventory().clear();
			p.setHealth(20D);
			unassignedLobby.add(s);
			if (api != null) {
				api.undisguiseToAll(superParent.getServer().getPlayer(s));
			}
			parent.getParent().getScores().addTechnology(s, technology.get(s));
			technology.put(s, 0);
		}

		deadPlayers.clear();

		blueScore = 0;
		redScore = 0;

		lastWinner = win;

		for (String s : getParticipants()) {
			balance.put(s, 0);
		}
		redTeam.clear();
		blueTeam.clear();

	}

	@SuppressWarnings("deprecation")
	public void exitAttempt(String ply, PlayerMoveEvent e) {
		int attempts = exitAttempts.get(ply);
		attempts++;
		if (attempts > parent.getParent().getConfig().getInt("exitattempts")) {

			if (blueTeam.contains(ply)) {
				Bukkit.getPlayer(ply).teleport(parent.getData().getBlueSpawn());
				parent.getParent()
						.getLogger()
						.log(Level.INFO,
								ply
										+ " tried to leave the arena too many times and was respawned");
				parent.getParent()
						.logToFile(
								ply
										+ " tried to leave the arena too many times and was teleported",
								null);
				e.setCancelled(false);
			} else {
				Bukkit.getPlayer(ply).teleport(parent.getData().getRedSpawn());
				parent.getParent()
						.getLogger()
						.log(Level.INFO,
								ply
										+ " tried to leave the arena too many times and was respawned");
				parent.getParent().logToFile(ply + " tried to leave the arena too many times and was teleported",null);
				e.setCancelled(false);
			}
			exitAttempts.put(ply, 0);
		} else {
			exitAttempts.put(ply, attempts);
		}
	}

	public Integer getBalance(String ply) {
		if (balance.containsKey(ply)) {
			return balance.get(ply);
		} else {
			return null;
		}
	}

	public int getBlueScore() {
		return blueScore;
	}

	public List<String> getBlueTeam() {
		return new LinkedList<String>(blueTeam);
	}

	public int getBlueTech() {
		int tech = 0;
		for (String s : blueTeam) {
			tech = tech + technology.get(s);
		}
		return tech;
	}

	/**
	 * @return The last team that won, if no matches have been played, returns
	 *         Team.DEAD
	 */
	public Team getLastWinner() {
		return lastWinner;
	}

	public List<String> getLobbists() {
		return new LinkedList<>(lobbists);
	}

	public MatchState getMatchState() {
		return state;
	}

	public Arena getParent() {
		return parent;
	}

	/**
	 * @return All players involved with this match
	 */
	public List<String> getParticipants() {
		LinkedList<String> ll = new LinkedList<String>();
		ll.addAll(blueTeam);
		ll.addAll(redTeam);
		ll.addAll(spectators);
		ll.addAll(lobbists);
		return ll;
	}

	public int getRedScore() {
		return redScore;
	}

	public List<String> getRedTeam() {
		return new LinkedList<String>(redTeam);
	}

	public int getRedTech() {
		int tech = 0;
		for (String s : redTeam) {
			tech = tech + technology.get(s);
		}
		return tech;
	}

	/**
	 * Get the Spectators
	 * 
	 * @return The spectators to this match, including dead players
	 */
	public List<String> getSpectators() {
		LinkedList<String> ll = new LinkedList<String>();
		ll.addAll(deadPlayers);
		ll.addAll(spectators);
		return ll;
	}

	public int getTimeTillMatchEnd() {
		return timeTillMatchEnd;
	}

	public int getTimeTillNextStage() {
		return timeTillNextStage;
	}

	public int getTimeTillStart() {
		return timeTillStart;
	}

	public boolean isArenaComplete() {
		return arenaComplete;
	}

	/**
	 * Adds the player to the game, putting them in unassigned. Also teleports
	 * them to the lobby.
	 * 
	 * @param ply
	 * @return The success of the operation, 1 for successfully joining a match,
	 *         -100 for already in this match, -101 for already in another
	 *         match, -102 for an unfinished arena, -103 for a too full arena -1
	 *         for generic error
	 */
	public int join(final String ply) {
		if (!arenaComplete) {
			return -102;
		}
		for (Arena a : parent.getParent().getArenas()) {
			for (String s : a.getCurrentMatch().getParticipants()) {
				if (s.equalsIgnoreCase(ply)) {
					if (a == parent) {
						return -100;
					} else {
						return -101;
					}
				}
			}

		}
		if (getParticipants().size() >= parent.getData().getMaxPlayers()) {
			return -103;
		}

		BukkitScheduler sche = parent.getParent().getServer().getScheduler();
		sche.scheduleSyncDelayedTask(parent.getParent(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				addPlayerToLobby(ply, Bukkit.getPlayer(ply).getLocation());
			}
		}, parent.getParent().getConfig().getInt("teleportdelay") * 20);
		return 1;

	}

	/**
	 * Adds a player to the blue team if they are eligible to be queued
	 * 
	 * @param ply
	 * @return The success of the operation, 1 for successfully adding to red
	 *         team, -100 if they are already playing in this arena, -101 if
	 *         they are already playing in another arena -102 if they are not in
	 *         the lobby or spectator area of this arena
	 */
	public int joinBlue(String ply) {
		for (Arena a : parent.getParent().getArenas()) {
			for (String s : a.getCurrentMatch().livingPlayers()) {
				if (s.equalsIgnoreCase(ply)) {
					if (a == parent) {
						return -100;
					} else {
						return -101;
					}
				}
			}
		}

		redLobby.remove(ply);
		blueLobby.remove(ply);
		unassignedLobby.remove(ply);
		spectatorLobby.remove(ply);

		if (lobbists.contains(ply) || spectators.contains(ply)) {
			blueLobby.add(ply);
			return 1;
		} else {
			return -102;
		}
	}

	/**
	 * Adds a player to the red team if they are eligible to be queued
	 * 
	 * @param ply
	 * @return The success of the operation, 1 for successfully adding to red
	 *         team, -100 if they are already playing in this arena, -101 if
	 *         they are already playing in another arena -102 if they are not in
	 *         the lobby or spectator area of this arena
	 */
	public int joinRed(String ply) {
		for (Arena a : parent.getParent().getArenas()) {
			for (String s : a.getCurrentMatch().livingPlayers()) {
				if (s.equalsIgnoreCase(ply)) {
					if (a == parent) {
						return -100;
					} else {
						return -101;
					}
				}
			}
		}

		redLobby.remove(ply);
		blueLobby.remove(ply);
		unassignedLobby.remove(ply);
		spectatorLobby.remove(ply);

		if (lobbists.contains(ply) || spectators.contains(ply)) {
			redLobby.add(ply);
			return 1;
		} else {
			return -102;
		}
	}

	public int joinSpectators(String ply) {
		for (Arena a : parent.getParent().getArenas()) {
			for (String s : a.getCurrentMatch().livingPlayers()) {
				if (s.equalsIgnoreCase(ply)) {
					if (a == parent) {
						return -100;
					} else {
						return -101;
					}
				}
			}
		}

		redLobby.remove(ply);
		blueLobby.remove(ply);
		spectatorLobby.remove(ply);

		if (lobbists.contains(ply) || spectators.contains(ply)) {
			spectatorLobby.add(ply);
			return 1;
		} else {
			return -102;
		}
	}

	@SuppressWarnings("deprecation")
	public void kick(String player, String kicker, String reason) {
		leave(player, true);
		if (reason != null && reason != "") {
			Bukkit.getServer()
					.getPlayer(player)
					.sendMessage(
							"You have been kicked from the arena for reason: "
									+ reason);
		} else {
			Bukkit.getServer().getPlayer(player)
					.sendMessage("You have been kicked from the arena!");
		}

		parent.getParent().logToFile(
				kicker + " kicked player " + player + " from arena "
						+ parent.getName(), "admin");

		parent.getParent().getScores().kick(player);
	}

	/**
	 * @param ply
	 * @return The success of leaving, -100 if they are alive and cannot leave,
	 *         -101 if they are not in this arena, 1 for success
	 */
	@SuppressWarnings("deprecation")
	public int leave(String ply, boolean force) {
		if (getParticipants().contains(ply)) {
			if (livingPlayers().contains(ply) && !force) {
				return -100;
			} else {
				spectators.remove(ply);
				redLobby.remove(ply);
				blueLobby.remove(ply);
				spectatorLobby.remove(ply);
				redTeam.remove(ply);
				blueTeam.remove(ply);
				unassignedLobby.remove(ply);
				lobbists.remove(ply);
				Player player = Bukkit.getServer().getPlayerExact(ply);
				player.getInventory().clear();
				player.getInventory().setArmorContents(
						inventories.get(ply).getValue());
				int i = 0;
				for (ItemStack is : inventories.get(ply).getKey()) {
					player.getInventory().setItem(i, is);
					i++;
				}
				Bukkit.getServer().getPlayer(ply).updateInventory();
				inventories.remove(ply);
				parent.getParent().getServer().getPlayerExact(ply)
						.teleport(locations.get(ply));
				locations.remove(ply);
				balance.remove(ply);
				exitAttempts.remove(ply);

				parent.getParent().logToFile(
						ply + " left arena " + parent.getName(), null);

				return 1;
			}
		} else {
			return -101;
		}
	}

	/**
	 * @return All the players still on the field
	 */
	public List<String> livingPlayers() {
		LinkedList<String> ll = new LinkedList<String>();
		ll.addAll(blueTeam);
		ll.addAll(redTeam);
		ll.removeAll(deadPlayers);
		return ll;
	}

	@SuppressWarnings("deprecation")
	public void messageBlue(String message) {
		Iterator<String> iter = blueTeam.iterator();
		while (iter.hasNext()) {
			Player ply = Bukkit.getServer().getPlayerExact(iter.next());
			if (ply != null) {
				ply.sendMessage(message);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void messageLobby(String message) {
		Iterator<String> iter = lobbists.iterator();
		while (iter.hasNext()) {
			Player ply = Bukkit.getServer().getPlayerExact(iter.next());
			if (ply != null) {
				ply.sendMessage(message);
			}
		}
	}

	public void messageParticipants(String message) {
		messageBlue(message);
		messageRed(message);
		messageLobby(message);
		messageSpectators(message);
	}

	@SuppressWarnings("deprecation")
	public void messageRed(String message) {
		Iterator<String> iter = redTeam.iterator();
		while (iter.hasNext()) {
			Player ply = Bukkit.getServer().getPlayerExact(iter.next());
			if (ply != null) {
				ply.sendMessage(message);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void messageSpectators(String message) {
		Iterator<String> iter = spectators.iterator();
		while (iter.hasNext()) {
			Player ply = Bukkit.getServer().getPlayerExact(iter.next());
			if (ply != null) {
				ply.sendMessage(message);
			}
		}
	}

	public void pay(String ply, int amount) {
		balance.put(ply, balance.get(ply) + amount);
	}

	/**
	 * @param ply
	 *            The player to look for
	 * @return 1 if the player is dead, 0 if the player is alive, -1 if the
	 *         player isn't in the arena
	 */
	public int playerDead(String ply) {

		if (playerTeam(ply) != null) {
			if (deadPlayers.contains(ply)) {
				return 1;
			} else {
				return 0;
			}
		}

		return -1;
	}

	/**
	 * Find if the match has this player, and what team they're on, or if
	 * they're dead
	 * 
	 * @param ply
	 *            The player to look for
	 * @return The team the player is on, null if not in this match
	 */
	public Team playerTeam(String ply) {
		if (blueTeam.contains(ply)) {
			return Team.BLUE;
		} else if (redTeam.contains(ply)) {
			return Team.RED;
		} else if (spectators.contains(ply)) {
			return Team.SPECTATE;
		} else if (lobbists.contains(ply)) {
			return Team.LOBBY;
		} else if (deadPlayers.contains(ply)) {
			return Team.DEAD;
		}

		return null;
	}

	public void setBlueScore(int blueScore) {
		this.blueScore = blueScore;
	}

	public void setRedScore(int redScore) {
		this.redScore = redScore;
	}

	public void setTimeTillMatchEnd(int timeTillMatchEnd) {
		this.timeTillMatchEnd = timeTillMatchEnd;
	}

	public void setTimeTillNextStage(int timeTillNextStage) {
		this.timeTillNextStage = timeTillNextStage;
	}

	public void setTimeTillStart(int timeTillStart) {
		this.timeTillStart = timeTillStart;
	}

	@SuppressWarnings("deprecation")
	public void startMatch() {

		state = MatchState.STAGE1;

		buildWall();

		CastleWars.messageServer(ChatColor.BOLD.toString() + ChatColor.AQUA
				+ "The match in arena " + parent.getName() + " is beginning!");

		for (String s : spectatorLobby) {
			Bukkit.getServer()
					.getPlayer(s)
					.teleport(parent.getData().getSpectatorSpawn().toLocation());
			Bukkit.getServer().getPlayer(s).getInventory().clear();
			spectatorLobby.remove(s);
			lobbists.remove(s);
			spectators.add(s);
		}

		for (String s : redLobby) {
			Bukkit.getServer().getPlayer(s).getInventory().clear();
			redLobby.remove(s);
			lobbists.remove(s);
			redTeam.add(s);
			Bukkit.getServer().getConsoleSender()
					.sendMessage(s + " assigned to red from red lobby");
		}
		for (String s : blueLobby) {
			Bukkit.getServer().getPlayer(s).getInventory().clear();
			blueLobby.remove(s);
			lobbists.remove(s);
			blueTeam.add(s);
			Bukkit.getServer().getConsoleSender()
					.sendMessage(s + " assigned to blue from blue lobby");
		}

		for (String s : unassignedLobby) {
			if (redTeam.size() >= blueTeam.size()) {
				// add to blue
				Bukkit.getServer().getPlayer(s).getInventory().clear();
				unassignedLobby.remove(s);
				lobbists.remove(s);
				blueTeam.add(s);
				Bukkit.getServer()
						.getConsoleSender()
						.sendMessage(
								s + " assigned to blue from unassigned lobby");
			} else {
				// add to red
				Bukkit.getServer().getPlayer(s).getInventory().clear();
				unassignedLobby.remove(s);
				lobbists.remove(s);
				redTeam.add(s);
				Bukkit.getServer()
						.getConsoleSender()
						.sendMessage(
								s + " assigned to red from unassigned lobby");
			}
		}

		while (redTeam.size() - 1 > blueTeam.size()) {
			String last = redTeam.get(redTeam.size() - 1);
			redTeam.remove(last);
			blueTeam.add(last);
			Bukkit.getServer().getPlayer(last)
					.sendMessage("&1You have been reassigned from red!");
			Bukkit.getServer()
					.getConsoleSender()
					.sendMessage(
							last + " assigned from red to blue for balancing");
		}

		while (blueTeam.size() - 1 > redTeam.size()) {
			String last = blueTeam.get(blueTeam.size() - 1);
			blueTeam.remove(last);
			redTeam.add(last);
			Bukkit.getServer().getPlayer(last)
					.sendMessage("&CYou have been reassigned from blue!");
			Bukkit.getServer()
					.getConsoleSender()
					.sendMessage(
							last + " assigned from red to blue for balancing");
		}

		if (Math.abs(blueTeam.size() - redTeam.size()) == 1) {
			if (parent.getParent().getConfig()
					.getBoolean("extraplayerspectate")) {
				if (blueTeam.size() > redTeam.size()) {
					String last = blueTeam.get(blueTeam.size() - 1);
					blueTeam.remove(last);
					spectators.add(last);
					Bukkit.getServer()
							.getPlayer(last)
							.sendMessage(
									"&0You have been reassigned to spectate!");
				} else if (redTeam.size() > blueTeam.size()) {
					String last = redTeam.get(redTeam.size() - 1);
					redTeam.remove(last);
					spectators.add(last);
					Bukkit.getServer()
							.getPlayer(last)
							.sendMessage(
									"&0You have been reassigned to spectate!");
				}
			}
		}

		StringBuilder red = new StringBuilder();
		StringBuilder blue = new StringBuilder();

		for (String s : blueTeam) {
			blue.append(s + ", ");
		}

		for (String s : redTeam) {
			red.append(s + ", ");
		}

		DisguiseAPI api = parent.getParent().getDisguise();

		for (String s : blueTeam) {
			Player p = Bukkit.getServer().getPlayer(s);
			p.teleport(parent.getData().getBlueSpawn());
			p.sendMessage("You have been assigned to the blue team! Your teammates are: "
					+ blue.toString() + "good luck!");
			parent.getParent().logToFile(
					s + " has been assigned to the blue team in arena "
							+ parent.getName(), null);
			if (api != null) {
				p.sendMessage(ChatColor.BOLD
						+ ChatColor.DARK_BLUE.toString()
						+ "You and your teammates have been disgusied as zombies for the duration of this match, be advised.");
				MobDisguise d = new MobDisguise(DisguiseType.ZOMBIE, true);
				d.setCustomName(p.getName());
				api.disguiseToAll(p, d);
			}

		}
		for (String s : redTeam) {
			Player p = Bukkit.getServer().getPlayer(s);
			p.teleport(parent.getData().getRedSpawn());
			p.sendMessage("You have been assigned to the red team! Your teammates are: "
					+ red.toString() + "good luck!");
			parent.getParent().logToFile(
					s + " has been assigned to the red team in arena "
							+ parent.getName(), null);
			if (api != null) {
				p.sendMessage(ChatColor.BOLD
						+ ChatColor.RED.toString()
						+ "You and your teammates have been disgusied as villagers for the duration of this match, be advised.");
				MobDisguise d = new MobDisguise(DisguiseType.VILLAGER, true);
				d.setCustomName(p.getName());
				api.disguiseToAll(p, d);
			}
		}

		parent.getParent().logToFile(
				"The match on " + parent.getName() + " is starting", null);
		CommandSender sender = Bukkit.getConsoleSender();

		for (String s : parent.getParent().getConfig()
				.getStringList("prematchcommands." + parent.getName())) {
			Bukkit.dispatchCommand(sender, s);
		}

	}

	public void think() {

		List<String> players = new LinkedList<String>();

		players.addAll(redTeam);
		players.addAll(blueTeam);
		players.addAll(spectators);

		if (state == MatchState.STARTING) {
			if (timeTillStart <= 0) {
				Bukkit.getPluginManager().callEvent(new MatchStartEvent(this));
			} else {
				timeTillStart--;
			}
		} else if (state == MatchState.STAGE1) {
			if (timeTillNextStage <= 0) {

				state = MatchState.STAGE2;
				MatchTransitionEvent event = new MatchTransitionEvent(this,
						players);
				Bukkit.getPluginManager().callEvent(event);
				destroyWall();
			} else {
				timeTillNextStage--;
			}
		} else if (state == MatchState.STAGE2) {
			if (timeTillMatchEnd <= 0) {
				Bukkit.getPluginManager().callEvent(new MatchEndEvent(this, 0));
			} else {
				timeTillMatchEnd--;
			}
		}

		if (state == MatchState.PRESTART) {
			timeTillStart = parent.getParent().getConfig()
					.getInt("matchcountdown");
			timeTillNextStage = parent.getParent().getConfig()
					.getInt("firststagelength");
			timeTillMatchEnd = parent.getParent().getConfig()
					.getInt("secondstagelength");

			if (getParticipants().size() >= parent.getData().getMinPlayers()) {
				state = MatchState.STARTING;
				MatchPrestartEvent event = new MatchPrestartEvent(this);
				Bukkit.getPluginManager().callEvent(event);
			}
		}
	}

	public void triggerDeath(Player ply, PlayerDeathEvent e) {

		// if the player is on the dead team
		if (deadPlayers.contains(ply.getName())) {
			ply.setHealth(20.0);
			parent.getParent()
					.getRespawnListener()
					.addPlayer(ply.getName(),
							parent.getData().getSpectatorSpawn().toLocation());
			return;
		}

		else if (redTeam.contains(ply.getName())) {
			blueScore++;
			parent.getParent()
					.getRespawnListener()
					.addPlayer(ply.getName(),
							parent.getData().getSpectatorSpawn().toLocation());
			if (ply.getLastDamageCause().getEntity() instanceof Player) {
				ply.sendMessage(ChatColor.DARK_BLUE
						+ "You died in combat, you were killed by: "
						+ ChatColor.RED
						+ ((Player) ply.getLastDamageCause().getEntity())
								.getName());
				messageParticipants(ChatColor.RED
						+ ply.getName()
						+ ChatColor.BLACK
						+ " has perished at the hands of "
						+ ChatColor.DARK_BLUE
						+ ((Player) ply.getLastDamageCause().getEntity())
								.getName() + ", point to blue!");
				parent.getParent()
						.getScores()
						.kill(ply.getName(),
								((Player) ply.getLastDamageCause().getEntity())
										.getName(), parent.getName());
				parent.getParent().logToFile(
						ply.getName()
								+ " was killed by "
								+ ((Player) ply.getLastDamageCause()
										.getEntity()).getName(), "death");
			} else if (ply.getLastDamageCause().getCause() == DamageCause.SUICIDE) {
				parent.getParent().getScores().suicide(ply.getName());
				parent.getParent().logToFile(
						ply.getName() + " commited suicide", "death");
				messageParticipants(ply.getName() + " has commited suicide!");
				ply.sendMessage(ChatColor.RED
						+ "You have commited suicide given the other team a point!");
			} else {
				ply.sendMessage(ChatColor.RED
						+ "You died in combat, you were killed by: "
						+ ChatColor.DARK_BLUE
						+ ply.getLastDamageCause().getEntityType().toString());
				messageParticipants(ply.getName()
						+ " has perished at the hands of "
						+ ply.getLastDamageCause().getEntityType().toString()
						+ ChatColor.DARK_BLUE + ", point to blue!");
				parent.getParent().getScores().otherDeath(ply.getName());
				parent.getParent().logToFile(
						ply.getName()
								+ " was killed by "
								+ ply.getLastDamageCause().getCause()
										.toString(), "death");
			}
			deadPlayers.add(ply.getName());
		}

		// if the player is on the blue team
		else if (blueTeam.contains(ply.getName())) {
			redScore++;
			parent.getParent()
					.getRespawnListener()
					.addPlayer(ply.getName(),
							parent.getData().getSpectatorSpawn().toLocation());
			if (ply.getLastDamageCause().getEntity() instanceof Player) {
				ply.sendMessage("You died in combat, you were killed by: "
						+ ((Player) ply.getLastDamageCause().getEntity())
								.getName());
				messageParticipants(ChatColor.DARK_BLUE
						+ ply.getName()
						+ " has perished at the hands of "
						+ ChatColor.RED
						+ ((Player) ply.getLastDamageCause().getEntity())
								.getName() + ", point to red!");
				parent.getParent()
						.getScores()
						.kill(ply.getName(),
								((Player) ply.getLastDamageCause().getEntity())
										.getName(), parent.getName());
				parent.getParent().logToFile(
						ply.getName()
								+ " was killed by "
								+ ((Player) ply.getLastDamageCause()
										.getEntity()).getName(), "death");
			} else if (ply.getLastDamageCause().getCause() == DamageCause.SUICIDE) {
				parent.getParent().getScores().suicide(ply.getName());
				messageParticipants(ply.getName() + " has commited suicide!");
				parent.getParent().logToFile(
						ply.getName() + " commited suicide", "death");
				ply.sendMessage(ChatColor.RED
						+ "You have commited suicide given the other team a point!");
			} else {
				ply.sendMessage("You died in combat, you were killed by: "
						+ ply.getLastDamageCause().getEntityType().toString());
				messageParticipants(ply.getName()
						+ " has perished at the hands of "
						+ ply.getLastDamageCause().getEntityType().toString()
						+ ChatColor.RED + ", point to red!");
				parent.getParent().getScores().otherDeath(ply.getName());
				parent.getParent().logToFile(
						ply.getName()
								+ " was killed by "
								+ ply.getLastDamageCause().getCause()
										.toString(), "death");
			}
			deadPlayers.add(ply.getName());
		} else if (spectators.contains(ply.getName())) {
			parent.getParent()
					.getRespawnListener()
					.addPlayer(ply.getName(),
							parent.getData().getSpectatorSpawn().toLocation());
			return;
		} else if (lobbists.contains(ply.getName())) {
			parent.getParent().getRespawnListener()
					.addPlayer(ply.getName(), parent.getData().getLobbySpawn());
			return;
		} else {
			return;
		}

		Team win = checkWin();

		if (win != null) {
			Bukkit.getPluginManager().callEvent(new MatchEndEvent(this, 1));
		}
	}

	public void tryComplete() {
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

}
