package me.dannynguyen.aethel.systems.rpg;

/**
 * Types of statuses.
 *
 * @author Danny Nguyen
 * @version 1.14.7
 * @since 1.14.7
 */
public enum RpgStatusType {
  /**
   * Cumulative stacking damage over time.
   */
  BLEED,

  /**
   * Cumulative stacks that can be triggered to deal an instance of damage.
   */
  BRITTLE,

  /**
   * Cumulative stacking damage over time that spreads
   * its remaining stacks to a nearby entity upon death.
   */
  ELECTROCUTE,

  /**
   * Highest instance stack that reduces the entity's effective armor value.
   */
  FRACTURE,

  /**
   * Cumulative stacks that allow and increase chain damage between entities.
   */
  SOAK,

  /**
   * Highest instance stack that increases the damage taken by the entity.
   */
  VULNERABLE
}
