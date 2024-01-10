package me.dannynguyen.aethel.objects;

import org.bukkit.inventory.ItemStack;

/**
 * ItemOwner is an object that relates an item with its owner.
 *
 * @author Danny Nguyen
 * @version 1.4.14
 * @since 1.4.14
 */
public class ItemOwner {
  private final String owner;
  private final ItemStack item;

  public ItemOwner(String owner, ItemStack pastItem) {
    this.owner = owner;
    this.item = pastItem;
  }

  public String getOwner() {
    return this.owner;
  }

  public ItemStack getItem() {
    return this.item;
  }
}
