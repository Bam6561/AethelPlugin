package me.dannynguyen.aethel.commands.showitem;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.showitem.object.ItemOwner;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * ShowItemPast is an inventory that shows past shared items.
 *
 * @author Danny Nguyen
 * @version 1.8.0
 * @since 1.4.5
 */
public class ShowItemPast {
  /**
   * Creates and names a ShowItemPast inventory.
   *
   * @param user user
   * @return ShowItemPast inventory
   */
  public static Inventory openInventory(Player user) {
    Inventory inv = createInventory(user);
    addPastShownItems(inv);
    return inv;
  }

  /**
   * Creates and names a ShowItemPast inventory.
   *
   * @param user user
   * @return ShowItemPast inventory
   */
  private static Inventory createInventory(Player user) {
    return Bukkit.createInventory(user, 9,
        ChatColor.DARK_GRAY + "Show " + ChatColor.DARK_PURPLE + "Past");
  }

  /**
   * Adds past shown items to the ShowItemPast inventory.
   *
   * @param inv interacting inventory
   */
  private static void addPastShownItems(Inventory inv) {
    int index = 0;
    for (ItemOwner itemOwner : PluginData.showItemData.getPastItems()) {
      String owner = itemOwner.getOwner();
      ItemStack item = itemOwner.getItem().clone();
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(ChatColor.DARK_PURPLE + owner + ChatColor.WHITE + " " + ItemReader.readName(item));
      item.setItemMeta(meta);
      inv.setItem(index, item);
      index++;
    }
  }
}
