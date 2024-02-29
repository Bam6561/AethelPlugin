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
 * @version 1.13.4
 * @since 1.8.9
 */
public class RpgPlayer {
  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * Total Aethel attributes.
   */
  private final Map<AethelAttribute, Double> aethelAttributes = createBlankAethelAttributes();

  /**
   * Player's equipment.
   */
  private final RpgEquipment equipment;

  /**
   * Player's health bar.
   */
  private final RpgHealthBar healthBar;

  /**
   * Associates a player with RPG metadata.
   *
   * @param player interacting player
   */
  public RpgPlayer(@NotNull Player player) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.equipment = new RpgEquipment(player, aethelAttributes);
    this.healthBar = new RpgHealthBar(player, aethelAttributes);
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
  public UUID getUuid() {
    return this.uuid;
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
  public RpgEquipment getEquipment() {
    return this.equipment;
  }

  /**
   * Gets the player's RPG health bar.
   *
   * @return player's RPG health bar
   */
  @NotNull
  public RpgHealthBar getHealthBar() {
    return this.healthBar;
  }
}
