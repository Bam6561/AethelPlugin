package me.dannynguyen.aethel.readers;

import org.bukkit.inventory.ItemStack;

/**
 * ItemMetaReader reads metadata from ItemStacks.
 *
 * @author Danny Nguyen
 * @version 1.1.4
 * @since 1.1.4
 */
public class ItemMetaReader {
  /**
   * Returns either an item's renamed value or its material.
   *
   * @param item item
   * @return effective item name
   */
  public String getItemName(ItemStack item) {
    if (item.getItemMeta().hasDisplayName()) {
      return item.getItemMeta().getDisplayName();
    } else {
      return item.getType().name();
    }
  }
}
