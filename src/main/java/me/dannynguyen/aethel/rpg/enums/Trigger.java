package me.dannynguyen.aethel.rpg.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Types of passive ability triggers.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.15.11
 */
public enum Trigger {
  /**
   * Below a % of max health.
   */
  BELOW_HP("Below % HP", "below_hp", TriggerCondition.HEALTH_COOLDOWN),

  /**
   * Damage dealt.
   */
  DAMAGE_DEALT("Damage Dealt", "damage_dealt", TriggerCondition.CHANCE_COOLDOWN),

  /**
   * Damage taken.
   */
  DAMAGE_TAKEN("Damage Taken", "damage_taken", TriggerCondition.CHANCE_COOLDOWN),

  /**
   * Killed an entity.
   */
  ON_KILL("On Kill", "on_kill", TriggerCondition.CHANCE_COOLDOWN);

  /**
   * Trigger's proper name.
   */
  private final String properName;

  /**
   * Trigger's ID.
   */
  private final String id;

  /**
   * Trigger condition.
   */
  private final TriggerCondition condition;

  /**
   * Associates an equipment slot with its proper name and id.
   *
   * @param properName proper name
   * @param id         trigger id
   * @param condition  trigger condition
   */
  Trigger(String properName, String id, TriggerCondition condition) {
    this.properName = properName;
    this.id = id;
    this.condition = condition;
  }

  /**
   * Gets the trigger's proper name.
   *
   * @return trigger's proper name
   */
  @NotNull
  public String getProperName() {
    return this.properName;
  }

  /**
   * Gets the trigger's ID.
   *
   * @return trigger's ID
   */
  @NotNull
  public String getId() {
    return this.id;
  }

  /**
   * Gets the trigger's condition.
   *
   * @return trigger's condition
   */
  @NotNull
  public TriggerCondition getCondition() {
    return this.condition;
  }
}
