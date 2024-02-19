package me.dannynguyen.aethel.commands.character;

/**
 * Types of Aethel attributes.
 *
 * @author Danny Nguyen
 * @version 1.11.3
 * @since 1.9.10
 */
enum AethelAttribute {
  /**
   * Critical hit chance.
   */
  CRITICAL_CHANCE("critical_chance"),

  /**
   * Critical hit damage multiplier.
   */
  CRITICAL_DAMAGE("critical_damage"),

  /**
   * Flat damage blocked.
   */
  BLOCK("block"),

  /**
   * Parry chance.
   */
  PARRY_CHANCE("parry_chance"),

  /**
   * Percentage of the damage deflected.
   */
  DEFLECT("deflect"),

  /**
   * Dodge chance.
   */
  DODGE_CHANCE("dodge_chance"),

  /**
   * Item damage multiplier.
   */
  ITEM_DAMAGE("item_damage"),

  /**
   * Item cooldown multiplier.
   */
  ITEM_COOLDOWN("item_cooldown"),

  /**
   * Apply status chance multiplier.
   */
  STATUS_CHANCE("status_chance");

  /**
   * Aethel attribute id.
   */
  private final String id;

  /**
   * Associates an Aethel attribute with its id.
   *
   * @param id attribute id
   */
  AethelAttribute(String id) {
    this.id = id;
  }

  /**
   * Gets the attribute's id.
   *
   * @return attribute id
   */
  public String getId() {
    return this.id;
  }
}
