package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.enums.PluginPlayerHead;
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
 * @version 1.9.17
 * @since 1.6.7
 */
public class ItemEditorCosmetic {
  /**
   * ItemEditorCosmetic GUI.
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
   * Associates a new ItemEditorCosmetic menu with its user.
   *
   * @param user user
   * @param item interacting item
   */
  public ItemEditorCosmetic(@NotNull Player user, @NotNull ItemStack item) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = Objects.requireNonNull(item, "Null item");
    this.menu = createMenu();
  }

  /**
   * Creates and names an ItemEditorCosmetic menu with its item being edited.
   *
   * @return ItemEditorCosmetic menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with cosmetic metadata.
   *
   * @return ItemEditorCosmetic menu
   */
  @NotNull
  public Inventory openMenu() {
    addActions();
    addContext();
    return menu;
  }

  /**
   * Adds item editor buttons.
   */
  private void addActions() {
    ItemMeta meta = item.getItemMeta();
    addDisplayNameAction();
    addCustomModelDataAction(meta);
    addLoreActions(meta);
    addGameplayActions();
    addItemFlagsMeta();
    addUnbreakableMeta();
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    ItemStack formatCodes = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
        ChatColor.GREEN + "Format Codes", List.of(
            ChatColor.WHITE + "&k " + ChatColor.MAGIC + "Magic",
            ChatColor.WHITE + "&l " + ChatColor.BOLD + "Bold",
            ChatColor.WHITE + "&m " + ChatColor.STRIKETHROUGH + "Strike",
            ChatColor.WHITE + "&n " + ChatColor.UNDERLINE + "Underline",
            ChatColor.WHITE + "&o " + ChatColor.ITALIC + "Italic",
            ChatColor.WHITE + "&r " + ChatColor.RESET + "Reset"));
    ItemStack colorCodes = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
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
   * Adds the display name metadata button.
   */
  private void addDisplayNameAction() {
    ItemStack displayName = ItemCreator.createItem(Material.NAME_TAG, ChatColor.AQUA + "Display Name", List.of(ChatColor.WHITE + ItemReader.readName(item)));
    menu.setItem(11, displayName);
  }

  /**
   * Adds the custom model data button.
   *
   * @param meta item meta
   */
  private void addCustomModelDataAction(ItemMeta meta) {
    ItemStack customModelData = (!meta.hasCustomModelData() ?
        ItemCreator.createItem(Material.OXEYE_DAISY, ChatColor.AQUA + "Custom Model Data") :
        ItemCreator.createItem(Material.OXEYE_DAISY, ChatColor.AQUA + "Custom Model Data", List.of(ChatColor.WHITE + String.valueOf(meta.getCustomModelData()))));
    menu.setItem(12, customModelData);
  }

  /**
   * Adds lore buttons.
   */
  private void addLoreActions(ItemMeta meta) {
    ItemStack lore;
    if (!meta.hasLore()) {
      lore = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head, ChatColor.GREEN + "Lore", List.of(ChatColor.GRAY + "None set."));
    } else {
      List<String> loreLines = meta.getLore();
      for (int i = 0; i < loreLines.size(); i++) {
        loreLines.set(i, ChatColor.WHITE + "" + (i + 1) + " " + ChatColor.RESET + loreLines.get(i));
      }
      lore = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head, ChatColor.GREEN + "Lore", loreLines);
    }
    ItemStack setLore = ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Set Lore", List.of(ChatColor.WHITE + "Separate lines by \",, \"."));
    ItemStack clearLore = ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Clear Lore");
    ItemStack addLore = ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Add Lore");
    ItemStack editLore = ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Edit Lore", List.of(ChatColor.WHITE + "Specify line, then new text."));
    ItemStack removeLore = ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Remove Lore", List.of(ChatColor.WHITE + "Specify line."));
    ItemStack generateLore = ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Generate Lore", List.of(ChatColor.WHITE + "Generates plugin-related lore."));
    menu.setItem(28, lore);
    menu.setItem(29, setLore);
    menu.setItem(30, clearLore);
    menu.setItem(37, addLore);
    menu.setItem(38, editLore);
    menu.setItem(39, removeLore);
    menu.setItem(47, generateLore);
  }

  /**
   * Adds attributes, enchants, and Aethel tags menu buttons.
   */
  private void addGameplayActions() {
    menu.setItem(14, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Attributes", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.ENCHANTED_BOOK, ChatColor.AQUA + "Enchants"));
    menu.setItem(16, ItemCreator.createItem(Material.RABBIT_FOOT, ChatColor.AQUA + "Aethel Tags"));
  }
}
