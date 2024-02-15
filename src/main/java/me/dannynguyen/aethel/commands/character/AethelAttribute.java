package me.dannynguyen.aethel.commands.character;

/**
 * Types of Aethel attributes.
 *
 * @author Danny Nguyen
 * @version 1.9.19
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
  PARRY_DEFLECT("parry_deflect"),

  /**
   * Dodge chance.
   */
  DODGE_CHANCE("dodge_chance"),

  /**
   * Ability damage multiplier.
   */
  ABILITY_DAMAGE("ability_damage"),

  /**
   * Ability cooldown multiplier.
   */
  ABILITY_COOLDOWN("ability_cooldown"),

  /**
   * Apply status chance multiplier.
   */
  APPLY_STATUS("apply_status");

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
