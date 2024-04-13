package me.dannynguyen.aethel.rpg;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's RPG metadata.
 *
 * @author Danny Nguyen
 * @version 1.22.20
 * @since 1.8.9
 */
public class RpgPlayer {
  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * {@link Settings}
   */
  private final Settings settings;

  /**
   * {@link Displays}
   */
  private final Displays displays;

  /**
   * {@link Equipment}
   */
  private final Equipment equipment;

  /**
   * Associates a player with RPG metadata.
   *
   * @param player interacting player
   */
  public RpgPlayer(@NotNull Player player) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.settings = new Settings(uuid);
    this.displays = new Displays(player, settings);
    this.equipment = new Equipment(player);
  }

  /**
   * Gets the UUID the RPG player belongs to.
   *
   * @return RPG player owner
   */
  @NotNull
  public UUID getUUID() {
    return this.uuid;
  }

  /**
   * Gets the {@link Settings}.
   *
   * @return {@link Settings}
   */
  @NotNull
  public Settings getSettings() {
    return this.settings;
  }

  /**
   * Gets the {@link Displays}.
   *
   * @return {@link Displays}
   */
  @NotNull
  public Displays getDisplays() {
    return this.displays;
  }

  /**
   * Gets the {@link Equipment}.
   *
   * @return {@link Equipment}
   */
  @NotNull
  public Equipment getEquipment() {
    return this.equipment;
  }
}
