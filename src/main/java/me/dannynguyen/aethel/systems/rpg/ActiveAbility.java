package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Types of active abilities.
 *
 * @author Danny Nguyen
 * @version 1.15.9
 * @since 1.15.1
 */
public enum ActiveAbility {
  /**
   * Forward facing teleport.
   */
  BLINK("Blink", "blink", ActiveAbilityEffect.MOVEMENT),

  /**
   * Forward facing movement.
   */
  DASH("Dash", "dash", ActiveAbilityEffect.MOVEMENT),

  /**
   * Forward facing teleport that after a delay,
   * teleports the player back to their original location.
   */
  PROJECTION("Projection", "projection", ActiveAbilityEffect.PROJECTION),

  /**
   * Immediately triggers all stacks of Brittle from nearby enemies.
   */
  SHATTER("Shatter", "shatter", ActiveAbilityEffect.SHATTER);

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
  private final ActiveAbilityEffect effect;

  /**
   * Associates an active ability with its effect.
   *
   * @param properName proper name
   * @param id         ability id
   * @param effect     ability effect
   */
  ActiveAbility(@NotNull String properName, @NotNull String id, @NotNull ActiveAbilityEffect effect) {
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
  public ActiveAbilityEffect getEffect() {
    return this.effect;
  }
}
