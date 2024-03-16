package me.dannynguyen.aethel.commands.itemeditor;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a passive ability's slot and condition.
 * <p>
 * Used for text display within the PassiveMenu.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.15.12
 * @since 1.15.12
 */
class SlotCondition {
  /**
   * Ability's equipment slot.
   */
  private final String slot;

  /**
   * Ability's trigger condition.
   */
  private final String condition;

  /**
   * Associates an equipment slot with its trigger condition.
   *
   * @param slot      equipment slot
   * @param condition trigger condition
   */
  protected SlotCondition(@NotNull String slot, @NotNull String condition) {
    this.slot = Objects.requireNonNull(slot, "Null slot");
    this.condition = Objects.requireNonNull(condition, "Null condition");
  }

  /**
   * Gets the ability's equipment slot.
   *
   * @return ability's equipment slot
   */
  protected String getSlot() {
    return this.slot;
  }

  /**
   * Gets the ability's trigger condition.
   *
   * @return ability's trigger condition
   */
  protected String getCondition() {
    return this.condition;
  }
}