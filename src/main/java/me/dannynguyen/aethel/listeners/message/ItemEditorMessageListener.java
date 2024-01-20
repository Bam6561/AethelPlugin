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

/**
 * ItemEditorMessageListener is a message listener for the ItemEditor command.
 *
 * @author Danny Nguyen
 * @version 1.6.7
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
  public static void setDisplayName(AsyncPlayerChatEvent e, Player player, ItemStack item, ItemMeta meta) {
    meta.setDisplayName(e.getMessage());
    item.setItemMeta(meta);
    player.sendMessage(ChatColor.GREEN + "[Named] " + ChatColor.WHITE + e.getMessage());

    Bukkit.getScheduler().runTask(AethelPlugin.getInstance(),
        () -> player.openInventory(ItemEditorMenu.openEditorMenu(player, item)));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "itemeditor.menu"));
  }

  /**
   * Sets the item's custom model data.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  public static void setCustomModelData(AsyncPlayerChatEvent e, Player player, ItemStack item, ItemMeta meta) {
    try {
      meta.setCustomModelData(Integer.parseInt(e.getMessage()));
      item.setItemMeta(meta);
      player.sendMessage(ChatColor.GREEN + "[Set Custom Model Data] " + ChatColor.WHITE + e.getMessage());
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "Invalid custom model data.");
    }

    Bukkit.getScheduler().runTask(AethelPlugin.getInstance(),
        () -> player.openInventory(ItemEditorMenu.openEditorMenu(player, item)));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "itemeditor.menu"));
  }
}
