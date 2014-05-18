package com.mythbusterma.CastleWars;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mythbusterma.CastleWars.Events.MatchThinkEvent;
import com.mythbusterma.CastleWars.Serializables.ArenaData;
import com.mythbusterma.CastleWars.Serializables.LocationSerializable;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import de.robingrether.idisguise.api.DisguiseAPI;

public class CastleWars extends JavaPlugin {

	public enum Team {
		BLUE,
		/**
		 * Dead players will be spectating the match
		 */
		DEAD, LOBBY, RED, SPECTATE,
		/**
		 * only used for match win, do not apply to player
		 */
		TIE;

		public Team fromInteger(int i) {
			if (i == 1) {
				return RED;
			} else if (i == 2) {
				return BLUE;
			} else if (i == 3) {
				return SPECTATE;
			} else if (i == 4) {
				return LOBBY;
			} else if (i == 5) {
				return DEAD;
			} else if (i == 6) {
				return TIE;
			} else {
				return null;
			}
		}

		public int toInteger(Team t) {
			if (t == RED) {
				return 1;
			} else if (t == BLUE) {
				return 2;
			} else if (t == SPECTATE) {
				return 3;
			} else if (t == LOBBY) {
				return 4;
			} else if (t == DEAD) {
				return 5;
			} else if (t == TIE) {
				return 6;
			} else {
				return -1;
			}
		}
	}

	public static final boolean Verbose = false;

	public static Integer getBukkitBuild() {
		String version = Bukkit.getVersion();
		Pattern pattern = Pattern.compile("(b)([0-9]+)(jnks)");
		Matcher matcher = pattern.matcher(version);

		if (matcher.find()) {
			return Integer.valueOf(matcher.group(2));
		}

		return null;
	}

	public static void log(Level l, String m) {
		Bukkit.getLogger().log(l, m);
	}

	public static void messageServer(String message) {
		for (World w : Bukkit.getServer().getWorlds()) {
			for (Player p : w.getPlayers()) {
				p.sendMessage(message);
			}
		}
	}

	private LinkedList<Arena> arenas;
	private ConfigAccessor arenasConfig;
	private DisguiseAPI disguises;
	private Economy eco;
	private Map<String, String> playerSelected; // player, arena name
	private RespawnListener respawnListener;
	private Scores scores;
	private ConfigAccessor scoresConfig;

	private SignManager signManager;

	private ConfigAccessor signsConfig;

	private TerrainManager tm;

	private WorldEditPlugin worldedit;

	public void addArena(Arena a) {
		arenas.add(a);
		if (Verbose) {
			getLogger().log(Level.INFO, "ADDED ARENA NAME: " + a.getName());
		}
	}

	public void deleteArena(Arena a) {
		arenas.remove(a);
		arenasConfig.getConfig().set("arenas." + a.getName(), null);

		File file = new File(getDataFolder() + "/arenas", a.getData()
				.getSchematicName() + ".schematic");

		file.delete();
		if (Verbose) {
			getLogger().log(Level.INFO, "DELETED ARENA NAME: " + a.getName());
		}
	}

	public Arena getArenaByName(String name) {
		for (Arena a : arenas) {
			if (a.getName().equalsIgnoreCase(name)) {
				return a;
			}
		}
		return null;
	}

	public List<Arena> getArenas() {
		return arenas;
	}

	public ConfigAccessor getArenasConfig() {
		return arenasConfig;
	}

	public DisguiseAPI getDisguise() {
		return disguises;
	}

	public Economy getEco() {
		return eco;
	}

	public Arena getPlayerSelection(CommandSender cs) {
		return getArenaByName(playerSelected.get(cs.getName()));
	}

	public RespawnListener getRespawnListener() {
		return respawnListener;
	}

	public Scores getScores() {
		return scores;
	}

	protected ConfigAccessor getScoresConfig() {
		return scoresConfig;
	}

	public ConfigAccessor getSignConfig() {
		return signsConfig;
	}

	public SignManager getSignManager() {
		return signManager;
	}

	public TerrainManager getTm() {
		return tm;
	}

	public WorldEditPlugin getWorldedit() {
		return worldedit;
	}

	public void loadArenas() {
		if (arenasConfig.getConfig().getConfigurationSection("arenas") != null) {
			Set<String> sl = arenasConfig.getConfig()
					.getConfigurationSection("arenas").getKeys(false);
			for (String s : sl) {
				Arena temp = Arena.arenaFromConfig(this, s);
				getServer().getConsoleSender()
						.sendMessage("Loaded Arena: " + s);
				if (Verbose) {
					getLogger().log(Level.INFO,
							"LOADED ARENA NAME :" + s + " FROM CONFIG");
				}
				addArena(temp);
			}
		}
	}

