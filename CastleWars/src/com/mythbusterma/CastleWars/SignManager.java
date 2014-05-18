package com.mythbusterma.CastleWars;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.util.BlockVector;

import com.mythbusterma.CastleWars.CastleWars.Team;
import com.mythbusterma.CastleWars.Match.MatchState;

public class SignManager {
	private static Set<BlockVector> scoreboardSigns = new LinkedHashSet<>();

	private CastleWars parent;

	public SignManager(CastleWars _parent) {
		parent = _parent;
	}

	/*
	 * Directions for wall_signs are: 2 for east, 3 for west, 4 for north, 5 for
	 * south
	 */

	@SuppressWarnings("unchecked")
	public void addSign(Location l) {
		BlockVector vector = new BlockVector(l.toVector());

		if (parent.getSignConfig().getConfig().getList("signs") != null) {
			List<BlockVector> temp = (List<BlockVector>) parent.getSignConfig()
					.getConfig().getList("signs");
			temp.add(vector);
			parent.getSignConfig().getConfig().set("signs", temp);
		} else {
			LinkedList<BlockVector> temp = new LinkedList<BlockVector>();
			temp.add(vector);
			parent.getSignConfig().getConfig().set("signs", temp);
		}
		parent.getSignConfig().saveConfig();
	}

	public Set<BlockVector> getScoreboardSigns() {
		return new LinkedHashSet<>(scoreboardSigns);
	}

