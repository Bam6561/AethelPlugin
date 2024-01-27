package me.dannynguyen.aethel.listeners.inventory.itemeditor;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.inventories.itemeditor.utility.ItemEditorToggles;
import me.dannynguyen.aethel.listeners.inventory.itemeditor.utility.ItemEditorInventoryItemFlags;
import me.dannynguyen.aethel.listeners.inventory.itemeditor.utility.ItemEditorInventoryMenuAction;
import me.dannynguyen.aethel.listeners.message.itemeditor.ItemEditorMessageCosmetic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * ItemEditorInventory is an inventory listener for
 * the ItemEditor command pertaining to its main menu.
 *
 * @author Danny Nguyen
 * @version 1.7.0
 * @since 1.6.7
 */
public class ItemEditorInventoryMenu {
  /**
   * Edits an item's metadata field.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void interpretMenuClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 11 -> {
          player.sendMessage(ChatColor.GOLD + "[!] " + ChatColor.WHITE + "Input display name.");
          ItemEditorInventoryMenuAction.awaitMessageResponse(player, "display_name");
        }
        case 12 -> {
          player.sendMessage(ChatColor.GOLD + "[!] " + ChatColor.WHITE + "Input custom model data value.");
          ItemEditorInventoryMenuAction.awaitMessageResponse(player, "custom_model_data");
        }
        case 14 -> ItemEditorInventoryMenuAction.openAttributesMenu(player);
        case 15 -> ItemEditorInventoryMenuAction.openEnchantsMenu(player);
        case 16 -> ItemEditorInventoryMenuAction.openTagsMenu(player);
        case 28, 29, 30, 37, 38, 39 -> interpretLoreAction(e.getSlot(), player);
        case 32, 33, 34, 41, 42, 43, 50, 51 -> interpretItemFlagToggle(e.getSlot(), e.getClickedInventory(), player);
        case 52 -> toggleUnbreakable(e.getClickedInventory(), player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either sets, clears, adds, edits, or removes lore.
   *
   * @param slotClicked slot clicked
   * @param player      interacting player
   */
  private static void interpretLoreAction(int slotClicked, Player player) {
    switch (slotClicked) {
      case 28 -> { // Lore Context
      }
      case 29 -> {
        player.sendMessage(ChatColor.GOLD + "[!] " + ChatColor.WHITE + "Input lore to set.");
        ItemEditorInventoryMenuAction.awaitMessageResponse(player, "lore-set");
      }
      case 30 -> readItemLore(player, "lore-clear");
      case 37 -> {
        player.sendMessage(ChatColor.GOLD + "[!] " + ChatColor.WHITE + "Input lore to add.");
        ItemEditorInventoryMenuAction.awaitMessageResponse(player, "lore-add");
      }
      case 38 -> readItemLore(player, "lore-edit");
      case 39 -> readItemLore(player, "lore-remove");
    }
  }

  /**
   * Toggles item flags.
   *
   * @param slotClicked slot clicked
   * @param inv         interacting inventory
   * @param player      interacting player
   */
  private static void interpretItemFlagToggle(int slotClicked, Inventory inv, Player player) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(player);
    ItemMeta meta = item.getItemMeta();

    switch (slotClicked) {
      case 32 -> ItemEditorInventoryItemFlags.toggleHideArmorTrim(inv, player, item, meta);
      case 33 -> ItemEditorInventoryItemFlags.toggleHideAttributes(inv, player, item, meta);
      case 34 -> ItemEditorInventoryItemFlags.toggleHideDestroys(inv, player, item, meta);
      case 41 -> ItemEditorInventoryItemFlags.toggleHideDye(inv, player, item, meta);
      case 42 -> ItemEditorInventoryItemFlags.toggleHideEnchants(inv, player, item, meta);
      case 43 -> ItemEditorInventoryItemFlags.toggleHidePlacedOn(inv, player, item, meta);
      case 50 -> ItemEditorInventoryItemFlags.toggleHidePotionEffects(inv, player, item, meta);
      case 51 -> ItemEditorInventoryItemFlags.toggleHideUnbreakable(inv, player, item, meta);
    }
  }

  /**
   * Toggles an item's ability to be broken.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   */
  private static void toggleUnbreakable(Inventory inv, Player player) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(player);
    ItemMeta meta = item.getItemMeta();

    if (!meta.isUnbreakable()) {
      meta.setUnbreakable(true);
      player.sendMessage(ChatColor.GREEN + "[Set Unbreakable]");
    } else {
      meta.setUnbreakable(false);
      player.sendMessage(ChatColor.RED + "[Set Unbreakable]");
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addUnbreakableMeta(inv, meta);
  }

  /**
   * Checks if the item has lore before making changes.
   *
   * @param player interacting player
   * @param action interaction type
   */
  private static void readItemLore(Player player, String action) {
    ItemMeta meta = PluginData.itemEditorData.getEditedItemMap().get(player).getItemMeta();
    if (meta.hasLore()) {
      switch (action) {
        case "lore-clear" -> {
          ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(player);
          ItemEditorMessageCosmetic.clearLore(player, item, item.getItemMeta());
        }
        case "lore-edit" -> {
          player.sendMessage(ChatColor.GOLD + "[!] " + ChatColor.WHITE + "Input line number and lore to edit.");
          ItemEditorInventoryMenuAction.awaitMessageResponse(player, action);
        }
        case "lore-remove" -> {
          player.sendMessage(ChatColor.GOLD + "[!] " + ChatColor.WHITE + "Input line number to remove.");
          ItemEditorInventoryMenuAction.awaitMessageResponse(player, action);
        }
      }
    } else {
      player.sendMessage(ChatColor.RED + "Item has no lore.");
    }
  }
}
