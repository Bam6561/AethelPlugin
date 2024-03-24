package me.dannynguyen.aethel.enums.rpg.abilities;

import me.dannynguyen.aethel.enums.rpg.StatusType;
import org.jetbrains.annotations.NotNull;

/**
 * Effects of {@link PassiveType}.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.15.9
 */
public enum PassiveEffect {
  /**
   * Causes {@link StatusType stack instances}.
   */
  STACK_INSTANCE("Self, Stacks, Duration(t)"),

  /**
   * Causes chain damage.
   */
  CHAIN_DAMAGE("Self, Damage, Radius(m)");

  /**
   * Passive ability effect's data.
   */
  private final String data;

  /**
   * Associates an effect with its data.
   *
   * @param data effect's data
   */
  PassiveEffect(String data) {
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
