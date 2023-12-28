package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ForgeMain is a shared inventory under the Forge command that supports
 * pagination for crafting, modifying, and deleting forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.11
 * @since 1.0.6
 */
public class ForgeMain {
  /**
   * Creates and names a ForgeMain inventory.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return ForgeMain inventory
   */
  private Inventory createInventory(Player player, String action) {
    String title = ChatColor.DARK_GRAY + "Forge";
    switch (action) {
      case "craft" -> title += ChatColor.BLUE + " Craft";
      case "modify" -> title += ChatColor.YELLOW + " Modify";
      case "delete" -> title += ChatColor.RED + " Delete";
    }
    Inventory inv = Bukkit.createInventory(player, 54, title);
    return inv;
  }


  /**
   * Loads a recipe page from memory.
   *
   * @param player      interacting player
   * @param action      type of interaction
   * @param pageRequest page to view
   * @return ForgeMain inventory with recipes
   */
  public Inventory openRecipePage(Player player, String action, int pageRequest) {
    AethelResources resources = AethelPlugin.getInstance().getResources();

    int numberOfPages = resources.getForgeRecipeData().getNumberOfPages();
    int pageViewed = calculatePageViewed(pageRequest, numberOfPages);

    // Load recipe page from memory
    Inventory inv = new ForgeMain().createInventory(player, action);
    inv.setContents(resources.getForgeRecipeData().getRecipePages().get(pageViewed).getContents());

    addPaginationButtons(inv, pageViewed, numberOfPages);
    addActionButtons(inv, action);

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
   * Adds previous and next page buttons based on the page number.
   *
   * @param inv           interacting inventory
   * @param pageViewed    page viewed
   * @param numberOfPages number of recipe pages
   */
  private void addPaginationButtons(Inventory inv, int pageViewed, int numberOfPages) {
    if (pageViewed > 0) {
      inv.setItem(0, new ItemCreator().createItem(Material.RED_WOOL, "Previous Page"));
    }
    if (numberOfPages - 1 > pageViewed) {
      inv.setItem(8, new ItemCreator().createItem(Material.GREEN_WOOL, "Next Page"));
    }
  }

  /**
   * Adds create, modify, and delete recipe buttons.
   *
   * @param inv    interacting inventory
   * @param action type of interaction
   */
  private void addActionButtons(Inventory inv, String action) {
    ItemCreator itemCreator = new ItemCreator();
    switch (action) {
      case "modify" -> {
        inv.setItem(3, itemCreator.createItem(Material.GREEN_CONCRETE, "Create Recipe"));
        inv.setItem(5, itemCreator.createItem(Material.RED_CONCRETE, "Delete Recipe"));
      }
      case "delete" -> {
        inv.setItem(3, itemCreator.createItem(Material.GREEN_CONCRETE, "Create Recipe"));
        inv.setItem(4, itemCreator.createItem(Material.YELLOW_CONCRETE, "Modify Recipe"));
      }
    }
  }
}
