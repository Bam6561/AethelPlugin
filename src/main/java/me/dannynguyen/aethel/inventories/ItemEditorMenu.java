package me.dannynguyen.aethel.inventories;

import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * ItemEditorMenu is an inventory under the ItemEditor command that displays an item's metadata fields.
 *
 * @author Danny Nguyen
 * @version 1.6.7
 * @since 1.6.7
 */
public class ItemEditorMenu {
  /**
   * Opens an ItemEditorMenu with metadata fields.
   *
   * @param player interacting player
   * @param item   interacting item
   * @return ItemEditorMenu with metadata fields
   */
  public static Inventory openEditorMenu(Player player, ItemStack item) {
    Inventory inv = createInventory(player, item);
    addMetadataButtons(inv);
    return inv;
  }

  /**
   * Creates and names an ItemEditorMenu inventory to the item.
   *
   * @param player interacting player
   * @return ItemEditorMenu
   */
  private static Inventory createInventory(Player player, ItemStack item) {
    Inventory inv = Bukkit.createInventory(player, 54,
        ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.WHITE + ItemReader.readItemName(item));
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Adds item metadata editor buttons.
   *
   * @param inv interacting inventory
   */
  private static void addMetadataButtons(Inventory inv) {
    inv.setItem(9, ItemCreator.createItem(Material.NAME_TAG,
        ChatColor.AQUA + "Set Display Name"));
    inv.setItem(10, ItemCreator.createItem(Material.RED_DYE,
        ChatColor.AQUA + "Set Custom Model Data"));
  }
}
