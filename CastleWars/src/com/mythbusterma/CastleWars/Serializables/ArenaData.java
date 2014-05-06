package com.mythbusterma.CastleWars.Serializables;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.BlockVector;

import com.mythbusterma.CastleWars.Arena;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.data.DataException;

@SerializableAs("ArenaData")
public class ArenaData implements ConfigurationSerializable {
	
	private BlockVector max;
	private BlockVector min;
	private LocationSerializable blueSpawn;
	private LocationSerializable redSpawn;
	private LocationSerializable lobbySpawn;
	private LocationSerializable spectatorSpawn;
	private String schematicName;
	private Orientation orientation;
	private World world;
	private Arena parent;
	
	public static enum Orientation 
	{
		NORTHSOUTH,
		EASTWEST,
		HORIZONTAL;
		
		public static Orientation fromInteger (int i) {
			if (i==1) {
				return NORTHSOUTH;
			}
			if (i==2) {
				return EASTWEST;
			}
			if(i==3) {
				return HORIZONTAL;
			}
			else return null;
		}
		
		public static int toInteger (Orientation o) {
			if (o==NORTHSOUTH) {
				return 1;
			}
			if (o==EASTWEST) {
				return 2;
			}
			if (o==HORIZONTAL) {
				return 3;
			}
			else return -1;
		}
	}
	
	public ArenaData (Map<String,Object> map) throws ArrayStoreException {
		max = (BlockVector)map.get("max");
		min = (BlockVector)map.get("min");
		//root = (BlockVector)map.get("root");
		
		blueSpawn = (LocationSerializable)map.get("blue");
		redSpawn = (LocationSerializable)map.get("red");
		lobbySpawn = (LocationSerializable)map.get("lobby");
		spectatorSpawn = (LocationSerializable)map.get("spectator");
		
		orientation = Orientation.fromInteger(((Integer)map.get("orientation")).intValue());
		
		schematicName = (String)map.get("schem");
		
	}
	
	public ArenaData (Selection s, String fileName, Arena arena) throws FilenameException, 
											DataException, IOException {
		max =  new BlockVector(s.getMaximumPoint().toVector());
		min = new BlockVector(s.getMinimumPoint().toVector());
		
		parent = arena;
		
		setSchematicName(fileName);
		
		new File(parent.getParent().getDataFolder() + "/arenas").mkdirs();
		
		File file = new File(parent.getParent().getDataFolder() + "/arenas",fileName);
		
		parent.getTM().saveTerrain(file, s.getMaximumPoint(), s.getMinimumPoint());
		System.out.println("Saved");
		this.getParent().getParent().getLogger().log(Level.INFO, "Saved new file: " +file);
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> temp = new HashMap<String,Object> ();
		temp.put("max", max);
		temp.put("min",min);
		temp.put("blue", blueSpawn);
		temp.put("red",redSpawn);
		temp.put("lobby", lobbySpawn);
		temp.put("schem",schematicName);
		temp.put("spectator", spectatorSpawn);
		temp.put("orientation", Orientation.toInteger(orientation));
		return temp;
	}

	public LocationSerializable getSpectatorSpawn() {
		return spectatorSpawn;
	}

	public void setSpectatorSpawn(LocationSerializable spectatorSpawn) {
		this.spectatorSpawn = spectatorSpawn;
	}

	public BlockVector getMax() {
		return max;
	}

	public BlockVector getMin() {
		return min;
	}

	public Location getBlueSpawn() {
		return blueSpawn.toLocation();
	}

	public void setBlueSpawn(Location blueSpawn) {
		this.blueSpawn = new LocationSerializable(blueSpawn);
	}

	public Location getRedSpawn() {
		return redSpawn.toLocation();
	}

	public void setRedSpawn(Location redSpawn) {
		this.redSpawn = new LocationSerializable(redSpawn);
	}

	public Location getLobbySpawn() {
		return lobbySpawn.toLocation();
	}
	
	public void setOrientation(Orientation o) {
		orientation = o;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}

	public void setLobbySpawn(Location lobbySpawn) {
		this.lobbySpawn = new LocationSerializable(lobbySpawn);
	}

	public String getSchematicName() {
		return schematicName;
	}

	public void setSchematicName(String schematicName) {
		this.schematicName = schematicName;
	}

	public Arena getParent() {
		return parent;
	}

	public void setParent(Arena parent) {
		this.parent = parent;
	}
}
