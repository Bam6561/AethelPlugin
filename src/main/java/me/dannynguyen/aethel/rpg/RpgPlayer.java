package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.rpg.abilities.Abilities;
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
   * {@link Settings}
   */
  private final Settings settings;

  /**
   * {@link AethelAttributes}
   */
  private final AethelAttributes aethelAttributes;

  /**
   * {@link Enchantments}
   */
  private final Enchantments enchantments;

  /**
   * {@link Abilities}
   */
  private final Abilities abilities;

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
   * Gets the {@link Settings}.
   *
   * @return {@link Settings}
   */
  @NotNull
  public Settings getSettings() {
    return this.settings;
  }

  /**
   * Gets the {@link AethelAttributes}.
   *
   * @return {@link AethelAttributes}
   */
  @NotNull
  public AethelAttributes getAethelAttributes() {
    return this.aethelAttributes;
  }

  /**
   * Gets the {@link Enchantments}.
   *
   * @return {@link Enchantments}
   */
  public Enchantments getEnchantments() {
    return this.enchantments;
  }

  /**
   * Gets the {@link Abilities}.
   *
   * @return {@link Abilities}
   */
  @NotNull
  public Abilities getAbilities() {
    return this.abilities;
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
