package me.dannynguyen.aethel.inventories.itemeditor;

import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.inventories.itemeditor.utility.ItemEditorToggle;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * ItemEditorMenu is an inventory under the ItemEditor command that displays an item's metadata fields.
 *
 * @author Danny Nguyen
 * @version 1.6.9
 * @since 1.6.7
 */
public class ItemEditorMenu {
  /**
   * Opens an ItemEditorMenu with metadata fields.
   *
   * @param player interacting player
   * @param item   interacting item
   * @return ItemEditorMenu with metadata fields
   */
  public static Inventory openEditorMenu(Player player, ItemStack item) {
    Inventory inv = createInventory(player, item);
    addMetadataButtons(inv, item);
    return inv;
  }

  /**
   * Creates and names an ItemEditorMenu inventory to the item.
   *
   * @param player interacting player
   * @return ItemEditorMenu
   */
  private static Inventory createInventory(Player player, ItemStack item) {
    Inventory inv = Bukkit.createInventory(player, 54, ChatColor.DARK_GRAY + "ItemEditor");
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
    ItemEditorToggle.addUnbreakableMeta(inv, meta);
    ItemEditorToggle.addItemFlagMetas(inv, meta);
  }

  /**
   * Adds the display name metadata button.
   *
   * @param inv  interacting inventory
   * @param item interacting item
   */
  private static void addDisplayNameMeta(Inventory inv, ItemStack item) {
    ItemStack displayName = ItemCreator.createItem(Material.NAME_TAG,
        ChatColor.AQUA + "Display Name", List.of(ChatColor.WHITE + ItemReader.readItemName(item)));
    inv.setItem(9, displayName);
  }

  /**
   * Adds the custom model data metadata button.
   *
   * @param inv  interacting inventory
   * @param meta item meta
   */
  private static void addCustomModelDataMeta(Inventory inv, ItemMeta meta) {
    ItemStack customModelData = (!meta.hasCustomModelData() ?
        ItemCreator.createItem(Material.RED_DYE, ChatColor.AQUA + "Custom Model Data") :
        ItemCreator.createItem(Material.RED_DYE, ChatColor.AQUA + "Custom Model Data",
            List.of(ChatColor.WHITE + String.valueOf(meta.getCustomModelData()))));
    inv.setItem(10, customModelData);
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
      lore = ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
          ChatColor.GREEN + "Lore", List.of(ChatColor.GRAY + "None set."));
    } else {
      List<String> loreLines = meta.getLore();
      for (int i = 0; i < loreLines.size(); i++) {
        loreLines.set(i, ChatColor.WHITE + "" + (i + 1) + " " + ChatColor.RESET + loreLines.get(i));
      }
      lore = ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
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

    inv.setItem(18, lore);
    inv.setItem(19, setLore);
    inv.setItem(20, clearLore);
    inv.setItem(27, addLore);
    inv.setItem(28, editLore);
    inv.setItem(29, removeLore);
  }
}
