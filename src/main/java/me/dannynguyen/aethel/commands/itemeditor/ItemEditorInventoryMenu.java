package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginMessage;
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
   * @param user interacting user
   */
  public static void interpretMenuClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 11 -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input display name.");
          ItemEditorInventoryMenuAction.awaitMessageResponse(user, "display_name");
        }
        case 12 -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input custom model data value.");
          ItemEditorInventoryMenuAction.awaitMessageResponse(user, "custom_model_data");
        }
        case 14 -> ItemEditorInventoryMenuAction.openAttributesMenu(user);
        case 15 -> ItemEditorInventoryMenuAction.openEnchantsMenu(user);
        case 16 -> ItemEditorInventoryMenuAction.openTagsMenu(user);
        case 28, 29, 30, 37, 38, 39 -> interpretLoreAction(e.getSlot(), user);
        case 32, 33, 34, 41, 42, 43, 50, 51 -> interpretItemFlagToggle(e.getSlot(), e.getClickedInventory(), user);
        case 52 -> toggleUnbreakable(e.getClickedInventory(), user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either sets, clears, adds, edits, or removes lore.
   *
   * @param slotClicked slot clicked
   * @param user      interacting user
   */
  private static void interpretLoreAction(int slotClicked, Player user) {
    switch (slotClicked) {
      case 28 -> { // Lore Context
      }
      case 29 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input lore to set.");
        ItemEditorInventoryMenuAction.awaitMessageResponse(user, "lore-set");
      }
      case 30 -> readItemLore(user, "lore-clear");
      case 37 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input lore to add.");
        ItemEditorInventoryMenuAction.awaitMessageResponse(user, "lore-add");
      }
      case 38 -> readItemLore(user, "lore-edit");
      case 39 -> readItemLore(user, "lore-remove");
    }
  }

  /**
   * Toggles item flags.
   *
   * @param slotClicked slot clicked
   * @param inv         interacting inventory
   * @param user      interacting user
   */
  private static void interpretItemFlagToggle(int slotClicked, Inventory inv, Player user) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();

    switch (slotClicked) {
      case 32 -> ItemEditorInventoryItemFlags.toggleHideArmorTrim(inv, user, item, meta);
      case 33 -> ItemEditorInventoryItemFlags.toggleHideAttributes(inv, user, item, meta);
      case 34 -> ItemEditorInventoryItemFlags.toggleHideDestroys(inv, user, item, meta);
      case 41 -> ItemEditorInventoryItemFlags.toggleHideDye(inv, user, item, meta);
      case 42 -> ItemEditorInventoryItemFlags.toggleHideEnchants(inv, user, item, meta);
      case 43 -> ItemEditorInventoryItemFlags.toggleHidePlacedOn(inv, user, item, meta);
      case 50 -> ItemEditorInventoryItemFlags.toggleHidePotionEffects(inv, user, item, meta);
      case 51 -> ItemEditorInventoryItemFlags.toggleHideUnbreakable(inv, user, item, meta);
    }
  }

  /**
   * Toggles an item's ability to be broken.
   *
   * @param inv    interacting inventory
   * @param user interacting user
   */
  private static void toggleUnbreakable(Inventory inv, Player user) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();

    if (!meta.isUnbreakable()) {
      meta.setUnbreakable(true);
      user.sendMessage(PluginMessage.Success.ITEMEDITOR_ENABLE_UNBREAKABLE.message);
    } else {
      meta.setUnbreakable(false);
      user.sendMessage(PluginMessage.Success.ITEMEDITOR_DISABLE_UNBREAKABLE.message);
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addUnbreakableMeta(inv, meta);
  }

  /**
   * Checks if the item has lore before making changes.
   *
   * @param user interacting user
   * @param action interaction type
   */
  private static void readItemLore(Player user, String action) {
    ItemMeta meta = PluginData.itemEditorData.getEditedItemMap().get(user).getItemMeta();
    if (meta.hasLore()) {
      switch (action) {
        case "lore-clear" -> {
          ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
          ItemEditorMessageCosmetic.clearLore(user, item, item.getItemMeta());
        }
        case "lore-edit" -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input line number and lore to edit.");
          ItemEditorInventoryMenuAction.awaitMessageResponse(user, action);
        }
        case "lore-remove" -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input line number to remove.");
          ItemEditorInventoryMenuAction.awaitMessageResponse(user, action);
        }
      }
    } else {
      user.sendMessage(PluginMessage.Failure.ITEMEDITOR_NO_LORE.message);
    }
  }
}
