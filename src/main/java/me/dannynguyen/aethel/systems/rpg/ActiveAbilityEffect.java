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
   * Active ability effect's fields.
   */
  private String fields;

  /**
   * Associates an active ability effect with its fields.
   *
   * @param fields effect's fields
   */
  ActiveAbilityEffect(@NotNull String fields) {
    this.fields = Objects.requireNonNull(fields, "Null fields");
  }

  /**
   * Gets the effect's fields.
   *
   * @return effect's fields
   */
  @NotNull
  public String getFields() {
    return this.fields;
  }
}
