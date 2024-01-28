package me.dannynguyen.aethel.commands.aethelItems;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.aethelItems.object.AethelItemsCategory;
import me.dannynguyen.aethel.enums.PluginContext;
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

import java.util.List;
import java.util.Set;

/**
 * AethelItemsInventory is an inventory that supports categorical
 * pagination for obtaining, creating, editing, and removing Aethel items.
 *
 * @author Danny Nguyen
 * @version 1.7.13
 * @since 1.4.0
 */
public class AethelItemsInventory {
  /**
   * Creates an AethelItems main menu with its buttons and item categories.
   *
   * @param user   user
   * @param action type of interaction
   * @return AethelItems main menu
   */
  public static Inventory openMainMenu(Player user, String action) {
    Inventory inv = createInventory(user, action);
    addCategories(inv);
    addContext("categories", inv);
    addActions("view", inv);
    return inv;
  }

  /**
   * Creates and names an AethelItems inventory with its action.
   *
   * @param user   user
   * @param action type of interaction
   * @return AethelItems inventory
   */
  private static Inventory createInventory(Player user, String action) {
    String title = ChatColor.DARK_GRAY + "Aethel Items";
    switch (action) {
      case "get" -> title += ChatColor.GREEN + " Get " +
          ChatColor.WHITE + user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
      case "remove" -> title += ChatColor.RED + " Remove " +
          ChatColor.WHITE + user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    }
    return Bukkit.createInventory(user, 54, title);
  }

  /**
   * Adds item categories.
   *
   * @param inv interacting inventory
   */
  private static void addCategories(Inventory inv) {
    Set<String> categories = PluginData.aethelItemsData.getItemCategoriesMap().keySet();
    if (!categories.isEmpty()) {
      int i = 9;
      for (String category : categories) {
        inv.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + category));
        i++;
      }
    }
  }

  /**
   * Loads an item category page from memory.
   *
   * @param user              user
   * @param action            type of interaction
   * @param requestedCategory requested category
   * @param requestedPage     requested page
   * @return AethelItems category page
   */
  public static Inventory openCategoryPage(Player user, String action,
                                           String requestedCategory, int requestedPage) {
    Inventory inv = createInventory(user, action);

    AethelItemsCategory category = PluginData.aethelItemsData.getItemCategoriesMap().get(requestedCategory);
    int numberOfPages = category.getNumberOfPages();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, requestedPage);
    user.setMetadata(PluginPlayerMeta.Namespace.PAGE.namespace,
        new FixedMetadataValue(Plugin.getInstance(), pageViewed));

    inv.setContents(category.getPages().get(pageViewed).getContents());

    addContext(requestedCategory, inv);
    addActions(action, inv);
    InventoryPages.addBackButton(inv, 6);
    InventoryPages.addPageButtons(inv, numberOfPages, pageViewed);
    return inv;
  }

  /**
   * Adds a help context to the AethelItem inventory.
   *
   * @param requestedCategory requested category
   * @param inv               interacting inventory
   */
  private static void addContext(String requestedCategory, Inventory inv) {
    List<String> helpLore;
    if (requestedCategory.equals("categories")) {
      helpLore = PluginContext.AETHELITEM_CATEGORIES.context;
    } else {
      helpLore = PluginContext.AETHELITEM_CATEGORY_PAGE.context;
    }

    inv.setItem(2, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds save, get, and remove actions.
   *
   * @param action type of interaction
   * @param inv    interacting inventory
   */
  private static void addActions(String action, Inventory inv) {
    switch (action) {
      case "get" -> {
        inv.setItem(4, ItemCreator.
            createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Save"));
        inv.setItem(5, ItemCreator.
            createPluginPlayerHead(PluginPlayerHead.TRASH_CAN.head, ChatColor.AQUA + "Remove"));
      }
      case "remove" -> {
        inv.setItem(4, ItemCreator.
            createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Save"));
        inv.setItem(5, ItemCreator.
            createPluginPlayerHead(PluginPlayerHead.BACKPACK_BROWN.head, ChatColor.AQUA + "Get"));
      }
      case "view" -> inv.setItem(4, ItemCreator.
          createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Save"));
    }
  }
}
