package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Effects of active abilities.
 *
 * @author Danny Nguyen
 * @version 1.15.9
 * @since 1.15.9
 */
public enum ActiveAbilityEffect {
  /**
   * Causes movement.
   */
  MOVEMENT("Distance(m), Cooldown(t)"),

  /**
   * Causes movement that returns to the original location after a delay.
   */
  PROJECTION("Distance(m), Delay(t), Cooldown(t)"),

  /**
   * Causes Shatter.
   */
  SHATTER("Radius(m), Cooldown(t)");

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
