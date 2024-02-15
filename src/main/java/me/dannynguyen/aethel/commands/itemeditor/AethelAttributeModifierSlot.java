package me.dannynguyen.aethel.commands.itemeditor;

/**
 * Represents an Aethel attribute modifier.
 *
 * @author Danny Nguyen
 * @version 1.9.18
 * @since 1.7.5
 */
public record AethelAttributeModifierSlot(String type, String slot) {
  /**
   * Gets the attribute type.
   *
   * @return attribute type
   */
  public String getType() {
    return this.type;
  }

  /**
   * Gets the equipment slot.
   *
   * @return equipment slot
   */
  public String getSlot() {
    return this.slot;
  }
}
