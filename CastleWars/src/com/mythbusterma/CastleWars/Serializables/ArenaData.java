package com.mythbusterma.CastleWars.Serializables;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.BlockVector;

import com.sk89q.worldedit.bukkit.selections.Selection;

@SerializableAs("ArenaData")
public class ArenaData implements ConfigurationSerializable {
	
	private BlockVector max;
	private BlockVector min;
	private LocationSerializable blueSpawn;
	private LocationSerializable redSpawn;
	private LocationSerializable lobbySpawn;
	private String schematicName;
	private Orientation orientation;
	private World world;
	
	public static enum Orientation {
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
	

	//@SuppressWarnings("unchecked")
	public ArenaData (Map<String,Object> map) throws ArrayStoreException {
		max = (BlockVector)map.get("max");
		min = (BlockVector)map.get("min");
		
		blueSpawn = (LocationSerializable)map.get("blue");
		redSpawn = (LocationSerializable)map.get("red");
		lobbySpawn = (LocationSerializable)map.get("lobby");
		orientation = Orientation.fromInteger(((Integer)map.get("orientation")).intValue());
		
	}
	
	public ArenaData (Selection s) {
		max =  new BlockVector(s.getMaximumPoint().toVector());
		min = new BlockVector(s.getMinimumPoint().toVector());
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> temp = new HashMap<String,Object> ();
		temp.put("max", max);
		temp.put("min",min);
		temp.put("blue", blueSpawn);
		temp.put("red",redSpawn);
		temp.put("lobby", lobbySpawn);
		temp.put("orientation", Orientation.toInteger(orientation));
		return temp;
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
}
