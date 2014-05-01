package com.mythbusterma.CastleWars;

import java.util.logging.Level;

import org.bukkit.World;

import com.mythbusterma.CastleWars.Serializables.ArenaData;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class Arena {
	
	private CastleWars parent;
	private String name;
	private ArenaData data;
	private World world;
	
	
	public Arena (CastleWars _parent, String _name, Selection s) {
		parent = _parent;
		name =_name;
		setData(new ArenaData(s));
		world = s.getWorld();
	}
	
	private Arena () {
		
	}
	
	public static Arena arenaFromConfig(CastleWars _parent, String _name) {
		Arena temp = new Arena();
		
		String path = "arenas." + _name;
		
		temp.setParent(_parent);
		temp.setName(_name);
		
		ConfigAccessor arenas = _parent.getArenasConfig();
		
		World w = _parent.getServer().getWorld(arenas.getConfig().getString(path + ".world"));
		temp.setWorld(w);
		
		ArenaData data = (ArenaData) arenas.getConfig().get(path + ".data");
		temp.setData(data);
		
		
		if(_parent.Verbose) {
			_parent.getLogger().log(Level.INFO, "CREATED ARENA " + _name+ " FROM CONFIG IN WORLD " + w.getName());
		}
		
		return temp;
	}

	private void setParent(CastleWars _parent) {
		parent = _parent;
		
	}

	public CastleWars getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArenaData getData() {
		return data;
	}

	private void setData(ArenaData data) {
		this.data = data;
	}

	public World getWorld() {
		return world;
	}

	private void setWorld(World world) {
		this.world = world;
	}
}
