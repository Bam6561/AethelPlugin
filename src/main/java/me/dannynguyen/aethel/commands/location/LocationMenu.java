package me.dannynguyen.aethel.commands.location;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.PlayerHead;
import me.dannynguyen.aethel.interfaces.CategoryMenu;
import me.dannynguyen.aethel.utils.InventoryPages;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a menu that supports categorical pagination for saving, removing,
 * tracking, and comparing {@link LocationRegistry.SavedLocation saved locations}.
 *
 * @author Danny Nguyen
 * @version 1.24.7
 * @since 1.24.7
 */
public class LocationMenu implements CategoryMenu {
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
   * User's {@link LocationRegistry}.
   */
  private final LocationRegistry locationRegistry;

  /**
   * GUI action.
   */
  private final LocationMenu.Action action;

  /**
   * Associates a new Location menu with its user and action.
   *
   * @param user   user
   * @param action type of interaction
   */
  public LocationMenu(@NotNull Player user, @NotNull LocationMenu.Action action) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.action = Objects.requireNonNull(action, "Null action");
    this.uuid = user.getUniqueId();
    this.locationRegistry = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getLocationRegistry();
    this.menu = createMenu();
  }

  /**
   * Creates and names a Location menu with its action and category.
   *
   * @return Location menu
   */
  private Inventory createMenu() {
    StringBuilder title = new StringBuilder(ChatColor.DARK_GRAY + "Location");
    String category = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().getCategory();
    switch (action) {
      case REMOVE -> title.append(ChatColor.RED).append(" Remove ").append(ChatColor.WHITE).append(category);
      case TRACK -> title.append(ChatColor.BLUE).append(" Track ").append(ChatColor.WHITE).append(category);
      case COMPARE -> title.append(ChatColor.YELLOW).append(" Compare ").append(ChatColor.WHITE).append(category);
    }
    return Bukkit.createInventory(user, 54, title.toString());
  }

  /**
   * Sets the menu to view {@link LocationRegistry.SavedLocation} categories.
   */
  @NotNull
  public Inventory getMainMenu() {
    addCategories();
    addActions();
    addContext();
    return menu;
  }

  /**
   * Sets the menu to load a {@link LocationRegistry.SavedLocation} category page.
   *
   * @param requestedCategory requested category
   * @param requestedPage     requested page
   * @return {@link LocationRegistry.SavedLocation} category page
   */
  @NotNull
  public Inventory getCategoryPage(String requestedCategory, int requestedPage) {
    List<Inventory> category = locationRegistry.getLocationCategories().get(requestedCategory);
    int numberOfPages = category.size();
    int pageViewed = InventoryPages.getPageViewed(numberOfPages, requestedPage);
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setPage(pageViewed);

    menu.setContents(category.get(pageViewed).getContents());
    addActions();
    addContext();
    InventoryPages.addBackButton(menu, 6);
    InventoryPages.addPagination(menu, numberOfPages, pageViewed);
    return menu;
  }

  /**
   * Adds save, edit, remove, track, and compare actions.
   */
  private void addActions() {
    switch (action) {
      case REMOVE -> {
        menu.setItem(2, ItemCreator.createItem(Material.LODESTONE, ChatColor.AQUA + "Save"));
        menu.setItem(4, ItemCreator.createItem(Material.COMPASS, ChatColor.AQUA + "Track"));
        menu.setItem(5, ItemCreator.createItem(Material.COMPARATOR, ChatColor.AQUA + "Compare"));
      }
      case TRACK -> {
        menu.setItem(2, ItemCreator.createItem(Material.LODESTONE, ChatColor.AQUA + "Save"));
        menu.setItem(3, ItemCreator.createPluginPlayerHead(PlayerHead.TRASH_CAN.getHead(), ChatColor.AQUA + "Remove"));
        menu.setItem(5, ItemCreator.createItem(Material.COMPARATOR, ChatColor.AQUA + "Compare"));
      }
      case COMPARE -> {
        menu.setItem(2, ItemCreator.createItem(Material.LODESTONE, ChatColor.AQUA + "Save"));
        menu.setItem(3, ItemCreator.createPluginPlayerHead(PlayerHead.TRASH_CAN.getHead(), ChatColor.AQUA + "Remove"));
        menu.setItem(4, ItemCreator.createItem(Material.COMPASS, ChatColor.AQUA + "Track"));
      }
      case VIEW -> menu.setItem(2, ItemCreator.createItem(Material.LODESTONE, ChatColor.AQUA + "Save"));
    }
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(1, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "Reload your changes to the", ChatColor.WHITE + "menu with /location reload.")));
  }

  /**
   * Adds {@link LocationRegistry.SavedLocation} categories.
   */
  private void addCategories() {
    List<String> categories = locationRegistry.getLocationCategoryNames();
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
     * Remove {@link LocationRegistry.SavedLocation saved locations}.
     */
    REMOVE,

    /**
     * Track {@link LocationRegistry.SavedLocation saved locations}.
     */
    TRACK,

    /**
     * Compare {@link LocationRegistry.SavedLocation saved locations}.
     */
    COMPARE,

    /**
     * View {@link LocationRegistry.SavedLocation} categories.
     */
    VIEW
  }
}
