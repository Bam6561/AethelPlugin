package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a condition ability pair.
 *
 * @author Danny Nguyen
 * @version 1.16.1
 * @since 1.16.1
 */
public class ConditionAbility {
  /**
   * Trigger condition.
   */
  private final TriggerCondition condition;

  /**
   * Passive ability.
   */
  private final PassiveAbilityType ability;

  /**
   * Associates a trigger condition with an ability.
   *
   * @param condition trigger condition
   * @param ability   passive ability
   */
  public ConditionAbility(@NotNull TriggerCondition condition, @NotNull PassiveAbilityType ability) {
    this.condition = Objects.requireNonNull(condition, "Null condition");
    this.ability = Objects.requireNonNull(ability, "Null ability");
  }

  /**
   * Gets the trigger condition.
   *
   * @return trigger condition
   */
  @NotNull
  public TriggerCondition getCondition() {
    return this.condition;
  }

  /**
   * Gets the passive ability.
   *
   * @return passive ability
   */
  @NotNull
  public PassiveAbilityType getAbility() {
    return this.ability;
  }
}
