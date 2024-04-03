package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.rpg.Buffs;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Collection of {@link me.dannynguyen.aethel.rpg.Buffs} change listeners.
 *
 * @author Danny Nguyen
 * @version 1.20.9
 * @since 1.20.9
 */
public class BuffEvent implements Listener {
  /**
   * Removes {@link me.dannynguyen.aethel.rpg.Buffs} on death.
   *
   * @param e player death event
   */
  private void onDeath(PlayerDeathEvent e) {
    Buffs buffs = Plugin.getData().getRpgSystem().getRpgPlayers().get(e.getEntity().getUniqueId()).getBuffs();
    buffs.removeAttributeBuffs();
    buffs.removeAethelAttributeBuffs();
  }
}
