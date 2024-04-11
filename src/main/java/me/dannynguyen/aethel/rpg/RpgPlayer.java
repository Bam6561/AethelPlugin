package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's RPG metadata.
 *
 * @author Danny Nguyen
 * @version 1.22.10
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
   * {@link Equipment}
   */
  private final Equipment equipment;

  /**
   * {@link Health}
   */
  private final Health health;

  /**
   * Associates a player with RPG metadata.
   *
   * @param player interacting player
   */
  public RpgPlayer(@NotNull Player player) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.settings = new Settings(uuid);
    this.equipment = new Equipment(player);
    this.health = new Health(player, player.getPersistentDataContainer(), settings);
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
   * Gets the affecting {@link Buffs}.
   *
   * @return affecting {@link Buffs}
   */
  @Nullable
  public Buffs getBuffs() {
    return Plugin.getData().getRpgSystem().getBuffs().get(uuid);
  }

  /**
   * Gets the affecting {@link Status statuses}.
   *
   * @return affecting {@link Status statuses}
   */
  @Nullable
  public Map<StatusType, Status> getStatuses() {
    return Plugin.getData().getRpgSystem().getStatuses().get(uuid);
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

  /**
   * Gets the {@link Health}.
   *
   * @return {@link Health}
   */
  @NotNull
  public Health getHealth() {
    return this.health;
  }
}
