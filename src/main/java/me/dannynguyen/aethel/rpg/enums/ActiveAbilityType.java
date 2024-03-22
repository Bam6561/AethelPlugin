package me.dannynguyen.aethel.rpg.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Types of active abilities.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.15.1
 */
public enum ActiveAbilityType {
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
   * teleports the user back to their original location.
   */
  PROJECTION("Projection", "projection", ActiveAbilityEffect.PROJECTION),

  /**
   * Immediately consumes all stacks of {@link StatusType#BRITTLE} from nearby enemies.
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
   * {@link ActiveAbilityEffect}
   */
  private final ActiveAbilityEffect effect;

  /**
   * Associates an active ability with its {@link ActiveAbilityEffect}.
   *
   * @param properName proper name
   * @param id         ability ID
   * @param effect     {@link ActiveAbilityEffect}
   */
  ActiveAbilityType(String properName, String id, ActiveAbilityEffect effect) {
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
   * Gets the {@link ActiveAbilityEffect}.
   *
   * @return {@link ActiveAbilityEffect}
   */
  @NotNull
  public ActiveAbilityEffect getEffect() {
    return this.effect;
  }
}
