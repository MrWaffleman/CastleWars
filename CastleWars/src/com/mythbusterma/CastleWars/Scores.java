package com.mythbusterma.CastleWars;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.mythbusterma.CastleWars.CastleWars.Team;

public class Scores {
	public class ArenaScore {
		private ArenaScore() {

		}
	}

	public class PlayerScore {

		private String id;
		private String lastName;
		private String path;
		// default constructor with empty scores
		private Map<String, Integer> values;

		private PlayerScore() {
			values = new HashMap<String, Integer>();
			id = null;
			path = null;

		}

		public PlayerScore(String identifier) {
			id = identifier;
			path = "players." + id;
			values = new HashMap<String, Integer>();
			Map<String, Object> temp = config.getConfigurationSection(path)
					.getValues(false);
			for (Entry<String, Object> e : temp.entrySet()) {
				if (e.getValue() instanceof Integer) {
					values.put(e.getKey(), (Integer) e.getValue());
				} else if (e.getValue() instanceof String) {
					lastName = (String) e.getValue();
				}
			}
		}

		public int getDeaths() {
			int deaths = 0;
			if (values.containsKey("reddeaths")) {
				deaths += values.get("reddeaths");
			}
			if (values.containsKey("bluedeaths")) {
				deaths += values.get("bluedeaths");
			}
			return deaths;
		}

		public double getKD() {
			if (getDeaths() != 0) {
				return getKills() / getDeaths();
			}
			return 0;
		}

		public int getKills() {
			int kills = 0;

			if (values.containsKey("redkills")) {
				kills += values.get("redkills");
			}
			if (values.containsKey("bluekills")) {
				kills += values.get("bluekills");
			}

			return kills;

		}

		public String getLastKnownName() {
			return lastName;
		}

		public int getSuicides() {
			if (values.containsKey("suicides")) {
				return values.get("suicides");
			}
			return 0;
		}

		public int getValue(String key) {
			return values.get(key);
		}
	}

	private FileConfiguration config;

	private CastleWars parent;

	private ConfigAccessor scoresConfigFile;

	public Scores(CastleWars _parent) {
		parent = _parent;
		scoresConfigFile = parent.getScoresConfig();
		config = scoresConfigFile.getConfig();
	}

	public void addTechnology(String player, int amount) {
		incrementDataPlayer(player, "technology", amount);
	}

	public void breakBlock(String player) {
		incrementDataPlayer(player, "blocksbroken", 1);
	}

	public void disconnect(String player) {
		incrementDataPlayer(player, "disconnects", 1);
	}

	public ArenaScore getArenaScore(String arena) {
		return null;
	}

