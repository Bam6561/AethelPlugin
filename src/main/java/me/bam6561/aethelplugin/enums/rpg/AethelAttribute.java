package me.bam6561.aethelplugin.enums.rpg;

import org.jetbrains.annotations.NotNull;

/**
 * Types of Aethel attributes.
 *
 * @author Danny Nguyen
 * @version 1.23.17
 * @since 1.9.10
 */
public enum AethelAttribute {
  /**
   * Critical hit chance.
   */
  CRITICAL_CHANCE("Critical Chance", "critical_chance", "0.0%"),

  /**
   * Critical hit damage multiplier.
   */
  CRITICAL_DAMAGE("Critical Damage", "critical_damage", "1.25x [Input / 100]"),

  /**
   * Reduces defender's counter chance.
   */
  FEINT_SKILL("Feint Skill", "feint_skill", "0"),

  /**
   * Reduces defender's dodge chance.
   */
  ACCURACY_SKILL("Accuracy Skill", "accuracy_skill", "0"),

  /**
   * Max health.
   */
  MAX_HEALTH("Max Health", "max_health", "20.0"),

  /**
   * Counterattack chance.
   */
  COUNTER_CHANCE("Counter Chance", "counter_chance", "0.0%"),

  /**
   * Dodge chance.
   */
  DODGE_CHANCE("Dodge Chance", "dodge_chance", "0.0%"),

  /**
   * Armor.
   */
  ARMOR("Armor", "armor", "0.0"),

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
  ITEM_COOLDOWN("Item Cooldown", "item_cooldown", "-0.0%"),

  /**
   * Reduces the duration of statuses.
   */
  TENACITY("Tenacity", "tenacity", "-0.0%");

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
   * @param id         ability ID
   * @param baseValue  attribute's base value
   */
  AethelAttribute(String properName, String id, String baseValue) {
    this.properName = properName;
    this.id = id;
    this.baseValue = baseValue;
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
