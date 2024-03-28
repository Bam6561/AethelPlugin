package me.dannynguyen.aethel.enums.rpg.abilities;

import me.dannynguyen.aethel.enums.rpg.StatusType;
import org.jetbrains.annotations.NotNull;

/**
 * Types of passive abilities.
 *
 * @author Danny Nguyen
 * @version 1.19.11
 * @since 1.15.1
 */
public enum PassiveAbilityType {
  /**
   * Applies stacks of {@link StatusType#BLEED}.
   */
  BLEED("Bleed", "bleed", Effect.STACK_INSTANCE),

  /**
   * Applies stacks of {@link StatusType#BRITTLE}.
   */
  BRITTLE("Brittle", "brittle", Effect.STACK_INSTANCE),

  /**
   * Applies stacks of {@link StatusType#ELECTROCUTE}.
   */
  ELECTROCUTE("Electrocute", "electrocute", Effect.STACK_INSTANCE),

  /**
   * Applies stacks of {@link StatusType#FRACTURE}.
   */
  FRACTURE("Fracture", "fracture", Effect.STACK_INSTANCE),

  /**
   * Applies potion effects.
   */
  POTION_EFFECT("Potion Effect", "potion_effect", Effect.POTION_EFFECT),

  /**
   * Apply stacks of {@link StatusType#SOAKED}.
   */
  SOAKED("Soaked", "soaked", Effect.STACK_INSTANCE),

  /**
   * Attacks chain to entities with stacks of {@link StatusType#SOAKED}.
   */
  SPARK("Spark", "spark", Effect.CHAIN_DAMAGE),

  /**
   * Applies stacks of {@link StatusType#VULNERABLE}.
   */
  VULNERABLE("Vulnerable", "vulnerable", Effect.STACK_INSTANCE);

  /**
   * Proper name.
   */
  private final String properName;

  /**
   * Ability ID.
   */
  private final String id;

  /**
   * {@link Effect}
   */
  private final Effect effect;

  /**
   * Associates a passive ability with its {@link Effect}.
   *
   * @param properName proper name
   * @param id         ability ID
   * @param effect     {@link Effect}
   */
  PassiveAbilityType(String properName, String id, Effect effect) {
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
   * Gets the {@link Effect}.
   *
   * @return {@link Effect}
   */
  @NotNull
  public PassiveAbilityType.Effect getEffect() {
    return this.effect;
  }

  /**
   * Effects of {@link PassiveAbilityType}.
   *
   * @author Danny Nguyen
   * @version 1.19.10
   * @since 1.15.9
   */
  public enum Effect {
    /**
     * Causes {@link StatusType stack instances}.
     */
    STACK_INSTANCE("Self, Stacks, Duration(t)"),

    /**
     * Causes chain damage.
     */
    CHAIN_DAMAGE("Self, Damage, Radius(m)"),

    /**
     * Causes a potion effect to be applied.
     */
    POTION_EFFECT("Self, PotionEffect, Amplifier, Duration(t), Ambient");

    /**
     * Passive ability effect's data.
     */
    private final String data;

    /**
     * Associates an effect with its data.
     *
     * @param data effect's data
     */
    Effect(String data) {
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
}
