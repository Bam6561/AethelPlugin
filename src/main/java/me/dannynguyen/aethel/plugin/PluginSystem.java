package me.dannynguyen.aethel.plugin;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents plugin metadata in memory.
 *
 * @author Danny Nguyen
 * @version 1.22.6
 * @since 1.10.1
 */
public class PluginSystem {
  /**
   * {@link PluginPlayer Plugin players}.
   */
  private final Map<UUID, PluginPlayer> pluginPlayers = new HashMap<>();

  /**
   * Players currently tracking locations.
   */
  private final Map<UUID, Location> trackedLocations = new HashMap<>();

  /**
   * No parameter constructor.
   */
  public PluginSystem() {
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

  /**
   * Gets tracked locations.
   *
   * @return tracked locations
   */
  @NotNull
  public Map<UUID, Location> getTrackedLocations() {
    return this.trackedLocations;
  }
}
