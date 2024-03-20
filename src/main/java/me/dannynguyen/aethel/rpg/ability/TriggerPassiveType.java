package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.PassiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.Trigger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a {@link Trigger trigger} {@link PassiveAbilityType passive ability} pair.
 *
 * @author Danny Nguyen
 * @version 1.16.3
 * @since 1.16.1
 */
public class TriggerPassiveType {
  /**
   * {@link Trigger Trigger}.
   */
  private final Trigger trigger;

  /**
   * {@link PassiveAbilityType Type}.
   */
  private final PassiveAbilityType type;

  /**
   * Associates a {@link Trigger trigger} with a {@link PassiveAbilityType type}.
   *
   * @param trigger {@link Trigger trigger}
   * @param type    {@link PassiveAbilityType type}
   */
  public TriggerPassiveType(@NotNull Trigger trigger, @NotNull PassiveAbilityType type) {
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.type = Objects.requireNonNull(type, "Null ability");
  }

  /**
   * Gets the {@link Trigger trigger}.
   *
   * @return {@link Trigger trigger}
   */
  @NotNull
  public Trigger getTrigger() {
    return this.trigger;
  }

  /**
   * Gets the {@link PassiveAbilityType type}.
   *
   * @return {@link PassiveAbilityType type}
   */
  @NotNull
  public PassiveAbilityType getType() {
    return this.type;
  }
}
