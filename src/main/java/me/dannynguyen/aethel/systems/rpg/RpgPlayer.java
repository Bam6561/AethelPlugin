package me.dannynguyen.aethel.systems.rpg;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's RPG metadata.
 *
 * @author Danny Nguyen
 * @version 1.16.4
 * @since 1.8.9
 */
public class RpgPlayer {
  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * Player's settings.
   */
  private final Settings settings;

  /**
   * Total Aethel attributes.
   */
  private final Map<AethelAttribute, Double> aethelAttributes = createBlankAethelAttributes();

  /**
   * Player's equipment.
   */
  private final Equipment equipment;

  /**
   * Player's health.
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
    this.equipment = new Equipment(player, aethelAttributes);
    this.health = new Health(player, aethelAttributes, settings);
  }

  /**
   * Creates a blank map of Aethel attributes.
   *
   * @return blank Aethel attributes
   */
  private Map<AethelAttribute, Double> createBlankAethelAttributes() {
    Map<AethelAttribute, Double> aethelAttributes = new HashMap<>();
    for (AethelAttribute attribute : AethelAttribute.values()) {
      aethelAttributes.put(attribute, 0.0);
    }
    return aethelAttributes;
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
   * Gets the player's settings.
   *
   * @return player's settings
   */
  @NotNull
  public Settings getSettings() {
    return this.settings;
  }

  /**
   * Gets the player's total Aethel attributes.
   *
   * @return total Aethel attributes
   */
  @NotNull
  public Map<AethelAttribute, Double> getAethelAttributes() {
    return this.aethelAttributes;
  }

  /**
   * Gets the player's RPG equipment.
   *
   * @return player's rpg equipment
   */
  @NotNull
  public Equipment getEquipment() {
    return this.equipment;
  }

  /**
   * Gets the player's RPG health.
   *
   * @return player's RPG health
   */
  @NotNull
  public Health getHealth() {
    return this.health;
  }
}
