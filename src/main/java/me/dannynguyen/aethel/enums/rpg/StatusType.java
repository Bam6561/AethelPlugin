package me.dannynguyen.aethel.enums.rpg;

import org.jetbrains.annotations.NotNull;

/**
 * Types of statuses.
 *
 * @author Danny Nguyen
 * @version 1.18.1
 * @since 1.14.7
 */
public enum StatusType {
  /**
   * Cumulative stacking damage over time.
   */
  BLEED("Bleed", true),

  /**
   * Cumulative stacks that can be triggered to deal an instance of damage.
   */
  BRITTLE("Brittle", true),

  /**
   * Cumulative stacking damage over time that spreads
   * its remaining stacks to a nearby entities upon death.
   */
  ELECTROCUTE("Electrocute", true),

  /**
   * Highest instance stack that reduces the entity's effective armor value.
   */
  FRACTURE("Fracture", false),

  /**
   * Cumulative stacks that allow and increase chain damage between entities.
   */
  SOAKED("Soaked", true),

  /**
   * Highest instance stack that increases the damage taken by the entity.
   */
  VULNERABLE("Vulnerable", false);

  /**
   * Proper name.
   */
  private final String properName;

  /**
   * If the status type's stack instances are cumulative.
   */
  private final boolean isCumulative;

  /**
   * Associates the status type with if its stack instances are cumulative.
   *
   * @param properName   proper name
   * @param isCumulative if the status type's stack instances are cumulative
   */
  StatusType(String properName, boolean isCumulative) {
    this.properName = properName;
    this.isCumulative = isCumulative;
  }

  /**
   * Gets the type's proper name.
   *
   * @return type's proper name
   */
  @NotNull
  public String getProperName() {
    return this.properName;
  }

  /**
   * Gets if the type's stacks instances are cumulative
   *
   * @return if the type's stack instances are cumulative
   */
  public boolean isCumulative() {
    return this.isCumulative;
  }
}
