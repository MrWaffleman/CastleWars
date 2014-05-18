package com.mythbusterma.CastleWars;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.BlockVector;

public class BlockListener implements Listener {

	private CastleWars parent;

	public BlockListener(CastleWars _parent) {
		parent = _parent;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {

		if (e.getBlock().getType().equals(Material.WALL_SIGN)
				|| e.getBlock().getType().equals(Material.SIGN_POST)) {
			Sign sign = (Sign) e.getBlock().getState();
			if (sign.getLine(0).equalsIgnoreCase("[castlewars]")) {
				if (e.getPlayer().hasPermission("castlewars.sign")) {
					e.getPlayer().sendMessage(
							"You have destroyed a castlewars sign");
					parent.logToFile(
							e.getPlayer().getName()
									+ " broke a CastleWars sign type "
									+ sign.getLine(1) + " at "
									+ e.getBlock().getX() + ", "
									+ e.getBlock().getY() + ", "
									+ e.getBlock().getZ(), "modify");
					return;
				} else {
					e.getPlayer()
							.sendMessage(
									"You don't have permission to destroy CastleWars signs!");
					e.setCancelled(true);
					if (parent.getConfig().getBoolean("punishblockbreak")) {
						e.getPlayer().damage(
								parent.getConfig()
										.getDouble("punishmentdamage"));
					}
					return;
				}
			}
			int x = e.getBlock().getX();
			int y = e.getBlock().getY();
			int z = e.getBlock().getZ();
			for (BlockVector bv : parent.getSignManager().getScoreboardSigns()) {
				if (bv.getBlockX() == x) {
					if (bv.getBlockY() == y) {
						if (bv.getBlockZ() == z) {
							if (!e.getPlayer().hasPermission("castlewars.sign")) {
								e.setCancelled(true);
								e.getPlayer().damage(
										parent.getConfig().getDouble(
												"punishmentdamage"));
								e.getPlayer().sendMessage(
										"You can't break scoreboard signs!");
							}
						}
					}
				}
			}
		}

		for (Arena a : parent.getArenas()) {
			for (String s : a.getCurrentMatch().getLobbists()) {
				if (e.getPlayer().getName().equalsIgnoreCase(s)) {
					e.getPlayer()
							.sendMessage(
									"You are not allowed to break blocks while in the lobby!");
					e.setCancelled(true);
					if (parent.getConfig().getBoolean("punishblockbreak")) {
						e.getPlayer().damage(
								parent.getConfig()
										.getDouble("punishmentdamage"));
					}
					return;
				}

			}
			for (String s : a.getCurrentMatch().getSpectators()) {
				if (e.getPlayer().getName().equalsIgnoreCase(s)) {
					e.getPlayer()
							.sendMessage(
									"You are not allowed to break blocks while in the spectator area!");
					e.setCancelled(true);
					if (parent.getConfig().getBoolean("punishblockbreak")) {
						e.getPlayer().damage(
								parent.getConfig()
										.getDouble("punishmentdamage"));
					}
					return;
				}
			}
			for (String s : a.getCurrentMatch().livingPlayers()) {
				if (e.getPlayer().getName().equalsIgnoreCase(s)) {
					parent.getScores().breakBlock(s);
				}
			}
		}

		for (Arena a : parent.getArenas()) {
			if (a.getWall() != null) {
				for (BlockVector v : a.getWall()) {
					if (new BlockVector(e.getBlock().getLocation().toVector())
							.equals(v)) {
						e.setCancelled(true);
						if (parent.getConfig().getBoolean("punishblockbreak")) {
							e.getPlayer().damage(
									parent.getConfig().getDouble(
											"punishmentdamage"));
						}
						e.getPlayer().sendMessage(
								"&C&LDo not try to break the dividing wall!");
						return;
					}
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent e) {
		Sign sign = (Sign) e.getBlock().getState();
		if (e.getLine(0).equalsIgnoreCase("[CastleWars]")) {
			if (e.getPlayer().hasPermission("castlewars.sign")) {
				parent.getSignManager().addSign(e.getBlock().getLocation());
				e.getPlayer().sendMessage("You have created a Castlewars sign");
				parent.logToFile(e.getPlayer().getName()
						+ " placed a CastleWars sign type " + sign.getLine(1)
						+ " at " + e.getBlock().getX() + ", "
						+ e.getBlock().getY() + ", " + e.getBlock().getZ(),
						"modify");
				return;
			} else {
				e.getPlayer()
						.sendMessage(
								"You don't have permission to create castlewars signs!");
				e.setCancelled(true);
			}
		}

	}

}
