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
public enum ActiveAbilityType {
  /**
   * Forward facing teleport.
   */
  BLINK("Blink", "blink", Effect.MOVEMENT),

  /**
   * Forward facing movement.
   */
  DASH("Dash", "dash", Effect.MOVEMENT),

  /**
   * Forward facing teleport that after a delay,
   * teleports the user back to their original location.
   */
  PROJECTION("Projection", "projection", Effect.PROJECTION),

  /**
   * Immediately consumes all stacks of {@link StatusType#BRITTLE} from nearby enemies.
   */
  SHATTER("Shatter", "shatter", Effect.SHATTER);

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
   * Associates an active ability with its {@link Effect}.
   *
   * @param properName proper name
   * @param id         ability ID
   * @param effect     {@link Effect}
   */
  ActiveAbilityType(String properName, String id, Effect effect) {
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
  public ActiveAbilityType.Effect getEffect() {
    return this.effect;
  }

  /**
   * Effects of {@link ActiveAbilityType active abilities}.
   *
   * @author Danny Nguyen
   * @version 1.17.7
   * @since 1.15.9
   */
  public enum Effect {
    /**
     * Causes movement.
     */
    MOVEMENT("Cooldown(t), Distance(m)"),

    /**
     * Causes movement that returns to the original location after a delay.
     */
    PROJECTION("Cooldown(t), Distance(m), Delay(t)"),

    /**
     * Causes Shatter.
     */
    SHATTER("Cooldown(t), Radius(m)");

    /**
     * Active ability effect's data.
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
