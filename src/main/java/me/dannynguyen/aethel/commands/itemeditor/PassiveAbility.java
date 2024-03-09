package me.dannynguyen.aethel.commands.itemeditor;

/**
 * Types of passive abilities.
 *
 * @author Danny Nguyen
 * @version 1.15.1
 * @since 1.15.1
 */
enum PassiveAbility {
  /**
   * Applies stacks of Brittle status.
   */
  CHILL,

  /**
   * Apply stacks of Soaked status.
   */
  DAMPEN,

  /**
   * Attacks chain to enemies with stacks of Soaked status.
   */
  SPARK,

  /**
   * Applies stacks of Bleed status.
   */
  RUPTURE
}
