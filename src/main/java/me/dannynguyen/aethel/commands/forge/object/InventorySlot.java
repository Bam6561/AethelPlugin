package me.dannynguyen.aethel.commands.forge.object;

import org.bukkit.inventory.ItemStack;

/**
 * InventorySlot is an object that relates an inventory slot
 * with the type and amount of materials it should have.
 *
 * @author Danny Nguyen
 * @version 1.4.12
 * @since 1.2.4
 */
public class InventorySlot {
  private final int slot;
  private final ItemStack item;
  private int amount;

  public InventorySlot(int slot, ItemStack item, int amount) {
    this.slot = slot;
    this.item = item;
    this.amount = amount;
  }

  public int getSlot() {
    return this.slot;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public int getAmount() {
    return this.amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }
}
