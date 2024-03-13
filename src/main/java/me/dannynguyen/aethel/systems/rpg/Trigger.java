package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Types of ability trigger conditions.
 *
 * @author Danny Nguyen
 * @version 1.15.11
 * @since 1.15.11
 */
public enum Trigger {
  /**
   * Below a % of max health.
   */
  BELOW_HP("Below % HP", "below_hp"),

  /**
   * Damage dealt.
   */
  DEAL_DAMAGE("Deal Damage", "deal_damage"),

  /**
   * Killed an entity.
   */
  KILL("Kill", "kill"),

  /**
   * Damage taken.
   */
  TAKE_DAMAGE("Take Damage", "take_damage");

  /**
   * Trigger's proper name.
   */
  private final String properName;

  /**
   * Trigger's ID.
   */
  private final String id;

  /**
   * Associates an equipment slot with its proper name and id.
   *
   * @param properName proper name
   * @param id         trigger id
   */
  Trigger(@NotNull String properName, @NotNull String id) {
    this.properName = Objects.requireNonNull(properName, "Null name");
    this.id = Objects.requireNonNull(id, "Null ID");
  }

  /**
   * Gets the trigger's proper name.
   *
   * @return trigger's proper name
   */
  public String getProperName() {
    return this.properName;
  }

  /**
   * Gets the trigger's ID.
   *
   * @return trigger's ID
   */
  public String getId() {
    return this.id;
  }
}
