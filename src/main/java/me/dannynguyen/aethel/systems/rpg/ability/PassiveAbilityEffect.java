package me.dannynguyen.aethel.systems.rpg.ability;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Effects of passive abilities.
 *
 * @author Danny Nguyen
 * @version 1.16.14.1
 * @since 1.15.9
 */
public enum PassiveAbilityEffect {
  /**
   * Causes stack instances.
   */
  STACK_INSTANCE("Self, Stacks, Duration(t)"),

  /**
   * Causes chain damage.
   */
  CHAIN_DAMAGE("Self, Damage, Radius(m)");

  /**
   * Passive ability effect's data.
   */
  private final String data;

  /**
   * Associates a passive ability effect with its data.
   *
   * @param data effect's data
   */
  PassiveAbilityEffect(@NotNull String data) {
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
