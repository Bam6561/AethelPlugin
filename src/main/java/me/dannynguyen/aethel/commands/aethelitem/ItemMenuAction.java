package me.dannynguyen.aethel.commands.aethelitem;

import org.jetbrains.annotations.Nullable;

/**
 * Types of interaction within AethelItem menus.
 *
 * @author Danny Nguyen
 * @version 1.9.9
 * @since 1.9.8
 */
public enum ItemMenuAction {
  /**
   * Obtain items.
   */
  GET,

  /**
   * Remove items.
   */
  REMOVE,

  /**
   * View item categories.
   */
  VIEW;

  /**
   * Gets the action as a string.
   *
   * @param action type of interaction
   * @return string value of action
   */
  @Nullable
  public static String asString(ItemMenuAction action) {
    switch (action) {
      case GET -> {
        return "get";
      }
      case REMOVE -> {
        return "remove";
      }
      case VIEW -> {
        return "view";
      }
      default -> {
        return null;
      }
    }
  }
}
