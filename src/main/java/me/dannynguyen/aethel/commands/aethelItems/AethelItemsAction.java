package me.dannynguyen.aethel.commands.aethelItems;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.aethelItems.objects.AethelItem;
import me.dannynguyen.aethel.enums.PluginDirectory;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.FileWriter;
import java.io.IOException;

/**
 * AethelItemsAction is a utility class that saves, gives, and removes Aethel items.
 *
 * @author Danny Nguyen
 * @version 1.7.13
 * @since 1.7.9
 */
public class AethelItemsAction {
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
   * Gives an item to the user.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void getItem(InventoryClickEvent e, Player user) {
    ItemStack item = PluginData.aethelItemsData.getItemsMap().
        get(ItemReader.readName(e.getCurrentItem())).getItem();

    if (user.getInventory().firstEmpty() != -1) {
      user.getInventory().addItem(item);
    } else {
      user.getWorld().dropItem(user.getLocation(), item);
    }
  }

  /**
   * Removes an existing item.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void removeItem(InventoryClickEvent e, Player user) {
    AethelItem aethelItem = PluginData.aethelItemsData.getItemsMap().
        get(ItemReader.readName(e.getCurrentItem()));

    aethelItem.getFile().delete();
    user.sendMessage(PluginMessage.Success.AETHELITEMS_REMOVE.message +
        ChatColor.WHITE + aethelItem.getName());
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
      FileWriter fw = new FileWriter(PluginDirectory.AETHELITEMS.file.getPath()
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
