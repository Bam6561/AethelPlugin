package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Collection of listeners for RPG system functionality.
 *
 * @author Danny Nguyen
 * @version 1.10.6
 * @since 1.10.6
 */
public class RpgEvent implements Listener {
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
