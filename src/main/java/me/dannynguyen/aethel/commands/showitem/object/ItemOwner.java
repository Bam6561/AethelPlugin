package me.dannynguyen.aethel.commands.showitem.object;

import org.bukkit.inventory.ItemStack;

/**
 * ItemOwner is an object relating an item with its owner.
 *
 * @author Danny Nguyen
 * @version 1.7.13
 * @since 1.4.14
 */
public record ItemOwner(String owner, ItemStack item) {
  public String getOwner() {
    return this.owner;
  }

  public ItemStack getItem() {
    return this.item;
  }
}
