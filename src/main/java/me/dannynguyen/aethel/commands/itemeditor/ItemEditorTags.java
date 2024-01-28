package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.InventoryPages;
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
   * @param user interacting user
   * @return ItemEditorTags inventory with Aethel tags
   */
  public static Inventory openTagsMenu(Player user) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
    Inventory inv = createInventory(user, item);
    addAethelTags(inv, user);
    addTagsContext(inv);
    InventoryPages.addBackButton(inv, 6);
    return inv;
  }

  /**
   * Creates and names an ItemEditorTags inventory.
   *
   * @param user interacting user
   * @param item   interacting item
   * @return ItemEditorTags inventory
   */
  private static Inventory createInventory(Player user, ItemStack item) {
    Inventory inv = Bukkit.createInventory(user, 54,
        ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.DARK_AQUA + "Aethel Tags");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Adds built-in Aethel tags.
   *
   * @param inv    interacting inventory
   * @param user interacting user
   */
  private static void addAethelTags(Inventory inv, Player user) {
    ItemEditorData itemEditorData = PluginData.itemEditorData;
    ArrayList<NamespacedKey> aethelTags = itemEditorData.getAethelTags();
    PersistentDataContainer dataContainer = PluginData.itemEditorData.
        getEditedItemMap().get(user).getItemMeta().getPersistentDataContainer();

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
    inv.setItem(2, ItemCreator.createPlayerHeadTexture("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }
}
