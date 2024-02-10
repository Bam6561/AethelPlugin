package me.dannynguyen.aethel.commands.character;

/**
 * Types of Aethel attributes.
 *
 * @author Danny Nguyen
 * @version 1.9.10
 * @since 1.9.10
 */
public enum AethelAttribute {
  CRITICAL_CHANCE("critical_chance"),
  CRITICAL_DAMAGE("critical_damage"),
  BLOCK("block"),
  PARRY_CHANCE("parry_chance"),
  PARRY_DEFLECT("parry_deflect"),
  DODGE_CHANCE("dodge_chance"),
  ABILITY_DAMAGE("ability_damage"),
  ABILITY_COOLDOWN("ability_cooldown"),
  APPLY_STATUS("apply_status");

  private final String id;

  AethelAttribute(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }
}
