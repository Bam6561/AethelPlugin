package me.dannynguyen.aethel.commands.aethelItems;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.aethelItems.objects.AethelItemsCategory;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * AethelItemsInventory is an inventory that supports categorical
 * pagination for obtaining, creating, editing, and removing Aethel items.
 *
 * @author Danny Nguyen
 * @version 1.7.6
 * @since 1.4.0
 */
public class AethelItemsInventory {
  /**
   * Creates an AethelItems main menu with its buttons and item categories.
   *
   * @param user   user
   * @param action type of interaction
   * @return AethelItems main menu with item categories
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
   * @param user          user
   * @param action        type of interaction
   * @param category      category name
   * @param requestedPage requested page
   * @return AethelItems main menu with item categories
   */
  public static Inventory openCategoryPage(Player user, String action,
                                           String category, int requestedPage) {
    Inventory inv = createInventory(user, action);

    AethelItemsCategory loadedCategory = PluginData.aethelItemsData.getItemCategoriesMap().get(category);
    int numberOfPages = loadedCategory.getNumberOfPages();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, requestedPage);
    user.setMetadata(PluginPlayerMeta.Namespace.PAGE.namespace, new FixedMetadataValue(Plugin.getInstance(), pageViewed));

    inv.setContents(loadedCategory.getPages().get(pageViewed).getContents());

    addContext(category, inv);
    addActions(action, inv);
    InventoryPages.addBackButton(inv, 6);
    InventoryPages.addPageButtons(inv, numberOfPages, pageViewed);
    return inv;
  }

  /**
   * Adds a help context to the AethelItem inventory.
   *
   * @param category requested category
   * @param inv      interacting inventory
   */
  private static void addContext(String category, Inventory inv) {
    List<String> helpLore;
    if (category.equals("categories")) {
      helpLore = Arrays.asList(ChatColor.WHITE + "Item Categories",
          "",
          ChatColor.WHITE + "Place an item to",
          ChatColor.WHITE + "the right of this",
          ChatColor.WHITE + "slot to save it.");
    } else {
      helpLore = Arrays.asList(
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

    inv.setItem(2, ItemCreator.createPluginPlayerHead("WHITE_QUESTION_MARK",
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
            createPluginPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Save"));
        inv.setItem(5, ItemCreator.
            createPluginPlayerHead("TRASH_CAN", ChatColor.AQUA + "Remove"));
      }
      case "remove" -> {
        inv.setItem(4, ItemCreator.
            createPluginPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Save"));
        inv.setItem(5, ItemCreator.
            createPluginPlayerHead("BROWN_BACKPACK", ChatColor.AQUA + "Get"));
      }
      case "view" -> inv.setItem(4, ItemCreator.
          createPluginPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Save"));
    }
  }
}
