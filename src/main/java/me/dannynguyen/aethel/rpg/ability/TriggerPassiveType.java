package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.PassiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.Trigger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a trigger passive ability pair.
 *
 * @author Danny Nguyen
 * @version 1.16.3
 * @since 1.16.1
 */
public class TriggerPassiveType {
  /**
   * Ability trigger type.
   */
  private final Trigger trigger;

  /**
   * Passive ability type.
   */
  private final PassiveAbilityType abilityType;

  /**
   * Associates a trigger with an ability.
   *
   * @param trigger     trigger
   * @param abilityType passive ability
   */
  public TriggerPassiveType(@NotNull Trigger trigger, @NotNull PassiveAbilityType abilityType) {
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.abilityType = Objects.requireNonNull(abilityType, "Null ability");
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
  public PassiveAbilityType getAbilityType() {
    return this.abilityType;
  }
}
