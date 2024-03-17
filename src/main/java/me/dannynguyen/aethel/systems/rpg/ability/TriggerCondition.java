package me.dannynguyen.aethel.systems.rpg.ability;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Conditions of ability triggers.
 *
 * @author Danny Nguyen
 * @version 1.16.14.1
 * @since 1.15.13
 */
public enum TriggerCondition {
  /**
   * Chance and cooldown.
   */
  CHANCE_COOLDOWN("% Chance, Cooldown(t)"),

  /**
   * % of max HP and cooldown.
   */
  HEALTH_COOLDOWN("% HP, Cooldown(t)");

  /**
   * Trigger condition's data.
   */
  private final String data;

  /**
   * Associates a trigger condition with its data.
   *
   * @param data condition's data
   */
  TriggerCondition(@NotNull String data) {
    this.data = Objects.requireNonNull(data, "Null data");
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
