package me.dannynguyen.aethel.rpg.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Types of RPG equipment slots.
 * <p>
 * Necklace and ring are only equippable in the Sheet menu.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.11.6
 */
public enum RpgEquipmentSlot {
  /**
   * Head.
   */
  HEAD("Head", "head"),

  /**
   * Chest.
   */
  CHEST("Chest", "chest"),

  /**
   * Legs.
   */
  LEGS("Legs", "legs"),

  /**
   * Feet.
   */
  FEET("Feet", "feet"),

  /**
   * Main hand.
   */
  HAND("Hand", "hand"),

  /**
   * Off hand.
   */
  OFF_HAND("Off Hand", "off_hand"),

  /**
   * Necklace.
   * <p>
   * Sheet menu only.
   * </p>
   */
  NECKLACE("Necklace", "necklace"),

  /**
   * Ring.
   * <p>
   * Sheet menu only.
   * </p>
   */
  RING("Ring", "ring");

  /**
   * Equipment slot's proper name.
   */
  private final String properName;

  /**
   * Equipment slot's ID.
   */
  private final String id;

  /**
   * Associates an equipment slot with its proper name and id.
   *
   * @param properName proper name
   * @param id         slot id
   */
  RpgEquipmentSlot(String properName, String id) {
    this.properName = properName;
    this.id = id;
  }

  /**
   * Gets the equipment slot's proper name.
   *
   * @return equipment slot's proper name
   */
  @NotNull
  public String getProperName() {
    return this.properName;
  }

  /**
   * Gets the equipment slot's ID.
   *
   * @return equipment slot's ID
   */
  @NotNull
  public String getId() {
    return this.id;
  }
}