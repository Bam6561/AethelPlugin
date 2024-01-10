package me.dannynguyen.aethel.inventories.aethelItem;

import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * AethelItemGet gives items upon click.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.4.0
 */
public class AethelItemGet {
  /**
   * Gives an item to the player.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void getItem(InventoryClickEvent e, Player player) {
    ItemStack item = AethelResources.aethelItemData.getItemsMap().
        get(ItemReader.readItemName(e.getCurrentItem())).getItem();

    if (player.getInventory().firstEmpty() != -1) {
      player.getInventory().addItem(item);
    } else {
      player.getWorld().dropItem(player.getLocation(), item);
    }
  }
}
