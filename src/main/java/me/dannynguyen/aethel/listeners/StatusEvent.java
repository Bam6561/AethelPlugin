package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Collection of {@link me.dannynguyen.aethel.rpg.system.Status} change listeners.
 *
 * @author Danny Nguyen
 * @version 1.16.5
 * @since 1.16.5
 */
public class StatusEvent implements Listener {
  /**
   * No parameter constructor.
   */
  public StatusEvent() {
  }

  /**
   * Clears player {@link me.dannynguyen.aethel.rpg.system.Status statuses} on death.
   *
   * @param e player death event
   */
  @EventHandler
  private void onDeath(PlayerDeathEvent e) {
    Plugin.getData().getRpgSystem().getStatuses().remove(e.getEntity().getUniqueId());
  }
}
