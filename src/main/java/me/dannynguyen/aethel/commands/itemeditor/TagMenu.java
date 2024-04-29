package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.PlayerHead;
import me.dannynguyen.aethel.interfaces.Menu;
import me.dannynguyen.aethel.utils.InventoryPages;
import me.dannynguyen.aethel.utils.item.ItemCreator;
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
 * Represents a menu that edits an item's {@link Key Aethel tags}.
 *
 * @author Danny Nguyen
 * @version 1.24.9
 * @since 1.6.15
 */
public class TagMenu implements Menu {
  /**
   * {@link Key Aethel tags} managed by the GUI.
   */
  private static final NamespacedKey[] aethelTags = {
      Key.RECIPE_FORGE_ID.getNamespacedKey()};

  /**
   * GUI.
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
  public TagMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
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
   * Sets the menu to display interactions with {@link Key Aethel tags}.
   *
   * @return Tag menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addAethelTags();
    addContext();
    InventoryPages.addBackButton(menu, 6);
    return menu;
  }

  /**
   * Adds editable {@link Key Aethel tags}.
   */
  private void addAethelTags() {
    PersistentDataContainer itemTags = item.getItemMeta().getPersistentDataContainer();
    int invSlot = 9;
    for (NamespacedKey tag : aethelTags) {
      boolean disabled = !itemTags.has(tag, PersistentDataType.STRING);
      menu.setItem(invSlot, disabled ?
          ItemCreator.createItem(Material.SMALL_AMETHYST_BUD, ChatColor.AQUA + tag.getKey().substring(7)) :
          ItemCreator.createItem(Material.AMETHYST_CLUSTER, ChatColor.AQUA + tag.getKey().substring(7), List.of(ChatColor.WHITE + itemTags.get(tag, PersistentDataType.STRING))));
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
