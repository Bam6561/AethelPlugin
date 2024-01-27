package me.dannynguyen.aethel.inventories.aethelItems;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.objects.aethelitems.AethelItem;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.FileWriter;
import java.io.IOException;

/**
 * AethelItems is a utility class that saves, gives, and removes Aethel items.
 *
 * @author Danny Nguyen
 * @version 1.7.9
 * @since 1.7.9
 */
public class AethelItemsA {
  /**
   * Checks if there is an item in the correct inventory slot to save.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void readSaveClick(InventoryClickEvent e, Player user) {
    ItemStack item = e.getClickedInventory().getItem(3);
    if (item != null) {
      saveItemToFile(user, item);
    } else {
      user.sendMessage(PluginMessage.Failure.AETHELITEMS_NO_ITEM.message);
    }
  }

  /**
   * Gives an item to the player.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void getItem(InventoryClickEvent e, Player player) {
    ItemStack item = PluginData.aethelItemsData.getItemsMap().
        get(ItemReader.readName(e.getCurrentItem())).getItem();

    if (player.getInventory().firstEmpty() != -1) {
      player.getInventory().addItem(item);
    } else {
      player.getWorld().dropItem(player.getLocation(), item);
    }
  }

  /**
   * Removes an existing item.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void removeItem(InventoryClickEvent e, Player player) {
    AethelItem aethelItem = PluginData.aethelItemsData.getItemsMap().
        get(ItemReader.readName(e.getCurrentItem()));

    aethelItem.getFile().delete();
    player.sendMessage(ChatColor.RED + "[Removed Aethel Item] "
        + ChatColor.WHITE + aethelItem.getName());
  }

  /**
   * Saves an item file to the file system.
   *
   * @param user user
   * @param item interacting item
   * @throws IOException file could not be created
   */
  private static void saveItemToFile(Player user, ItemStack item) {
    try {
      FileWriter fw = new FileWriter(PluginData.aethelItemsDirectory
          + "/" + nameItemFile(item) + "_itm.txt");
      fw.write(ItemCreator.encodeItem(item));
      fw.close();
      user.sendMessage(PluginMessage.Success.AETHELITEMS_SAVE.message
          + ChatColor.WHITE + ItemReader.readName(item));
    } catch (IOException ex) {
      user.sendMessage(PluginMessage.Failure.AETHELITEMS_SAVE_FAILED.message);
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
}
