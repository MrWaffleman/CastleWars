package com.mythbusterma.CastleWars;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.BlockVector;

import com.mythbusterma.CastleWars.Serializables.ArenaData;
import com.mythbusterma.CastleWars.Serializables.ArenaData.Orientation;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.data.DataException;

public class Arena {

	public static Arena arenaFromConfig(CastleWars _parent, String _name) {
		Arena temp = new Arena();

		String path = "arenas." + _name;

		temp.setParent(_parent);
		temp.setName(_name);

		ConfigAccessor arenas = _parent.getArenasConfig();

		World w = _parent.getServer().getWorld(
				arenas.getConfig().getString(path + ".world"));
		temp.setWorld(w);

		ArenaData data = (ArenaData) arenas.getConfig().get(path + ".data");
		temp.setData(data);
		data.setParent(temp);

		if (CastleWars.Verbose) {
			_parent.getLogger().log(
					Level.INFO,
					"CREATED ARENA " + _name + " FROM CONFIG IN WORLD "
							+ w.getName());
		}
		Match tempMatch = new Match(temp);

		temp.setCurrentMatch(tempMatch);

		return temp;
	}

	private Match currentMatch;
	private ArenaData data;
	private String name;
	private CastleWars parent;
	private List<BlockState> prewall;
	private List<BlockVector> wall;

	private World world;

	private Arena() {
	}

	public Arena(CastleWars _parent, String _name, Selection s) {
		parent = _parent;
		name = _name;
		ArenaData _data = null;
		try {
			_data = new ArenaData(s, name, this);
		} catch (FilenameException | DataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setData(_data);
		currentMatch = new Match(this);

		world = s.getWorld();
		wall = new LinkedList<BlockVector>();
		prewall = new LinkedList<BlockState>();
	}

	@SuppressWarnings("deprecation")
	public List<BlockVector> buildWall() {

		LinkedList<BlockVector> blocks = new LinkedList<BlockVector>();

		LinkedList<BlockState> preBlocks = new LinkedList<BlockState>();
		int wallmat = parent.getConfig().getInt("wallmat");

		if (data.getOrientation() == Orientation.NORTHSOUTH) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage("Orientation: northsouth");
			int middle = (data.getMin().getBlockX() + data.getMax().getBlockX()) / 2;
			for (int i = data.getMin().getBlockZ() - 1; i <= data.getMax()
					.getBlockZ(); i++) {
				for (int j = data.getMin().getBlockY() - 1; j <= data.getMax()
						.getBlockY(); j++) {
					Block block = world.getBlockAt(middle, j, i);

					preBlocks.add(block.getState());
					block.setTypeId(wallmat);
					BlockVector bv = new BlockVector(middle, j, i);
					blocks.add(bv);
				}
			}
		} else if (data.getOrientation() == Orientation.HORIZONTAL) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage("Orientation: horizontal");
			int middle = (data.getMin().getBlockY() + data.getMax().getBlockY()) / 2;
			for (int i = data.getMin().getBlockZ() - 1; i <= data.getMax()
					.getBlockZ(); i++) {
				for (int j = data.getMin().getBlockX() - 1; j <= data.getMax()
						.getBlockX(); j++) {
					Block block = world.getBlockAt(j, middle, i);

					preBlocks.add(block.getState());
					block.setTypeId(wallmat);
					BlockVector bv = new BlockVector(j, middle, i);
					blocks.add(bv);
				}
			}
		} else if (data.getOrientation() == Orientation.EASTWEST) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage("Orientation: eastwest");
			int middle = (data.getMin().getBlockZ() + data.getMax().getBlockZ()) / 2;
			for (int i = data.getMin().getBlockY() - 1; i <= data.getMax()
					.getBlockY(); i++) {
				for (int j = data.getMin().getBlockX() - 1; j <= data.getMax()
						.getBlockX(); j++) {
					Block block = world.getBlockAt(j, i, middle);

					preBlocks.add(block.getState());
					block.setTypeId(wallmat);
					BlockVector bv = new BlockVector(j, i, middle);
					blocks.add(bv);
				}
			}
		}

		prewall = preBlocks;
		wall = blocks;
		return blocks;
	}

	public void destroyWall() {
		if (prewall != null) {
			for (BlockState bs : prewall) {
				bs.update(true, false);
			}
			prewall.clear();
			wall.clear();
		}
	}

	public Match getCurrentMatch() {
		return currentMatch;
	}

	public ArenaData getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	public CastleWars getParent() {
		return parent;
	}

	public TerrainManager getTM() {
		return parent.getTm();
	}

	public List<BlockVector> getWall() {
		return wall;
	}

	public World getWorld() {
		return world;
	}

	public void restore() throws NullPointerException, FilenameException {
		if (data.getSchematicName() == null || data.getSchematicName() == "") {
			throw new FilenameException("Schematic name is null");
		}

		File file = new File(parent.getDataFolder() + "/arenas",
				data.getSchematicName());

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
	public void save() {
		File file = new File(parent.getDataFolder() + "/arenas",
				data.getSchematicName());
		try {
			parent.getTm().saveTerrain(file, data.getMax().toLocation(world),
					data.getMin().toLocation(world));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCurrentMatch(Match currentMatch) {
		this.currentMatch = currentMatch;
	}

	private void setData(ArenaData data) {
		this.data = data;
	}

	public void setName(String name) {
		this.name = name;
	}

	private void setParent(CastleWars _parent) {
		parent = _parent;

	}

	private void setWorld(World world) {
		this.world = world;
	}

}
