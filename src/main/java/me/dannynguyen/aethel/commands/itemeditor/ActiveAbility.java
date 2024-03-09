package me.dannynguyen.aethel.commands.itemeditor;

/**
 * Types of active abilities.
 *
 * @author Danny Nguyen
 * @version 1.15.1
 * @since 1.15.1
 */
enum ActiveAbility {
  /**
   * Forward facing teleport.
   */
  BLINK,

  /**
   * Forward facing movement.
   */
  DASH,

  /**
   * Forward facing teleport that after a delay,
   * teleports the player back to their original location.
   */
  PROJECTION,

  /**
   * Immediately triggers all stacks of Brittle from nearby enemies.
   */
  SHATTER
}
