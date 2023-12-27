package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemMetaReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

/**
 * ForgeMain is a shared inventory under the Forge command that supports
 * pagination for crafting, modifying, and deleting forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.5
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
    return inv;
  }

  /**
   * Determines which page of recipes to view.
   *
   * @param player      interacting player
   * @param action      type of interaction
   * @param pageRequest page to view
   * @return ForgeMain inventory with recipes
   */
  public Inventory processPageToDisplay(Player player, String action, int pageRequest) {
    Inventory inv = new ForgeMain().createInventory(player, action);
    ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>(AethelPlugin.getInstance().getForgeRecipes());

    int numberOfRecipes = forgeRecipes.size();
    int numberOfPages = calculateNumberOfPages(numberOfRecipes);
    int viewPageNumber = calculateViewPageNumber(pageRequest, numberOfPages);
    int startIndex = calculateStartIndex(numberOfRecipes, viewPageNumber);
    int endIndex = Math.min(startIndex + 45, numberOfRecipes);

    // Add previous and next page buttons
    if (viewPageNumber > 0) {
      inv.setItem(0, new ItemCreator().createItem(Material.RED_WOOL, "Previous Page"));
    }
    if (numberOfPages - 1 > viewPageNumber) {
      inv.setItem(8, new ItemCreator().createItem(Material.GREEN_WOOL, "Next Page"));
    }

    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), viewPageNumber));
    return loadRecipesIntoInventory(inv, forgeRecipes, startIndex, endIndex);
  }

  /**
   * Determines how many pages of recipes exist and whether there are partially filled pages.
   *
   * @param numberOfRecipes number of recipes
   * @return number of pages
   */
  private int calculateNumberOfPages(int numberOfRecipes) {
    int numberOfPages = numberOfRecipes / 45;
    boolean partiallyFilledPage = (numberOfRecipes % 45) > 0;
    if (partiallyFilledPage) numberOfPages += 1;
    return numberOfPages;
  }

  /**
   * Determines which page is viewed.
   *
   * @param pageRequest   page to view
   * @param numberOfPages number of pages
   * @return interpreted page to view
   */
  private int calculateViewPageNumber(int pageRequest, int numberOfPages) {
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
   * Determines which recipe index to begin displaying on the page.
   *
   * @param numberOfRecipes number of recipes
   * @param pageRequest     page to view
   * @return starting recipe index on page
   */
  private int calculateStartIndex(int numberOfRecipes, int pageRequest) {
    int startIndex = pageRequest * 45;
    if (startIndex == numberOfRecipes) {
      startIndex -= 45;
    }
    return startIndex;
  }

  /**
   * Loads recipes into the ForgeMain inventory from memory.
   *
   * @param inv        inventory
   * @param recipes    recipes loaded from memory
   * @param startIndex starting recipe index
   * @param endIndex   ending recipe index
   * @return ForgeMain inventory with recipes
   */
  private Inventory loadRecipesIntoInventory(Inventory inv, ArrayList<ForgeRecipe> recipes,
                                             int startIndex, int endIndex) {
    // i = recipes index
    // j = inventory slot index

    // Recipes begin on the second row
    int j = 9;
    for (int i = startIndex; i < endIndex; i++) {
      ForgeRecipe forgeRecipe = recipes.get(i);
      ArrayList<ItemStack> results = forgeRecipe.getResults();

      inv.setItem(j, createResultsDisplay(results.get(0), results));
      j++;
    }
    return inv;
  }

  /**
   * Creates an item display for recipes with multiple results.
   * <p>
   * Format:
   * x<Amount> Item
   * ...
   * </p>
   *
   * @param displayItem item to be shown
   * @param results     recipe results
   * @return display item labeled with its result(s)
   */
  private ItemStack createResultsDisplay(ItemStack displayItem, ArrayList<ItemStack> results) {
    if (results.size() > 1) {
      List<String> recipeResults = new ArrayList<>();
      ItemMetaReader metaReader = new ItemMetaReader();
      for (ItemStack item : results) {
        int amount = item.getAmount();
        String itemName = metaReader.getItemName(item);
        recipeResults.add(ChatColor.AQUA + "x" + amount + ChatColor.WHITE + " " + itemName);
      }

      ItemStack resultDisplay = new ItemStack(displayItem.getType());
      ItemMeta meta = resultDisplay.getItemMeta();
      meta.setDisplayName(metaReader.getItemName(displayItem));
      meta.setLore(recipeResults);
      resultDisplay.setItemMeta(meta);
      return resultDisplay;
    } else {
      return displayItem;
    }
  }
}
