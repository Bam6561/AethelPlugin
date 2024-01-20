package me.dannynguyen.aethel.listeners.message;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.ItemEditorMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

/**
 * ItemEditorMessageListener is a message listener for the ItemEditor command.
 *
 * @author Danny Nguyen
 * @version 1.6.8
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
   * Checks if the lore edit request was formatted correctly before interpreting its usage.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  public static void readLoreRequest(AsyncPlayerChatEvent e, Player player,
                                     ItemStack item, ItemMeta meta) {
    String[] input = e.getMessage().toLowerCase().split(" ");
    if (input.length >= 2) {
      interpretLoreAction(e, player, item, meta, input);
    } else if (input[0].equals("-clear") || input[0].equals("-c")) {
      clearLoreLines(player, item, meta);
    } else {
      player.sendMessage(ChatColor.RED + "Missing input content.");
    }
    returnToEditorMenu(player, item);
  }

  /**
   * Sets the lore.
   * <p>
   * Input Splits:
   * - Add: action | input
   * - Edit: action | line | input
   * - Remove: action | line
   * - Set: action | input
   * </p>
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   * @param input  user input
   */
  private static void interpretLoreAction(AsyncPlayerChatEvent e, Player player,
                                          ItemStack item, ItemMeta meta, String[] input) {
    switch (input[0]) {
      case "-add", "-a" -> {
        input = e.getMessage().split(" ", 2);
        addLoreLine(player, item, meta, input[1]);
      }

      case "-edit", "-e" -> {
        input = e.getMessage().split(" ", 3);
        editLoreLine(player, item, meta, input);
      }

      case "-remove", "-r" -> {
        input = e.getMessage().split(" ", 2);
        removeLoreLine(player, item, meta, input);
      }

      case "-set", "-s" -> {
        input = e.getMessage().split(" ", 2);
        setLoreLines(player, item, meta, input[1]);
      }

      default -> player.sendMessage(ChatColor.RED + "Unrecognized action flag.");
    }
  }

  /**
   * Adds a line of lore.
   *
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   * @param input  user input
   */
  private static void addLoreLine(Player player, ItemStack item,
                                  ItemMeta meta, String input) {
    if (meta.hasLore()) {
      List<String> lore = meta.getLore();
      lore.add(input);
      meta.setLore(lore);
    } else {
      meta.setLore(List.of(input));
    }
    item.setItemMeta(meta);
    player.sendMessage(ChatColor.GREEN + "[Lore Added] " + ChatColor.WHITE + input);
  }

  /**
   * Edits a line of lore.
   *
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   * @param input  user input
   * @throws NumberFormatException     not a number
   * @throws IndexOutOfBoundsException invalid index
   */
  private static void editLoreLine(Player player, ItemStack item,
                                   ItemMeta meta, String[] input) {
    try {
      if (meta.hasLore()) {
        List<String> lore = meta.getLore();
        lore.set(Integer.parseInt(input[1]) - 1, input[2]);
        meta.setLore(lore);
        item.setItemMeta(meta);
        player.sendMessage(ChatColor.GREEN + "[Lore Edited] " + ChatColor.WHITE + input[1]);
      } else {
        player.sendMessage(ChatColor.RED + "No lore to edit.");
      }
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "Invalid line number.");
    } catch (IndexOutOfBoundsException ex) {
      player.sendMessage(ChatColor.RED + "Line does not exist.");
    }
  }

  /**
   * Removes a line of lore.
   *
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   * @param input  user input
   * @throws NumberFormatException     not a number
   * @throws IndexOutOfBoundsException invalid index
   */
  private static void removeLoreLine(Player player, ItemStack item,
                                     ItemMeta meta, String[] input) {
    try {
      if (meta.hasLore()) {
        List<String> lore = meta.getLore();
        lore.remove(Integer.parseInt(input[1]) - 1);
        meta.setLore(lore);
        item.setItemMeta(meta);
        player.sendMessage(ChatColor.GREEN + "[Lore Removed]");
      } else {
        player.sendMessage(ChatColor.RED + "No lore to remove.");
      }
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "Invalid line number.");
    } catch (IndexOutOfBoundsException ex) {
      player.sendMessage(ChatColor.RED + "Line does not exist.");
    }
  }

  /**
   * Sets the lore.
   *
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   * @param input  user input
   */
  private static void setLoreLines(Player player, ItemStack item,
                                   ItemMeta meta, String input) {
    meta.setLore(List.of(input.split(",,")));
    item.setItemMeta(meta);
    player.sendMessage(ChatColor.GREEN + "[Lore Set]");
  }

  /**
   * Clears the lore.
   *
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  private static void clearLoreLines(Player player, ItemStack item, ItemMeta meta) {
    if (meta.hasLore()) {
      List<String> lore = meta.getLore();
      lore.clear();
      meta.setLore(lore);
      item.setItemMeta(meta);
      player.sendMessage(ChatColor.GREEN + "[Lore Cleared]");
    } else {
      player.sendMessage(ChatColor.RED + "No lore to clear.");
    }
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
