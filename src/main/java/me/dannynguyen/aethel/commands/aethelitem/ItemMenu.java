package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginPlayerHead;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a menu that supports categorical pagination
 * for obtaining, creating, editing, and removing items.
 *
 * @author Danny Nguyen
 * @version 1.9.9
 * @since 1.4.0
 */
public class ItemMenu {
  /**
   * AethelItem GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * GUI action.
   */
  private final ItemMenuAction action;

  /**
   * Associates an AethelItem menu with its user and action.
   *
   * @param user   user
   * @param action type of interaction
   */
  public ItemMenu(@NotNull Player user, @NotNull ItemMenuAction action) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.action = Objects.requireNonNull(action, "Null action");
    this.menu = createMenu();
  }

  /**
   * Creates and names an AethelItem menu with its action and category.
   *
   * @return AethelItem menu
   */
  private Inventory createMenu() {
    String title = ChatColor.DARK_GRAY + "Aethel Item";
    String category = ChatColor.WHITE + user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    switch (action) {
      case GET -> title += ChatColor.GREEN + " Get " + category;
      case REMOVE -> title += ChatColor.RED + " Remove " + category;
    }
    return Bukkit.createInventory(user, 54, title);
  }

  /**
   * Sets the menu to view item categories.
   *
   * @return AethelItem main menu
   */
  @NotNull
  public Inventory openMainMenu() {
    addCategories();
    addContext(null);
    addActions();
    return menu;
  }

  /**
   * Sets the menu to load an item category page.
   *
   * @return AethelItem item category page
   */
  @NotNull
  public Inventory openCategoryPage(String requestedCategory, int requestedPage) {
    List<Inventory> category = PluginData.itemRegistry.getCategoryMap().get(requestedCategory);
    int numberOfPages = category.size();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, requestedPage);
    user.setMetadata(PluginPlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), pageViewed));

    menu.setContents(category.get(pageViewed).getContents());
    addContext(requestedCategory);
    addActions();
    InventoryPages.addBackButton(menu, 6);
    InventoryPages.addPageButtons(menu, numberOfPages, pageViewed);
    return menu;
  }

  /**
   * Adds a help context to the menu.
   */
  private void addContext(String requestedCategory) {
    List<String> helpLore;
    if (requestedCategory != null) {
      helpLore = List.of(
          ChatColor.WHITE + "Place an item to",
          ChatColor.WHITE + "the right of this",
          ChatColor.WHITE + "slot to save it.");
    } else {
      helpLore = List.of(
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
    menu.setItem(2, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head, ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds save, get, and remove actions.
   */
  private void addActions() {
    switch (action) {
      case GET -> {
        menu.setItem(4, ItemCreator.createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Save"));
        menu.setItem(5, ItemCreator.createPluginPlayerHead(PluginPlayerHead.TRASH_CAN.head, ChatColor.AQUA + "Remove"));
      }
      case REMOVE -> {
        menu.setItem(4, ItemCreator.createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Save"));
        menu.setItem(5, ItemCreator.createPluginPlayerHead(PluginPlayerHead.BACKPACK_BROWN.head, ChatColor.AQUA + "Get"));
      }
      case VIEW -> menu.setItem(4, ItemCreator.createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Save"));
    }
  }

  /**
   * Adds item categories.
   */
  private void addCategories() {
    Set<String> categories = PluginData.itemRegistry.getCategoryMap().keySet();
    if (!categories.isEmpty()) {
      int i = 9;
      for (String category : categories) {
        menu.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + category));
        i++;
      }
    }
  }
}
