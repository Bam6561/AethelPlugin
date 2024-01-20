package me.dannynguyen.aethel.inventories.aethelItem.utility;

import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.FileWriter;
import java.io.IOException;

/**
 * AethelItemSave is a utility class that saves Aethel Items.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.4.0
 */
public class AethelItemSave {
  /**
   * Checks if there is an item in the correct inventory slot to save.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void readSaveClick(InventoryClickEvent e, Player player) {
    ItemStack item = e.getClickedInventory().getItem(3);
    if (item != null) {
      saveItemToFile(player, nameItemFile(item), ItemCreator.encodeItem(item));
    } else {
      player.sendMessage(ChatColor.RED + "No item to save.");
    }
  }

  /**
   * Names an item file by either its display name or material.
   *
   * @param item interacting item
   * @return item file name
   */
  private static String nameItemFile(ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    if (meta.hasDisplayName()) {
      return meta.getDisplayName().toLowerCase().replace(" ", "_");
    } else {
      return item.getType().name().toLowerCase();
    }
  }

  /**
   * Saves an item file to the file system.
   *
   * @param player      interacting player
   * @param itemName    item name
   * @param encodedItem encoded item string
   * @throws IOException file could not be created
   */
  private static void saveItemToFile(Player player, String itemName, String encodedItem) {
    try {
      FileWriter fw = new FileWriter(AethelResources.aethelItemDirectory
          + "/" + itemName + "_itm.txt");
      fw.write(encodedItem);
      fw.close();
      player.sendMessage(ChatColor.GREEN + "[Saved] " + ChatColor.WHITE + itemName + "_itm.txt");
    } catch (IOException ex) {
      player.sendMessage(ChatColor.RED + "Unable to save item.");
    }
  }
}
