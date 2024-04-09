package me.dannynguyen.aethel.enums.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Types of statuses.
 *
 * @author Danny Nguyen
 * @version 1.19.4
 * @since 1.14.7
 */
public enum StatusType {
  /**
   * Cumulative stacking damage over time.
   */
  BLEED("Bleed", true),

  /**
   * Cumulative stacks that can be triggered by
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#SHATTER}
   * to deal an instance of damage.
   */
  BRITTLE("Brittle", true),

  /**
   * Cumulative stacking damage over time that spreads
   * its remaining stacks to nearby entities upon death.
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

  /**
   * Classification of status type.
   *
   * @author Danny Nguyen
   * @version 1.19.4
   * @since 1.19.4
   */
  public enum Type {
    /**
     * Damaging status types.
     */
    DAMAGE(Set.of(BLEED, ELECTROCUTE)),

    /**
     * Non-damaging status types.
     */
    NON_DAMAGE(Set.of(BRITTLE, FRACTURE, SOAKED, VULNERABLE));

    /**
     * Associated status types.
     */
    private final Set<StatusType> statusTypes;

    /**
     * Associates a classification type with status types.
     *
     * @param statusTypes associated status types
     */
    Type(Set<StatusType> statusTypes) {
      this.statusTypes = statusTypes;
    }

    /**
     * Gets associated status types.
     *
     * @return associated status types
     */
    public Set<StatusType> getStatusTypes() {
      return this.statusTypes;
    }
  }
}
