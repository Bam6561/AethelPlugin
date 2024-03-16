package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a trigger ability pair.
 *
 * @author Danny Nguyen
 * @version 1.16.3
 * @since 1.16.1
 */
public class TriggerAbility {
  /**
   * Type of ability trigger.
   */
  private final Trigger trigger;

  /**
   * Passive ability.
   */
  private final PassiveAbilityType ability;

  /**
   * Associates a trigger with an ability.
   *
   * @param trigger trigger
   * @param ability passive ability
   */
  public TriggerAbility(@NotNull Trigger trigger, @NotNull PassiveAbilityType ability) {
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.ability = Objects.requireNonNull(ability, "Null ability");
  }

  /**
   * Gets the trigger condition.
   *
   * @return trigger condition
   */
  @NotNull
  public Trigger getTrigger() {
    return this.trigger;
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
