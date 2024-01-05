package me.dannynguyen.aethel.inventories.aethelItem;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.objects.AethelItem;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * AethelItemDelete is an inventory under the AethelItem command that deletes items.
 *
 * @author Danny Nguyen
 * @version 1.4.3
 * @since 1.4.0
 */
public class AethelItemDelete {
  /**
   * Deletes an existing item.
   *
   * @param e      inventory click event
   * @param player interacting player
   * @throws NullPointerException invalid item
   */
  public void deleteItem(InventoryClickEvent e, Player player) {
    try {
      AethelResources resources = AethelPlugin.getInstance().getResources();
      AethelItem aethelItem = resources.getAethelItemData().getItemsMap().
          get(new ItemReader().readItemName(e.getCurrentItem()));

      aethelItem.getFile().delete();
      player.sendMessage(ChatColor.RED + "[Deleted] " + ChatColor.WHITE +
          aethelItem.getName().toLowerCase().replace(" ", "_") + "_itm.txt");
    } catch (NullPointerException ignored) {
    }
  }
}
