package com.mythbusterma.CastleWars;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythbusterma.CastleWars.Serializables.ArenaData.Orientation;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class Utils {

	private CastleWars parent;

	public Utils(CastleWars _parent) {
		parent = _parent;
	}

	public int createArena(CommandSender creator, String[] args) {

		boolean used = false;
		for (String s : parent.getArenasConfig().getConfig()
				.getStringList("arenas")) {
			if (s.equalsIgnoreCase(args[0])) {
				used = true;
			}
		}

		if (used) {
			creator.sendMessage("Arena by that name already exists!");
			return -100;
		}

		else {
			Selection selection = parent.getWorldedit().getSelection(
					(Player) creator);

			if (selection != null) {
				if (selection instanceof Polygonal2DSelection) {
					creator.sendMessage("Plugin does not accept polyagnoal selections!");
					return -101;
				} else {
					int minX = parent.getConfig().getInt("mins.minx");
					int minY = parent.getConfig().getInt("mins.miny");
					int minZ = parent.getConfig().getInt("mins.minz");

					Location max = selection.getMaximumPoint();
					Location min = selection.getMinimumPoint();

					if (Math.abs(max.getBlockX() - min.getBlockX()) < minX) {
						creator.sendMessage("Size in X direction is too small!");
						return -201;
					}
					if (Math.abs(max.getBlockY() - min.getBlockY()) < minY) {
						creator.sendMessage("Size in Y direction is too small!");
						return -202;
					}
					if (Math.abs(max.getBlockZ() - min.getBlockZ()) < minZ) {
						creator.sendMessage("Size in Z direction is too small!");
						return -203;
					}

					if (CastleWars.Verbose) {
						parent.getLogger().log(
								Level.INFO,
								creator.getName() + " CREATED ARENA NAME: "
										+ args[0]);
					}

					Arena temp = new Arena(parent, args[0], selection);

					creator.sendMessage("Created new arena! You must set Red, Blue, and Lobby spawns before it can be used!");

					parent.addArena(temp);

					parent.setPlayerSelection(creator, temp);
					parent.logToFile(creator.getName() + " created arena "
							+ temp.getName(), "modify");
				}
			} else {
				creator.sendMessage("You haven't selected anything!");
				return -102;
			}

		}

		return 0;

	}

	public void setBlueSpawn(Player sender, String arena) {
		parent.getArenaByName(arena).getData()
				.setBlueSpawn(sender.getLocation());
		parent.getArenaByName(arena).getCurrentMatch().tryComplete();
		parent.logToFile(sender.getName() + " set the blue team spawn of "
				+ arena, "modify");

	}

	public void setLobbySpawn(Player sender, String arena)
			throws NullPointerException {
		parent.getArenaByName(arena).getData()
				.setLobbySpawn(sender.getLocation());
		parent.getArenaByName(arena).getCurrentMatch().tryComplete();
		parent.logToFile(sender.getName() + " set the lobby spawn of " + arena,
				"modify");
	}

	public void setOrientation(String arena, Orientation o, CommandSender sender) {
		parent.getArenaByName(arena).getData().setOrientation(o);
		parent.getArenaByName(arena).getCurrentMatch().tryComplete();
		parent.logToFile(sender + " changed the orientation of " + arena
				+ " to " + o.toString(), "modify");

	}

	public void setRedSpawn(Player sender, String arena) {
		parent.getArenaByName(arena).getData()
				.setRedSpawn(sender.getLocation());
		parent.getArenaByName(arena).getCurrentMatch().tryComplete();
		parent.logToFile(sender.getName() + " set the red team spawn of "
				+ arena, "modify");
	}
}
