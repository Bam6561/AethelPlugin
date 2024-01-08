package me.dannynguyen.aethel.inventories.aethelItem;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.data.AethelItemData;
import me.dannynguyen.aethel.inventories.PageCalculator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

/**
 * AethelItemMain is a shared inventory under the AethelItem command that
 * supports pagination for getting, creating, modifying, and deleting items.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.4.0
 */
public class AethelItemMain {
  /**
   * Loads an item page from memory.
   *
   * @param player      interacting player
   * @param action      type of interaction
   * @param pageRequest page to view
   * @return AethelMain inventory with items
   */
  public static Inventory openItemPage(Player player, String action, int pageRequest) {
    AethelItemData itemData = AethelResources.aethelItemData;

    int numberOfPages = itemData.getNumberOfPages();
    int pageViewed = PageCalculator.calculatePageViewed(numberOfPages, pageRequest);
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), pageViewed));

    Inventory inv = createInventory(player, action);
    if (numberOfPages > 0) {
      inv.setContents(itemData.getItemPages().get(pageViewed).getContents());
      addPageButtons(inv, numberOfPages, pageViewed);
    }
    addItemContext(inv);
    addActionButton(inv, action);
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
      case "get" -> title += ChatColor.GREEN + " Get";
      case "delete" -> title += ChatColor.RED + " Delete";
    }
    return Bukkit.createInventory(player, 54, title);
  }

  /**
   * Adds a help context to the AethelItem inventory.
   *
   * @param inv interacting inventory
   */
  private static void addItemContext(Inventory inv) {
    List<String> helpLore = Arrays.asList(
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

    inv.setItem(2, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
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
   * Adds save, get, and delete buttons.
   *
   * @param inv    interacting inventory
   * @param action type of interaction
   */
  private static void addActionButton(Inventory inv, String action) {
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
    }
  }
}
