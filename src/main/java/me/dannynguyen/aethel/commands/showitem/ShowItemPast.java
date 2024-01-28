package me.dannynguyen.aethel.commands.showitem;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.showitem.objects.ItemOwner;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * ShowItemPast is an inventory under the ShowItem command that shows past shared items.
 *
 * @author Danny Nguyen
 * @version 1.6.12
 * @since 1.4.5
 */
public class ShowItemPast {
  /**
   * Creates and names a ShowItemPast inventory.
   *
   * @param user user
   * @return ShowItemPast inventory
   */
  public static Inventory createInventory(Player user) {
    Inventory inv = Bukkit.createInventory(user, 9,
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
    for (ItemOwner itemOwner : PluginData.showItemData.getPastItems()) {
      ItemStack item = itemOwner.getItem().clone();
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(ChatColor.DARK_PURPLE + itemOwner.getOwner() +
          ChatColor.WHITE + " " + ItemReader.readName(item));
      item.setItemMeta(meta);
      inv.setItem(index, item);
      index++;
    }
  }
}
