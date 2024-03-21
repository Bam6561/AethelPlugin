package me.dannynguyen.aethel.commands.itemeditor;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a
 * {@link me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey#PASSIVE_LIST passive ability's}
 * {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot} and
 * {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition}.
 * <p>
 * Used for text display within the {@link PassiveMenu}.
 *
 * @author Danny Nguyen
 * @version 1.17.12
 * @since 1.15.12
 */
class UniquePassiveIdentifier {
  /**
   * {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   */
  private final String slot;

  /**
   * {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition}
   */
  private final String condition;

  /**
   * Associates an {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   * with its {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition}.
   *
   * @param eSlot     {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   * @param condition {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition}.
   */
  UniquePassiveIdentifier(@NotNull String eSlot, @NotNull String condition) {
    this.slot = Objects.requireNonNull(eSlot, "Null slot");
    this.condition = Objects.requireNonNull(condition, "Null condition");
  }

  /**
   * Gets the {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}.
   *
   * @return {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   */
  @NotNull
  protected String getSlot() {
    return this.slot;
  }

  /**
   * Gets the {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition}.
   *
   * @return {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition}
   */
  @NotNull
  protected String getCondition() {
    return this.condition;
  }
}
