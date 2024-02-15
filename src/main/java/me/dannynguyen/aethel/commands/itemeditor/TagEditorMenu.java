package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.PluginEnum;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents a menu that edits an item's Aethel tags.
 *
 * @author Danny Nguyen
 * @version 1.9.21
 * @since 1.6.15
 */
class TagEditorMenu {
  /**
   * Aethel tags managed by the GUI.
   */
  private static final NamespacedKey[] aethelTags = {
      PluginEnum.Key.ITEM_CATEGORY.getNamespacedKey(),
      PluginEnum.Key.RECIPE_CATEGORY.getNamespacedKey(),
      PluginEnum.Key.RECIPE_ID.getNamespacedKey()};

  /**
   * TagEditor GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * ItemStack being edited.
   */
  private final ItemStack item;

  /**
   * Associates a new TagEditor menu with its user and editing item.
   *
   * @param user user
   */
  protected TagEditorMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = PluginData.editedItemCache.getEditedItemMap().get(user);
    this.menu = createMenu();
  }

  /**
   * Creates and names a TagEditor menu.
   *
   * @return TagEditor menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.DARK_AQUA + "Aethel Tags");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with Aethel tags.
   *
   * @return TagEditor menu
   */
  @NotNull
  protected Inventory openMenu() {
    addAethelTags();
    addContext();
    InventoryPages.addBackButton(menu, 6);
    return menu;
  }

  /**
   * Adds editable Aethel tags.
   */
  private void addAethelTags() {
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
    int invSlot = 9;
    for (NamespacedKey tag : aethelTags) {
      boolean disabled = !dataContainer.has(tag, PersistentDataType.STRING);
      menu.setItem(invSlot, disabled ?
          ItemCreator.createItem(Material.ENDER_PEARL, ChatColor.AQUA + tag.getKey().substring(7)) :
          ItemCreator.createItem(Material.ENDER_EYE, ChatColor.AQUA + tag.getKey().substring(7), List.of(ChatColor.WHITE + dataContainer.get(tag, PersistentDataType.STRING))));
      invSlot++;
    }
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(2, ItemCreator.createPluginPlayerHead(PluginEnum.PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove a tag, input \"-\".")));
  }
}
