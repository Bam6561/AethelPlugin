package me.dannynguyen.aethel.plugin;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's plugin metadata.
 *
 * @author Danny Nguyen
 * @version 1.22.5
 * @since 1.17.16
 */
public class PluginPlayer {
  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * {@link MenuInput}
   */
  private final MenuInput menuInput = new MenuInput();

  /**
   * {@link LocationRegistry}
   */
  private final LocationRegistry locationRegistry;

  /**
   * Associates a player with plugin metadata.
   */
  public PluginPlayer(@NotNull Player player) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.locationRegistry = new LocationRegistry(uuid);
  }

  /**
   * Gets the {@link MenuInput}.
   *
   * @return {@link MenuInput}
   */
  public MenuInput getMenuInput() {
    return this.menuInput;
  }

  /**
   * Gets the {@link LocationRegistry}.
   *
   * @return {@link LocationRegistry}
   */
  public LocationRegistry getLocationRegistry() {
    return this.locationRegistry;
  }
}
