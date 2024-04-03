package me.dannynguyen.aethel.enums.rpg.abilities;

import me.dannynguyen.aethel.enums.rpg.StatusType;
import org.jetbrains.annotations.NotNull;

/**
 * Types of active abilities.
 *
 * @author Danny Nguyen
 * @version 1.20.10
 * @since 1.15.1
 */
public enum ActiveAbilityType {
  /**
   * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute} {@link me.dannynguyen.aethel.rpg.Buffs}.
   */
  AETHEL_ATTRIBUTE("Aethel Attribute", "aethel_attribute", Effect.BUFF),

  /**
   * Minecraft attribute {@link me.dannynguyen.aethel.rpg.Buffs}.
   */
  ATTRIBUTE("Attribute", "attribute", Effect.BUFF),

  /**
   * Forward facing teleport.
   * <p>
   * Omnidirectional.
   */
  BLINK("Blink", "blink", Effect.TELEPORT),

  /**
   * Forward facing movement.
   */
  DASH("Dash", "dash", Effect.MOVEMENT),

  /**
   * Clears all non-damaging statuses.
   */
  DISMISS("Dismiss", "dismiss", Effect.CLEAR_STATUS),

  /**
   * Clears all damaging statuses.
   */
  DISREGARD("Disregard", "disregard", Effect.CLEAR_STATUS),

  /**
   * Spherical shaped attack.
   */
  EXPLODE("Explode", "explode", Effect.DISTANCE_DAMAGE),

  /**
   * Forward facing angular movement.
   * <p>
   * Omnidirectional.
   */
  LEAP("Leap", "leap", Effect.MOVEMENT),

  /**
   * Applies a potion effect.
   */
  POTION_EFFECT("Potion Effect", "potion_effect", Effect.POTION_EFFECT),

  /**
   * Forward facing teleport that after a delay,
   * teleports the user back to their original location.
   * <p>
   * Omnidirectional.
   */
  PROJECTION("Projection", "projection", Effect.PROJECTION),

  /**
   * Circular shaped attack.
   */
  QUAKE("Quake", "quake", Effect.DISTANCE_DAMAGE),

  /**
   * Immediately consumes all stacks of {@link StatusType#BRITTLE} from nearby enemies.
   */
  SHATTER("Shatter", "shatter", Effect.SHATTER),

  /**
   * Upwards facing movement.
   */
  SPRING("Spring", "spring", Effect.MOVEMENT),

  /**
   * Forward facing triangular arc shaped attack.
   */
  FORCE_SWEEP("Force Sweep", "force_sweep", Effect.DISTANCE_DAMAGE),

  /**
   * Forward facing line shaped attack.
   * <p>
   * Omnidirectional.
   */
  FORCE_WAVE("Force Wave", "force_wave", Effect.DISTANCE_DAMAGE),

  /**
   * Backwards facing movement.
   */
  WITHDRAW("Withdraw", "withdraw", Effect.MOVEMENT);

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
   * @version 1.20.10
   * @since 1.15.9
   */
  public enum Effect {
    /**
     * Causes {@link me.dannynguyen.aethel.rpg.Buffs}.
     */
    BUFF("Cooldown(t), Attribute, Value, Duration(t)"),

    /**
     * Causes statuses to be removed.
     */
    CLEAR_STATUS("Cooldown(t)"),

    /**
     * Causes damage at a distance.
     */
    DISTANCE_DAMAGE("Cooldown(t), Damage, Distance(m)"),

    /**
     * Causes movement with velocity.
     */
    MOVEMENT("Cooldown(t), Modifier(%)"),

    /**
     * Applies a potion effect.
     */
    POTION_EFFECT("Cooldown(t), PotionEffect, Amplifier, Duration(t), Ambient"),

    /**
     * Causes instant movement that returns to the original location after a delay.
     */
    PROJECTION("Cooldown(t), Distance(m), Delay(t)"),

    /**
     * Causes Shatter.
     */
    SHATTER("Cooldown(t), Radius(m)"),

    /**
     * Causes instant movement.
     */
    TELEPORT("Cooldown(t), Distance(m)");

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
