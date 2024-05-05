package me.dannynguyen.aethel.enums.rpg.abilities;

import org.jetbrains.annotations.NotNull;

/**
 * Types of {@link PassiveAbilityType} triggers.
 *
 * @author Danny Nguyen
 * @version 1.24.9
 * @since 1.15.11
 */
public enum PassiveTriggerType {
  /**
   * Below a % of max health.
   */
  BELOW_HEALTH("Below % HP", "below_health", Condition.HEALTH_COOLDOWN),

  /**
   * Damage dealt.
   */
  DAMAGE_DEALT("Damage Dealt", "damage_dealt", Condition.CHANCE_COOLDOWN),

  /**
   * Damage taken.
   */
  DAMAGE_TAKEN("Damage Taken", "damage_taken", Condition.CHANCE_COOLDOWN),

  /**
   * On an interval.
   */
  INTERVAL("Interval", "interval", Condition.COOLDOWN),

  /**
   * Killed an entity.
   */
  ON_KILL("On Kill", "on_kill", Condition.CHANCE_COOLDOWN);

  /**
   * Trigger's proper name.
   */
  private final String properName;

  /**
   * Trigger's ID.
   */
  private final String id;

  /**
   * Trigger condition.
   */
  private final Condition condition;

  /**
   * Associates an equipment slot with its proper name and ID.
   *
   * @param properName proper name
   * @param id         trigger ID
   * @param condition  {@link Condition}
   */
  PassiveTriggerType(String properName, String id, Condition condition) {
    this.properName = properName;
    this.id = id;
    this.condition = condition;
  }

  /**
   * Gets the trigger's proper name.
   *
   * @return trigger's proper name
   */
  @NotNull
  public String getProperName() {
    return this.properName;
  }

  /**
   * Gets the trigger's ID.
   *
   * @return trigger's ID
   */
  @NotNull
  public String getId() {
    return this.id;
  }

  /**
   * Gets the {@link Condition}.
   *
   * @return {@link Condition}
   */
  @NotNull
  public PassiveTriggerType.Condition getCondition() {
    return this.condition;
  }

  /**
   * {@link PassiveAbilityType} {@link PassiveTriggerType} conditions.
   *
   * @author Danny Nguyen
   * @version 1.24.9
   * @since 1.15.13
   */
  public enum Condition {
    /**
     * Cooldown.
     */
    COOLDOWN("Cooldown(t),"),

    /**
     * Chance and cooldown.
     */
    CHANCE_COOLDOWN("% Chance, Cooldown(t),"),

    /**
     * % of max HP and cooldown.
     */
    HEALTH_COOLDOWN("% HP, Cooldown(t),");

    /**
     * Trigger condition's data.
     */
    private final String data;

    /**
     * Associates a condition with its data.
     *
     * @param data condition's data
     */
    Condition(String data) {
      this.data = data;
    }

    /**
     * Gets the condition's data.
     *
     * @return condition's data
     */
    @NotNull
    public String getData() {
      return this.data;
    }
  }
}