	/**
	 * Only works for online players
	 * 
	 * @see Scores#getOfflinePlayer(UUID id)
	 * @param player
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public PlayerScore getPlayerScore(String player) {
		String identifier = player;

		if (parent.getServer().getPlayer(player) == null) {
			throw new NullPointerException("Player by that name not found!");
		}
		if (CastleWars.getBukkitBuild() >= 3052) {
			identifier = parent.getServer().getPlayer(player).getUniqueId()
					.toString();
		}
		String path = "players." + identifier;
		if (config.contains(path)) {
			return new PlayerScore(identifier);
		} else {
			return new PlayerScore();
		}
	}

	public PlayerScore getPlayerScore(UUID id) {
		if (CastleWars.getBukkitBuild() >= 3052) {
			return null;
		} else {
			return new PlayerScore(id.toString());
		}
	}

	public void incrementDataArena(String arena, String data, int amount) {
		if (parent.getArenaByName(arena) == null) {
			throw new NullPointerException("Arena by that name not found!");
		}
		int dataAmount = -1;
		String path = "arenas." + arena + ".";
		if (config.contains(path + data)) {
			dataAmount = config.getInt(path + data);
			dataAmount += amount;
		} else {
			dataAmount = amount;
		}
		config.set(path + data, dataAmount);
		save();
	}

	@SuppressWarnings("deprecation")
	public void incrementDataPlayer(String player, String data, int amount) {
		String identifier = player;

		if (parent.getServer().getPlayer(player) == null) {
			throw new NullPointerException("Player by that name not found!");
		}
		if (CastleWars.getBukkitBuild() >= 3052) {
			identifier = parent.getServer().getPlayer(player).getUniqueId()
					.toString();
		}
		int dataAmount = -1;
		String path = "players." + identifier + ".";
		if (config.contains(path + data)) {
			dataAmount = config.getInt(path + data);
			dataAmount += amount;
		} else {
			dataAmount = amount;
		}
		config.set(path + data, dataAmount);
		save();
	}

	public void kick(String player) {
		incrementDataPlayer(player, "kicks", 1);
	}

	public void kill(String victim, String killer, String arena) {
		Team killTeam = parent.getArenaByName(arena).getCurrentMatch()
				.playerTeam(killer);
		Team victimTeam = parent.getArenaByName(arena).getCurrentMatch()
				.playerTeam(victim);

		if (victimTeam == Team.RED) {
			incrementDataPlayer(victim, "reddeaths", 1);
		} else if (victimTeam == Team.BLUE) {
			incrementDataPlayer(victim, "bluedeaths", 1);
		}

		if (killTeam == victimTeam) {
			incrementDataPlayer(killer, "friendlykills", 1);
		} else if (killTeam == Team.RED) {
			incrementDataPlayer(killer, "redkills", 1);
		} else if (killTeam == Team.BLUE) {
			incrementDataPlayer(killer, "bluekills", 1);
		}
	}

	/**
	 * Accepts codes for game overs and stores the information to the scores,
	 * used for keeping track of scores if any lists don't contain players, pass
	 * empty lists not null
	 * 
	 * 
	 * @param cause
	 *            0 for time, 1 for all players on one team eliminated, 2 for
	 *            force stop, 3 for other game over
	 * @param winner
	 *            The team that won, will accept Team.TIE
	 * @param arena
	 *            The arena this match was played on (name)
	 * @param lastManStanding
	 *            List of the last players standing on the winning team
	 * @param lastHopes
	 *            List of the last players standing on the losing team
	 * @param blueTeam
	 *            The blue team
	 * @param redTeam
	 *            The red team
	 */
	public void matchEnd(int cause, Team winner, String arena,
			List<String> lastManStanding, List<String> lastHopes,
			List<String> blueTeam, List<String> redTeam) {
		if (cause == 2) {
			incrementDataArena(arena, "forcestops", 1);
			return;
		} else if (cause == 1 || cause == 0 || cause == 3) {
			if (winner == Team.BLUE) {
				incrementDataArena(arena, "bluewins", 1);
			} else if (winner == Team.RED) {
				incrementDataArena(arena, "redwins", 1);
			} else if (winner == Team.TIE) {
				incrementDataArena(arena, "ties", 1);
			} else {
				throw new IllegalArgumentException(
						"Winner was not RED, BLUE, or TIE");
			}
		}
		if (cause == 1) {
			incrementDataArena(arena, "allplayerseliminatedends", 1);
		} else if (cause == 0) {
			incrementDataArena(arena, "timeexpiredends", 1);
		} else if (cause == 3) {
			incrementDataArena(arena, "othergameends", 1);
		}

		for (String s : lastManStanding) {
			incrementDataPlayer(s, "lastmanstanding", 1);
		}
		for (String s : lastHopes) {
			incrementDataPlayer(s, "lasthope", 1);
		}
		for (String s : blueTeam) {
			incrementDataPlayer(s, "blueplays", 1);
		}
		for (String s : redTeam) {
			incrementDataPlayer(s, "redplays", 1);
		}
		if (winner != Team.TIE) {
			if (winner == Team.BLUE) {
				for (String s : blueTeam) {
					incrementDataPlayer(s, "bluewins", 1);
				}
			} else {
				for (String s : redTeam) {
					incrementDataPlayer(s, "redwins", 1);
				}
			}
		} else {
			List<String> temp = new LinkedList<String>(redTeam);
			temp.addAll(blueTeam);
			for (String s : temp) {
				incrementDataPlayer(s, "ties", 1);
			}
		}

	}

	public void otherDeath(String player) {
		incrementDataPlayer(player, "otherdeaths", 1);
	}

	public void payout(String player, int amount) {
		incrementDataPlayer(player, "totalpayout", amount);
	}

	public void save() {
		scoresConfigFile.saveConfig();
	}

	public void suicide(String victim) {
		incrementDataPlayer(victim, "suicides", 1);
	}

	public void updateName(Player player) {
		String identifier = player.getName();
		if (CastleWars.getBukkitBuild() >= 3052) {
			identifier = player.getUniqueId().toString();
		}

		String path = "players." + identifier + ".";

		config.set(path + "lastname", player.getName());
		save();
	}

}
