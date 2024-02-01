package me.dannynguyen.aethel.commands.itemeditor.utility;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * ItemEditorItemFlags is a utility class that toggles an item's item flags.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.7.0
 */
public class ItemEditorItemFlags {
  /**
   * Toggles an item's hide armor trim flag.
   *
   * @param inv  interacting inventory
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void toggleHideArmorTrim(Inventory inv, Player user, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
      meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
      user.sendMessage(Success.ENABLE_HIDE_ARMOR_TRIM.message);
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
      user.sendMessage(Success.DISABLE_HIDE_ARMOR_TRIM.message);
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addHideArmorTrimMeta(inv, meta);
  }

  /**
   * Toggles an item's hide attributes flag.
   *
   * @param inv  interacting inventory
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void toggleHideAttributes(Inventory inv, Player user, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      user.sendMessage(Success.ENABLE_HIDE_ATTRIBUTES.message);
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      user.sendMessage(Success.DISABLE_HIDE_ATTRIBUTES.message);
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addHideAttributesMeta(inv, meta);
  }

  /**
   * Toggles an item's hide destroys flag.
   *
   * @param inv  interacting inventory
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void toggleHideDestroys(Inventory inv, Player user, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
      meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
      user.sendMessage(Success.ENABLE_HIDE_DESTROYS.message);
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
      user.sendMessage(Success.DISABLE_HIDE_DESTROYS.message);
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addHideDestroysMeta(inv, meta);
  }

  /**
   * Toggles an item's hide dye flag.
   *
   * @param inv  interacting inventory
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void toggleHideDye(Inventory inv, Player user, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_DYE)) {
      meta.addItemFlags(ItemFlag.HIDE_DYE);
      user.sendMessage(Success.ENABLE_HIDE_DYE.message);
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_DYE);
      user.sendMessage(Success.DISABLE_HIDE_DYE.message);
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addHideDyeMeta(inv, meta);
  }

  /**
   * Toggles an item's hide enchants flag.
   *
   * @param inv  interacting inventory
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void toggleHideEnchants(Inventory inv, Player user, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      user.sendMessage(Success.ENABLE_HIDE_ENCHANTS.message);
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
      user.sendMessage(Success.DISABLE_HIDE_ENCHANTS.message);
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addHideEnchantsMeta(inv, meta);
  }

  /**
   * Toggles an item's hide placed on flag.
   *
   * @param inv  interacting inventory
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void toggleHidePlacedOn(Inventory inv, Player user, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON)) {
      meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
      user.sendMessage(Success.ENABLE_HIDE_PLACED_ON.message);
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_PLACED_ON);
      user.sendMessage(Success.DISABLE_HIDE_PLACED_ON.message);
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addHidePlacedOnMeta(inv, meta);
  }

  /**
   * Toggles an item's hide potion effects flag.
   *
   * @param inv  interacting inventory
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void toggleHidePotionEffects(Inventory inv, Player user, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)) {
      meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      user.sendMessage(Success.ENABLE_HIDE_POTION_EFFECTS.message);
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      user.sendMessage(Success.DISABLE_HIDE_POTION_EFFECTS.message);
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addHidePotionEffectsMeta(inv, meta);
  }

  /**
   * Toggles an item's hide unbreakable flag.
   *
   * @param inv  interacting inventory
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void toggleHideUnbreakable(Inventory inv, Player user, ItemStack item, ItemMeta meta) {
    if (!meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
      meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
      user.sendMessage(Success.ENABLE_HIDE_UNBREAKABLE.message);
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
      user.sendMessage(Success.DISABLE_HIDE_UNBREAKABLE.message);
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addHideUnbreakableMeta(inv, meta);
  }

  private enum Success {
    ENABLE_HIDE_ARMOR_TRIM(ChatColor.GREEN + "[Hide Armor Trim]"),
    ENABLE_HIDE_ATTRIBUTES(ChatColor.GREEN + "[Hide Attributes]"),
    ENABLE_HIDE_DESTROYS(ChatColor.GREEN + "[Hide Destroys]"),
    ENABLE_HIDE_DYE(ChatColor.GREEN + "[Hide Dye]"),
    ENABLE_HIDE_ENCHANTS(ChatColor.GREEN + "[Hide Enchants]"),
    ENABLE_HIDE_PLACED_ON(ChatColor.GREEN + "[Hide Placed On]"),
    ENABLE_HIDE_POTION_EFFECTS(ChatColor.GREEN + "[Hide Potion Effects]"),
    ENABLE_HIDE_UNBREAKABLE(ChatColor.GREEN + "[Hide Unbreakable]"),
    DISABLE_HIDE_ARMOR_TRIM(ChatColor.RED + "[Hide Armor Trim]"),
    DISABLE_HIDE_ATTRIBUTES(ChatColor.RED + "[Hide Attributes]"),
    DISABLE_HIDE_DESTROYS(ChatColor.RED + "[Hide Destroys]"),
    DISABLE_HIDE_DYE(ChatColor.RED + "[Hide Dye]"),
    DISABLE_HIDE_ENCHANTS(ChatColor.RED + "[Hide Enchants]"),
    DISABLE_HIDE_PLACED_ON(ChatColor.RED + "[Hide Placed On]"),
    DISABLE_HIDE_POTION_EFFECTS(ChatColor.RED + "[Hide Potion Effects]"),
    DISABLE_HIDE_UNBREAKABLE(ChatColor.RED + "[Hide Unbreakable]");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }
}
