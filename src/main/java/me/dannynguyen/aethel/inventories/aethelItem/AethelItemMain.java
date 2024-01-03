package me.dannynguyen.aethel.inventories.aethelItem;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
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
 * @version 1.4.0
 * @since 1.4.0
 */
public class AethelItemMain {
  /**
   * Creates and names an AethelItemMain inventory.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return AethelItemMain inventory
   */
  private Inventory createInventory(Player player, String action) {
    String title = ChatColor.DARK_GRAY + "Aethel Item";
    switch (action) {
      case "get" -> title += ChatColor.GREEN + " Get";
      case "delete" -> title += ChatColor.RED + " Delete";
    }
    Inventory inv = Bukkit.createInventory(player, 54, title);
    return inv;
  }

  /**
   * Loads an item page from memory.
   *
   * @param player      interacting player
   * @param action      type of interaction
   * @param pageRequest page to view
   * @return AethelMain inventory with items
   */
  public Inventory openItemPage(Player player, String action, int pageRequest) {
    Inventory inv = new AethelItemMain().createInventory(player, action);

    AethelResources resources = AethelPlugin.getInstance().getResources();

    int numberOfPages = resources.getAethelItemData().getNumberOfPages();
    int pageViewed;
    if (numberOfPages != 0) {
      pageViewed = calculatePageViewed(pageRequest, numberOfPages);
      inv.setContents(resources.getAethelItemData().getItemPages().get(pageViewed).getContents());
      addPaginationButtons(inv, pageViewed, numberOfPages);
    } else {
      pageViewed = 0;
    }

    addEditorHelp(inv);
    addActionButton(inv, action);

    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), pageViewed));
    return inv;
  }

  /**
   * Determines which page is viewed.
   *
   * @param pageRequest   page to view
   * @param numberOfPages number of pages
   * @return interpreted page to view
   */
  private int calculatePageViewed(int pageRequest, int numberOfPages) {
    boolean requestMoreThanTotalPages = pageRequest >= numberOfPages;
    boolean requestNegativePageNumber = pageRequest < 0;
    if (requestMoreThanTotalPages) {
      pageRequest = numberOfPages - 1;
    } else if (requestNegativePageNumber) {
      pageRequest = 0;
    }
    return pageRequest;
  }

  /**
   * Adds a help context to the editor.
   *
   * @param inv interacting inventory
   */
  private void addEditorHelp(Inventory inv) {
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

    inv.setItem(2, new ItemCreator().createPlayerHead("White Question Mark",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds previous and next page buttons based on the page number.
   *
   * @param inv           interacting inventory
   * @param pageViewed    page viewed
   * @param numberOfPages number of recipe pages
   */
  private void addPaginationButtons(Inventory inv, int pageViewed, int numberOfPages) {
    if (pageViewed > 0) {
      inv.setItem(0, new ItemCreator().
          createPlayerHead("Red Backward", ChatColor.AQUA + "Previous Page"));
    }
    if (numberOfPages - 1 > pageViewed) {
      inv.setItem(8, new ItemCreator().
          createPlayerHead("Lime Forward", ChatColor.AQUA + "Next Page"));
    }
  }

  /**
   * Adds save, get, and delete buttons.
   *
   * @param inv    interacting inventory
   * @param action type of interaction
   */
  private void addActionButton(Inventory inv, String action) {
    switch (action) {
      case "get" -> {
        ItemCreator itemCreator = new ItemCreator();
        inv.setItem(4, itemCreator.
            createPlayerHead("Crafting Table", ChatColor.AQUA + "Save"));
        inv.setItem(5, itemCreator.
            createPlayerHead("Trash Can", ChatColor.AQUA + "Delete"));
      }
      case "delete" -> {
        ItemCreator itemCreator = new ItemCreator();
        inv.setItem(4, itemCreator.
            createPlayerHead("Crafting Table", ChatColor.AQUA + "Save"));
        inv.setItem(5, itemCreator.
            createPlayerHead("Brown Backpack", ChatColor.AQUA + "Get"));
      }
    }
  }
}
