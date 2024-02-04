package me.dannynguyen.aethel.commands.aethelItems;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.aethelItems.object.AethelItem;
import me.dannynguyen.aethel.enums.PluginDirectory;
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
 * @version 1.9.3
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
    if (ItemReader.isNotNullOrAir(item)) {
      saveItemToFile(user, item);
    } else {
      user.sendMessage(Failure.NO_ITEM_TO_SAVE.message);
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
    user.sendMessage(Success.REMOVE_ITEM.message + ChatColor.WHITE + aethelItem.getName());
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
      user.sendMessage(Success.SAVE_ITEM.message + ChatColor.WHITE + ItemReader.readName(item));
    } catch (IOException ex) {
      user.sendMessage(Failure.UNABLE_TO_SAVE.message);
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

  private enum Success {
    SAVE_ITEM(ChatColor.GREEN + "[Saved Aethel Item] "),
    REMOVE_ITEM(ChatColor.RED + "[Removed Aethel Item] ");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }

  private enum Failure {
    NO_ITEM_TO_SAVE(ChatColor.RED + "No item to save."),
    UNABLE_TO_SAVE(ChatColor.RED + "Unable to save item.");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }
}
