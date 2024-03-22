package me.dannynguyen.aethel.rpg.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Types of passive abilities.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.15.1
 */
public enum PassiveType {
  /**
   * Applies stacks of {@link StatusType#BRITTLE}.
   */
  BRITTLE("Brittle", "brittle", PassiveEffect.STACK_INSTANCE),

  /**
   * Apply stacks of {@link StatusType#SOAKED}.
   */
  SOAKED("Soaked", "soaked", PassiveEffect.STACK_INSTANCE),

  /**
   * Attacks chain to entities with stacks of {@link StatusType#SOAKED}.
   */
  SPARK("Spark", "spark", PassiveEffect.CHAIN_DAMAGE),

  /**
   * Applies stacks of {@link StatusType#BLEED}.
   */
  BLEED("Bleed", "bleed", PassiveEffect.STACK_INSTANCE);

  /**
   * Proper name.
   */
  private final String properName;

  /**
   * Ability ID.
   */
  private final String id;

  /**
   * {@link PassiveEffect}
   */
  private final PassiveEffect effect;

  /**
   * Associates a passive ability with its {@link PassiveEffect}.
   *
   * @param properName proper name
   * @param id         ability ID
   * @param effect     {@link PassiveEffect}
   */
  PassiveType(String properName, String id, PassiveEffect effect) {
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
   * Gets the {@link PassiveEffect}.
   *
   * @return {@link PassiveEffect}
   */
  @NotNull
  public PassiveEffect getEffect() {
    return this.effect;
  }
}
