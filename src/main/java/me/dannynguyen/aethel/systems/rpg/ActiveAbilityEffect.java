package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Effects of active abilities.
 *
 * @author Danny Nguyen
 * @version 1.16.0
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
  private String data;

  /**
   * Associates an active ability effect with its data.
   *
   * @param data effect's data
   */
  ActiveAbilityEffect(@NotNull String data) {
    this.data = Objects.requireNonNull(data, "Null data");
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
