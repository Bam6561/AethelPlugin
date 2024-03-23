package me.dannynguyen.aethel.plugin.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Collection of listeners for {@link me.dannynguyen.aethel.plugin.system.PluginSystem} functionality.
 *
 * @author Danny Nguyen
 * @version 1.17.19
 * @since 1.10.1
 */
public class PluginEvent implements Listener {
  /**
   * No parameter constructor.
   */
  public PluginEvent() {
  }

  /**
   * Associates a player with {@link PluginPlayer}.
   *
   * @param e player join event
   */
  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    UUID playerUUID = e.getPlayer().getUniqueId();
    Map<UUID, PluginPlayer> pluginPlayers = Plugin.getData().getPluginSystem().getPluginPlayers();
    if (!pluginPlayers.containsKey(playerUUID)) {
      pluginPlayers.put(playerUUID, new PluginPlayer());
    }
  }
}
