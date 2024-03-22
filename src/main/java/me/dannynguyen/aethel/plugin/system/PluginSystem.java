package me.dannynguyen.aethel.plugin.system;

import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents plugin metadata in memory.
 *
 * @author Danny Nguyen
 * @version 1.17.17
 * @since 1.10.1
 */
public class PluginSystem {
  /**
   * Plugin's {@link PlayerMeta player metadata}.
   */
  private final Map<UUID, Map<PlayerMeta, String>> playerMetadata = new HashMap<>();

  /**
   * {@link PluginPlayer Plugin players}.
   */
  private final Map<UUID, PluginPlayer> pluginPlayers = new HashMap<>();

  /**
   * No parameter constructor.
   */
  public PluginSystem() {
  }

  /**
   * Gets players' {@link PlayerMeta plugin metadata}.
   *
   * @return players' {@link PlayerMeta plugin metadata}
   */
  @NotNull
  public Map<UUID, Map<PlayerMeta, String>> getPlayerMetadata() {
    return playerMetadata;
  }

  /**
   * Gets {@link PluginPlayer Plugin players}.
   *
   * @return {@link PluginPlayer Plugin players}
   */
  @NotNull
  public Map<UUID, PluginPlayer> getPluginPlayers() {
    return this.pluginPlayers;
  }
}
