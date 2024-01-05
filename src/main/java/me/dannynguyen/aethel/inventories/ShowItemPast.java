package me.dannynguyen.aethel.inventories;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * ShowItemPast is an inventory under the Show command that shows past shared items.
 *
 * @author Danny Nguyen
 * @version 1.4.5
 * @since 1.4.5
 */
public class ShowItemPast {
  /**
   * Creates and names a ShowItemPast inventory.
   *
   * @param player interacting player
   * @return ShowItemPast inventory
   */
  public Inventory createInventory(Player player) {
    Inventory inv = Bukkit.createInventory(player, 9,
        ChatColor.DARK_GRAY + "ShowPast");
    addPastShownItems(inv);
    return inv;
  }

  /**
   * Adds past shown items to the ShowItemPast inventory.
   *
   * @param inv interacting inventory
   */
  private void addPastShownItems(Inventory inv) {
    ArrayList<ItemStack> items = AethelPlugin.getInstance().getResources().getShowItemData().getPastItems();
    int index = 0;
    for (ItemStack item : items) {
      inv.setItem(index, item);
      index++;
    }
  }
}
