package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Effects of passive abilities.
 *
 * @author Danny Nguyen
 * @version 1.15.9
 * @since 1.15.9
 */
public enum PassiveAbilityEffect {
  /**
   * Causes Stack instances.
   */
  STACK_INSTANCE("Stacks, Duration(t)"),

  /**
   * Causes Spark.
   */
  SPARK("Damage, Radius(m)");

  /**
   * Passive ability effect's fields.
   */
  private String fields;

  /**
   * Associates a passive ability effect with its fields.
   *
   * @param fields effect's fields
   */
  PassiveAbilityEffect(@NotNull String fields) {
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
