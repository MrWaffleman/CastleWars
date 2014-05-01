package com.mythbusterma.CastleWars.Serializables;

import java.util.*;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class ArenaData implements ConfigurationSerializable {
	
	private Location max;
	private Location min;
	private List<BlockState> blocks;
	private Location blueSpawn;
	private Location redSpawn;
	private Location lobbySpawn;
	private Orientation orientation;
	
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
		max = (Location)map.get("max");
		min = (Location)map.get("min");
		
		blueSpawn = (Location)map.get("blue");
		redSpawn = (Location)map.get("red");
		lobbySpawn = (Location)map.get("lobby");
		try {
			blocks = (LinkedList<BlockState>)map.get("blocks");
			if (blocks == null) {
				throw new ArrayStoreException("Blocks recived from config was null!");
			}
		}
		finally {
		}
		orientation = Orientation.fromInteger(((Integer)map.get("orientation")).intValue());
		
	}
	
	public ArenaData (Selection s) {
		blocks = new LinkedList<BlockState>();
		
		max = s.getMaximumPoint();
		min = s.getMinimumPoint();
		boolean xIncreasing = max.getX()>min.getX();
		boolean yIncreasing = max.getY()>min.getY();
		boolean zIncreasing = max.getZ()>min.getZ();
		
		int x = min.getBlockX();
		int y = min.getBlockY();
		int z = min.getBlockZ();
		while (true) {
			while(true) {
				while(true) {
					
					blocks.add(s.getWorld().getBlockAt(x, y, z).getState());
					
					if(zIncreasing) {
						if(z>=max.getBlockZ()) {
							break;
						}
						z++;
					}
					else {
						if(z<=max.getBlockZ()) {
							break;
						}
						z--;
					}
					
				}
				if(yIncreasing) {
					if(y>=max.getBlockY()) {
						break;
					}
					y++;
				}
				else {
					if(y<=max.getBlockY()) {
						break;
					}
					y--;
				}
			}
			if (xIncreasing) {
				if(x>=max.getBlockX()){
					break;
				}
				x++;
			}
			else {
				if(x<=max.getBlockX()) {
					break;
				}
				x--;
			}
		}
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> temp = new HashMap<String,Object> ();
		temp.put("max", max);
		temp.put("min",min);
		temp.put("blocks", blocks);
		temp.put("blue", blueSpawn);
		temp.put("red", blueSpawn);
		temp.put("lobby", lobbySpawn);
		temp.put("orientation", Orientation.toInteger(orientation));
		return temp;
	}

	public Location getMax() {
		return max;
	}


	public Location getMin() {
		return min;
	}

	public Location getBlueSpawn() {
		return blueSpawn;
	}

	public void setBlueSpawn(Location blueSpawn) {
		this.blueSpawn = blueSpawn;
	}

	public Location getRedSpwan() {
		return redSpawn;
	}

	public void setRedSpwan(Location redSpawn) {
		this.redSpawn = redSpawn;
	}

	public Location getLobbySpawn() {
		return lobbySpawn;
	}
	
	public void setOrientation(Orientation o) {
		orientation = o;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}

	public void setLobbySpawn(Location lobbySpawn) {
		this.lobbySpawn = lobbySpawn;
	}
}
