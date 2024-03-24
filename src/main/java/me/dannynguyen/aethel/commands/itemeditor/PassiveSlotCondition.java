package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.TriggerCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a
 * {@link Key#PASSIVE_LIST passive ability's}
 * {@link RpgEquipmentSlot} and
 * {@link TriggerCondition}.
 * <p>
 * Used for text display within the {@link PassiveMenu}.
 *
 * @author Danny Nguyen
 * @version 1.17.12
 * @since 1.15.12
 */
class PassiveSlotCondition {
  /**
   * {@link RpgEquipmentSlot}
   */
  private final String slot;

  /**
   * {@link TriggerCondition}
   */
  private final String condition;

  /**
   * Associates an {@link RpgEquipmentSlot}
   * with its {@link TriggerCondition}.
   *
   * @param eSlot     {@link RpgEquipmentSlot}
   * @param condition {@link TriggerCondition}.
   */
  PassiveSlotCondition(@NotNull String eSlot, @NotNull String condition) {
    this.slot = Objects.requireNonNull(eSlot, "Null slot");
    this.condition = Objects.requireNonNull(condition, "Null condition");
  }

  /**
   * Gets the {@link RpgEquipmentSlot}.
   *
   * @return {@link RpgEquipmentSlot}
   */
  @NotNull
  protected String getSlot() {
    return this.slot;
  }

  /**
   * Gets the {@link TriggerCondition}.
   *
   * @return {@link TriggerCondition}
   */
  @NotNull
  protected String getCondition() {
    return this.condition;
  }
}
