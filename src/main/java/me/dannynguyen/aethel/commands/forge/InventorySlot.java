package me.dannynguyen.aethel.commands.forge;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an inventory slot containing an ItemStack.
 * <p>
 * The "amount" field is separate from the ItemStack's built-in
 * amount, as it is used in ForgeCraftOperation to set a new value
 * post-craft if the craft operation's requirements are met.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.9.15
 * @since 1.2.4
 */
public class InventorySlot {
  /**
   * Inventory slot number.
   */
  private final int slot;

  /**
   * ItemStack at the inventory slot.
   */
  private final ItemStack item;

  /**
   * Post-craft amount of ItemStack.
   */
  private int amount;

  /**
   * Associates an inventory slot with its item and current amount.
   *
   * @param slot   inventory slot number
   * @param item   ItemStack
   * @param amount amount of ItemStack
   */
  public InventorySlot(int slot, @NotNull ItemStack item, int amount) {
    this.slot = slot;
    this.item = Objects.requireNonNull(item, "Item is null");
    this.amount = amount;
  }

  /**
   * Gets the inventory slot.
   *
   * @return inventory slot
   */
  public int getSlot() {
    return this.slot;
  }

  /**
   * Gets the ItemStack.
   *
   * @return ItemStack
   */
  @NotNull
  public ItemStack getItem() {
    return this.item;
  }

  /**
   * Gets the post-craft amount of ItemStack.
   *
   * @return post-craft amount of ItemStack
   */
  public int getAmount() {
    return this.amount;
  }

  /**
   * Sets the post-craft amount of ItemStack.
   *
   * @param amount post-craft amount of ItemStack
   */
  public void setAmount(int amount) {
    this.amount = amount;
  }
}
