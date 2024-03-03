package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.PluginData;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Collection of listeners for RPG system functionality.
 *
 * @author Danny Nguyen
 * @version 1.13.4
 * @since 1.10.6
 */
public class RpgEvent implements Listener {
  /**
   * Assigns an RPG player to a player upon joining the server.
   *
   * @param e player join event
   */
  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    if (PluginData.rpgSystem.getRpgPlayers().get(player.getUniqueId()) == null) {
      PluginData.rpgSystem.loadRpgPlayer(player);
    } else {
      BossBar healthBar = PluginData.rpgSystem.getRpgPlayers().get(player.getUniqueId()).getHealth().getBar();
      healthBar.removeAll();
      healthBar.addPlayer(player);
    }
  }

  /**
   * Updates the player's health to account for absorption and health boost status effects.
   *
   * @param e entity potion effect event
   */
  @EventHandler
  private void onPotionEffect(EntityPotionEffectEvent e) {
    if (e.getEntity() instanceof Player player) {
      switch (e.getModifiedType().getName()) {
        case "ABSORPTION" -> {
          if (e.getAction() == EntityPotionEffectEvent.Action.ADDED || e.getAction() == EntityPotionEffectEvent.Action.CHANGED) {
            Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> PluginData.rpgSystem.getRpgPlayers().get(player.getUniqueId()).getHealth().updateOvershield(), 1);
          }
        }
        case "HEALTH_BOOST" -> Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> PluginData.rpgSystem.getRpgPlayers().get(player.getUniqueId()).getHealth().updateMaxHealth(), 1);
      }
    }
  }

  /**
   * Resets the player's health.
   *
   * @param e player respawn event
   */
  @EventHandler
  private void onRespawn(PlayerRespawnEvent e) {
    PluginData.rpgSystem.getRpgPlayers().get(e.getPlayer().getUniqueId()).getHealth().reset();
  }
}
