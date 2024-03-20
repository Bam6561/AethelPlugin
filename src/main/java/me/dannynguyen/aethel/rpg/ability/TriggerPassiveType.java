package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.PassiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.Trigger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a {@link Trigger} {@link PassiveAbilityType} pair.
 *
 * @author Danny Nguyen
 * @version 1.16.3
 * @since 1.16.1
 */
public class TriggerPassiveType {
  /**
   * {@link Trigger}
   */
  private final Trigger trigger;

  /**
   * {@link PassiveAbilityType}
   */
  private final PassiveAbilityType type;

  /**
   * Associates a {@link Trigger} with a {@link PassiveAbilityType}.
   *
   * @param trigger {@link Trigger}
   * @param type    {@link PassiveAbilityType}
   */
  public TriggerPassiveType(@NotNull Trigger trigger, @NotNull PassiveAbilityType type) {
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.type = Objects.requireNonNull(type, "Null ability");
  }

  /**
   * Gets the {@link Trigger}.
   *
   * @return {@link Trigger}
   */
  @NotNull
  public Trigger getTrigger() {
    return this.trigger;
  }

  /**
   * Gets the {@link PassiveAbilityType}.
   *
   * @return {@link PassiveAbilityType}
   */
  @NotNull
  public PassiveAbilityType getType() {
    return this.type;
  }
}
