package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Types of Aethel attributes.
 *
 * @author Danny Nguyen
 * @version 1.15.9
 * @since 1.9.10
 */
public enum AethelAttribute {
  /**
   * Max health.
   */
  MAX_HEALTH("Max Health", "max_health", "20.0"),

  /**
   * Critical hit chance.
   */
  CRITICAL_CHANCE("Critical Chance", "critical_chance", "0.0%"),

  /**
   * Critical hit damage multiplier.
   */
  CRITICAL_DAMAGE("Critical Damage", "critical_damage", "1.25x [Input / 100]"),

  /**
   * Counterattack chance.
   */
  COUNTER_CHANCE("Counter Chance", "counter_chance", "0.0%"),

  /**
   * Dodge chance.
   */
  DODGE_CHANCE("Dodge Chance", "dodge_chance", "0.0%"),

  /**
   * Flat damage blocked.
   */
  ARMOR_TOUGHNESS("Armor Toughness", "armor_toughness", "0.0"),

  /**
   * Item damage multiplier.
   */
  ITEM_DAMAGE("Item Damage", "item_damage", "1.0x [Input / 100]"),

  /**
   * Item cooldown multiplier.
   */
  ITEM_COOLDOWN("Item Cooldown", "item_cooldown", "-0.0%");

  /**
   * Proper name.
   */
  private final String properName;

  /**
   * Attribute ID.
   */
  private final String id;

  /**
   * Attribute's base value.
   */
  private final String baseValue;

  /**
   * Associates the attribute with its base value.
   *
   * @param properName proper name
   * @param id         ability id
   * @param baseValue  attribute's base value
   */
  AethelAttribute(@NotNull String properName, @NotNull String id, @NotNull String baseValue) {
    this.properName = Objects.requireNonNull(properName, "Null name");
    this.id = Objects.requireNonNull(id, "Null id");
    this.baseValue = Objects.requireNonNull(baseValue, "Null value");
  }

  /**
   * Gets the attribute's proper name.
   *
   * @return attribute's proper name
   */
  @NotNull
  public String getProperName() {
    return this.properName;
  }

  /**
   * Gets the attribute's ID.
   *
   * @return attribute's ID
   */
  @NotNull
  public String getId() {
    return this.id;
  }

  /**
   * Gets the attribute's base value.
   *
   * @return attribute's base value
   */
  @NotNull
  public String getBaseValue() {
    return this.baseValue;
  }
}
