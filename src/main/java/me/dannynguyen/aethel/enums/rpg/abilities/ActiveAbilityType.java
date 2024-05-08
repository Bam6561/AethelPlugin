package me.dannynguyen.aethel.enums.rpg.abilities;

import me.dannynguyen.aethel.enums.rpg.StatusType;
import org.jetbrains.annotations.NotNull;

/**
 * Types of active abilities.
 *
 * @author Danny Nguyen
 * @version 1.25.2
 * @since 1.15.1
 */
public enum ActiveAbilityType {
  /**
   * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute} {@link me.dannynguyen.aethel.rpg.Buffs}.
   */
  AETHEL_ATTRIBUTE("Aethel Attribute", "aethel_attribute", Effect.BUFF),

  /**
   * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute} {@link me.dannynguyen.aethel.rpg.Buffs}.
   */
  AETHEL_ATTRIBUTE_2("Aethel Attribute 2", "aethel_attribute_2", Effect.BUFF),

  /**
   * Spherical shaped pull of entities towards the caster.
   */
  ATTRACT("Attract", "attract", Effect.DISPLACEMENT),

  /**
   * Minecraft attribute {@link me.dannynguyen.aethel.rpg.Buffs}.
   */
  ATTRIBUTE("Attribute", "attribute", Effect.BUFF),

  /**
   * Minecraft attribute {@link me.dannynguyen.aethel.rpg.Buffs}.
   */
  ATTRIBUTE_2("Attribute 2", "attribute_2", Effect.BUFF),

  /**
   * Forward facing line shaped attack.
   * <p>
   * Unidirectional.
   */
  BEAM("Beam", "beam", Effect.DISTANCE_DAMAGE),

  /**
   * Forward facing teleport.
   * <p>
   * Unidirectional.
   */
  BLINK("Blink", "blink", Effect.TELEPORT),

  /**
   * Forward facing movement.
   */
  DASH("Dash", "dash", Effect.MOVEMENT),

  /**
   * Clears all non-damaging potion effects and statuses.
   */
  DISMISS("Dismiss", "dismiss", Effect.CLEAR_STATUS),

  /**
   * Clears all damaging potion effects and statuses.
   */
  DISREGARD("Disregard", "disregard", Effect.CLEAR_STATUS),

  /**
   * Forward-facing pull of entities towards the caster.
   * <p>
   * Unidirectional.
   */
  DRAG("Drag", "drag", Effect.DISPLACEMENT),

  /**
   * Forward facing caster teleport to an entity.
   * <p>
   * Unidirectional.
   */
  EMERGE("Emerge", "emerge", Effect.TELEPORT),

  /**
   * Spherical shaped attack.
   */
  EXPLODE("Explode", "explode", Effect.DISTANCE_DAMAGE),

  /**
   * Forward facing triangular arc shaped attack.
   */
  SWEEP("Sweep", "sweep", Effect.DISTANCE_DAMAGE),

  /**
   * Forward facing entity teleport to the caster.
   * <p>
   * Unidirectional.
   */
  HOOK("Hook", "hook", Effect.TELEPORT),

  /**
   * Forward facing angular movement.
   * <p>
   * Unidirectional.
   */
  LEAP("Leap", "leap", Effect.MOVEMENT),

  /**
   * Applies a potion effect.
   */
  POTION_EFFECT("Potion Effect", "potion_effect", Effect.POTION_EFFECT),

  /**
   * Applies a potion effect.
   */
  POTION_EFFECT_2("Potion Effect 2", "potion_effect_2", Effect.POTION_EFFECT),

  /**
   * Forward facing teleport that after a delay,
   * teleports the user back to their original location.
   * <p>
   * Unidirectional.
   */
  PROJECTION("Projection", "projection", Effect.PROJECTION),

  /**
   * Spherical shaped push of entities away from the caster.
   */
  REPEL("Repel", "repel", Effect.DISPLACEMENT),

  /**
   * Forward facing location switch with an entity.
   * <p>
   * Unidirectional.
   */
  SWITCH("Switch", "switch", Effect.TELEPORT),

  /**
   * Circular shaped attack.
   */
  QUAKE("Quake", "quake", Effect.DISTANCE_DAMAGE),

  /**
   * Immediately consumes all stacks of {@link StatusType#CHILL}
   * from nearby enemies to deal an instance of damage.
   */
  SHATTER("Shatter", "shatter", Effect.SHATTER),

  /**
   * Upwards facing movement.
   */
  SPRING("Spring", "spring", Effect.MOVEMENT),

  /**
   * Forward facing push of entities away from the caster.
   * <p>
   * Unidirectional.
   */
  THRUST("Thrust", "thrust", Effect.DISPLACEMENT),

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
   * @version 1.24.0
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
     * Causes entity movement with velocity.
     */
    DISPLACEMENT("Cooldown(t), Modifier(%), Distance(m)"),

    /**
     * Causes damage at a distance.
     */
    DISTANCE_DAMAGE("Cooldown(t), Damage, Distance(m)"),

    /**
     * Causes caster movement with velocity.
     */
    MOVEMENT("Cooldown(t), Modifier(%)"),

    /**
     * Applies a potion effect.
     */
    POTION_EFFECT("Cooldown(t), PotionEffect, Amplifier, Duration(t), Particles"),

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
