package me.dannynguyen.aethel.objects;

/**
 * SlotAmount is an object that relates an inventory slot
 * with the amount of materials it should have.
 *
 * @author Danny Nguyen
 * @version 1.2.3
 * @since 1.2.3
 */
public class SlotAmount {
  private int slot;
  private int amount;

  public SlotAmount(int slot, int amount) {
    this.slot = slot;
    this.amount = amount;
  }

  public int getSlot() {
    return this.slot;
  }

  public int getAmount() {
    return this.amount;
  }
}
