package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Types of passive abilities.
 *
 * @author Danny Nguyen
 * @version 1.15.9
 * @since 1.15.1
 */
public enum PassiveAbility {
  /**
   * Applies stacks of Brittle status.
   */
  CHILL("Chill", "chill", PassiveAbilityEffect.STACK_INSTANCE),

  /**
   * Apply stacks of Soaked status.
   */
  DAMPEN("Dampen", "dampen", PassiveAbilityEffect.STACK_INSTANCE),

  /**
   * Attacks chain to enemies with stacks of Soaked status.
   */
  SPARK("Spark", "spark", PassiveAbilityEffect.SPARK),

  /**
   * Applies stacks of Bleed status.
   */
  RUPTURE("Rupture", "rupture", PassiveAbilityEffect.STACK_INSTANCE);

  /**
   * Proper name.
   */
  private final String properName;

  /**
   * Ability ID.
   */
  private final String id;

  /**
   * Ability effect.
   */
  private final PassiveAbilityEffect effect;

  /**
   * Associates a passive ability with its effect.
   *
   * @param properName proper name
   * @param id         ability id
   * @param effect     ability effect
   */
  PassiveAbility(@NotNull String properName, @NotNull String id, @NotNull PassiveAbilityEffect effect) {
    this.properName = Objects.requireNonNull(properName, "Null name");
    this.id = Objects.requireNonNull(id, "Null id");
    this.effect = Objects.requireNonNull(effect, "Null effect");
  }

  /**
   * Gets the ability's proper name.
   *
   * @return ability's proper name
   */
  @NotNull
  public String getProperName() {
    return this.properName;
  }

  /**
   * Gets the ability's ID.
   *
   * @return ability's ID
   */
  @NotNull
  public String getId() {
    return this.id;
  }

  /**
   * Gets the ability's effect.
   *
   * @return ability's effect
   */
  @NotNull
  public PassiveAbilityEffect getEffect() {
    return this.effect;
  }
}
