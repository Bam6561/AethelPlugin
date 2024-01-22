package me.dannynguyen.aethel.inventories.itemeditor;

import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.formatters.TextFormatter;
import me.dannynguyen.aethel.inventories.utility.Pagination;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ItemEditorEnchants is an inventory under the ItemEditor command that edits an item's enchantments.
 *
 * @author Danny Nguyen
 * @version 1.6.16
 * @since 1.6.16
 */
public class ItemEditorEnchants {
  /**
   * Opens an ItemEditorEnchants inventory with enchants.
   *
   * @param player interacting player
   * @return ItemEditorEnchants inventory with enchants
   */
  public static Inventory openEnchantsMenu(Player player) {
    ItemStack item = AethelResources.itemEditorData.getEditedItemMap().get(player);
    Inventory inv = createInventory(player, item);
    addEnchants(inv, player);
    addEnchantsContext(inv);
    Pagination.addBackButton(inv, 6);
    return inv;
  }

  /**
   * Creates and names an ItemEditorEnchants inventory.
   *
   * @param player interacting player
   * @param item   interacting item
   * @return ItemEditorEnchants inventory
   */
  private static Inventory createInventory(Player player, ItemStack item) {
    Inventory inv = Bukkit.createInventory(player, 54,
        ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.DARK_AQUA + "Enchants");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Adds enchants.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   */
  private static void addEnchants(Inventory inv, Player player) {
    ItemMeta meta = AethelResources.itemEditorData.getEditedItemMap().get(player).getItemMeta();
    Map<Enchantment, Integer> metaEnchants = meta.getEnchants();

    int invSlot = 9;
    for (Enchantment enchant : AethelResources.itemEditorData.getEnchants()) {
      String enchantName = ChatColor.AQUA + TextFormatter.capitalizeProperly(enchant.getKey().getKey());

      boolean disabled = metaEnchants.get(enchant) == null;
      inv.setItem(invSlot, disabled ?
          ItemCreator.createItem(Material.BOOK, enchantName) :
          ItemCreator.createItem(Material.ENCHANTED_BOOK, enchantName,
              List.of(ChatColor.WHITE + String.valueOf(metaEnchants.get(enchant)))));
      invSlot++;
    }
  }

  /**
   * Adds a help context to the enchants editor.
   *
   * @param inv interacting inventory
   */
  private static void addEnchantsContext(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.WHITE + "To remove an enchant, input \"0\".");
    inv.setItem(2, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }
}
