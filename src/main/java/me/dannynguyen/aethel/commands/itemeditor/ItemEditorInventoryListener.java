package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginConstant;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryListener;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditorInventory is an inventory listener for the ItemEditor inventories.
 *
 * @author Danny Nguyen
 * @version 1.8.7
 * @since 1.6.7
 */
public class ItemEditorInventoryListener {
  /**
   * Edits an item's cosmetic metadata fields or opens
   * a gameplay-related metadata field editing inventory.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretMainMenuClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 11 -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              Success.INPUT_DISPLAY_NAME.message);
          ItemEditorAction.awaitMessageResponse(user, "display_name");
        }
        case 12 -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              Success.INPUT_CUSTOMMODELDATA.message);
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
   * Either changes the equipment slot mode or sets an item's attribute.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretAttributesMenuClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0, 1 -> { // Context, Item
        }
        case 2 -> ItemEditorAction.returnToMainMenu(user);
        case 3 -> setMode(user, "head");
        case 4 -> setMode(user, "chest");
        case 5 -> setMode(user, "legs");
        case 6 -> setMode(user, "feet");
        case 7 -> setMode(user, "hand");
        case 8 -> setMode(user, "off_hand");
        default -> readAttribute(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Sets an item's enchant.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretEnchantsMenuClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 2, 4 -> { // Context, Item
        }
        case 6 -> ItemEditorAction.returnToMainMenu(user);
        default -> readEnchant(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Edits an item's Aethel tag.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretTagsMenuClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 2, 4 -> { // Context, Item
        }
        case 6 -> ItemEditorAction.returnToMainMenu(user);
        default -> readTag(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either sets, clears, add, edits, or removes lore.
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
            Success.INPUT_SET_LORE.message);
        ItemEditorAction.awaitMessageResponse(user, "lore-set");
      }
      case 30 -> readItemLore(user, "lore-clear");
      case 37 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
            Success.INPUT_ADD_LORE.message);
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
      user.sendMessage(Success.ENABLE_UNBREAKABLE.message);
    } else {
      meta.setUnbreakable(false);
      user.sendMessage(Success.DISABLE_UNBREAKABLE.message);
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
          ItemEditorMessageListener.clearLore(user, item, item.getItemMeta());
        }
        case "lore-edit" -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              Success.INPUT_EDIT_LORE.message);
          ItemEditorAction.awaitMessageResponse(user, action);
        }
        case "lore-remove" -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              Success.INPUT_REMOVE_LORE.message);
          ItemEditorAction.awaitMessageResponse(user, action);
        }
      }
    } else {
      user.sendMessage(Failure.NO_ITEM_LORE.message);
    }
  }

  /**
   * Sets the user's interacting equipment slot.
   *
   * @param user          user
   * @param equipmentSlot interacting equipment slot.
   */
  private static void setMode(Player user, String equipmentSlot) {
    user.setMetadata(PluginPlayerMeta.Namespace.SLOT.namespace,
        new FixedMetadataValue(Plugin.getInstance(), equipmentSlot));

    user.openInventory(ItemEditorAttributes.openAttributesMenu(user, equipmentSlot));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(),
            InventoryListener.Inventory.ITEMEDITOR_ATTRIBUTES.inventory));
  }

  /**
   * Determines the attribute to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private static void readAttribute(InventoryClickEvent e, Player user) {
    String attributeName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    String attribute;
    if (PluginConstant.minecraftAttributes.contains(attributeName)) {
      attribute = "GENERIC_" + attributeName.replace(" ", "_").toUpperCase();
    } else {
      attribute = "aethel.attribute." + attributeName.replace(" ", "_").toLowerCase();
    }

    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
        ChatColor.WHITE + "Input " + ChatColor.AQUA + attributeName + ChatColor.WHITE + " value.");

    user.setMetadata(PluginPlayerMeta.Namespace.TYPE.namespace,
        new FixedMetadataValue(Plugin.getInstance(), attribute));
    ItemEditorAction.awaitMessageResponse(user, "attributes");
  }

  /**
   * Determines the enchantment to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private static void readEnchant(InventoryClickEvent e, Player user) {
    String enchant = ChatColor.stripColor(e.getCurrentItem().
        getItemMeta().getDisplayName().replace(" ", "_").toLowerCase());

    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message
        + ChatColor.WHITE + "Input " + ChatColor.AQUA
        + TextFormatter.capitalizePhrase(enchant) + ChatColor.WHITE + " value.");

    user.setMetadata(PluginPlayerMeta.Namespace.TYPE.namespace, new FixedMetadataValue(Plugin.getInstance(), enchant));
    ItemEditorAction.awaitMessageResponse(user, "enchants");
  }

  /**
   * Determines the Aethel tag to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private static void readTag(InventoryClickEvent e, Player user) {
    String aethelTag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
        ChatColor.WHITE + "Input " + ChatColor.AQUA + aethelTag + ChatColor.WHITE + " value.");

    user.setMetadata(PluginPlayerMeta.Namespace.TYPE.namespace, new FixedMetadataValue(Plugin.getInstance(), aethelTag));
    ItemEditorAction.awaitMessageResponse(user, "tags");
  }

  private enum Success {
    ENABLE_UNBREAKABLE(ChatColor.GREEN + "[Set Unbreakable]"),
    DISABLE_UNBREAKABLE(ChatColor.RED + "[Set Unbreakable]"),
    INPUT_DISPLAY_NAME(ChatColor.WHITE + "Input display name."),
    INPUT_CUSTOMMODELDATA(ChatColor.WHITE + "Input custom model data value."),
    INPUT_SET_LORE(ChatColor.WHITE + "Input lore to set."),
    INPUT_ADD_LORE(ChatColor.WHITE + "Input lore to add."),
    INPUT_EDIT_LORE(ChatColor.WHITE + "Input line number and lore to edit."),
    INPUT_REMOVE_LORE(ChatColor.WHITE + "Input line number to remove.");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }

  private enum Failure {
    NO_ITEM_LORE(ChatColor.RED + "Item has no lore.");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }
}
