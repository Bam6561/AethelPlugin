package me.dannynguyen.aethel.listeners.inventory.itemeditor.utility;

import me.dannynguyen.aethel.inventories.itemeditor.utility.ItemEditorToggle;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * ItemEditorInventoryItemFlags is a utility class that toggles an item's item flags.
 *
 * @author Danny Nguyen
 * @version 1.7.0
 * @since 1.7.0
 */
public class ItemEditorInventoryItemFlags {
  /**
   * Toggles an item's hide armor trim flag.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  public static void toggleHideArmorTrim(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
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
  public static void toggleHideAttributes(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
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
  public static void toggleHideDestroys(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
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
  public static void toggleHideDye(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
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
  public static void toggleHideEnchants(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
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
  public static void toggleHidePlacedOn(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
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
  public static void toggleHidePotionEffects(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
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
  public static void toggleHideUnbreakable(Inventory inv, Player player, ItemStack item, ItemMeta meta) {
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
