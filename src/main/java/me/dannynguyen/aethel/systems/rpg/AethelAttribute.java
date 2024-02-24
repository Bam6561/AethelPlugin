package me.dannynguyen.aethel.systems.rpg;

import javax.annotation.Nullable;

/**
 * Types of Aethel attributes.
 *
 * @author Danny Nguyen
 * @version 1.12.8
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
   * Counterattack chance.
   */
  COUNTER_CHANCE,

  /**
   * Dodge chance.
   */
  DODGE_CHANCE,

  /**
   * Flat damage blocked.
   */
  TOUGHNESS,

  /**
   * Item damage multiplier.
   */
  ITEM_DAMAGE,

  /**
   * Item cooldown multiplier.
   */
  ITEM_COOLDOWN;

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
      case "counter_chance" -> {
        return COUNTER_CHANCE;
      }
      case "toughness" -> {
        return TOUGHNESS;
      }
      case "item_damage" -> {
        return ITEM_DAMAGE;
      }
      case "item_cooldown" -> {
        return ITEM_COOLDOWN;
      }
      default -> {
        return null;
      }
    }
  }
}
