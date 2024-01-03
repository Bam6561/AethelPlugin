package me.dannynguyen.aethel.inventories.aethelItem;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

/**
 * AethelItemCreate creates Aethel Items.
 *
 * @author Danny Nguyen
 * @version 1.4.0
 * @since 1.4.0
 */
public class AethelItemCreate {
  /**
   * Checks if there is an item in the correct inventory slot to save.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void readSaveClick(InventoryClickEvent e, Player player) {
    ItemStack item = e.getClickedInventory().getItem(3);
    if (item != null) {
      saveItemToFile(player, nameItemFile(item), encodeItem(item));
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
  private String nameItemFile(ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    if (meta.hasDisplayName()) {
      return meta.getDisplayName().toLowerCase().replace(" ", "_");
    } else {
      return item.getType().name().toLowerCase();
    }
  }

  /**
   * Encodes an item into bytes.
   *
   * @param item item to encode
   * @return encoded item string
   * @throws IOException item could not be encoded
   */
  private String encodeItem(ItemStack item) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
      boos.writeObject(item);
      boos.flush();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException ex) {
      return null;
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
  private void saveItemToFile(Player player, String itemName, String encodedItem) {
    try {
      FileWriter fw = new FileWriter(AethelPlugin.getInstance().getResources().getAethelItemDirectory()
          + "/" + itemName + ".txt");
      fw.write(encodedItem);
      fw.close();
      player.sendMessage(ChatColor.GREEN + "[Saved] " + ChatColor.WHITE + itemName + ".txt");
    } catch (IOException e) {
      player.sendMessage(ChatColor.RED + "Unable to save item.");
    }
  }
}
