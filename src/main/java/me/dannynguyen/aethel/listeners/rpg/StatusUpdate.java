package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Collection of status update listeners.
 *
 * @author Danny Nguyen
 * @version 1.16.5
 * @since 1.16.5
 */
public class StatusUpdate implements Listener {
  /**
   * Clears player statuses on death.
   *
   * @param e player death event
   */
  @EventHandler
  private void onDeath(PlayerDeathEvent e) {
    Plugin.getData().getRpgSystem().getStatuses().remove(e.getEntity().getUniqueId());
  }
}
