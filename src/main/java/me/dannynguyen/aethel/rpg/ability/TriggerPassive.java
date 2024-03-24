package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.listeners.EquipmentEvent;
import me.dannynguyen.aethel.rpg.enums.PassiveType;
import me.dannynguyen.aethel.rpg.enums.Trigger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a {@link Trigger} {@link PassiveType} pair.
 * <p>
 * Used to remove abilities upon {@link EquipmentEvent}.
 *
 * @author Danny Nguyen
 * @version 1.18.1
 * @since 1.16.1
 */
public record TriggerPassive(Trigger trigger, PassiveType type) {
  /**
   * Associates a {@link Trigger} with a {@link PassiveType}.
   *
   * @param trigger {@link Trigger}
   * @param type    {@link PassiveType}
   */
  public TriggerPassive(@NotNull Trigger trigger, @NotNull PassiveType type) {
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.type = Objects.requireNonNull(type, "Null ability");
  }

  /**
   * Gets the {@link Trigger}.
   *
   * @return {@link Trigger}
   */
  @NotNull
  public Trigger trigger() {
    return this.trigger;
  }

  /**
   * Gets the {@link PassiveType}.
   *
   * @return {@link PassiveType}
   */
  @NotNull
  public PassiveType type() {
    return this.type;
  }
}
