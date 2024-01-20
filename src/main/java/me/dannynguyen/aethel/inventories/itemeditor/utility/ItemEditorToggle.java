package me.dannynguyen.aethel.inventories.itemeditor.utility;

import me.dannynguyen.aethel.creators.ItemCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * ItemEditorToggle is a utility class to update the status of toggleable metadata buttons.
 *
 * @author Danny Nguyen
 * @version 1.6.10
 * @since 1.6.10
 */
public class ItemEditorToggle {
  /**
   * Adds the unbreakable toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addUnbreakableMeta(Inventory inv, ItemMeta meta) {
    String isUnbreakable = !meta.isUnbreakable() ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(2, ItemCreator.createItem(Material.BEDROCK,
        ChatColor.AQUA + "Unbreakable", List.of(isUnbreakable)));
  }

  /**
   * Adds item flag toggle buttons.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addItemFlagMetas(Inventory inv, ItemMeta meta) {
    addHideArmorTrimMeta(inv, meta);
    addHideAttributesMeta(inv, meta);
    addHideDestroysMeta(inv, meta);
    addHideDyeMeta(inv, meta);
    addHideEnchantsMeta(inv, meta);
    addHidePlacedOnMeta(inv, meta);
    addHidePotionEffectsMeta(inv, meta);
    addHideUnbreakableMeta(inv, meta);
  }

  /**
   * Adds hide armor trim toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideArmorTrimMeta(Inventory inv, ItemMeta meta) {
    String armorTrim = !meta.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM) ?
        ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(36, ItemCreator.createItem(Material.BARRIER,
        ChatColor.AQUA + "Hide Armor Trims", List.of(armorTrim)));
  }

  /**
   * Adds hide attributes toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideAttributesMeta(Inventory inv, ItemMeta meta) {
    String attributes = !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) ?
        ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(37, ItemCreator.createItem(Material.BARRIER,
        ChatColor.AQUA + "Hide Attributes", List.of(attributes)));
  }

  /**
   * Adds hide destroys toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideDestroysMeta(Inventory inv, ItemMeta meta) {
    String destroys = !meta.hasItemFlag(ItemFlag.HIDE_DESTROYS) ?
        ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(38, ItemCreator.createItem(Material.BARRIER,
        ChatColor.AQUA + "Hide Destroys", List.of(destroys)));
  }

  /**
   * Adds hide dye toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideDyeMeta(Inventory inv, ItemMeta meta) {
    String dye = !meta.hasItemFlag(ItemFlag.HIDE_DYE) ?
        ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(39, ItemCreator.createItem(Material.BARRIER,
        ChatColor.AQUA + "Hide Dyes", List.of(dye)));
  }

  /**
   * Adds hide enchants toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideEnchantsMeta(Inventory inv, ItemMeta meta) {
    String enchants = !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) ?
        ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(45, ItemCreator.createItem(Material.BARRIER,
        ChatColor.AQUA + "Hide Enchants", List.of(enchants)));
  }

  /**
   * Adds hide placed on toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHidePlacedOnMeta(Inventory inv, ItemMeta meta) {
    String placedOn = !meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON) ?
        ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(46, ItemCreator.createItem(Material.BARRIER,
        ChatColor.AQUA + "Hide Placed On", List.of(placedOn)));
  }

  /**
   * Adds hide potion effects toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHidePotionEffectsMeta(Inventory inv, ItemMeta meta) {
    String potionEffects = !meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS) ?
        ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(47, ItemCreator.createItem(Material.BARRIER,
        ChatColor.AQUA + "Hide Potion Effects", List.of(potionEffects)));
  }

  /**
   * Adds hide unbreakable toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideUnbreakableMeta(Inventory inv, ItemMeta meta) {
    String unbreakable = !meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE) ?
        ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(48, ItemCreator.createItem(Material.BARRIER,
        ChatColor.AQUA + "Hide Unbreakable", List.of(unbreakable)));
  }
}
