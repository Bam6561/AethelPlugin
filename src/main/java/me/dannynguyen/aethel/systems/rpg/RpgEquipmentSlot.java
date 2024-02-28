package me.dannynguyen.aethel.systems.rpg;

/**
 * Types of RPG equipment slots.
 * <p>
 * Necklace and ring are only equippable in the Sheet menu.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.13.1
 * @since 1.11.6
 */
public enum RpgEquipmentSlot {
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
  RING
}
