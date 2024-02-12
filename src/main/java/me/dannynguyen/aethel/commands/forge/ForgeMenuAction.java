package me.dannynguyen.aethel.commands.forge;

import javax.annotation.Nullable;

/**
 * Types of interactions within Forge menus.
 *
 * @author Danny Nguyen
 * @version 1.9.15
 * @since 1.9.15
 */
public enum ForgeMenuAction {
  /**
   * Craft recipes.
   */
  CRAFT,

  /**
   * Edit recipes.
   */
  EDIT,

  /**
   * Remove recipes.
   */
  REMOVE;

  /**
   * Gets the string as an action.
   *
   * @param action type of interaction
   * @return action value of string
   */
  @Nullable
  public static ForgeMenuAction asAction(String action) {
    switch (action) {
      case "craft" -> {
        return CRAFT;
      }
      case "edit" -> {
        return EDIT;
      }
      case "remove" -> {
        return REMOVE;
      }
      default -> {
        return null;
      }
    }
  }

  /**
   * Gets the action as a string.
   *
   * @param action type of interaction
   * @return string value of action
   */
  @Nullable
  public static String asString(ForgeMenuAction action) {
    switch (action) {
      case CRAFT -> {
        return "craft";
      }
      case EDIT -> {
        return "edit";
      }
      case REMOVE -> {
        return "remove";
      }
      default -> {
        return null;
      }
    }
  }
}
