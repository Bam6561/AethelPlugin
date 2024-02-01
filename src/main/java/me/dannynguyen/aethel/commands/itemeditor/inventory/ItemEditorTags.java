package me.dannynguyen.aethel.commands.itemeditor.inventory;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginConstant;
import me.dannynguyen.aethel.enums.PluginPlayerHead;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * ItemEditorTags is an inventory that edits an item's Aethel tags.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.6.15
 */
public class ItemEditorTags {
  /**
   * Opens an ItemEditorTags menu with Aethel tags.
   *
   * @param user user
   * @return ItemEditorTags menu
   */
  public static Inventory openTagsMenu(Player user) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
    Inventory inv = createInventory(user, item);
    addAethelTags(inv, user);
    addContext(inv);
    InventoryPages.addBackButton(inv, 6);
    return inv;
  }

  /**
   * Creates and names an ItemEditorTags inventory.
   *
   * @param user user
   * @param item interacting item
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
   * @param inv  interacting inventory
   * @param user user
   */
  private static void addAethelTags(Inventory inv, Player user) {
    PersistentDataContainer dataContainer = PluginData.itemEditorData.
        getEditedItemMap().get(user).getItemMeta().getPersistentDataContainer();

    int invSlot = 9;
    for (NamespacedKey tag : PluginConstant.aethelTags) {
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
  private static void addContext(Inventory inv) {
    inv.setItem(2, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
        ChatColor.GREEN + "Help", Context.TAGS.context));
  }

  private enum Context {
    TAGS(List.of(ChatColor.WHITE + "To remove a tag, input \"-\"."));

    public final List<String> context;

    Context(List<String> context) {
      this.context = context;
    }
  }
}
