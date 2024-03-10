package me.dannynguyen.aethel.systems.rpg;

/**
 * Types of passive abilities.
 *
 * @author Danny Nguyen
 * @version 1.15.6
 * @since 1.15.1
 */
public enum PassiveAbility {
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
