package me.dannynguyen.aethel.inventories.aethelItem;

import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.objects.aethelitem.AethelItem;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * AethelItemDelete is a utility class that deletes Aethel items.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.4.0
 */
public class AethelItemDelete {
  /**
   * Deletes an existing item.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void deleteItem(InventoryClickEvent e, Player player) {
    AethelItem aethelItem = AethelResources.aethelItemData.getItemsMap().
        get(ItemReader.readItemName(e.getCurrentItem()));

    aethelItem.getFile().delete();
    player.sendMessage(ChatColor.RED + "[Deleted] " + ChatColor.WHITE +
        aethelItem.getName().toLowerCase().replace(" ", "_") + "_itm.txt");
  }
}
