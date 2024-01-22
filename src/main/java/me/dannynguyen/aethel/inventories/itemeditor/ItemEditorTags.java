package me.dannynguyen.aethel.inventories.itemeditor;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.data.ItemEditorData;
import me.dannynguyen.aethel.inventories.utility.Pagination;
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
 * @version 1.6.16
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
    ItemStack item = AethelResources.itemEditorData.getEditedItemMap().get(player);
    Inventory inv = createInventory(player, item);
    addAethelTags(inv, player);
    addTagsContext(inv);
    Pagination.addBackButton(inv, 6);
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
    ItemEditorData itemEditorData = AethelResources.itemEditorData;
    ArrayList<String> aethelTags = itemEditorData.getAethelTags();
    PersistentDataContainer dataContainer = AethelResources.itemEditorData.
        getEditedItemMap().get(player).getItemMeta().getPersistentDataContainer();

    int invSlot = 9;
    for (String tag : aethelTags) {
      NamespacedKey aethelTagKey = new NamespacedKey(AethelPlugin.getInstance(), "aethel." + tag);
      boolean disabled = !dataContainer.has(aethelTagKey, PersistentDataType.STRING);

      inv.setItem(invSlot, disabled ?
          ItemCreator.createItem(Material.ENDER_PEARL, ChatColor.AQUA + tag) :
          ItemCreator.createItem(Material.ENDER_EYE, ChatColor.AQUA + tag,
              List.of(ChatColor.WHITE + dataContainer.get(aethelTagKey, PersistentDataType.STRING))));
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
