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

import java.util.List;

/**
 * ItemEditorInventory is an inventory that displays an item's metadata fields.
 *
 * @author Danny Nguyen
 * @version 1.9.5
 * @since 1.6.7
 */
public class ItemEditorInventory {
  /**
   * Opens an ItemEditor menu with metadata fields.
   *
   * @param user user
   * @param item interacting item
   * @return ItemEditor main menu
   */
  public static Inventory openMainMenu(Player user, ItemStack item) {
    Inventory inv = createInventory(user, item);
    addActions(inv, item);
    addContexts(inv);
    return inv;
  }

  /**
   * Creates and names an ItemEditor inventory to the item and adds the item being edited.
   *
   * @param user user
   * @return ItemEditor inventory
   */
  private static Inventory createInventory(Player user, ItemStack item) {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Adds item metadata editor buttons.
   *
   * @param inv  interacting inventory
   * @param item interacting item
   */
  private static void addActions(Inventory inv, ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    addDisplayNameAction(inv, item);
    addCustomModelDataAction(inv, meta);
    addLoreActions(inv, meta);
    addGameplayActions(inv);
    ItemEditorToggles.addItemFlagsMeta(inv, meta);
    ItemEditorToggles.addUnbreakableMeta(inv, meta);
  }

  /**
   * Adds help contexts to the ItemEditor inventory.
   *
   * @param inv interacting inventory
   */
  private static void addContexts(Inventory inv) {
    ItemStack formatCodes = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
        ChatColor.GREEN + "Format Codes", Context.SPIGOT_FORMAT_CODES.context);
    ItemStack colorCodes = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
        ChatColor.GREEN + "Color Codes", Context.SPIGOT_COLOR_CODES.context);

    inv.setItem(9, formatCodes);
    inv.setItem(10, colorCodes);
  }

  /**
   * Adds the display name metadata button.
   *
   * @param inv  interacting inventory
   * @param item interacting item
   */
  private static void addDisplayNameAction(Inventory inv, ItemStack item) {
    ItemStack displayName = ItemCreator.createItem(Material.NAME_TAG,
        ChatColor.AQUA + "Display Name", List.of(ChatColor.WHITE + ItemReader.readName(item)));
    inv.setItem(11, displayName);
  }

  /**
   * Adds the custom model data metadata button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  private static void addCustomModelDataAction(Inventory inv, ItemMeta meta) {
    ItemStack customModelData = (!meta.hasCustomModelData() ?
        ItemCreator.createItem(Material.OXEYE_DAISY, ChatColor.AQUA + "Custom Model Data") :
        ItemCreator.createItem(Material.OXEYE_DAISY, ChatColor.AQUA + "Custom Model Data",
            List.of(ChatColor.WHITE + String.valueOf(meta.getCustomModelData()))));
    inv.setItem(12, customModelData);
  }

  /**
   * Adds lore metadata buttons.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  private static void addLoreActions(Inventory inv, ItemMeta meta) {
    ItemStack lore;
    if (!meta.hasLore()) {
      lore = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
          ChatColor.GREEN + "Lore", List.of(ChatColor.GRAY + "None set."));
    } else {
      List<String> loreLines = meta.getLore();
      for (int i = 0; i < loreLines.size(); i++) {
        loreLines.set(i, ChatColor.WHITE + "" + (i + 1) + " " + ChatColor.RESET + loreLines.get(i));
      }
      lore = ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
          ChatColor.GREEN + "Lore", loreLines);
    }

    ItemStack setLore = ItemCreator.createItem(Material.PAPER,
        ChatColor.AQUA + "Set Lore", List.of(ChatColor.WHITE + "Separate lines by \",, \"."));
    ItemStack clearLore = ItemCreator.createItem(Material.PAPER,
        ChatColor.AQUA + "Clear Lore");
    ItemStack addLore = ItemCreator.createItem(Material.PAPER,
        ChatColor.AQUA + "Add Lore");
    ItemStack editLore = ItemCreator.createItem(Material.PAPER,
        ChatColor.AQUA + "Edit Lore", List.of(ChatColor.WHITE + "Specify line, then new text."));
    ItemStack removeLore = ItemCreator.createItem(Material.PAPER,
        ChatColor.AQUA + "Remove Lore", List.of(ChatColor.WHITE + "Specify line."));

    inv.setItem(28, lore);
    inv.setItem(29, setLore);
    inv.setItem(30, clearLore);
    inv.setItem(37, addLore);
    inv.setItem(38, editLore);
    inv.setItem(39, removeLore);
  }


  /**
   * Adds attributes, enchants, and Aethel tags metadata buttons.
   *
   * @param inv interacting inventory
   */
  private static void addGameplayActions(Inventory inv) {
    inv.setItem(14, ItemCreator.createItem(Material.IRON_HELMET,
        ChatColor.AQUA + "Attributes", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(15, ItemCreator.createItem(Material.ENCHANTED_BOOK, ChatColor.AQUA + "Enchants"));
    inv.setItem(16, ItemCreator.createItem(Material.RABBIT_FOOT, ChatColor.AQUA + "Aethel Tags"));
  }

  private enum Context {
    SPIGOT_FORMAT_CODES(List.of(
        ChatColor.WHITE + "&k " + ChatColor.MAGIC + "Magic",
        ChatColor.WHITE + "&l " + ChatColor.BOLD + "Bold",
        ChatColor.WHITE + "&m " + ChatColor.STRIKETHROUGH + "Strike",
        ChatColor.WHITE + "&n " + ChatColor.UNDERLINE + "Underline",
        ChatColor.WHITE + "&o " + ChatColor.ITALIC + "Italic",
        ChatColor.WHITE + "&r " + ChatColor.RESET + "Reset")),
    SPIGOT_COLOR_CODES(List.of(
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

    public final List<String> context;

    Context(List<String> context) {
      this.context = context;
    }
  }
}
