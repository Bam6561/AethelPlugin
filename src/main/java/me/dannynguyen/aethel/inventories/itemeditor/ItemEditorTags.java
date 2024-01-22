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
import java.util.List;

/**
 * ItemEditorTags is an inventory under the ItemEditor command that edits Aethel tags.
 *
 * @author Danny Nguyen
 * @version 1.6.15
 * @since 1.6.15
 */
public class ItemEditorTags {
  public static Inventory openInventory(Player player) {
    ItemStack item = AethelResources.itemEditorData.getEditedItemMap().get(player);
    Inventory inv = createInventory(player, item);
    Pagination.addBackButton(inv, 6);
    addAethelTags(inv, player);
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
}
