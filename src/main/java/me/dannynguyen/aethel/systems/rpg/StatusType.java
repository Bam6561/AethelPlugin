package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Types of statuses.
 *
 * @author Danny Nguyen
 * @version 1.15.9
 * @since 1.14.7
 */
public enum StatusType {
  /**
   * Cumulative stacking damage over time.
   */
  BLEED("Bleed", "bleed", true),

  /**
   * Cumulative stacks that can be triggered to deal an instance of damage.
   */
  BRITTLE("Brittle", "brittle", true),

  /**
   * Cumulative stacking damage over time that spreads
   * its remaining stacks to a nearby entity upon death.
   */
  ELECTROCUTE("Electrocute", "electrocute", true),

  /**
   * Highest instance stack that reduces the entity's effective armor value.
   */
  FRACTURE("Fracture", "fracture", false),

  /**
   * Cumulative stacks that allow and increase chain damage between entities.
   */
  SOAK("Soak", "soak", true),

  /**
   * Highest instance stack that increases the damage taken by the entity.
   */
  VULNERABLE("Vulnerable", "vulnerable", false);

  /**
   * Proper name.
   */
  private final String properName;

  /**
   * Type ID.
   */
  private final String id;

  /**
   * If the status type's stack instances are cumulative.
   */
  private final boolean isCumulative;

  /**
   * Associates the status type with if its stack instances are cumulative.
   *
   * @param properName   proper name
   * @param id           type id
   * @param isCumulative if the status type's stack instances are cumulative
   */
  StatusType(@NotNull String properName, @NotNull String id, boolean isCumulative) {
    this.properName = Objects.requireNonNull(properName, "Null name");
    this.id = Objects.requireNonNull(id, "Null id");
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
   * Gets the type's ID.
   *
   * @return type's ID
   */
  @NotNull
  public String getId() {
    return this.id;
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