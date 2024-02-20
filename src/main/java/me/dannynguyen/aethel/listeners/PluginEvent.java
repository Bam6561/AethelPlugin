package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.plugin.PlayerMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Collection of listeners for plugin system functionality.
 *
 * @author Danny Nguyen
 * @version 1.11.3
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
    Map<Player, Map<PlayerMeta, String>> playerMetadata = PluginData.pluginSystem.getPlayerMetadata();
    if (!playerMetadata.containsKey(e.getPlayer())) {
      playerMetadata.put(e.getPlayer(), new HashMap<>());
    }
  }
}
