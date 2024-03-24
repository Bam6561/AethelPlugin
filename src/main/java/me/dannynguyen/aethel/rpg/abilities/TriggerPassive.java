package me.dannynguyen.aethel.rpg.abilities;

import me.dannynguyen.aethel.enums.rpg.abilities.PassiveType;
import me.dannynguyen.aethel.enums.rpg.abilities.TriggerType;
import me.dannynguyen.aethel.listeners.EquipmentEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a {@link TriggerType} {@link PassiveType} pair.
 * <p>
 * Used to remove abilities upon {@link EquipmentEvent}.
 *
 * @author Danny Nguyen
 * @version 1.18.1
 * @since 1.16.1
 */
public record TriggerPassive(TriggerType triggerType, PassiveType type) {
  /**
   * Associates a {@link TriggerType} with a {@link PassiveType}.
   *
   * @param triggerType {@link TriggerType}
   * @param type    {@link PassiveType}
   */
  public TriggerPassive(@NotNull TriggerType triggerType, @NotNull PassiveType type) {
    this.triggerType = Objects.requireNonNull(triggerType, "Null trigger");
    this.type = Objects.requireNonNull(type, "Null ability");
  }

  /**
   * Gets the {@link TriggerType}.
   *
   * @return {@link TriggerType}
   */
  @NotNull
  public TriggerType triggerType() {
    return this.triggerType;
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
