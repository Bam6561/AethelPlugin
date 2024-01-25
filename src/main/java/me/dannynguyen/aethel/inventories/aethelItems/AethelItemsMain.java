package me.dannynguyen.aethel.inventories.aethelItems;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.inventories.utility.InventoryPages;
import me.dannynguyen.aethel.objects.aethelitems.AethelItemsCategory;
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
 * AethelItemsMain is a shared inventory under the AethelItem command that supports
 * categorical pagination for getting, creating, editing, and removing Aethel items.
 *
 * @author Danny Nguyen
 * @version 1.5.4
 * @since 1.4.0
 */
public class AethelItemsMain {
  /**
   * Creates an AethelItemsMain page containing categories.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return AethelItemsMain inventory with item categories
   */
  public static Inventory openItemMainPage(Player player, String action) {
    Inventory inv = createInventory(player, action);
    addItemCategories(inv);
    addItemContext("categories", inv);
    addActionButtons("view", inv);
    return inv;
  }

  /**
   * Creates and names an AethelItemsMain inventory with its action.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return AethelItemsMain inventory
   */
  private static Inventory createInventory(Player player, String action) {
    String title = ChatColor.DARK_GRAY + "Aethel Item";
    switch (action) {
      case "get" -> title += ChatColor.GREEN + " Get " +
          ChatColor.WHITE + player.getMetadata("category").get(0).asString();
      case "remove" -> title += ChatColor.RED + " Remove " +
          ChatColor.WHITE + player.getMetadata("category").get(0).asString();
    }
    return Bukkit.createInventory(player, 54, title);
  }

  /**
   * Adds item categories.
   *
   * @param inv interacting inventory
   */
  private static void addItemCategories(Inventory inv) {
    Set<String> categoryNames = AethelResources.aethelItemsData.getItemCategoriesMap().keySet();
    if (!categoryNames.isEmpty()) {
      int i = 9;
      for (String categoryName : categoryNames) {
        inv.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + categoryName));
        i++;
      }
    }
  }

  /**
   * Loads an item category page from memory.
   *
   * @param player       interacting player
   * @param action       type of interaction
   * @param categoryName category to view
   * @param pageRequest  page to view
   * @return AethelMain inventory with items
   */
  public static Inventory openItemCategoryPage(Player player, String action,
                                               String categoryName, int pageRequest) {
    Inventory inv = createInventory(player, action);

    AethelItemsCategory itemCategory = AethelResources.aethelItemsData.getItemCategoriesMap().get(categoryName);
    int numberOfPages = itemCategory.getNumberOfPages();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, pageRequest);
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), pageViewed));

    inv.setContents(itemCategory.getPages().get(pageViewed).getContents());

    addItemContext(categoryName, inv);
    addActionButtons(action, inv);
    InventoryPages.addBackButton(inv, 6);
    InventoryPages.addPageButtons(inv, numberOfPages, pageViewed);
    return inv;
  }

  /**
   * Adds a help context to the AethelItem inventory.
   *
   * @param categoryName category to view
   * @param inv          interacting inventory
   */
  private static void addItemContext(String categoryName, Inventory inv) {
    List<String> helpLore;
    if (categoryName.equals("categories")) {
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

    inv.setItem(2, ItemCreator.createLoadedPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds save, get, and remove buttons.
   *
   * @param action type of interaction
   * @param inv    interacting inventory
   */
  private static void addActionButtons(String action, Inventory inv) {
    switch (action) {
      case "get" -> {
        inv.setItem(4, ItemCreator.
            createLoadedPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Save"));
        inv.setItem(5, ItemCreator.
            createLoadedPlayerHead("TRASH_CAN", ChatColor.AQUA + "Remove"));
      }
      case "remove" -> {
        inv.setItem(4, ItemCreator.
            createLoadedPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Save"));
        inv.setItem(5, ItemCreator.
            createLoadedPlayerHead("BROWN_BACKPACK", ChatColor.AQUA + "Get"));
      }
      case "view" -> inv.setItem(4, ItemCreator.
          createLoadedPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Save"));
    }
  }
}
