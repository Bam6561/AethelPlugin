package me.dannynguyen.aethel.commands.itemeditor.utility;

import me.dannynguyen.aethel.utility.ItemCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * ItemEditorToggles is a utility class to update the status of toggleable metadata buttons.
 *
 * @author Danny Nguyen
 * @version 1.6.13
 * @since 1.6.10
 */
public class ItemEditorToggles {
  /**
   * Adds the unbreakable toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addUnbreakableMeta(Inventory inv, ItemMeta meta) {
    boolean disabled = !meta.isUnbreakable();
    String unbreakable = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(52, ItemCreator.createItem(disabled ? Material.CLAY : Material.BEDROCK,
        ChatColor.AQUA + "Unbreakable", List.of(unbreakable)));
  }

  /**
   * Adds item flag toggle buttons.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addItemFlagsMeta(Inventory inv, ItemMeta meta) {
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
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM);
    String armorTrim = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(32, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE,
        ChatColor.AQUA + "Hide Armor Trims", List.of(armorTrim)));
  }

  /**
   * Adds hide attributes toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideAttributesMeta(Inventory inv, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
    String attributes = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(33, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE,
        ChatColor.AQUA + "Hide Attributes", List.of(attributes)));
  }

  /**
   * Adds hide destroys toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideDestroysMeta(Inventory inv, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_DESTROYS);
    String destroys = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(34, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE,
        ChatColor.AQUA + "Hide Destroys", List.of(destroys)));
  }

  /**
   * Adds hide dye toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideDyeMeta(Inventory inv, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_DYE);
    String dye = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(41, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE,
        ChatColor.AQUA + "Hide Dyes", List.of(dye)));
  }

  /**
   * Adds hide enchants toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideEnchantsMeta(Inventory inv, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    String enchants = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(42, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE,
        ChatColor.AQUA + "Hide Enchants", List.of(enchants)));
  }

  /**
   * Adds hide placed on toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHidePlacedOnMeta(Inventory inv, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON);
    String placedOn = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(43, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE,
        ChatColor.AQUA + "Hide Placed On", List.of(placedOn)));
  }

  /**
   * Adds hide potion effects toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHidePotionEffectsMeta(Inventory inv, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
    String potionEffects = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(50, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE,
        ChatColor.AQUA + "Hide Potion Effects", List.of(potionEffects)));
  }

  /**
   * Adds hide unbreakable toggle button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  public static void addHideUnbreakableMeta(Inventory inv, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);
    String unbreakable = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    inv.setItem(51, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE,
        ChatColor.AQUA + "Hide Unbreakable", List.of(unbreakable)));
  }
}
