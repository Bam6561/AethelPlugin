package me.dannynguyen.aethel.listeners.inventory;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.inventories.itemeditor.utility.ItemEditorToggle;
import me.dannynguyen.aethel.listeners.message.ItemEditorMessageListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditorInventoryListener is an inventory listener for the ItemEditor command.
 *
 * @author Danny Nguyen
 * @version 1.6.10
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
        case 2 -> toggleUnbreakable(e.getClickedInventory(), player);
        case 9 -> awaitMessageResponse(player, "display_name");
        case 10 -> awaitMessageResponse(player, "custom_model_data");
        case 18, 19, 20, 27, 28, 29 -> interpretLoreAction(e.getSlot(), player);
        case 36, 37, 38, 39, 45, 46, 47, 48 -> interpretItemFlagToggle(e.getSlot(), e.getClickedInventory(), player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Toggles an item's ability to be broken.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   */
  private static void toggleUnbreakable(Inventory inv, Player player) {
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

    ItemEditorToggle.addUnbreakableMeta(inv, meta);
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
   * Toggles item flags.
   *
   * @param slotClicked slot clicked
   * @param inv         interacting inventory
   * @param player      interacting player
   */
  private static void interpretItemFlagToggle(int slotClicked, Inventory inv, Player player) {
    ItemStack item = AethelResources.itemEditorData.getEditedItemMap().get(player);
    ItemMeta meta = item.getItemMeta();

    switch (slotClicked) {
      case 36 -> toggleHideArmorTrim(inv, player, item, meta);
      case 37 -> toggleHideAttributes(inv, player, item, meta);
      case 38 -> toggleHideDestroys(inv, player, item, meta);
      case 39 -> toggleHideDye(inv, player, item, meta);
      case 45 -> toggleHideEnchants(inv, player, item, meta);
      case 46 -> toggleHidePlacedOn(inv, player, item, meta);
      case 47 -> toggleHidePotionEffects(inv, player, item, meta);
      case 48 -> toggleHideUnbreakable(inv, player, item, meta);
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
   * Toggles an item's hide armor trim flag.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  private static void toggleHideArmorTrim(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
      meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
      player.sendMessage(ChatColor.GREEN + "[Hide Armor Trim]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
      player.sendMessage(ChatColor.RED + "[Hide Armor Trim]");
    }
    item.setItemMeta(meta);

    ItemEditorToggle.addHideArmorTrimMeta(inv, meta);
  }

  /**
   * Toggles an item's hide attributes flag.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  private static void toggleHideAttributes(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      player.sendMessage(ChatColor.GREEN + "[Hide Attributes]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      player.sendMessage(ChatColor.RED + "[Hide Attributes]");
    }
    item.setItemMeta(meta);

    ItemEditorToggle.addHideAttributesMeta(inv, meta);
  }

  /**
   * Toggles an item's hide destroys flag.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  private static void toggleHideDestroys(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
      meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
      player.sendMessage(ChatColor.GREEN + "[Hide Destroys]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
      player.sendMessage(ChatColor.RED + "[Hide Destroys]");
    }
    item.setItemMeta(meta);

    ItemEditorToggle.addHideDestroysMeta(inv, meta);
  }

  /**
   * Toggles an item's hide dye flag.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  private static void toggleHideDye(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_DYE)) {
      meta.addItemFlags(ItemFlag.HIDE_DYE);
      player.sendMessage(ChatColor.GREEN + "[Hide Dye]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_DYE);
      player.sendMessage(ChatColor.RED + "[Hide Dye]");
    }
    item.setItemMeta(meta);

    ItemEditorToggle.addHideDyeMeta(inv, meta);
  }

  /**
   * Toggles an item's hide enchants flag.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  private static void toggleHideEnchants(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      player.sendMessage(ChatColor.GREEN + "[Hide Enchants]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
      player.sendMessage(ChatColor.RED + "[Hide Enchants]");
    }
    item.setItemMeta(meta);

    ItemEditorToggle.addHideEnchantsMeta(inv, meta);
  }

  /**
   * Toggles an item's hide placed on flag.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  private static void toggleHidePlacedOn(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON)) {
      meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
      player.sendMessage(ChatColor.GREEN + "[Hide Placed On]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_PLACED_ON);
      player.sendMessage(ChatColor.RED + "[Hide Placed On]");
    }
    item.setItemMeta(meta);

    ItemEditorToggle.addHidePlacedOnMeta(inv, meta);
  }

  /**
   * Toggles an item's hide potion effects flag.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  private static void toggleHidePotionEffects(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)) {
      meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      player.sendMessage(ChatColor.GREEN + "[Hide Potion Effects]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      player.sendMessage(ChatColor.RED + "[Hide Potion Effects]");
    }
    item.setItemMeta(meta);

    ItemEditorToggle.addHidePotionEffectsMeta(inv, meta);
  }

  /**
   * Toggles an item's hide unbreakable flag.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  private static void toggleHideUnbreakable(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
      meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
      player.sendMessage(ChatColor.GREEN + "[Hide Unbreakable]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
      player.sendMessage(ChatColor.RED + "[Hide Unbreakable]");
    }
    item.setItemMeta(meta);

    ItemEditorToggle.addHideUnbreakableMeta(inv, meta);
  }
}
