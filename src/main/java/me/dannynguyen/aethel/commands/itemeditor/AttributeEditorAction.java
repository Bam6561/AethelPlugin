package me.dannynguyen.aethel.commands.itemeditor;

import javax.annotation.Nullable;

/**
 * Types of interactions within AttributeEditor menus.
 *
 * @author Danny Nguyen
 * @version 1.9.18
 * @since 1.9.18
 */
public enum AttributeEditorAction {
  /**
   * Head slot.
   */
  HEAD,

  /**
   * Chest slot.
   */
  CHEST,

  /**
   * Legs slot.
   */
  LEGS,

  /**
   * Feet slot.
   */
  FEET,

  /**
   * Hand slot.
   */
  HAND,

  /**
   * Off hand slot.
   */
  OFF_HAND,

  /**
   * Necklace slot.
   */
  NECKLACE,

  /**
   * Ring slot.
   */
  RING;

  /**
   * Gets the string as an enum.
   *
   * @param action type of interaction
   * @return enum value of string
   */
  @Nullable
  public static AttributeEditorAction asEnum(String action) {
    action = action.toLowerCase();
    switch (action) {
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
      case "necklace" -> {
        return NECKLACE;
      }
      case "ring" -> {
        return RING;
      }
      default -> {
        return null;
      }
    }
  }

  /**
   * Gets the enum as a string.
   *
   * @param action type of interaction
   * @return string value of enum
   */
  @Nullable
  public static String asString(AttributeEditorAction action) {
    switch (action) {
      case HEAD -> {
        return "head";
      }
      case CHEST -> {
        return "chest";
      }
      case LEGS -> {
        return "legs";
      }
      case FEET -> {
        return "feet";
      }
      case HAND -> {
        return "hand";
      }
      case OFF_HAND -> {
        return "off_hand";
      }
      case NECKLACE -> {
        return "necklace";
      }
      case RING -> {
        return "ring";
      }
      default -> {
        return null;
      }
    }
  }
}
