package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.PlayerHead;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
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
 * @version 1.14.5
 * @since 1.6.15
 */
class TagMenu {
  /**
   * Aethel tags managed by the GUI.
   */
  private static final NamespacedKey[] aethelTags = {
      PluginNamespacedKey.ITEM_CATEGORY.getNamespacedKey(),
      PluginNamespacedKey.RECIPE_CATEGORY.getNamespacedKey(),
      PluginNamespacedKey.RECIPE_FORGE_ID.getNamespacedKey()};

  /**
   * Tag GUI.
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
   * Associates a new Tag menu with its user and editing item.
   *
   * @param user user
   */
  protected TagMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = Plugin.getData().getEditedItemCache().getEditedItemMap().get(user.getUniqueId());
    this.menu = createMenu();
  }

  /**
   * Creates and names a Tag menu.
   *
   * @return Tag menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Aethel Tags");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with Aethel tags.
   *
   * @return Tag menu
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
    menu.setItem(2, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove a tag, input \"-\".")));
  }
}
