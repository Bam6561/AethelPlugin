package me.dannynguyen.aethel.systems;

import javax.annotation.Nullable;

/**
 * Types of Aethel attributes.
 *
 * @author Danny Nguyen
 * @version 1.11.7
 * @since 1.9.10
 */
public enum AethelAttribute {
  /**
   * Max health.
   */
  MAX_HP,

  /**
   * Critical hit chance.
   */
  CRITICAL_CHANCE,

  /**
   * Critical hit damage multiplier.
   */
  CRITICAL_DAMAGE,

  /**
   * Dodge chance.
   */
  DODGE_CHANCE,

  /**
   * Parry chance.
   */
  PARRY_CHANCE,

  /**
   * Percentage of the damage deflected.
   */
  DEFLECT,

  /**
   * Flat damage blocked.
   */
  BLOCK,

  /**
   * Item damage multiplier.
   */
  ITEM_DAMAGE,

  /**
   * Item cooldown multiplier.
   */
  ITEM_COOLDOWN,

  /**
   * Apply status chance multiplier.
   */
  STATUS_CHANCE;

  /**
   * Gets the string as an enum.
   *
   * @param attribute Aethel attribute
   * @return enum value of string
   */
  @Nullable
  public static AethelAttribute asEnum(String attribute) {
    attribute = attribute.toLowerCase();
    switch (attribute) {
      case "max_hp" -> {
        return MAX_HP;
      }
      case "critical_chance" -> {
        return CRITICAL_CHANCE;
      }
      case "critical_damage" -> {
        return CRITICAL_DAMAGE;
      }
      case "dodge_chance" -> {
        return DODGE_CHANCE;
      }
      case "parry_chance" -> {
        return PARRY_CHANCE;
      }
      case "deflect" -> {
        return DEFLECT;
      }
      case "block" -> {
        return BLOCK;
      }
      case "item_damage" -> {
        return ITEM_DAMAGE;
      }
      case "item_cooldown" -> {
        return ITEM_COOLDOWN;
      }
      case "status_chance" -> {
        return STATUS_CHANCE;
      }
      default -> {
        return null;
      }
    }
  }
}
