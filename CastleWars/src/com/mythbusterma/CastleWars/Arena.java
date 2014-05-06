package com.mythbusterma.CastleWars;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.World;

import com.mythbusterma.CastleWars.Serializables.ArenaData;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.data.DataException;

public class Arena {
	
	private CastleWars parent;
	private String name;
	private ArenaData data;
	private World world;
	private Match currentMatch;
	
	public Arena (CastleWars _parent, String _name, Selection s) {
		parent = _parent;
		name =_name;
		ArenaData _data = null;
		try {
			_data = new ArenaData(s,name,this);
		} catch (FilenameException | DataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		currentMatch = new Match(this);
		
		setData(_data);
		world = s.getWorld();
	}
	
	public void restore () throws NullPointerException, FilenameException {
		if (data.getSchematicName() == null || data.getSchematicName() == "") {
			throw new FilenameException ("Schematic name is null");
		}
		
		File file = new File(parent.getDataFolder() + "/arenas", data.getSchematicName());
		
		try {
			parent.getTm().loadSchematic(file);
		} catch (MaxChangedBlocksException | EmptyClipboardException
				| DataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Saves over previous instances of the schematic for this arena
	 * 
	 */
	public void save () {
		File file = new File(parent.getDataFolder() + "/arenas",data.getSchematicName());
		try {
			parent.getTm().saveTerrain(file, data.getMax().toLocation(world),data.getMin().toLocation(world));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
		data.setParent(temp);
		
		if(CastleWars.Verbose) {
			_parent.getLogger().log(Level.INFO, "CREATED ARENA " + _name+ " FROM CONFIG IN WORLD " + w.getName());
		}
		Match tempMatch = new Match(temp);
		
		temp.setCurrentMatch(tempMatch);
		tempMatch.initilize(_parent);
		
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
	
	public TerrainManager getTM () {
		return parent.getTm();
	}

	public Match getCurrentMatch() {
		return currentMatch;
	}

	public void setCurrentMatch(Match currentMatch) {
		this.currentMatch = currentMatch;
	}
}
