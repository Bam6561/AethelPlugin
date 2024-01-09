package me.dannynguyen.aethel.inventories.aethelItem;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.inventories.PageCalculator;
import me.dannynguyen.aethel.objects.AethelItemCategory;
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
 * AethelItemMain is a shared inventory under the AethelItem command that
 * supports pagination for getting, creating, modifying, and deleting items.
 *
 * @author Danny Nguyen
 * @version 1.5.0
 * @since 1.4.0
 */
public class AethelItemMain {
  /**
   * Creates an AethelItemMain page containing categories.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return AethelItemMain inventory with item categories
   */
  public static Inventory openItemMainPage(Player player, String action) {
    Inventory inv = createInventory(player, action);
    addItemCategories(inv);
    addItemContext("categories", inv);
    addActionButtons(inv, "view");
    return inv;
  }

  /**
   * Creates and names an AethelItemMain inventory with its action.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return AethelItemMain inventory
   */
  private static Inventory createInventory(Player player, String action) {
    String title = ChatColor.DARK_GRAY + "Aethel Item";
    switch (action) {
      case "get" -> title += ChatColor.GREEN + " Get " +
          ChatColor.WHITE + player.getMetadata("category").get(0).asString();
      case "delete" -> title += ChatColor.RED + " Delete " +
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
    Set<String> categoryNames = AethelResources.aethelItemData.getItemCategoriesMap().keySet();
    if (!categoryNames.isEmpty()) {
      int i = 9;
      for (String categoryName : categoryNames) {
        inv.setItem(i, ItemCreator.createItem(Material.BOOK,
            ChatColor.WHITE + capitalizeProperly(categoryName)));
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

    AethelItemCategory itemCategory = AethelResources.aethelItemData.getItemCategoriesMap().get(categoryName);
    int numberOfPages = itemCategory.getNumberOfPages();
    int pageViewed = PageCalculator.calculatePageViewed(numberOfPages, pageRequest);
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), pageViewed));

    inv.setContents(itemCategory.getPages().get(pageViewed).getContents());

    addItemContext(categoryName, inv);
    addActionButtons(inv, action);
    addBackButton(inv);
    addPageButtons(inv, numberOfPages, pageViewed);
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
          ChatColor.WHITE + "Get and Delete modes by",
          ChatColor.WHITE + "clicking on their button.",
          "",
          ChatColor.WHITE + "To undo a deletion,",
          ChatColor.WHITE + "get the item and save",
          ChatColor.WHITE + "it before reloading.");
    }

    inv.setItem(2, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds save, get, and delete buttons.
   *
   * @param inv    interacting inventory
   * @param action type of interaction
   */
  private static void addActionButtons(Inventory inv, String action) {
    switch (action) {
      case "get" -> {
        inv.setItem(4, ItemCreator.
            createPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Save"));
        inv.setItem(5, ItemCreator.
            createPlayerHead("TRASH_CAN", ChatColor.AQUA + "Delete"));
      }
      case "delete" -> {
        inv.setItem(4, ItemCreator.
            createPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Save"));
        inv.setItem(5, ItemCreator.
            createPlayerHead("BROWN_BACKPACK", ChatColor.AQUA + "Get"));
      }
      case "view" -> inv.setItem(4, ItemCreator.
          createPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Save"));
    }
  }

  /**
   * Returns the player to the stat categories page.
   *
   * @param inv interacting inventory
   */
  private static void addBackButton(Inventory inv) {
    inv.setItem(6, ItemCreator.createPlayerHead("CHISELED_BOOKSHELF",
        ChatColor.AQUA + "Back"));
  }

  /**
   * Adds previous and next page buttons based on the page number.
   *
   * @param inv           interacting inventory
   * @param numberOfPages number of pages
   * @param pageViewed    page viewed
   */
  private static void addPageButtons(Inventory inv, int numberOfPages, int pageViewed) {
    if (pageViewed > 0) {
      inv.setItem(0, ItemCreator.
          createPlayerHead("RED_BACKWARD", ChatColor.AQUA + "Previous Page"));
    }
    if (numberOfPages - 1 > pageViewed) {
      inv.setItem(8, ItemCreator.
          createPlayerHead("LIME_FORWARD", ChatColor.AQUA + "Next Page"));
    }
  }

  /**
   * Capitalizes the first character of every word.
   *
   * @param phrase phrase
   * @return proper phrase
   */
  private static String capitalizeProperly(String phrase) {
    phrase = phrase.replace("_", " ");
    String[] words = phrase.split(" ");

    StringBuilder properPhrase = new StringBuilder();
    for (String word : words) {
      properPhrase.append(word.replace(word.substring(1), word.substring(1).toLowerCase()) + " ");
    }
    return properPhrase.toString().trim();
  }
}
