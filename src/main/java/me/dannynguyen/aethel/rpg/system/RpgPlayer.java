package me.dannynguyen.aethel.rpg.system;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's RPG metadata.
 *
 * @author Danny Nguyen
 * @version 1.17.9
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
  private final AethelAttributes aethelAttributes;

  /**
   * Total enchantments.
   */
  private final Enchantments enchantments;

  /**
   * Passive and active abilities.
   */
  private final Abilities abilities;

  /**
   * All equipment.
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
    this.aethelAttributes = new AethelAttributes();
    this.enchantments = new Enchantments(uuid);
    this.abilities = new Abilities();
    this.equipment = new Equipment(player, aethelAttributes, enchantments, abilities);
    this.health = new Health(player, aethelAttributes, settings);
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
   * @return player's total Aethel attributes
   */
  @NotNull
  public AethelAttributes getAethelAttributes() {
    return this.aethelAttributes;
  }

  /**
   * Gets the player's total enchantments.
   *
   * @return player's total enchantments
   */
  public Enchantments getEnchantments() {
    return this.enchantments;
  }

  /**
   * Gets the player's RPG abilities.
   *
   * @return player's RPG abilities
   */
  @NotNull
  public Abilities getAbilities() {
    return this.abilities;
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
