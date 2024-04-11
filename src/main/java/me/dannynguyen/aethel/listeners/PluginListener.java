package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import me.dannynguyen.aethel.plugin.PluginSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Collection of {@link PluginSystem} listeners.
 *
 * @author Danny Nguyen
 * @version 1.22.5
 * @since 1.10.1
 */
public class PluginListener implements Listener {
  /**
   * No parameter constructor.
   */
  public PluginListener() {
  }

  /**
   * Associates a {@link PluginPlayer} to a player upon joining the server.
   *
   * @param e player join event
   */
  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    UUID playerUUID = player.getUniqueId();
    Map<UUID, PluginPlayer> pluginPlayers = Plugin.getData().getPluginSystem().getPluginPlayers();
    if (!pluginPlayers.containsKey(playerUUID)) {
      pluginPlayers.put(playerUUID, new PluginPlayer(player));
    }
  }
}
