package me.dannynguyen.aethel.systems.rpg;

/**
 * Types of active abilities.
 *
 * @author Danny Nguyen
 * @version 1.15.6
 * @since 1.15.1
 */
public enum ActiveAbility {
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
