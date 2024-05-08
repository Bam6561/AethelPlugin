package me.dannynguyen.aethel.enums.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Types of statuses.
 *
 * @author Danny Nguyen
 * @version 1.25.2
 * @since 1.14.7
 */
public enum StatusType {
  /**
   * Stacking damage over time.
   * <p>
   * Cumulative.
   */
  BLEED("Bleed", true),

  /**
   * Reduces the entity's effective armor value.
   * <p>
   * Highest instance.
   */
  BRITTLE("brittle", false),

  /**
   * Cumulative stacks that can be triggered by
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#SHATTER}
   * to deal an instance of damage.
   * <p>
   * Cumulative.
   */
  CHILL("Chill", true),

  /**
   * Cumulative stacking damage over time that spreads
   * its remaining stacks to nearby entities upon death.
   * <p>
   * Cumulative.
   */
  ELECTROCUTE("Electrocute", true),

  /**
   * Cumulative stacks that allow and increase chain damage between entities.
   * <p>
   * Cumulative.
   */
  SOAK("Soak", true),

  /**
   * Highest instance stack that increases the damage taken by the entity.
   * <p>
   * Highest instance.
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
    NON_DAMAGE(Set.of(CHILL, BRITTLE, SOAK, VULNERABLE));

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
