package me.dannynguyen.aethel.commands.forge;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an inventory slot containing an ItemStack.
 * <p>
 * The "amount" field is separate from the ItemStack's built-in
 * amount, as it is used in RecipeCraft to set a new value
 * post-craft if the craft operation's requirements are met.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.15.5
 * @since 1.2.4
 */
class InventorySlot {
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
  protected InventorySlot(int slot, @NotNull ItemStack item, int amount) {
    this.item = Objects.requireNonNull(item, "Item is null");
    this.slot = slot;
    this.amount = amount;
  }

  /**
   * Gets the inventory slot.
   *
   * @return inventory slot
   */
  protected int getSlot() {
    return this.slot;
  }

  /**
   * Gets the ItemStack.
   *
   * @return ItemStack
   */
  @NotNull
  protected ItemStack getItem() {
    return this.item;
  }

  /**
   * Gets the post-craft amount of ItemStack.
   *
   * @return post-craft amount of ItemStack
   */
  protected int getAmount() {
    return this.amount;
  }

  /**
   * Sets the post-craft amount of ItemStack.
   *
   * @param amount post-craft amount of ItemStack
   */
  protected void setAmount(int amount) {
    this.amount = amount;
  }
}
