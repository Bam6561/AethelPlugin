package me.dannynguyen.aethel.systems.plugin;

import me.dannynguyen.aethel.systems.plugin.enums.PlayerMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents plugin metadata in memory.
 *
 * @author Danny Nguyen
 * @version 1.12.0
 * @since 1.10.1
 */
public class PluginSystem {
  /**
   * Players' plugin metadata.
   */
  private final Map<UUID, Map<PlayerMeta, String>> playerMetadata = new HashMap<>();

  /**
   * Gets players' plugin metadata.
   *
   * @return players' plugin metadata
   */
  @NotNull
  public Map<UUID, Map<PlayerMeta, String>> getPlayerMetadata() {
    return playerMetadata;
  }
}
