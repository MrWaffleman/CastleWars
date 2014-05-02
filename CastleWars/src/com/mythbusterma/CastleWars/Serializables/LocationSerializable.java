package com.mythbusterma.CastleWars.Serializables;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Location")
public class LocationSerializable implements ConfigurationSerializable {
	
	private final float x;
	private final float y;
	private final float z;
	private final float pitch;
	private final float yaw;
	private final String world;
	
	public LocationSerializable (Map<String,Object> map) {
		x = Float.valueOf(((String) map.get("x"))).floatValue();
		y = Float.valueOf(((String) map.get("y"))).floatValue();
		z = Float.valueOf(((String) map.get("z"))).floatValue();
		pitch = Float.valueOf(((String) map.get("pitch"))).floatValue();
		yaw = Float.valueOf(((String) map.get("yaw"))).floatValue();
		world = (String) map.get("world");
	}
	
	
	public LocationSerializable (Location l) {
		x = (float)l.getX();
		y = (float)l.getY();
		z = (float)l.getZ();
		yaw = (float) l.getYaw();
		pitch = (float) l.getPitch();
		world = l.getWorld().getName();
	}
	
	public Location toLocation() {
		return new Location (Bukkit.getWorld(world),
				x, 
				y, 
				z,
				yaw, 
				pitch);
	}
	
	@Override
	public Map<String, Object> serialize() {
		// TODO Auto-generated method stub
		
		HashMap<String,Object> temp = new HashMap<String,Object>();
		
		temp.put("x",Float.valueOf(x).toString());
		temp.put("y",Float.valueOf(y).toString());
		temp.put("z",Float.valueOf(z).toString());
		temp.put("pitch",Float.valueOf(pitch).toString());
		temp.put("yaw", Float.valueOf(yaw).toString());
		temp.put("world", world);
		temp.put("yaw",Float.valueOf(yaw).toString());
		
		return temp;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public String getWorld() {
		return world;
	}

}
