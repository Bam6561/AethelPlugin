package me.dannynguyen.aethel.plugin.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
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
 * @version 1.14.5
 * @since 1.10.1
 */
public class PluginEvent implements Listener {
  /**
   * No parameter constructor.
   */
  public PluginEvent() {
  }

  /**
   * Associates a player with {@link PlayerMeta metadata}.
   *
   * @param e player join event
   */
  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    UUID playerUUID = e.getPlayer().getUniqueId();
    Map<UUID, Map<PlayerMeta, String>> playerMetadata = Plugin.getData().getPluginSystem().getPlayerMetadata();
    if (!playerMetadata.containsKey(playerUUID)) {
      playerMetadata.put(playerUUID, new HashMap<>());
    }
  }
}
