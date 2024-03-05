package me.dannynguyen.aethel.listeners.plugin;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Collection of listeners for plugin system functionality.
 *
 * @author Danny Nguyen
 * @version 1.12.0
 * @since 1.10.1
 */
public class PluginEvent implements Listener {
  /**
   * Associates a player with metadata.
   *
   * @param e player join event
   */
  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    UUID playerUUID = e.getPlayer().getUniqueId();
    Map<UUID, Map<PlayerMeta, String>> playerMetadata = PluginData.pluginSystem.getPlayerMetadata();
    if (!playerMetadata.containsKey(playerUUID)) {
      playerMetadata.put(playerUUID, new HashMap<>());
    }
  }
}
