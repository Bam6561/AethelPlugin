package me.dannynguyen.aethel.commands.itemeditor.listener;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.itemeditor.utility.ItemEditorAction;
import me.dannynguyen.aethel.commands.itemeditor.utility.ItemEditorItemFlags;
import me.dannynguyen.aethel.commands.itemeditor.utility.ItemEditorToggles;
import me.dannynguyen.aethel.enums.PluginMessage;
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
 * @version 1.8.0
 * @since 1.6.7
 */
public class ItemEditorInventoryListener {
  /**
   * Edits an item's metadata field.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretMenuClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 11 -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              PluginMessage.Success.ITEMEDITOR_INPUT_DISPLAY_NAME.message);
          ItemEditorAction.awaitMessageResponse(user, "display_name");
        }
        case 12 -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              PluginMessage.Success.ITEMEDITOR_INPUT_CUSTOMMODELDATA.message);
          ItemEditorAction.awaitMessageResponse(user, "custom_model_data");
        }
        case 14 -> ItemEditorAction.openAttributesMenu(user);
        case 15 -> ItemEditorAction.openEnchantsMenu(user);
        case 16 -> ItemEditorAction.openTagsMenu(user);
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
   * @param user        user
   */
  private static void interpretLoreAction(int slotClicked, Player user) {
    switch (slotClicked) {
      case 28 -> { // Lore Context
      }
      case 29 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
            PluginMessage.Success.ITEMEDITOR_INPUT_SET_LORE.message);
        ItemEditorAction.awaitMessageResponse(user, "lore-set");
      }
      case 30 -> readItemLore(user, "lore-clear");
      case 37 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
            PluginMessage.Success.ITEMEDITOR_INPUT_ADD_LORE.message);
        ItemEditorAction.awaitMessageResponse(user, "lore-add");
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
   * @param user        user
   */
  private static void interpretItemFlagToggle(int slotClicked, Inventory inv, Player user) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();

    switch (slotClicked) {
      case 32 -> ItemEditorItemFlags.toggleHideArmorTrim(inv, user, item, meta);
      case 33 -> ItemEditorItemFlags.toggleHideAttributes(inv, user, item, meta);
      case 34 -> ItemEditorItemFlags.toggleHideDestroys(inv, user, item, meta);
      case 41 -> ItemEditorItemFlags.toggleHideDye(inv, user, item, meta);
      case 42 -> ItemEditorItemFlags.toggleHideEnchants(inv, user, item, meta);
      case 43 -> ItemEditorItemFlags.toggleHidePlacedOn(inv, user, item, meta);
      case 50 -> ItemEditorItemFlags.toggleHidePotionEffects(inv, user, item, meta);
      case 51 -> ItemEditorItemFlags.toggleHideUnbreakable(inv, user, item, meta);
    }
  }

  /**
   * Toggles an item's ability to be broken.
   *
   * @param inv  interacting inventory
   * @param user user
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
   * @param user   user
   * @param action interaction type
   */
  private static void readItemLore(Player user, String action) {
    ItemMeta meta = PluginData.itemEditorData.getEditedItemMap().get(user).getItemMeta();
    if (meta.hasLore()) {
      switch (action) {
        case "lore-clear" -> {
          ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
          ItemEditorMessageListenerCosmetic.clearLore(user, item, item.getItemMeta());
        }
        case "lore-edit" -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              PluginMessage.Success.ITEMEDITOR_INPUT_EDIT_LORE.message);
          ItemEditorAction.awaitMessageResponse(user, action);
        }
        case "lore-remove" -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              PluginMessage.Success.ITEMEDITOR_INPUT_REMOVE_LORE.message);
          ItemEditorAction.awaitMessageResponse(user, action);
        }
      }
    } else {
      user.sendMessage(PluginMessage.Failure.ITEMEDITOR_NO_LORE.message);
    }
  }
}
