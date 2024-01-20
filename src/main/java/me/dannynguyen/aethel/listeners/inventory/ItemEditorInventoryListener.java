package me.dannynguyen.aethel.listeners.inventory;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.listeners.message.ItemEditorMessageListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditorInventoryListener is an inventory listener for the ItemEditor command.
 *
 * @author Danny Nguyen
 * @version 1.6.9
 * @since 1.6.7
 */
public class ItemEditorInventoryListener {
  /**
   * Edits an item's metadata field.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void interpretMenuClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 9 -> awaitMessageResponse(player, "display_name");
        case 10 -> awaitMessageResponse(player, "custom_model_data");
        case 18, 19, 20, 27, 28, 29 -> interpretLoreAction(e.getSlot(), player);
        case 34 -> toggleUnbreakable(player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Uses the user's next message as the field's input.
   *
   * @param player   interacting player
   * @param metadata metadata field
   */
  private static void awaitMessageResponse(Player player, String metadata) {
    player.closeInventory();
    player.setMetadata("message",
        new FixedMetadataValue(AethelPlugin.getInstance(), "itemeditor." + metadata));
  }

  /**
   * Either sets, clears, adds, edits, or removes lore.
   *
   * @param slotClicked slot clicked
   * @param player      interacting player
   */
  private static void interpretLoreAction(int slotClicked, Player player) {
    switch (slotClicked) {
      case 18 -> { // Lore Context
      }
      case 19 -> {
        player.sendMessage(ChatColor.GOLD + "[!] " + ChatColor.WHITE + "Input lore to set.");
        awaitMessageResponse(player, "lore-set");
      }
      case 20 -> readItemLore(player, "lore-clear");
      case 27 -> {
        player.sendMessage(ChatColor.GOLD + "[!] " + ChatColor.WHITE + "Input lore to add.");
        awaitMessageResponse(player, "lore-add");
      }
      case 28 -> readItemLore(player, "lore-edit");
      case 29 -> readItemLore(player, "lore-remove");
    }
  }

  /**
   * Checks if the item has lore before making changes.
   *
   * @param player interacting player
   * @param action interaction type
   */
  private static void readItemLore(Player player, String action) {
    ItemMeta meta = AethelResources.itemEditorData.getEditedItemMap().get(player).getItemMeta();
    if (meta.hasLore()) {
      switch (action) {
        case "lore-clear" -> {
          ItemStack item = AethelResources.itemEditorData.getEditedItemMap().get(player);
          ItemEditorMessageListener.clearLore(player, item, item.getItemMeta());
        }
        case "lore-edit" -> {
          player.sendMessage(ChatColor.GOLD + "[!] " + ChatColor.WHITE + "Input line number and lore to edit.");
          awaitMessageResponse(player, action);
        }
        case "lore-remove" -> {
          player.sendMessage(ChatColor.GOLD + "[!] " + ChatColor.WHITE + "Input line number to remove.");
          awaitMessageResponse(player, action);
        }
      }
    } else {
      player.sendMessage(ChatColor.RED + "Item has no lore.");
    }
  }

  /**
   * Toggles an item's ability to be broken.
   *
   * @param player interacting player
   */
  private static void toggleUnbreakable(Player player) {
    ItemStack item = AethelResources.itemEditorData.getEditedItemMap().get(player);
    ItemMeta meta = item.getItemMeta();

    if (!meta.isUnbreakable()) {
      meta.setUnbreakable(true);
      player.sendMessage(ChatColor.GREEN + "[Unbreakable]");
    } else {
      meta.setUnbreakable(false);
      player.sendMessage(ChatColor.RED + "[Unbreakable]");
    }
    item.setItemMeta(meta);
  }
}
