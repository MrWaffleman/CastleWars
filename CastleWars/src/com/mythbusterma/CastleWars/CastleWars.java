package com.mythbusterma.CastleWars;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mythbusterma.CastleWars.Serializables.ArenaData;
import com.mythbusterma.CastleWars.Serializables.LocationSerializable;
import com.sk89q.worldedit.bukkit.*;

public class CastleWars extends JavaPlugin {
	
	private WorldEditPlugin worldedit;
	private ConfigAccessor arenasConfig;
	private HashMap<String, String> playerSelected; // player, arena name
	private LinkedList<Arena> arenas;
	public static final boolean Verbose = true;
	private TerrainManager tm;
	
	public enum Team 
	{
		RED,
		BLUE,
		SPECTATE,
		LOBBY;
		
		public int toInteger(Team t) {
			if (t==RED) {
				return 1;
			}
			else if (t==BLUE){
				return 2;
			}
			else if (t==SPECTATE) {
				return 3;
			}
			else if (t==LOBBY) {
				return 4;
			}
			else {
				return -1;
			}
		}
		public Team fromInteger(int i) {
			if (i == 1) {
				return RED;
			}
			else if (i== 2) {
				return BLUE;
			}
			else if(i == 3) {
				return SPECTATE;
			}
			else if (i == 4) {
				return LOBBY;
			}
			else return null;
		}
	}
	
	public void onEnable() {
		
		ConfigurationSerialization.registerClass(ArenaData.class);
		ConfigurationSerialization.registerClass(LocationSerializable.class);
		
		worldedit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		
		arenasConfig = new ConfigAccessor(this, "arenas.yml");
		arenasConfig.saveDefaultConfig();
		
		PluginManager pm = this.getServer().getPluginManager();
		
		pm.registerEvents(new MatchThinkListener(this), this);
		
		getCommand("castlewars").setExecutor(new CastleWarsCommands(this));
		getCommand("cw").setExecutor(new CastleWarsCommands(this));
		
		playerSelected = new HashMap<String, String>(getServer().getMaxPlayers());
		
		arenas = new LinkedList<Arena>();
		
		saveDefaultConfig();
		
		if(getConfig().getString("world") == null) {
			getConfig().set("world", getServer().getWorlds().get(0).getName());
			saveConfig();
		}
		
		tm = new TerrainManager(worldedit,getServer().getWorld(getConfig().getString("world")));
		
		loadArenas();
	}
	
	public void onDisable() {
		saveArenas();
	}

	public WorldEditPlugin getWorldedit() {
		return worldedit;
	}

	public ConfigAccessor getArenasConfig() {
		return arenasConfig;
	}
	
	public void setPlayerSelection(CommandSender cs, Arena a) {
		if ( a == null) {
			playerSelected.put(cs.getName(), null);
			return;
		}
		playerSelected.put(cs.getName(), a.getName());
	}
	
	public Arena getPlayerSelection(CommandSender cs) {
		return getArenaByName(playerSelected.get(cs.getName()));
	}
	
	public List<Arena> getArenas() {
		return arenas;
	}
	
	public void addArena (Arena a) {
		arenas.add(a);
		if(Verbose) {
			this.getLogger().log(Level.INFO, "ADDED ARENA NAME: " + a.getName());
		}
	}
	
	public Arena getArenaByName (String name) {
		for(Arena a: arenas) {
			if (a.getName().equalsIgnoreCase(name)) {
				return a;
			}
		}
		return null;
	}
	
	public void loadArenas () {
		if(arenasConfig.getConfig().getConfigurationSection("arenas")!= null){
			Set<String> sl = arenasConfig.getConfig().getConfigurationSection("arenas").getKeys(false);
			for (String s:sl) {
				Arena temp = Arena.arenaFromConfig(this, s);
				this.getServer().getConsoleSender().sendMessage("Loaded Arena: " + s);
				if(Verbose) {
					this.getLogger().log(Level.INFO, "LOADED ARENA NAME :" + s  + " FROM CONFIG");
				}
				addArena(temp);
			}
		}
	}
	
	public void saveArena (Arena a) {
		String path = new String("arenas."+a.getName());
		
		arenasConfig.getConfig().set(path+".world",a.getWorld().getName());
		arenasConfig.getConfig().set(path+".data", a.getData());
		
		
		
		if(Verbose) {
			this.getLogger().log(Level.INFO, "SAVED ARENA NAME: " + a.getName() + " TO CONFIG");
		}
	}
	
	public void deleteArena(Arena a) {
		arenas.remove(a);
		arenasConfig.getConfig().set("arenas." +a.getName(), null);
		
		File file = new File(this.getDataFolder()+ "/arenas",a.getData().getSchematicName() + ".schematic");
		
		file.delete();
		if(Verbose) {
			this.getLogger().log(Level.INFO, "DELETED ARENA NAME: " + a.getName());
		}
	}
	
	public void saveArenas () {
		for(Arena a: arenas) {
			saveArena(a);
			if(Verbose) {
				this.getLogger().log(Level.INFO, "SAVED ARENA NAME: " + a.getName() + " TO CONFIG");
			}
		}
		arenasConfig.saveConfig();
	}

	public TerrainManager getTm() {
		return tm;
	}
}
