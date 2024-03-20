package me.dannynguyen.aethel.rpg.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Types of passive abilities.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.15.1
 */
public enum PassiveAbilityType {
  /**
   * Applies stacks of Brittle.
   */
  BRITTLE("Brittle", "brittle", PassiveAbilityEffect.STACK_INSTANCE),

  /**
   * Apply stacks of Soaked.
   */
  SOAKED("Soaked", "soaked", PassiveAbilityEffect.STACK_INSTANCE),

  /**
   * Attacks chain to entities with stacks of Soaked.
   */
  SPARK("Spark", "spark", PassiveAbilityEffect.CHAIN_DAMAGE),

  /**
   * Applies stacks of Bleed.
   */
  BLEED("Bleed", "bleed", PassiveAbilityEffect.STACK_INSTANCE);

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
  PassiveAbilityType(String properName, String id, PassiveAbilityEffect effect) {
    this.properName = properName;
    this.id = id;
    this.effect = effect;
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
