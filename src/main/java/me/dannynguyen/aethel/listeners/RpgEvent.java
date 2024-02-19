package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import org.bukkit.Bukkit;
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
 * @version 1.11.2
 * @since 1.10.6
 */
public class RpgEvent implements Listener {
  /**
   * Assigns an RPG profile to a player upon joining the server.
   *
   * @param e player join event
   */
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    if (PluginData.rpgSystem.getRpgProfiles().get(player) == null) {
      PluginData.rpgSystem.loadRpgPlayer(player);
    }
  }


  /**
   * Updates the player's health bar to account for absorption and health boost status effects.
   *
   * @param e entity potion effect event
   */
  @EventHandler
  public void onPotionEffect(EntityPotionEffectEvent e) {
    if (e.getEntity() instanceof Player player) {
      switch (e.getModifiedType().getName()) {
        case "ABSORPTION" -> {
          if (e.getAction() == EntityPotionEffectEvent.Action.ADDED || e.getAction() == EntityPotionEffectEvent.Action.CHANGED) {
            Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> PluginData.rpgSystem.getRpgProfiles().get(player).updateHealthBar(), 1);
          }
        }
        case "HEALTH_BOOST" -> Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> PluginData.rpgSystem.getRpgProfiles().get(player).updateHealthBar(), 1);
      }
    }
  }

  /**
   * Resets the player's health bar.
   *
   * @param e player respawn event
   */
  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    PluginData.rpgSystem.getRpgProfiles().get(e.getPlayer()).resetHealthBar();
  }
}
