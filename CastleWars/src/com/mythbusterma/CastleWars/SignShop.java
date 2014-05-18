package com.mythbusterma.CastleWars;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SignShop implements Listener {
	private CastleWars parent;

	public SignShop(CastleWars _parent) {
		parent = _parent;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.WALL_SIGN
					|| e.getClickedBlock().getType() == Material.SIGN_POST) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase(
						"[CastleWars]")) {
					if (sign.getLine(1).equalsIgnoreCase("[join]")) {
						Arena sel = null;
						String name = sign.getLine(2).toLowerCase();
						for (Arena a : parent.getArenas()) {
							for (String s : a.getCurrentMatch()
									.getParticipants()) {
								if (e.getPlayer().getName().equalsIgnoreCase(s)) {
									e.getPlayer().sendMessage(
											"You are already in an arena!");
									return;
								}
							}
							if (a.getName().equalsIgnoreCase(name)) {
								sel = a;
							}
						}
						if (sel != null) {
							sel.getCurrentMatch().join(e.getPlayer().getName());
							e.getPlayer().sendMessage(
									"Joining arena "
											+ sel.getName()
											+ " you will be teleported in "
											+ parent.getConfig().getInt(
													"teleportdelay")
											+ " second(s)");
						} else {
							parent.logToFile("NO ARENA FOUND BY NAME OF "
									+ name + " ON SIGN AT " + sign.getX()
									+ ", " + sign.getY() + ", " + sign.getZ(),
									"modify");
							parent.getLogger().log(
									Level.WARNING,
									"NO ARENA FOUND BY NAME OF " + name
											+ " ON SIGN AT " + sign.getX()
											+ ", " + sign.getY() + ", "
											+ sign.getZ());
						}

					}
					if (sign.getLine(1).equalsIgnoreCase("[leave]")) {
						for (Arena a : parent.getArenas()) {
							for (String s : a.getCurrentMatch()
									.getParticipants()) {
								if (e.getPlayer().getName().equalsIgnoreCase(s)) {
									int ret = a.getCurrentMatch().leave(s,
											false);
									if (ret == -100) {
										e.getPlayer()
												.sendMessage(
														"You can't leave a match in progress!");
									} else if (ret == 1) {
										e.getPlayer().sendMessage(
												"You have left the arena");
									} else if (ret == -101) {
										e.getPlayer()
												.sendMessage(
														"An internal error occured id: 505");
									}
								}
							}
						}
					}

					for (Arena a : parent.getArenas()) {
						for (String s : a.getCurrentMatch().getParticipants()) {
							if (e.getPlayer().getName().equalsIgnoreCase(s)) {
								if (sign.getLine(2) != null
										|| sign.getLine(2) != "") {
									if (sign.getLine(3) != null
											|| sign.getLine(3) != "") {
										String[] item = sign.getLine(2).split(
												":");
										for (ItemStack is : e.getPlayer()
												.getInventory()) {
											try {
												if (is != null) {
													if (is.getTypeId() == Integer
															.valueOf(item[0])) {
														int remove = 1; // number
																		// to
																		// remove
														if (item.length > 1) {
															remove = Integer
																	.valueOf(item[1]);
														}
														if (is.getAmount() < remove) {
															e.getPlayer()
																	.sendMessage(
																			"You don't have enough items in a stack to sell!");
															return;
														}

														if (is.getAmount() == 1) {
															e.getPlayer()
																	.getInventory()
																	.remove(is);
														} else {
															is.setAmount(is
																	.getAmount()
																	- remove);
														}
														e.getPlayer()
																.updateInventory();

														String[] price = sign
																.getLine(3)
																.split(":");

														if (price[0] == null
																|| price[0] == "") {
															e.getPlayer()
																	.sendMessage(
																			"You cannot sell this item!");
															return;
														}
														int sell = Integer
																.valueOf(price[0]);
														a.getCurrentMatch()
																.pay(e.getPlayer()
																		.getName(),
																		sell);
														e.getPlayer()
																.sendMessage(
																		"You have sold "
																				+ remove
																				+ " of "
																				+ sign.getLine(2)
																				+ " for "
																				+ price[0]);
														return;
													}
												}
											} catch (Exception ex) {
												Bukkit.getConsoleSender()
														.sendMessage(
																"Invalid sign at: "
																		+ sign.getX()
																		+ ", "
																		+ sign.getY()
																		+ ", "
																		+ sign.getZ()
																		+ "Exception");
												ex.printStackTrace();
												return;
											}
										}
										e.getPlayer()
												.sendMessage(
														"You don't have any of this item to sell!");
										return;
									} else {
										Bukkit.getConsoleSender().sendMessage(
												"Invalid sign at: "
														+ sign.getX() + ", "
														+ sign.getY() + ", "
														+ sign.getZ()
														+ " case1");
										return;
									}
								} else {
									Bukkit.getConsoleSender().sendMessage(
											"Invalid sign at: " + sign.getX()
													+ ", " + sign.getY() + ", "
													+ sign.getZ() + " case2");
									return;
								}
							}
						}
					}
					if (sign.getLine(1).equalsIgnoreCase("[join]")) {

					} else if (sign.getLine(1).equalsIgnoreCase("[leave]")) {

					} else if (sign.getLine(1).equalsIgnoreCase("[scoreboard]")) {

					} else {
						e.getPlayer()
								.sendMessage(
										"You can't use these signs while not in an arena!");
					}
				}
			}
		} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.WALL_SIGN
					|| e.getClickedBlock().getType() == Material.SIGN_POST) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				if (sign.getLine(0).equalsIgnoreCase("[CastleWars]")) {
					for (Arena a : parent.getArenas()) {
						for (String s : a.getCurrentMatch().getParticipants()) {
							if (e.getPlayer().getName().equalsIgnoreCase(s)) {
								if (sign.getLine(2) != null
										|| sign.getLine(3) != "") {
									String[] item = sign.getLine(2).split(":");
									if (sign.getLine(3) != null
											|| sign.getLine(3) != "") {
										try {

											int add = 1; // number to remove
											if (item.length > 1) {
												add = Integer.valueOf(item[1]);
											}
											String[] price = sign.getLine(3)
													.split(":");
											if (price.length == 1
													|| price.length == 0) {
												e.getPlayer()
														.sendMessage(
																"You cannot buy this item");
												return;
											}
											Integer.valueOf(price[1]);
											if (a.getCurrentMatch().deduct(
													e.getPlayer().getName(),
													Integer.valueOf(price[1]))) {
												e.getPlayer().sendMessage(
														"You have bought "
																+ add + " of "
																+ item[0]
																+ " for "
																+ price[1]);
												e.getPlayer()
														.getInventory()
														.addItem(
																new ItemStack(
																		Integer.valueOf(item[0]),
																		Integer.valueOf(add)));
												return;
											} else {
												e.getPlayer()
														.sendMessage(
																"You don't have enough money to buy this item! You only have "
																		+ a.getCurrentMatch()
																				.getBalance(
																						e.getPlayer()
																								.getName())
																		+ " "
																		+ parent.getConfig()
																				.getString(
																						"currencyunit"));
												return;
											}
										}

										catch (Exception ex) {
											Bukkit.getConsoleSender()
													.sendMessage(
															"Invalid sign at: "
																	+ sign.getX()
																	+ ", "
																	+ sign.getY()
																	+ ", "
																	+ sign.getZ()
																	+ " ex");
											return;
										}
									} else {
										Bukkit.getConsoleSender().sendMessage(
												"Invalid sign at: "
														+ sign.getX() + ", "
														+ sign.getY() + ", "
														+ sign.getZ()
														+ " case2");
										return;
									}
								} else {
									Bukkit.getConsoleSender().sendMessage(
											"Invalid sign at: " + sign.getX()
													+ ", " + sign.getY() + ", "
													+ sign.getZ() + " case1");
									return;
								}
							}
						}
					}
					e.getPlayer().sendMessage(
							"You can't use these signs while not in an arena!");
					return;
				}
			}
		}
	}
}
