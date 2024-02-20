package me.dannynguyen.aethel.rpg;

import javax.annotation.Nullable;

/**
 * Types of equipment slots.
 * <p>
 * Necklace and ring are only equippable in the Sheet menu.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.11.6
 * @since 1.11.6
 */
public enum EquipmentSlot {
  /**
   * Head.
   */
  HEAD,

  /**
   * Chest.
   */
  CHEST,

  /**
   * Legs.
   */
  LEGS,

  /**
   * Feet.
   */
  FEET,

  /**
   * Main hand.
   */
  HAND,

  /**
   * Off hand.
   */
  OFF_HAND,

  /**
   * Necklace.
   * <p>
   * Sheet menu only.
   * </p>
   */
  NECKLACE,

  /**
   * Ring.
   * <p>
   * Sheet menu only.
   * </p>
   */
  RING;

  /**
   * Gets the string as an enum.
   *
   * @param slot equipment slot
   * @return enum value of string
   */
  @Nullable
  public static EquipmentSlot asEnum(String slot) {
    slot = slot.toLowerCase();
    switch (slot) {
      case "head" -> {
        return HEAD;
      }
      case "chest" -> {
        return CHEST;
      }
      case "legs" -> {
        return LEGS;
      }
      case "feet" -> {
        return FEET;
      }
      case "hand" -> {
        return HAND;
      }
      case "off_hand" -> {
        return OFF_HAND;
      }
      default -> {
        return null;
      }
    }
  }
}
