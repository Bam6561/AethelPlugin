package me.dannynguyen.aethel.inventories;

import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * ItemEditorMenu is an inventory under the ItemEditor command that displays an item's metadata fields.
 *
 * @author Danny Nguyen
 * @version 1.6.8
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
    addMetadataButtons(inv, item);
    return inv;
  }

  /**
   * Creates and names an ItemEditorMenu inventory to the item.
   *
   * @param player interacting player
   * @return ItemEditorMenu
   */
  private static Inventory createInventory(Player player, ItemStack item) {
    Inventory inv = Bukkit.createInventory(player, 54, ChatColor.DARK_GRAY + "ItemEditor");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Adds item metadata editor buttons.
   *
   * @param inv  interacting inventory
   * @param item interacting item
   */
  private static void addMetadataButtons(Inventory inv, ItemStack item) {
    ItemMeta meta = item.getItemMeta();

    ItemStack displayName = ItemCreator.createItem(Material.NAME_TAG,
        ChatColor.AQUA + "Display Name", List.of(ChatColor.WHITE + ItemReader.readItemName(item)));
    ItemStack customModelData = (!meta.hasCustomModelData() ?
        ItemCreator.createItem(Material.RED_DYE, ChatColor.AQUA + "Custom Model Data") :
        ItemCreator.createItem(Material.RED_DYE, ChatColor.AQUA + "Custom Model Data",
            List.of(ChatColor.WHITE + String.valueOf(item.getItemMeta().getCustomModelData()))));
    ItemStack lore = (!meta.hasLore() ?
        ItemCreator.createItem(Material.WRITABLE_BOOK, ChatColor.AQUA + "Lore") :
        ItemCreator.createItem(Material.WRITABLE_BOOK, ChatColor.AQUA + "Lore", meta.getLore()));

    inv.setItem(9, displayName);
    inv.setItem(10, customModelData);
    inv.setItem(11, lore);
  }
}
