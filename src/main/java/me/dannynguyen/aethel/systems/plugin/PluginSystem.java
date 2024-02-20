package me.dannynguyen.aethel.systems.plugin;

import me.dannynguyen.aethel.systems.plugin.enums.PlayerMeta;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents plugin metadata in memory.
 *
 * @author Danny Nguyen
 * @version 1.10.1
 * @since 1.10.1
 */
public class PluginSystem {
  /**
   * Players' plugin metadata.
   */
  private final Map<Player, Map<PlayerMeta, String>> playerMetadata = new HashMap<>();

  /**
   * Gets players' plugin metadata.
   *
   * @return players' plugin metadata
   */
  public Map<Player, Map<PlayerMeta, String>> getPlayerMetadata() {
    return playerMetadata;
  }
}
