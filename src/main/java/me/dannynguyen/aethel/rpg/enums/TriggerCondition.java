package me.dannynguyen.aethel.rpg.enums;

import org.jetbrains.annotations.NotNull;

/**
 * {@link PassiveType} {@link Trigger} conditions.
 *
 * @author Danny Nguyen
 * @version 1.17.7
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
   * Associates a condition with its data.
   *
   * @param data condition's data
   */
  TriggerCondition(String data) {
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
