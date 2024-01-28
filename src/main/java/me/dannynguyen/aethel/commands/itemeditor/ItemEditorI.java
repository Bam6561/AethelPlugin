package me.dannynguyen.aethel.commands.itemeditor;

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
 * ItemEditorMenu is an inventory under the ItemEditor command that displays an item's metadata fields.
 *
 * @author Danny Nguyen
 * @version 1.7.0
 * @since 1.6.7
 */
public class ItemEditorI {
  /**
   * Opens an ItemEditorMenu with metadata fields.
   *
   * @param user interacting user
   * @param item   interacting item
   * @return ItemEditorMenu with metadata fields
   */
  public static Inventory openCosmeticMenu(Player user, ItemStack item) {
    Inventory inv = createInventory(user, item);
    addMetadataButtons(inv, item);
    return inv;
  }

  /**
   * Creates and names an ItemEditorMenu inventory to the item.
   *
   * @param user interacting user
   * @return ItemEditorMenu
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
  private static void addMetadataButtons(Inventory inv, ItemStack item) {
    ItemMeta meta = item.getItemMeta();

    addDisplayNameMeta(inv, item);
    addCustomModelDataMeta(inv, meta);
    addLoreMeta(inv, meta);
    addGameplayMeta(inv);
    ItemEditorToggles.addItemFlagsMeta(inv, meta);
    ItemEditorToggles.addUnbreakableMeta(inv, meta);
  }

  /**
   * Adds the display name metadata button.
   *
   * @param inv  interacting inventory
   * @param item interacting item
   */
  private static void addDisplayNameMeta(Inventory inv, ItemStack item) {
    ItemStack formatCodes = ItemCreator.createPlayerHeadTexture("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Format Codes",
        List.of(ChatColor.WHITE + "&k " + ChatColor.MAGIC + "Magic",
            ChatColor.WHITE + "&l " + ChatColor.BOLD + "Bold",
            ChatColor.WHITE + "&m " + ChatColor.STRIKETHROUGH + "Strike",
            ChatColor.WHITE + "&n " + ChatColor.UNDERLINE + "Underline",
            ChatColor.WHITE + "&o " + ChatColor.ITALIC + "Italic",
            ChatColor.WHITE + "&r " + ChatColor.RESET + "Reset"));
    ItemStack colorCodes = ItemCreator.createPlayerHeadTexture("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Color Codes",
        List.of(ChatColor.WHITE + "&0 " + ChatColor.BLACK + "Black",
            ChatColor.WHITE + "&1 " + ChatColor.DARK_BLUE + "Dark Blue",
            ChatColor.WHITE + "&2 " + ChatColor.DARK_GREEN + "Dark Green",
            ChatColor.WHITE + "&3 " + ChatColor.DARK_RED + "Dark Red",
            ChatColor.WHITE + "&4 " + ChatColor.DARK_AQUA + "Dark Aqua",
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
    ItemStack displayName = ItemCreator.createItem(Material.NAME_TAG,
        ChatColor.AQUA + "Display Name", List.of(ChatColor.WHITE + ItemReader.readName(item)));

    inv.setItem(9, formatCodes);
    inv.setItem(10, colorCodes);
    inv.setItem(11, displayName);
  }

  /**
   * Adds the custom model data metadata button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  private static void addCustomModelDataMeta(Inventory inv, ItemMeta meta) {
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
  private static void addLoreMeta(Inventory inv, ItemMeta meta) {
    ItemStack lore;
    if (!meta.hasLore()) {
      lore = ItemCreator.createPlayerHeadTexture("WHITE_QUESTION_MARK",
          ChatColor.GREEN + "Lore", List.of(ChatColor.GRAY + "None set."));
    } else {
      List<String> loreLines = meta.getLore();
      for (int i = 0; i < loreLines.size(); i++) {
        loreLines.set(i, ChatColor.WHITE + "" + (i + 1) + " " + ChatColor.RESET + loreLines.get(i));
      }
      lore = ItemCreator.createPlayerHeadTexture("WHITE_QUESTION_MARK",
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
  private static void addGameplayMeta(Inventory inv) {
    inv.setItem(14, ItemCreator.createItem(Material.IRON_HELMET,
        ChatColor.AQUA + "Attributes", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(15, ItemCreator.createItem(Material.ENCHANTED_BOOK, ChatColor.AQUA + "Enchants"));
    inv.setItem(16, ItemCreator.createItem(Material.RABBIT_FOOT, ChatColor.AQUA + "Aethel Tags"));
  }
}
