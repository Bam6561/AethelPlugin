package me.dannynguyen.aethel.listeners.message;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.itemeditor.ItemEditorMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemEditorMessageListener is a message listener for the ItemEditor command.
 *
 * @author Danny Nguyen
 * @version 1.6.9
 * @since 1.6.7
 */
public class ItemEditorMessageListener {
  /**
   * Sets the item's display name.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  public static void setDisplayName(AsyncPlayerChatEvent e, Player player,
                                    ItemStack item, ItemMeta meta) {
    meta.setDisplayName(e.getMessage());
    item.setItemMeta(meta);
    player.sendMessage(ChatColor.GREEN + "[Named] " + ChatColor.WHITE + e.getMessage());
    returnToEditorMenu(player, item);
  }

  /**
   * Sets the item's custom model data.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   * @throws NumberFormatException not an integer
   */
  public static void setCustomModelData(AsyncPlayerChatEvent e, Player player,
                                        ItemStack item, ItemMeta meta) {
    try {
      meta.setCustomModelData(Integer.parseInt(e.getMessage()));
      item.setItemMeta(meta);
      player.sendMessage(ChatColor.GREEN + "[Custom Model Data] " + ChatColor.WHITE + e.getMessage());
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "Invalid custom model data.");
    }
    returnToEditorMenu(player, item);
  }

  /**
   * Sets the lore.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  public static void setLore(AsyncPlayerChatEvent e, Player player,
                             ItemStack item, ItemMeta meta) {
    meta.setLore(List.of(e.getMessage().split(",, ")));
    item.setItemMeta(meta);
    player.sendMessage(ChatColor.GREEN + "[Set Lore]");
    returnToEditorMenu(player, item);
  }

  /**
   * Clears the lore.
   *
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  public static void clearLore(Player player, ItemStack item, ItemMeta meta) {
    meta.setLore(new ArrayList<>());
    item.setItemMeta(meta);
    player.sendMessage(ChatColor.GREEN + "[Cleared Lore]");
    returnToEditorMenu(player, item);
  }

  /**
   * Adds a line of lore.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  public static void addLore(AsyncPlayerChatEvent e, Player player,
                             ItemStack item, ItemMeta meta) {
    if (meta.hasLore()) {
      List<String> lore = meta.getLore();
      lore.add(e.getMessage());
      meta.setLore(lore);
    } else {
      meta.setLore(List.of(e.getMessage()));
    }
    item.setItemMeta(meta);
    player.sendMessage(ChatColor.GREEN + "[Added Lore]");
    returnToEditorMenu(player, item);
  }

  /**
   * Edits a line of lore.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   * @throws NumberFormatException     not a number
   * @throws IndexOutOfBoundsException invalid index
   */
  public static void editLore(AsyncPlayerChatEvent e, Player player,
                              ItemStack item, ItemMeta meta) {
    String[] input = e.getMessage().split(" ", 2);
    try {
      List<String> lore = meta.getLore();
      lore.set(Integer.parseInt(input[0]) - 1, input[1]);
      meta.setLore(lore);
      item.setItemMeta(meta);
      player.sendMessage(ChatColor.GREEN + "[Edited Lore]");
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "Invalid line number.");
    } catch (IndexOutOfBoundsException ex) {
      player.sendMessage(ChatColor.RED + "Line does not exist.");
    }
    returnToEditorMenu(player, item);
  }

  /**
   * Removes a line of lore.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   * @throws NumberFormatException     not a number
   * @throws IndexOutOfBoundsException invalid index
   */
  public static void removeLore(AsyncPlayerChatEvent e, Player player,
                                ItemStack item, ItemMeta meta) {
    try {
      List<String> lore = meta.getLore();
      lore.remove(Integer.parseInt(e.getMessage()) - 1);
      meta.setLore(lore);
      item.setItemMeta(meta);
      player.sendMessage(ChatColor.GREEN + "[Removed Lore]");
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "Invalid line number.");
    } catch (IndexOutOfBoundsException ex) {
      player.sendMessage(ChatColor.RED + "Line does not exist.");
    }
    returnToEditorMenu(player, item);
  }

  /**
   * Returns to the editor menu.
   *
   * @param player interacting player
   * @param item   interacting item
   */
  private static void returnToEditorMenu(Player player, ItemStack item) {
    player.removeMetadata("message", AethelPlugin.getInstance());
    Bukkit.getScheduler().runTask(AethelPlugin.getInstance(),
        () -> player.openInventory(ItemEditorMenu.openEditorMenu(player, item)));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "itemeditor.menu"));
  }
}
