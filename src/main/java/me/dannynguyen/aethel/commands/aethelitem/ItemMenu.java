package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.PlayerHead;
import me.dannynguyen.aethel.plugin.interfaces.CategoryMenu;
import me.dannynguyen.aethel.util.InventoryPages;
import me.dannynguyen.aethel.util.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a menu that supports categorical pagination for obtaining,
 * creating, editing, and removing {@link PersistentItem items}.
 *
 * @author Danny Nguyen
 * @version 1.17.19
 * @since 1.4.0
 */
public class ItemMenu implements CategoryMenu {
  /**
   * GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * GUI action.
   */
  private final Action action;

  /**
   * Associates a new AethelItem menu with its user and action.
   *
   * @param user   user
   * @param action type of interaction
   */
  public ItemMenu(@NotNull Player user, @NotNull Action action) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.action = Objects.requireNonNull(action, "Null action");
    this.uuid = user.getUniqueId();
    this.menu = createMenu();
  }

  /**
   * Creates and names an AethelItem menu with its action and category.
   *
   * @return AethelItem menu
   */
  private Inventory createMenu() {
    String title = ChatColor.DARK_GRAY + "Aethel Item";
    switch (action) {
      case GET -> title += ChatColor.GREEN + " Get " + ChatColor.WHITE + Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getCategory();
      case REMOVE -> title += ChatColor.RED + " Remove " + ChatColor.WHITE + Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getCategory();
    }
    return Bukkit.createInventory(user, 54, title);
  }

  /**
   * Sets the menu to view {@link PersistentItem item} categories.
   *
   * @return AethelItem main menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addCategories();
    addContext(null);
    addActions();
    return menu;
  }

  /**
   * Sets the menu to load an {@link PersistentItem item} category page.
   *
   * @param requestedCategory requested category
   * @param requestedPage     requested page
   * @return {@link PersistentItem item} category page
   */
  @NotNull
  public Inventory getCategoryPage(String requestedCategory, int requestedPage) {
    List<Inventory> category = Plugin.getData().getItemRegistry().getItemCategories().get(requestedCategory);
    int numberOfPages = category.size();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, requestedPage);
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setPage(pageViewed);

    menu.setContents(category.get(pageViewed).getContents());
    addContext(requestedCategory);
    addActions();
    InventoryPages.addBackButton(menu, 6);
    InventoryPages.addPageButtons(menu, numberOfPages, pageViewed);
    return menu;
  }

  /**
   * Adds contextual help.
   *
   * @param requestedCategory requested category
   */
  private void addContext(String requestedCategory) {
    List<String> lore;
    if (requestedCategory != null) {
      lore = List.of(
          ChatColor.WHITE + "Place an item to",
          ChatColor.WHITE + "the right of this",
          ChatColor.WHITE + "slot to save it.");
    } else {
      lore = List.of(
          ChatColor.WHITE + "Place an item to",
          ChatColor.WHITE + "the right of this",
          ChatColor.WHITE + "slot to save it.",
          "",
          ChatColor.WHITE + "You can toggle between",
          ChatColor.WHITE + "Get and Remove modes by",
          ChatColor.WHITE + "clicking on their button.",
          "",
          ChatColor.WHITE + "To undo a removal,",
          ChatColor.WHITE + "get the item and save",
          ChatColor.WHITE + "it before reloading.");
    }
    menu.setItem(2, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", lore));
  }

  /**
   * Adds save, get, and remove actions.
   */
  private void addActions() {
    switch (action) {
      case GET -> {
        menu.setItem(4, ItemCreator.createPluginPlayerHead(PlayerHead.CRAFTING_TABLE.getHead(), ChatColor.AQUA + "Save"));
        menu.setItem(5, ItemCreator.createPluginPlayerHead(PlayerHead.TRASH_CAN.getHead(), ChatColor.AQUA + "Remove"));
      }
      case REMOVE -> {
        menu.setItem(4, ItemCreator.createPluginPlayerHead(PlayerHead.CRAFTING_TABLE.getHead(), ChatColor.AQUA + "Save"));
        menu.setItem(5, ItemCreator.createPluginPlayerHead(PlayerHead.BACKPACK_BROWN.getHead(), ChatColor.AQUA + "Get"));
      }
      case VIEW -> menu.setItem(4, ItemCreator.createPluginPlayerHead(PlayerHead.CRAFTING_TABLE.getHead(), ChatColor.AQUA + "Save"));
    }
  }

  /**
   * Adds {@link PersistentItem item} categories.
   */
  private void addCategories() {
    Set<String> categories = Plugin.getData().getItemRegistry().getItemCategories().keySet();
    if (!categories.isEmpty()) {
      int i = 9;
      for (String category : categories) {
        menu.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + category));
        i++;
      }
    }
  }

  /**
   * Types of interactions.
   */
  public enum Action {
    /**
     * Obtain {@link PersistentItem items}.
     */
    GET,

    /**
     * Remove {@link PersistentItem items}.
     */
    REMOVE,

    /**
     * View {@link PersistentItem item} categories.
     */
    VIEW
  }
}
