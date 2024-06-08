package me.bam6561.aethelplugin.plugin;

import me.bam6561.aethelplugin.commands.location.LocationRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's plugin metadata.
 *
 * @author Danny Nguyen
 * @version 1.22.6
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
   *
   * @param player interacting player
   */
  public PluginPlayer(@NotNull Player player) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.locationRegistry = new LocationRegistry(uuid);
  }

  /**
   * Gets the UUID the Plugin player belongs to.
   *
   * @return Plugin player owner
   */
  @NotNull
  public UUID getUuid() {
    return this.uuid;
  }

  /**
   * Gets the {@link MenuInput}.
   *
   * @return {@link MenuInput}
   */
  @NotNull
  public MenuInput getMenuInput() {
    return this.menuInput;
  }

  /**
   * Gets the {@link LocationRegistry}.
   *
   * @return {@link LocationRegistry}
   */
  @NotNull
  public LocationRegistry getLocationRegistry() {
    return this.locationRegistry;
  }
}