	/**
	 * @param message
	 * @param type
	 *            admin, death, payout, modify, or null for other
	 */
	public void logToFile(String message, String type) {
		try {
			if (type != null) {
				if (type.equalsIgnoreCase("admin")) {
					message = new String("@ -- ").concat(message);
				} else if (type.equalsIgnoreCase("death")) {
					message = new String("# -- ").concat(message);
				} else if (type.equalsIgnoreCase("payout")) {
					message = new String("$ -- ").concat(message);
				} else if (type.equalsIgnoreCase("modify")) {
					message = new String("*** -- ").concat(message);
				} else {
					message = new String("0 -- ").concat(message);
				}
			} else {
				message = new String("0 -- ").concat(message);
			}

			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");
			Date date = new Date(System.currentTimeMillis());

			message = new String(dateFormat.format(date) + " -- " + message);

			File dataFolder = getDataFolder();
			if (!dataFolder.exists()) {
				dataFolder.mkdir();
			}

			File saveTo = new File(getDataFolder(), "logs.txt");
			if (!saveTo.exists()) {
				saveTo.createNewFile();
			}
			FileWriter fw = new FileWriter(saveTo, true);

			PrintWriter pw = new PrintWriter(fw);

			pw.println(message);
			pw.println();

			pw.flush();

			pw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDisable() {
		saveArenas();
		saveConfig();
		for (Arena a : arenas) {
			for (String s : a.getCurrentMatch().getParticipants()) {
				a.getCurrentMatch().leave(s, true);
			}
			a.destroyWall();
		}
	}

	@Override
	public void onEnable() {

		// register the serializables
		ConfigurationSerialization.registerClass(ArenaData.class);
		ConfigurationSerialization.registerClass(LocationSerializable.class);

		worldedit = (WorldEditPlugin) getServer().getPluginManager().getPlugin(
				"WorldEdit");

		arenasConfig = new ConfigAccessor(this, "arenas.yml");
		arenasConfig.saveDefaultConfig();

		scoresConfig = new ConfigAccessor(this, "scores.yml");
		scoresConfig.saveDefaultConfig();

		signsConfig = new ConfigAccessor(this, "signs.yml");
		signsConfig.saveDefaultConfig();

		saveDefaultConfig();

		signManager = new SignManager(this);

		scores = new Scores(this);

		PluginManager pm = getServer().getPluginManager();

		respawnListener = new RespawnListener(this);

		pm.registerEvents(new MatchListener(this), this);
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new SignShop(this), this);
		pm.registerEvents(respawnListener, this);

		getCommand("castlewars").setExecutor(new CastleWarsCommands(this));
		getCommand("cw").setExecutor(new CastleWarsCommands(this));

		getServer().getLogger().log(Level.INFO,"CastleWars is still a beta! If you have any issues or problems with the pulgin please "
				+ "email wiirocksme@gmail.com with a screenshot of the error, a discription of what you were trying to do and what went wrong, "
				+ "and any other information you think may be useful in fixing the issue.");
		
		playerSelected = new HashMap<String, String>(getServer()
				.getMaxPlayers());

		arenas = new LinkedList<Arena>();

		if (getServer().getServicesManager().getRegistration(DisguiseAPI.class) != null) {
			disguises = getServer().getServicesManager()
					.getRegistration(DisguiseAPI.class).getProvider();
		}
		if (disguises != null) {
			log(Level.INFO, "iDisguises detected, enabling disguise support...");
		}

		// find the default world and initialize the terrain manager
		if (getConfig().getString("world") == null) {
			getConfig().set("world", getServer().getWorlds().get(0).getName());
			saveConfig();
		}

		tm = new TerrainManager(worldedit, getServer().getWorld(
				getConfig().getString("world")));

		loadArenas();

		// Schedule the match think events
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new Runnable() {
					@Override
					public void run() {
						MatchThinkEvent event = new MatchThinkEvent();
						Bukkit.getServer().getPluginManager().callEvent(event);
					}
				}, 100L, 20L);

		setupEconomy();
	}

	@SuppressWarnings("deprecation")
	public void payPlayer(String player, double amount, String reason) {
		if (amount == 0.00) {
			return;
		}

		if (eco != null) {
			EconomyResponse r = eco.depositPlayer(player, amount);
			if (reason != null && reason != "") {
				Bukkit.getPlayer(player).sendMessage(
						"You were given " + eco.format(r.amount) + " for "
								+ reason);
			} else {
				Bukkit.getPlayer(player).sendMessage(
						"You were given " + eco.format(r.amount));
			}
			getScores().payout(player, Double.valueOf(amount).intValue());
			Bukkit.getPlayer(player).sendMessage(
					"You now have " + eco.format(eco.getBalance(player)));
			logToFile(
					player + " was given " + amount + " for reason " + reason,
					"payout");
		}
	}

	public void saveArena(Arena a) {
		String path = new String("arenas." + a.getName());

		arenasConfig.getConfig().set(path + ".world", a.getWorld().getName());
		arenasConfig.getConfig().set(path + ".data", a.getData());

		if (Verbose) {
			getLogger().log(Level.INFO,
					"SAVED ARENA NAME: " + a.getName() + " TO CONFIG");
		}
	}

	public void saveArenas() {
		for (Arena a : arenas) {
			saveArena(a);
			if (Verbose) {
				getLogger().log(Level.INFO,
						"SAVED ARENA NAME: " + a.getName() + " TO CONFIG");
			}
		}
		arenasConfig.saveConfig();
	}

	public void setPlayerSelection(CommandSender cs, Arena a) {
		if (a == null) {
			playerSelected.put(cs.getName(), null);
			return;
		}
		playerSelected.put(cs.getName(), a.getName());
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			eco = economyProvider.getProvider();
		}
		return eco != null;
	}
}
