package me.dannynguyen.aethel.utils.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Gets or modifies existing items' repair costs.
 *
 * @author Danny Nguyen
 * @version 1.23.4
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
  @NotNull
  public static String getRepairCost(@NotNull ItemStack item) {
    Objects.requireNonNull(item, "Null item");
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
  public static void setRepairCost(@NotNull ItemStack item, int cost) {
    Objects.requireNonNull(item, "Null item");
    if (!(item.getItemMeta() instanceof Repairable repair)) {
      return;
    }

    repair.setRepairCost(cost);
    item.setItemMeta(repair);
  }
}
