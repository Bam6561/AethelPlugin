package me.dannynguyen.aethel.commands.status;

/**
 * Types of Status command actions.
 *
 * @author Danny Nguyen
 * @version 1.14.8
 * @since 1.14.8
 */
enum StatusCommandAction {
  /**
   * Reads the entity's statuses.
   */
  GET,

  /**
   * Sets a status on the entity.
   */
  SET,

  /**
   * Removes a status from the entity.
   */
  REMOVE,

  /**
   * Removes all statuses from the entity.
   */
  REMOVE_ALL
}
