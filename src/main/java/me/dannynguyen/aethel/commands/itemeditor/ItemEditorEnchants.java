package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginPlayerHead;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;

/**
 * ItemEditorEnchants is an inventory that edits an item's enchantments.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.6.16
 */
public class ItemEditorEnchants {
  private static final List<Enchantment> sortedEnchantments = sortEnchantments();

  /**
   * Opens an ItemEditorEnchants menu.
   *
   * @param user user
   * @return ItemEditorEnchants menu
   */
  public static Inventory openMenu(Player user) {
    ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
    Inventory inv = createInventory(user, item);
    addEnchants(inv, user);
    addContext(inv);
    InventoryPages.addBackButton(inv, 6);
    return inv;
  }

  /**
   * Creates and names an ItemEditorEnchants menu.
   *
   * @param user user
   * @param item interacting item
   * @return ItemEditorEnchants inventory
   */
  private static Inventory createInventory(Player user, ItemStack item) {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.DARK_AQUA + "Enchants");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Adds enchants.
   *
   * @param inv  interacting inventory
   * @param user user
   */
  private static void addEnchants(Inventory inv, Player user) {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    Map<Enchantment, Integer> metaEnchants = meta.getEnchants();
    int invSlot = 9;
    for (Enchantment enchant : sortedEnchantments) {
      String enchantName = ChatColor.AQUA + TextFormatter.capitalizePhrase(enchant.getKey().getKey());
      boolean disabled = metaEnchants.get(enchant) == null;
      inv.setItem(invSlot, disabled ?
          ItemCreator.createItem(Material.BOOK, enchantName) :
          ItemCreator.createItem(Material.ENCHANTED_BOOK, enchantName, List.of(ChatColor.WHITE + String.valueOf(metaEnchants.get(enchant)))));
      invSlot++;
    }
  }

  /**
   * Adds a help context to the enchants editor.
   *
   * @param inv interacting inventory
   */
  private static void addContext(Inventory inv) {
    inv.setItem(2, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head, ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove an enchant, input \"0\".")));
  }

  /**
   * Sorts enchantments by name.
   *
   * @return sorted enchantments
   */
  private static List<Enchantment> sortEnchantments() {
    List<Enchantment> enchantments = Arrays.asList(Enchantment.values());
    Comparator<Enchantment> enchantmentComparator = comparing(e -> e.getKey().getKey());
    enchantments.sort(enchantmentComparator);
    return enchantments;
  }
}
