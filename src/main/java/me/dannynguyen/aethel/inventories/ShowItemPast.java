package me.dannynguyen.aethel.inventories;

import me.dannynguyen.aethel.AethelResources;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * ShowItemPast is an inventory under the Show command that shows past shared items.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.4.5
 */
public class ShowItemPast {
  /**
   * Creates and names a ShowItemPast inventory.
   *
   * @param player interacting player
   * @return ShowItemPast inventory
   */
  public static Inventory createInventory(Player player) {
    Inventory inv = Bukkit.createInventory(player, 9,
        ChatColor.DARK_GRAY + "Show " + ChatColor.DARK_PURPLE + "Past");
    addPastShownItems(inv);
    return inv;
  }

  /**
   * Adds past shown items to the ShowItemPast inventory.
   *
   * @param inv interacting inventory
   */
  private static void addPastShownItems(Inventory inv) {
    int index = 0;
    for (ItemStack item : AethelResources.showItemData.getPastItems()) {
      inv.setItem(index, item);
      index++;
    }
  }
}
