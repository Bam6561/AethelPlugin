package me.dannynguyen.aethel.utility;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

/**
 * Gets or modifies existing items' repair costs.
 *
 * @author Danny Nguyen
 * @version 1.13.7
 * @since 1.13.7
 */
public class ItemRepairCost {
  /**
   * Utility methods only.
   */
  private ItemRepairCost() {
  }

  /**
   * Gets the item's repair cost.
   *
   * @param item interacting item
   * @return repair cost
   */
  public static String getRepairCost(ItemStack item) {
    if (item.getItemMeta() instanceof Repairable repair) {
      return String.valueOf(repair.getRepairCost());
    } else {
      return "";
    }
  }

  /**
   * Sets the item's repair cost.
   *
   * @param item interacting item
   * @param cost repair cost to be set
   */
  public static void setRepairCost(ItemStack item, int cost) {
    if (item.getItemMeta() instanceof Repairable repair) {
      repair.setRepairCost(cost);
      item.setItemMeta(repair);
    }
  }
}
