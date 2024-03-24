package me.dannynguyen.aethel.enums.rpg.abilities;

import me.dannynguyen.aethel.enums.rpg.StatusType;
import org.jetbrains.annotations.NotNull;

/**
 * Types of active abilities.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.15.1
 */
public enum ActiveType {
  /**
   * Forward facing teleport.
   */
  BLINK("Blink", "blink", ActiveEffect.MOVEMENT),

  /**
   * Forward facing movement.
   */
  DASH("Dash", "dash", ActiveEffect.MOVEMENT),

  /**
   * Forward facing teleport that after a delay,
   * teleports the user back to their original location.
   */
  PROJECTION("Projection", "projection", ActiveEffect.PROJECTION),

  /**
   * Immediately consumes all stacks of {@link StatusType#BRITTLE} from nearby enemies.
   */
  SHATTER("Shatter", "shatter", ActiveEffect.SHATTER);

  /**
   * Proper name.
   */
  private final String properName;

  /**
   * Ability ID.
   */
  private final String id;

  /**
   * {@link ActiveEffect}
   */
  private final ActiveEffect effect;

  /**
   * Associates an active ability with its {@link ActiveEffect}.
   *
   * @param properName proper name
   * @param id         ability ID
   * @param effect     {@link ActiveEffect}
   */
  ActiveType(String properName, String id, ActiveEffect effect) {
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
   * Gets the {@link ActiveEffect}.
   *
   * @return {@link ActiveEffect}
   */
  @NotNull
  public ActiveEffect getEffect() {
    return this.effect;
  }
}
