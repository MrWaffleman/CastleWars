package com.mythbusterma.CastleWars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.BlockVector;

import com.mythbusterma.CastleWars.Match.MatchState;

public class PlayerListener implements Listener {
	private CastleWars parent;

	public PlayerListener(CastleWars parent) {
		this.parent = parent;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBow(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player) {
			for (Arena a : parent.getArenas()) {
				if (a.getCurrentMatch().getMatchState() == MatchState.STAGE1) {
					for (String s : a.getCurrentMatch().livingPlayers()) {
						if (s.equalsIgnoreCase(((Player) e.getEntity())
								.getName())) {
							Player p = (Player) e.getEntity();
							p.sendMessage("You can't shoot an arrow during the first part of the match");
							e.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}

	// see if they are trying to execute a forbidden command while in the arena
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		for (Arena a : parent.getArenas()) {
			for (String s : a.getCurrentMatch().livingPlayers()) {
				if (e.getPlayer().getName().equalsIgnoreCase(s)) {
					for (String s1 : parent.getConfig().getStringList(
							"disallowedCommands")) {
						if (e.getMessage().startsWith(s1 + ' ')) {
							e.setCancelled(true);
							e.getPlayer()
									.sendMessage(
											"You can't use that command while playing CastleWars!");
							if (parent.getConfig().getBoolean("punishcommand")) {
								e.getPlayer().damage(
										parent.getConfig().getDouble(
												"punishmentdamage"));
							}
							return;
						}
					}
				}
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		for (Arena a : parent.getArenas()) {
			a.getCurrentMatch().triggerDeath(e.getEntity(), e);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {

			if (parent.getConfig().getBoolean("invinciblelobby")) {
				for (Arena a : parent.getArenas()) {
					for (String s : a.getCurrentMatch().getLobbists()) {
						if (((Player) e.getEntity()).getName() == s) {
							e.setCancelled(true);
							return;
						}
					}
				}
			}

			else if (parent.getConfig().getBoolean("invinciblespectators")) {
				for (Arena a : parent.getArenas()) {
					for (String s : a.getCurrentMatch().getSpectators()) {
						if (((Player) e.getEntity()).getName() == s) {
							e.setCancelled(true);
							return;
						}
					}
				}
			}

			if (e instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) e;
				if (edbee.getDamager() instanceof Player) {
					for (Arena a : parent.getArenas()) {
						a.getCurrentMatch().checkFriendlyFire(
								(Player) e.getEntity(),
								(Player) edbee.getDamager());
					}
				} else {
					if (parent.getConfig().getBoolean("disablemobdamage")) {
						for (Arena a : parent.getArenas()) {
							for (String s : a.getCurrentMatch().livingPlayers()) {
								if (((Player) e.getEntity()).getName() == s) {
									e.setCancelled(true);
									return;
								}
							}
						}
					}
				}
			}

			/*
			 * ********************************************************
			 * Only apply to living players in arenas
			 */

			// see if it was an explosion, and cancel if disabled in config
			else if (e.getCause() == DamageCause.ENTITY_EXPLOSION
					|| e.getCause() == DamageCause.BLOCK_EXPLOSION) {
				if (parent.getConfig().getBoolean("disableexplosion")) {
					outer: for (Arena a : parent.getArenas()) {
						for (String s : a.getCurrentMatch().livingPlayers()) {
							if (((Player) e.getEntity()).getName() == s) {
								e.setCancelled(true);
								break outer;
							}
						}
					}
				}
			}
			// see if it was drowning, and cancel if disabled in config
			else if (e.getCause() == DamageCause.DROWNING) {
				if (parent.getConfig().getBoolean("disabledrown")) {
					outer: for (Arena a : parent.getArenas()) {
						for (String s : a.getCurrentMatch().livingPlayers()) {
							if (((Player) e.getEntity()).getName() == s) {
								e.setCancelled(true);
								break outer;
							}
						}
					}
				}
			} else if (e.getCause() == DamageCause.FALL) {
				if (parent.getConfig().getBoolean("disablefall")) {
					outer: for (Arena a : parent.getArenas()) {
						for (String s : a.getCurrentMatch().livingPlayers()) {
							if (((Player) e.getEntity()).getName() == s) {
								e.setCancelled(true);
								break outer;
							}
						}
					}
				}
			} else if (e.getCause() == DamageCause.STARVATION) {
				if (parent.getConfig().getBoolean("disablehunger")) {
					outer: for (Arena a : parent.getArenas()) {
						for (String s : a.getCurrentMatch().livingPlayers()) {
							if (((Player) e.getEntity()).getName() == s) {
								e.setCancelled(true);
								break outer;
							}
						}
					}
				}
			} else if (e.getCause() == DamageCause.SUFFOCATION) {
				if (parent.getConfig().getBoolean("disablesuffocation")) {
					outer: for (Arena a : parent.getArenas()) {
						for (String s : a.getCurrentMatch().livingPlayers()) {
							if (((Player) e.getEntity()).getName() == s) {
								e.setCancelled(true);
								break outer;
							}
						}
					}
				}
			}

			else {

			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHungerDecrease(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			// Cancel hunger decrease for lobbists
			if (parent.getConfig().getBoolean("hungerlobbydisable")) {
				for (Arena a : parent.getArenas()) {
					for (String s : a.getCurrentMatch().getLobbists()) {
						if (((Player) e.getEntity()).getName() == s) {
							e.setCancelled(true);
							return;
						}
					}
				}
			}

			// Cancel hunger decrease for spectators
			else if (parent.getConfig().getBoolean("hungerspectatordisable")) {
				for (Arena a : parent.getArenas()) {
					for (String s : a.getCurrentMatch().getSpectators()) {
						if (((Player) e.getEntity()).getName() == s) {
							e.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		for (Arena a : parent.getArenas()) {
			for (String s : a.getCurrentMatch().livingPlayers()) {
				if (e.getPlayer().getName().equalsIgnoreCase(s)) {
					parent.getScores().disconnect(s);
					parent.logToFile(s + " has disconnected during a match on "
							+ a.getName(), "admin");
					a.getCurrentMatch().leave(s, true);
					if (parent.getEco() != null) {
						parent.payPlayer(
								s,
								-1
										* parent.getConfig().getDouble(
												"playerleavepunish"), null);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		parent.getScores().updateName(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent e) {
		for (Arena a : parent.getArenas()) {
			for (String s : a.getCurrentMatch().livingPlayers()) {
				if (s.equalsIgnoreCase(e.getPlayer().getName())) {
					BlockVector max = a.getData().getMax();
					BlockVector min = a.getData().getMin();

					if (e.getTo().getX() > max.getX()
							|| e.getTo().getX() < min.getX()) {
						e.setCancelled(true);
						e.getPlayer()
								.sendMessage(
										"You can't leave the arena while playing CastleWars!");
						a.getCurrentMatch().exitAttempt(s, e);
					} else if (e.getTo().getY() > max.getY()
							|| e.getTo().getY() < min.getY()) {
						e.setCancelled(true);
						e.getPlayer()
								.sendMessage(
										"You can't leave the arena while playing CastleWars!");
						a.getCurrentMatch().exitAttempt(s, e);

					} else if (e.getTo().getZ() > max.getZ()
							|| e.getTo().getZ() < min.getZ()) {
						e.setCancelled(true);
						e.getPlayer()
								.sendMessage(
										"You can't leave the arena while playing CastleWars!");
						a.getCurrentMatch().exitAttempt(s, e);

					}

					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onRespawn(PlayerRespawnEvent e) {
		Bukkit.getServer()
				.getConsoleSender()
				.sendMessage(
						"Respawned player: " + e.getPlayer() + " to location "
								+ e.getRespawnLocation().toString());
	}

}