	@SuppressWarnings("deprecation")
	private void updateScoreboard(Sign sign) {

		String name = sign.getLine(2);
		Arena sel = null;
		for (Arena a : parent.getArenas()) {
			if (a.getName().equalsIgnoreCase(name)) {
				sel = a;
				break;
			}
		}

		Match match = sel.getCurrentMatch();
		BlockState[] blueTeam = new BlockState[6];
		BlockState[] redTeam = new BlockState[6];
		Sign blueLabel = null;
		Sign redLabel = null;
		Sign blueTech = null;
		Sign redTech = null;

		// update things that get updated always
		sign.setLine(3,
				String.valueOf(sel.getCurrentMatch().getParticipants().size())
						+ '/' + sel.getData().getMaxPlayers());
		sign.update();

		if (!(sign.getBlock().getRelative(BlockFace.DOWN).getState() instanceof Sign)) {
			sign.getBlock().getRelative(BlockFace.DOWN)
					.setType(Material.WALL_SIGN);
			sign.getBlock().getRelative(BlockFace.DOWN)
					.setData(sign.getBlock().getData());
			Sign below = (Sign) sign.getBlock().getRelative(BlockFace.DOWN)
					.getState();
			below.setLine(0, "Red Score: ");
			below.setLine(1, "Blue Score: ");
			below.setLine(2, "Status");
		}

		if (!(sign.getBlock().getRelative(BlockFace.UP).getState() instanceof Sign)) {
			sign.getBlock().getRelative(BlockFace.UP)
					.setType(Material.WALL_SIGN);
			sign.getBlock().getRelative(BlockFace.UP)
					.setData(sign.getBlock().getData());
			Sign above = (Sign) sign.getBlock().getRelative(BlockFace.UP)
					.getState();
			above.setLine(0, "Next stage");
			above.setLine(2, "Winner");
		}

		// if the sign is facing east
		if (sign.getBlock().getData() == 5) {

			if (!(sign.getBlock().getRelative(BlockFace.NORTH)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.NORTH)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.NORTH)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			redLabel = (Sign) sign.getBlock().getRelative(BlockFace.NORTH)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState();
			if (!(sign.getBlock().getRelative(BlockFace.NORTH)
					.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.NORTH)
						.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.NORTH)
						.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			redTech = (Sign) sign.getBlock().getRelative(BlockFace.NORTH)
					.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState();

			if (!(sign.getBlock().getRelative(BlockFace.SOUTH)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.SOUTH)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.SOUTH)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			blueLabel = (Sign) sign.getBlock().getRelative(BlockFace.SOUTH)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState();

			if (!(sign.getBlock().getRelative(BlockFace.SOUTH)
					.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.SOUTH)
						.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.SOUTH)
						.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			blueTech = (Sign) sign.getBlock().getRelative(BlockFace.SOUTH)
					.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState();

			Block rel = sign.getBlock().getRelative(BlockFace.NORTH)
					.getRelative(BlockFace.UP);
			redTeam[0] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[1] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[2] = rel.getState();
			rel = rel.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP);
			redTeam[3] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[4] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[5] = rel.getState();

			rel = sign.getBlock().getRelative(BlockFace.SOUTH)
					.getRelative(BlockFace.UP);
			blueTeam[0] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[1] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[2] = rel.getState();
			rel = rel.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP);
			blueTeam[3] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[4] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[5] = rel.getState();

		}
		// if the sign is facing west
		else if (sign.getBlock().getData() == 4) {

			if (!(sign.getBlock().getRelative(BlockFace.SOUTH)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.SOUTH)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.SOUTH)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			redLabel = (Sign) sign.getBlock().getRelative(BlockFace.SOUTH)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState();
			if (!(sign.getBlock().getRelative(BlockFace.SOUTH)
					.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.SOUTH)
						.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.SOUTH)
						.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			redTech = (Sign) sign.getBlock().getRelative(BlockFace.SOUTH)
					.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState();

			if (!(sign.getBlock().getRelative(BlockFace.NORTH)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.NORTH)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.NORTH)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			blueLabel = (Sign) sign.getBlock().getRelative(BlockFace.NORTH)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState();

			if (!(sign.getBlock().getRelative(BlockFace.NORTH)
					.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.NORTH)
						.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.NORTH)
						.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			blueTech = (Sign) sign.getBlock().getRelative(BlockFace.NORTH)
					.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState();

			Block rel = sign.getBlock().getRelative(BlockFace.SOUTH)
					.getRelative(BlockFace.UP);
			redTeam[0] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[1] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[2] = rel.getState();
			rel = rel.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP);
			redTeam[3] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[4] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[5] = rel.getState();

			rel = sign.getBlock().getRelative(BlockFace.NORTH)
					.getRelative(BlockFace.UP);
			blueTeam[0] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[1] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[2] = rel.getState();
			rel = rel.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP);
			blueTeam[3] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[4] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[5] = rel.getState();
		}
		// if the sign is facing north
		else if (sign.getBlock().getData() == 2) {
			if (!(sign.getBlock().getRelative(BlockFace.WEST)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.WEST)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.WEST)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			redLabel = (Sign) sign.getBlock().getRelative(BlockFace.WEST)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState();
			if (!(sign.getBlock().getRelative(BlockFace.WEST)
					.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.WEST)
						.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.WEST)
						.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			redTech = (Sign) sign.getBlock().getRelative(BlockFace.WEST)
					.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState();

			if (!(sign.getBlock().getRelative(BlockFace.EAST)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.EAST)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.EAST)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			blueLabel = (Sign) sign.getBlock().getRelative(BlockFace.EAST)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState();

			if (!(sign.getBlock().getRelative(BlockFace.EAST)
					.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.EAST)
						.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.EAST)
						.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			blueTech = (Sign) sign.getBlock().getRelative(BlockFace.EAST)
					.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState();

			Block rel = sign.getBlock().getRelative(BlockFace.WEST)
					.getRelative(BlockFace.UP);
			redTeam[0] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[1] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[2] = rel.getState();
			rel = rel.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP);
			redTeam[3] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[4] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[5] = rel.getState();

			rel = sign.getBlock().getRelative(BlockFace.EAST)
					.getRelative(BlockFace.UP);
			blueTeam[0] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[1] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[2] = rel.getState();
			rel = rel.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP);
			blueTeam[3] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[4] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[5] = rel.getState();
		}
		// if the sign is facing south
		else if (sign.getBlock().getData() == 3) {
			if (!(sign.getBlock().getRelative(BlockFace.EAST)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.EAST)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.EAST)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			redLabel = (Sign) sign.getBlock().getRelative(BlockFace.EAST)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState();

			if (!(sign.getBlock().getRelative(BlockFace.EAST)
					.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.EAST)
						.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.EAST)
						.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			redTech = (Sign) sign.getBlock().getRelative(BlockFace.EAST)
					.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState();

			if (!(sign.getBlock().getRelative(BlockFace.WEST)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.WEST)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.WEST)
						.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			blueLabel = (Sign) sign.getBlock().getRelative(BlockFace.WEST)
					.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.getState();

			if (!(sign.getBlock().getRelative(BlockFace.WEST)
					.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState() instanceof Sign)) {
				sign.getBlock().getRelative(BlockFace.WEST)
						.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
				sign.getBlock().getRelative(BlockFace.WEST)
						.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP)
						.setData(sign.getBlock().getData());
			}
			blueTech = (Sign) sign.getBlock().getRelative(BlockFace.WEST)
					.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)
					.getRelative(BlockFace.UP).getState();

			Block rel = sign.getBlock().getRelative(BlockFace.EAST)
					.getRelative(BlockFace.UP);
			redTeam[0] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[1] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[2] = rel.getState();
			rel = rel.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getRelative(BlockFace.UP);
			redTeam[3] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[4] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			redTeam[5] = rel.getState();

			rel = sign.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.UP);
			blueTeam[0] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[1] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[2] = rel.getState();
			rel = rel.getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getRelative(BlockFace.UP);
			blueTeam[3] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[4] = rel.getState();
			rel = rel.getRelative(BlockFace.DOWN);
			blueTeam[5] = rel.getState();
		}

		for (BlockState bs : redTeam) {
			if (!(bs instanceof Sign)) {
				bs.getBlock().setType(Material.WALL_SIGN);
				bs.getBlock().setData(sign.getBlock().getData());
			}
		}
		for (BlockState bs : blueTeam) {
			if (!(bs instanceof Sign)) {
				bs.getBlock().setType(Material.WALL_SIGN);
				bs.getBlock().setData(sign.getBlock().getData());
			}
		}

		// update signs here

		Sign above = (Sign) sign.getBlock().getRelative(BlockFace.UP)
				.getState();
		Sign below = (Sign) sign.getBlock().getRelative(BlockFace.DOWN)
				.getState();

		above.setLine(2, "Winner");

		if (match.getLastWinner() == Team.RED) {
			above.setLine(3, ChatColor.RED + "RED");
		} else if (match.getLastWinner() == Team.BLUE) {
			above.setLine(3, ChatColor.DARK_BLUE + "BLUE");
		} else if (match.getLastWinner() == Team.TIE) {
			above.setLine(3, ChatColor.DARK_PURPLE + "TIE");
		} else if (match.getLastWinner() == Team.DEAD) {
			above.setLine(3, ChatColor.YELLOW + "NO MATCHES");
		} else {
			above.setLine(3, "ERROR");
		}

		below.setLine(2, "Status");

		MatchState ms = match.getMatchState();
		if (ms == MatchState.PRESTART) {
			below.setLine(3, "WAITING");
			above.setLine(0, "Needs "
					+ (match.getParent().getData().getMinPlayers() - match
							.getParticipants().size()));
			above.setLine(1, "More players");
		} else if (ms == MatchState.STARTING) {
			below.setLine(3, "STARTING");
			above.setLine(0, "Match Start in");
			int time = sel.getCurrentMatch().getTimeTillStart();
			above.setLine(1, time / 60 + ":" + time % 60);
		} else if (ms == MatchState.STAGE1) {
			above.setLine(0, "Wall Drop in");
			int time = sel.getCurrentMatch().getTimeTillNextStage();
			above.setLine(1, time / 60 + ":" + time % 60);
			below.setLine(3, "MINING");
		} else if (ms == MatchState.STAGE2) {
			above.setLine(0, "Match End in");
			int time = sel.getCurrentMatch().getTimeTillMatchEnd();
			above.setLine(1, time / 60 + ":" + time % 60);
			below.setLine(3, "FIGHTING");
		} else {
			below.setLine(3, "error");
		}

		redTech.setLine(1, "Technology:");
		blueTech.setLine(1, "Technology:");

		redLabel.setLine(1, "Red Team");
		blueLabel.setLine(1, "Blue Team");

		if (!(match.getMatchState() == MatchState.PRESTART || match
				.getMatchState() == MatchState.STARTING)) {

			// update things that won't be updated after a match ends
			below.setLine(0,
					ChatColor.RED + "Red Score: " + match.getRedScore());
			below.setLine(1,
					ChatColor.DARK_BLUE + "Blue Score: " + match.getBlueScore());

			Iterator<String> blueIter = match.getBlueTeam().iterator();
			List<String> livingPlayers = match.livingPlayers();

			redTech.setLine(2, String.valueOf(match.getRedTech()));

			blueTech.setLine(2, String.valueOf(match.getBlueTech()));

			redLabel.setLine(2, ChatColor.RED.toString()
					+ match.getRedTeam().size() + " players");
			blueLabel.setLine(2, ChatColor.DARK_BLUE.toString()
					+ match.getBlueTeam().size() + " players");

			redTech.setLine(2, ChatColor.RED.toString() + match.getRedTech());
			blueTech.setLine(2,
					ChatColor.DARK_BLUE.toString() + match.getBlueTech());

			for (BlockState bs : blueTeam) {
				for (int i = 0; i <= 3; i++) {
					if (blueIter.hasNext()) {
						String player = blueIter.next();
						if (livingPlayers.contains(player)) {
							((Sign) bs)
									.setLine(i, ChatColor.DARK_BLUE + player);
						} else {
							((Sign) bs).setLine(i, player);
						}
					} else {
						break;
					}
				}
			}

			Iterator<String> redIter = match.getRedTeam().iterator();

			for (BlockState bs : redTeam) {
				for (int i = 0; i <= 3; i++) {
					if (redIter.hasNext()) {
						String player = redIter.next();
						if (livingPlayers.contains(player)) {
							((Sign) bs).setLine(i, ChatColor.RED + player);
						} else {
							((Sign) bs).setLine(i, player);
						}
					} else {
						break;
					}
				}
			}

		}
		// save the signs to the set
		BlockState temp = below;
		scoreboardSigns.add(new BlockVector(temp.getX(), temp.getY(), temp.getZ()));
		temp = above;
		scoreboardSigns.add(new BlockVector(temp.getX(), temp.getY(), temp.getZ()));
		temp = blueLabel;
		scoreboardSigns.add(new BlockVector(temp.getX(), temp.getY(), temp.getZ()));
		temp = redLabel;
		scoreboardSigns.add(new BlockVector(temp.getX(), temp.getY(), temp.getZ()));
		temp = redTech;
		scoreboardSigns.add(new BlockVector(temp.getX(), temp.getY(), temp.getZ()));
		temp = blueTech;
		scoreboardSigns.add(new BlockVector(temp.getX(), temp.getY(), temp.getZ()));

		// "update" the signs

		below.update();
		above.update();
		blueLabel.update();
		redLabel.update();
		redTech.update();
		blueTech.update();
		for (BlockState bs : blueTeam) {
			bs.update();
			scoreboardSigns.add(new BlockVector(bs.getX(), bs.getY(), bs.getZ()));
		}
		for (BlockState bs : redTeam) {
			bs.update();
			scoreboardSigns.add(new BlockVector(bs.getX(), bs.getY(), bs.getZ()));
		}
	}

	@SuppressWarnings("unchecked")
	public void updateSigns() {
		List<BlockVector> blocks = (List<BlockVector>) parent.getSignConfig().getConfig().getList("signs");
		World w = parent.getServer().getWorld(parent.getConfig().getString("world"));
		if (blocks == null) {
			return;
		}
		List<BlockVector> toRemove = new LinkedList<>();

		for (BlockVector bv : blocks) {
			Location l = bv.toLocation(w);
			if (w.getBlockAt(l).getType().equals(Material.SIGN_POST)
					|| w.getBlockAt(l).getType().equals(Material.WALL_SIGN)) {
				if (w.getBlockAt(l).getState() instanceof Sign) {
					Sign sign = (Sign) w.getBlockAt(l).getState();
					if (sign.getLine(0).equalsIgnoreCase("[castlewars]")) {

						if (sign.getLine(1).equalsIgnoreCase("[scoreboard]")) {
							if (sign.getType().equals(Material.SIGN_POST)) {
								parent.getLogger().log(Level.WARNING,
										"Scoreboards can only be wall signs!");
								toRemove.add(bv);
								w.getBlockAt(l).setType(Material.AIR);
							} else {
								String name = sign.getLine(2);
								Arena sel = null;
								for (Arena a : parent.getArenas()) {
									if (a.getName().equalsIgnoreCase(name)) {
										sel = a;
										break;
									}
								}
								if (sel == null) {
									toRemove.add(bv);
									parent.getLogger().log(
											Level.WARNING,
											"Removed sign containing nonexistant arena "
													+ sign.getLine(2));
									w.getBlockAt(l).setType(Material.AIR);
								} else {
									updateScoreboard(sign);
								}
							}
						}
						if (sign.getLine(1).equalsIgnoreCase("[join]")) {
							String name = sign.getLine(2);
							Arena sel = null;
							for (Arena a : parent.getArenas()) {
								if (a.getName().equalsIgnoreCase(name)) {
									sel = a;
									break;
								}
							}
							if (sel == null) {
								toRemove.add(bv);
								parent.getLogger().log(
										Level.WARNING,
										"Removed sign containing nonexistant arena "
												+ sign.getLine(2));
								w.getBlockAt(l).setType(Material.AIR);
							} else {
								sign.setLine(
										3,
										String.valueOf(sel.getCurrentMatch()
												.getParticipants().size())
												+ '/'
												+ sel.getData().getMaxPlayers());
								sign.update();
							}
						}

						if (sign.getLine(1).equalsIgnoreCase("[status]")) {
							String name = sign.getLine(2);
							Arena sel = null;
							for (Arena a : parent.getArenas()) {
								if (a.getName().equalsIgnoreCase(name)) {
									sel = a;
									break;
								}
							}
							if (sel == null) {
								toRemove.add(bv);
								parent.getLogger().log(
										Level.WARNING,
										"Removed sign containing nonexistant arena "
												+ sign.getLine(2));
								w.getBlockAt(l).setType(Material.AIR);
							} else {
								sign.setLine(
										3,
										String.valueOf(sel.getCurrentMatch()
												.getParticipants().size())
												+ '/'
												+ sel.getData().getMaxPlayers());
								sign.update();
							}

							if (w.getBlockAt(l).getRelative(BlockFace.DOWN).getType().equals(Material.SIGN_POST) ||
									w.getBlockAt(l).getRelative(BlockFace.DOWN).getType().equals(Material.WALL_SIGN)) {
								Sign below = (Sign) w.getBlockAt(l).getRelative(BlockFace.DOWN).getState();
								below.setLine(0, "Red Score: "+ sel.getCurrentMatch().getRedScore());
								below.setLine(1, "Blue Score: "+ sel.getCurrentMatch().getBlueScore());
								MatchState ms = sel.getCurrentMatch().getMatchState();

								if (ms == MatchState.PRESTART) {
									below.setLine(2, "WAITING");
									below.setLine(3, "-:--");
								} else if (ms == MatchState.STARTING) {
									below.setLine(2, "STARTING");
									int time = sel.getCurrentMatch().getTimeTillStart();
									below.setLine(3, time / 60 + ":" + time% 60);
								} else if (ms == MatchState.STAGE1) {
									below.setLine(2, "MINING");
									int time = sel.getCurrentMatch().getTimeTillNextStage();
									below.setLine(3, time / 60 + ":" + time% 60);
								} else if (ms == MatchState.STAGE2) {
									below.setLine(2, "FIGHTING");
									int time = sel.getCurrentMatch().getTimeTillMatchEnd();
									below.setLine(3, time / 60 + ":" + time% 60);
								} else {
									below.setLine(2, "-----");
									below.setLine(3, "error");
								}

								below.update();

							}

						}

					} else {
						toRemove.add(bv);
						w.getBlockAt(l).setType(Material.AIR);
					}
				}
			} else {
				toRemove.add(bv);
				w.getBlockAt(l).setType(Material.AIR);
			}

		}

		for (BlockVector bv : toRemove) {
			blocks.remove(bv);
		}
		toRemove.clear();

		parent.getSignConfig().getConfig().set("signs", blocks);
		parent.getSignConfig().saveConfig();

	}

}
