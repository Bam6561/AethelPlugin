package me.dannynguyen.aethel.inventories.itemeditor;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.data.ItemEditorData;
import me.dannynguyen.aethel.inventories.utility.InventoryPages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ItemEditorTags is an inventory under the ItemEditor command that edits an item's Aethel tags.
 *
 * @author Danny Nguyen
 * @version 1.7.8
 * @since 1.6.15
 */
public class ItemEditorTags {
  /**
   * Opens an ItemEditorTags inventory with Aethel tags.
   *
   * @param player interacting player
   * @return ItemEditorTags inventory with Aethel tags
   */
  public static Inventory openTagsMenu(Player player) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(player);
    Inventory inv = createInventory(player, item);
    addAethelTags(inv, player);
    addTagsContext(inv);
    InventoryPages.addBackButton(inv, 6);
    return inv;
  }

  /**
   * Creates and names an ItemEditorTags inventory.
   *
   * @param player interacting player
   * @param item   interacting item
   * @return ItemEditorTags inventory
   */
  private static Inventory createInventory(Player player, ItemStack item) {
    Inventory inv = Bukkit.createInventory(player, 54,
        ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.DARK_AQUA + "Aethel Tags");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Adds built-in Aethel tags.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   */
  private static void addAethelTags(Inventory inv, Player player) {
    ItemEditorData itemEditorData = PluginData.itemEditorData;
    ArrayList<NamespacedKey> aethelTags = itemEditorData.getAethelTags();
    PersistentDataContainer dataContainer = PluginData.itemEditorData.
        getEditedItemMap().get(player).getItemMeta().getPersistentDataContainer();

    int invSlot = 9;
    for (NamespacedKey tag : aethelTags) {
      boolean disabled = !dataContainer.has(tag, PersistentDataType.STRING);

      inv.setItem(invSlot, disabled ?
          ItemCreator.createItem(Material.ENDER_PEARL, ChatColor.AQUA + tag.getKey().substring(7)) :
          ItemCreator.createItem(Material.ENDER_EYE, ChatColor.AQUA + tag.getKey().substring(7),
              List.of(ChatColor.WHITE + dataContainer.get(tag, PersistentDataType.STRING))));
      invSlot++;
    }
  }

  /**
   * Adds a help context to the Aethel Tags editor.
   *
   * @param inv interacting inventory
   */
  private static void addTagsContext(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.WHITE + "To remove a tag, input \"-\".");
    inv.setItem(2, ItemCreator.createLoadedPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }
}
