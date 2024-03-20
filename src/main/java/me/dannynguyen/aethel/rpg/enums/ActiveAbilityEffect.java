package me.dannynguyen.aethel.rpg.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Effects of active abilities.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.15.9
 */
public enum ActiveAbilityEffect {
  /**
   * Causes movement.
   */
  MOVEMENT("Cooldown(t), Distance(m)"),

  /**
   * Causes movement that returns to the original location after a delay.
   */
  PROJECTION("Cooldown(t), Distance(m), Delay(t)"),

  /**
   * Causes Shatter.
   */
  SHATTER("Cooldown(t), Radius(m)");

  /**
   * Active ability effect's data.
   */
  private final String data;

  /**
   * Associates an active ability effect with its data.
   *
   * @param data effect's data
   */
  ActiveAbilityEffect(String data) {
    this.data = data;
  }

  /**
   * Gets the effect's data.
   *
   * @return effect's data
   */
  @NotNull
  public String getData() {
    return this.data;
  }
}
