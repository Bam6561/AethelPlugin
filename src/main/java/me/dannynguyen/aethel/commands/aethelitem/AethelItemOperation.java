package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginDirectory;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents an action done within an AethelItem menu that can either save, give, or remove items.
 *
 * @author Danny Nguyen
 * @version 1.9.8
 * @since 1.7.9
 */
public record AethelItemOperation(@NotNull InventoryClickEvent e, @NotNull Player user) {
  /**
   * Associates an inventory click event with its user.
   *
   * @param e    inventory click event
   * @param user user
   */
  public AethelItemOperation {
    Objects.requireNonNull(e, "Null inventory click event");
    Objects.requireNonNull(user, "Null user");
  }

  /**
   * Gives an item to the user.
   */
  public void getItem() {
    ItemStack item = PluginData.itemRegistry.getItemMap().get(ItemReader.readName(e.getCurrentItem())).getItem();
    if (user.getInventory().firstEmpty() != -1) {
      user.getInventory().addItem(item);
    } else {
      user.getWorld().dropItem(user.getLocation(), item);
    }
  }

  /**
   * Removes an existing item.
   */
  public void removeItem() {
    PersistentItem aethelItem = PluginData.itemRegistry.getItemMap().get(ItemReader.readName(e.getCurrentItem()));
    if (aethelItem.delete()) {
      user.sendMessage(ChatColor.RED + "[Removed Aethel Item] " + ChatColor.WHITE + aethelItem.getName());
    } else {
      user.sendMessage(ChatColor.WHITE + aethelItem.getName() + ChatColor.RED + " has already been removed.");
    }
  }

  /**
   * Checks if there is an item in the designated save slot before saving the item to a file.
   */
  public void saveItem() {
    ItemStack item = e.getClickedInventory().getItem(3);
    if (ItemReader.isNotNullOrAir(item)) {
      String encodedItem = ItemCreator.encodeItem(item);
      if (encodedItem != null) {
        try {
          FileWriter fw = new FileWriter(PluginDirectory.AETHELITEMS.file.getPath() + "/" + nameItemFile(item) + "_itm.txt");
          fw.write(encodedItem);
          fw.close();
          user.sendMessage(ChatColor.GREEN + "[Saved Aethel Item] " + ChatColor.WHITE + ItemReader.readName(item));
        } catch (IOException ex) {
          user.sendMessage(ChatColor.RED + "Failed to write item to file.");
        }
      } else {
        user.sendMessage(ChatColor.RED + "Failed to save item.");
      }
    } else {
      user.sendMessage(ChatColor.RED + "No item to save.");
    }
  }

  /**
   * Names an item file by either its display name or material.
   *
   * @param item interacting item
   * @return item file name
   */
  private String nameItemFile(ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    if (meta.hasDisplayName()) {
      return meta.getDisplayName().toLowerCase().replace(" ", "_");
    } else {
      return item.getType().name().toLowerCase();
    }
  }
}
