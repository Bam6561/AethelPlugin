package me.dannynguyen.aethel.inventories.aethelItem;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.readers.ItemMetaReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * AethelItemGet gives items upon click.
 *
 * @author Danny Nguyen
 * @version 1.4.0
 * @since 1.4.0
 */
public class AethelItemGet {
  /**
   * Gets an item.
   *
   * @param e      inventory click event
   * @param player interacting player
   * @throws NullPointerException invalid item
   */
  public void getItem(InventoryClickEvent e, Player player) {
    try {
      AethelResources resources = AethelPlugin.getInstance().getResources();
      ItemStack item = resources.getAethelItemData().getItemsMap().
          get(new ItemMetaReader().readItemName(e.getCurrentItem())).getItem();

      if (player.getInventory().firstEmpty() != -1) {
        player.getInventory().addItem(item);
      } else {
        player.getWorld().dropItem(player.getLocation(), item);
      }
    } catch (NullPointerException ignored) {
    }
  }
}
