package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.ForgeRecipe;
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
 * ForgeMain is a shared menu under the Forge command for crafting, modifying, and deleting forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.0.9
 * @since 1.0.6
 */
public class ForgeMain {
  /**
   * Creates the default view for the Forge-Main menu.
   *
   * @param player interacting player
   * @return Forge-Main default view
   */
  public Inventory createDefaultView(Player player) {
    Inventory craftMenu = Bukkit.createInventory(player, 54, "Forge");
    craftMenu.setItem(0, createItem(Material.RED_WOOL, "Previous Page"));
    for (int i = 1; i < 8; i++) {
      craftMenu.setItem(i, createItem(Material.BLACK_STAINED_GLASS_PANE, ""));
    }
    craftMenu.setItem(8, createItem(Material.GREEN_WOOL, "Next Page"));
    return craftMenu;
  }

  /**
   * Creates a named item.
   *
   * @param material    item material
   * @param displayName item name
   * @return named item
   */
  private ItemStack createItem(Material material, String displayName) {
    ItemStack item = new ItemStack(material, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Populates the default Forge-Main menu with recipes loaded from memory.
   *
   * @param player        interacting player
   * @param pageRequested recipe page to view
   */
  public Inventory populateView(Player player, int pageRequested) {
    Inventory view = new ForgeMain().createDefaultView(player);
    ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>(AethelPlugin.getInstance().getForgeRecipes());

    int numberOfRecipes = forgeRecipes.size();
    int numberOfPages = calculateNumberOfPages(numberOfRecipes);
    int viewPageNumber = calculateViewPageNumber(pageRequested, numberOfPages);
    int startIndexOnPage = calculateViewPageStartIndex(numberOfRecipes, viewPageNumber);
    int endIndexOnPage = Math.min(startIndexOnPage + 20, numberOfRecipes);

    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), viewPageNumber));
    return createViewPage(view, forgeRecipes, startIndexOnPage, endIndexOnPage);
  }

  /**
   * Determines how many pages of recipes exist and whether there are partially filled pages.
   *
   * @param numberOfRecipes number of recipes
   * @return number of recipe pages
   */
  private int calculateNumberOfPages(int numberOfRecipes) {
    int numberOfPages = numberOfRecipes / 20;
    boolean partiallyFilledPage = (numberOfRecipes % 20) > 0;
    if (partiallyFilledPage) {
      numberOfPages += 1;
    }
    return numberOfPages;
  }

  /**
   * Determines which page to be viewed.
   *
   * @param pageRequested recipe page to view
   * @param numberOfPages number of recipe pages
   * @return interpreted recipe page to view
   */
  private int calculateViewPageNumber(int pageRequested, int numberOfPages) {
    boolean requestMoreThanTotalPages = pageRequested >= numberOfPages;
    boolean requestNegativePageNumber = pageRequested < 0;
    if (requestMoreThanTotalPages) {
      pageRequested = numberOfPages - 1;
    } else if (requestNegativePageNumber) {
      pageRequested = 0;
    }
    return pageRequested;
  }

  /**
   * Determines which recipe index to begin displaying on page.
   *
   * @param numberOfRecipes number of recipes
   * @param pageRequested   recipe page to view
   * @return starting recipe index on page
   */
  private int calculateViewPageStartIndex(int numberOfRecipes, int pageRequested) {
    int startIndex = pageRequested * 20;
    if (startIndex == numberOfRecipes) {
      startIndex -= 20;
    }
    return startIndex;
  }

  /**
   * Creates the Forge-Main page the player wishes to view.
   *
   * @param view       items in the inventory
   * @param recipes    recipes
   * @param startIndex starting index
   * @param endIndex   ending index
   * @return Forge-Main menu page
   */
  private Inventory createViewPage(Inventory view, ArrayList<ForgeRecipe> recipes, int startIndex, int endIndex) {
    // i = recipes index
    // j = inventory slot index

    // Skip first inventory row
    int j = 9;
    for (int i = startIndex; i < endIndex; i++) {
      //Skip end inventory row slots
      if (j == 17 || j == 26 || j == 35 || j == 44) j++;

      ForgeRecipe forgeRecipe = recipes.get(i);
      ArrayList<ItemStack> results = forgeRecipe.getResults();
      ArrayList<ItemStack> components = forgeRecipe.getComponents();

      view.setItem(j, createItemDetails(results.get(0), results, false));
      view.setItem(j + 1, createItemDetails(new ItemStack(Material.PAPER), components, true));
      j += 2;
    }
    return view;
  }

  /**
   * Creates expanded item details on a display item showing either the results or components of a recipe.
   * <p>
   * Format:
   * [Amount] Material
   * ...
   * </p>
   *
   * @param displayItem      item to be shown
   * @param relatedItems     results or components of the recipe
   * @param renameComponents components of the recipe
   * @return item labelled with its results of components
   */
  private ItemStack createItemDetails(ItemStack displayItem, ArrayList<ItemStack> relatedItems, boolean renameComponents) {
    List<String> itemDetails = new ArrayList<>();
    for (ItemStack item : relatedItems) {
      int amount = item.getAmount();
      String itemName = getItemName(item);
      itemDetails.add(ChatColor.AQUA + "x" + amount + ChatColor.WHITE + " " + itemName);
    }

    ItemStack finalItem = new ItemStack(displayItem.getType(), 1);
    ItemMeta meta = finalItem.getItemMeta();
    if (renameComponents) meta.setDisplayName(ChatColor.RESET + "Components");
    meta.setLore(itemDetails);
    finalItem.setItemMeta(meta);
    return finalItem;
  }

  /**
   * Returns either an item's renamed value or its material.
   *
   * @param item item
   * @return effective item name
   */
  private String getItemName(ItemStack item) {
    if (item.getItemMeta().hasDisplayName()) {
      return item.getItemMeta().getDisplayName();
    } else {
      return item.getType().name();
    }
  }
}
