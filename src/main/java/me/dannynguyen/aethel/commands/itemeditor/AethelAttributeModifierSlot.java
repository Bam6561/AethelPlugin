package me.dannynguyen.aethel.commands.itemeditor;

/**
 * AethelAttributeModifierSlot is an object relating
 * an Aethel attribute modifier to its equipment slot.
 *
 * @author Danny Nguyen
 * @version 1.8.9
 * @since 1.7.5
 */
public record AethelAttributeModifierSlot(String type, String slot) {

  public String getType() {
    return this.type;
  }

  public String getSlot() {
    return this.slot;
  }
}
