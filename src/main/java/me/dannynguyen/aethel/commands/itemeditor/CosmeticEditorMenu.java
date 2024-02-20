package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.PluginPlayerHead;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents a menu that allows the user to edit an item's cosmetic metadata.
 * <p>
 * From this menu, the user can also navigate to gameplay metadata menus.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.9.21
 * @since 1.6.7
 */
class CosmeticEditorMenu {
  /**
   * CosmeticEditor GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * ItemStack being edited.
   */
  private final ItemStack item;

  /**
   * ItemStack meta.
   */
  private final ItemMeta meta;

  /**
   * Associates a new CosmeticEditor menu with its user and item.
   *
   * @param user user
   */
  protected CosmeticEditorMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = PluginData.editedItemCache.getEditedItemMap().get(user);
    this.meta = Objects.requireNonNull(item.getItemMeta(), "Null meta");
    this.menu = createMenu();
  }

  /**
   * Creates and names an CosmeticEditor menu with its item being edited.
   *
   * @return CosmeticEditor menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with cosmetic metadata.
   *
   * @return CosmeticEditor menu
   */
  @NotNull
  protected Inventory openMenu() {
    addContext();
    addDisplayName();
    addCustomModelData();
    addLore();
    addGameplay();
    addItemFlags();
    addUnbreakable(menu, meta);
    return menu;
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    ItemStack formatCodes = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.getHead(),
        ChatColor.GREEN + "Format Codes", List.of(
            ChatColor.WHITE + "&k " + ChatColor.MAGIC + "Magic",
            ChatColor.WHITE + "&l " + ChatColor.BOLD + "Bold",
            ChatColor.WHITE + "&m " + ChatColor.STRIKETHROUGH + "Strike",
            ChatColor.WHITE + "&n " + ChatColor.UNDERLINE + "Underline",
            ChatColor.WHITE + "&o " + ChatColor.ITALIC + "Italic",
            ChatColor.WHITE + "&r " + ChatColor.RESET + "Reset"));
    ItemStack colorCodes = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.getHead(),
        ChatColor.GREEN + "Color Codes", List.of(
            ChatColor.WHITE + "&0 " + ChatColor.BLACK + "Black",
            ChatColor.WHITE + "&1 " + ChatColor.DARK_BLUE + "Dark Blue",
            ChatColor.WHITE + "&2 " + ChatColor.DARK_GREEN + "Dark Green",
            ChatColor.WHITE + "&3 " + ChatColor.DARK_RED + "Dark Aqua",
            ChatColor.WHITE + "&4 " + ChatColor.DARK_AQUA + "Dark Red",
            ChatColor.WHITE + "&5 " + ChatColor.DARK_PURPLE + "Dark Purple",
            ChatColor.WHITE + "&6 " + ChatColor.GOLD + "Gold",
            ChatColor.WHITE + "&7 " + ChatColor.GRAY + "Gray",
            ChatColor.WHITE + "&8 " + ChatColor.DARK_GRAY + "Dark Gray",
            ChatColor.WHITE + "&9 " + ChatColor.BLUE + "Blue",
            ChatColor.WHITE + "&a " + ChatColor.GREEN + "Green",
            ChatColor.WHITE + "&b " + ChatColor.AQUA + "Aqua",
            ChatColor.WHITE + "&c " + ChatColor.RED + "Red",
            ChatColor.WHITE + "&d " + ChatColor.LIGHT_PURPLE + "Light Purple",
            ChatColor.WHITE + "&e " + ChatColor.YELLOW + "Yellow",
            ChatColor.WHITE + "&f " + ChatColor.WHITE + "White"));
    menu.setItem(9, formatCodes);
    menu.setItem(10, colorCodes);
  }

  /**
   * Adds the display name button.
   */
  private void addDisplayName() {
    menu.setItem(11, ItemCreator.createItem(Material.NAME_TAG, ChatColor.AQUA + "Display Name", List.of(ChatColor.WHITE + ItemReader.readName(item))));
  }

  /**
   * Adds the custom model data button.
   */
  private void addCustomModelData() {
    menu.setItem(12, !meta.hasCustomModelData() ?
        ItemCreator.createItem(Material.OXEYE_DAISY, ChatColor.AQUA + "Custom Model Data") :
        ItemCreator.createItem(Material.OXEYE_DAISY, ChatColor.AQUA + "Custom Model Data", List.of(ChatColor.WHITE + String.valueOf(meta.getCustomModelData()))));
  }

  /**
   * Adds lore buttons.
   */
  private void addLore() {
    ItemStack lore;
    if (!meta.hasLore()) {
      lore = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Lore", List.of(ChatColor.GRAY + "None set."));
    } else {
      List<String> loreLines = meta.getLore();
      for (int i = 0; i < loreLines.size(); i++) {
        loreLines.set(i, ChatColor.WHITE + "" + (i + 1) + " " + ChatColor.RESET + loreLines.get(i));
      }
      lore = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Lore", loreLines);
    }
    menu.setItem(28, lore);
    menu.setItem(29, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Set Lore", List.of(ChatColor.WHITE + "Separate lines by \",, \".")));
    menu.setItem(30, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Clear Lore"));
    menu.setItem(37, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Add Lore"));
    menu.setItem(38, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Edit Lore", List.of(ChatColor.WHITE + "Specify line, then new text.")));
    menu.setItem(39, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Remove Lore", List.of(ChatColor.WHITE + "Specify line.")));
    menu.setItem(47, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Generate Lore", List.of(ChatColor.WHITE + "Generates plugin-related lore.")));
  }

  /**
   * Adds attribute, enchantment, and Aethel tags editor menu buttons.
   */
  private void addGameplay() {
    menu.setItem(14, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Attributes", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.ENCHANTED_BOOK, ChatColor.AQUA + "Enchantments"));
    menu.setItem(16, ItemCreator.createItem(Material.RABBIT_FOOT, ChatColor.AQUA + "Aethel Tags"));
  }

  /**
   * Adds item flag toggle buttons.
   */
  private void addItemFlags() {
    addHideArmorTrim(menu, meta);
    addHideAttributes(menu, meta);
    addHideDestroys(menu, meta);
    addHideDye(menu, meta);
    addHideEnchants(menu, meta);
    addHidePlacedOn(menu, meta);
    addHidePotionEffects(menu, meta);
    addHideUnbreakable(menu, meta);
  }

  /**
   * Adds the unbreakable toggle button.
   *
   * @param menu CosmeticEditor menu
   * @param meta item meta
   */
  protected static void addUnbreakable(Inventory menu, ItemMeta meta) {
    boolean disabled = !meta.isUnbreakable();
    String unbreakable = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(52, ItemCreator.createItem(disabled ? Material.CLAY : Material.BEDROCK, ChatColor.AQUA + "Unbreakable", List.of(unbreakable)));
  }

  /**
   * Adds hide armor trim toggle button.
   *
   * @param menu CosmeticEditor menu
   * @param meta item meta
   */
  protected static void addHideArmorTrim(Inventory menu, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM);
    String armorTrim = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(32, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Armor Trims", List.of(armorTrim)));
  }

  /**
   * Adds hide attributes toggle button.
   *
   * @param menu CosmeticEditor menu
   * @param meta item meta
   */
  protected static void addHideAttributes(Inventory menu, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
    String attributes = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(33, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Attributes", List.of(attributes)));
  }

  /**
   * Adds hide destroys toggle button.
   *
   * @param menu CosmeticEditor menu
   * @param meta item meta
   */
  protected static void addHideDestroys(Inventory menu, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_DESTROYS);
    String destroys = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(34, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Destroys", List.of(destroys)));
  }

  /**
   * Adds hide dye toggle button.
   *
   * @param menu CosmeticEditor menu
   * @param meta item meta
   */
  protected static void addHideDye(Inventory menu, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_DYE);
    String dye = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(41, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Dyes", List.of(dye)));
  }

  /**
   * Adds hide enchants toggle button.
   *
   * @param menu CosmeticEditor menu
   * @param meta item meta
   */
  protected static void addHideEnchants(Inventory menu, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    String enchants = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(42, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Enchants", List.of(enchants)));
  }

  /**
   * Adds hide placed on toggle button.
   *
   * @param menu CosmeticEditor menu
   * @param meta item meta
   */
  protected static void addHidePlacedOn(Inventory menu, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON);
    String placedOn = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(43, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Placed On", List.of(placedOn)));
  }

  /**
   * Adds hide potion effects toggle button.
   *
   * @param menu CosmeticEditor menu
   * @param meta item meta
   */
  protected static void addHidePotionEffects(Inventory menu, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
    String potionEffects = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(50, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Potion Effects", List.of(potionEffects)));
  }

  /**
   * Adds hide unbreakable toggle button.
   *
   * @param menu CosmeticEditor menu
   * @param meta item meta
   */
  protected static void addHideUnbreakable(Inventory menu, ItemMeta meta) {
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);
    String unbreakable = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(51, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Unbreakable", List.of(unbreakable)));
  }
}
