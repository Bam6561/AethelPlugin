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

import java.util.UUID;

/**
 * Collection of listeners for RPG system functionality.
 *
 * @author Danny Nguyen
 * @version 1.12.0
 * @since 1.10.6
 */
public class RpgEvent implements Listener {
  /**
   * Assigns an RPG profile to a player upon joining the server.
   *
   * @param e player join event
   */
  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    UUID playerUUID = e.getPlayer().getUniqueId();
    if (PluginData.rpgSystem.getRpgProfiles().get(playerUUID) == null) {
      PluginData.rpgSystem.loadRpgPlayer(playerUUID);
    } else {
      BossBar healthBar = PluginData.rpgSystem.getRpgProfiles().get(playerUUID).getHealthBar();
      healthBar.removeAll();
      healthBar.addPlayer(e.getPlayer());
    }
  }


  /**
   * Updates the player's health bar to account for absorption and health boost status effects.
   *
   * @param e entity potion effect event
   */
  @EventHandler
  private void onPotionEffect(EntityPotionEffectEvent e) {
    if (e.getEntity() instanceof Player player) {
      switch (e.getModifiedType().getName()) {
        case "ABSORPTION" -> {
          if (e.getAction() == EntityPotionEffectEvent.Action.ADDED || e.getAction() == EntityPotionEffectEvent.Action.CHANGED) {
            Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> PluginData.rpgSystem.getRpgProfiles().get(player.getUniqueId()).updateHealthBar(), 1);
          }
        }
        case "HEALTH_BOOST" -> Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> PluginData.rpgSystem.getRpgProfiles().get(player.getUniqueId()).updateHealthBar(), 1);
      }
    }
  }

  /**
   * Resets the player's health bar.
   *
   * @param e player respawn event
   */
  @EventHandler
  private void onRespawn(PlayerRespawnEvent e) {
    PluginData.rpgSystem.getRpgProfiles().get(e.getPlayer().getUniqueId()).resetHealthBar();
  }
}
