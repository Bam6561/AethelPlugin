package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.readers.ForgeRecipeReader;
import me.dannynguyen.aethel.readers.ItemMetaReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

/**
 * ForgeRecipeData contains information about forge recipes loaded in memory.
 *
 * @author Danny Nguyen
 * @version 1.2.1
 * @since 1.1.11
 */
public class ForgeRecipeData {
  private ArrayList<ForgeRecipe> recipes = new ArrayList<>();
  private HashMap<String, ForgeRecipe> recipesMap = new HashMap<>();
  private ArrayList<Inventory> recipePages = new ArrayList<>();
  private int numberOfPages = 0;

  /**
   * (Re)loads forge recipes into memory.
   */
  public void loadRecipes() {
    ArrayList<ForgeRecipe> recipes = getRecipes();
    HashMap<String, ForgeRecipe> recipesMap = getRecipesMap();

    recipes.clear();
    recipesMap.clear();
    getRecipePages().clear();
    setNumberOfPages(0);

    File[] directory = new File(AethelPlugin.getInstance().getResources().getForgeRecipeDirectory()).listFiles();
    Collections.sort(Arrays.asList(directory));
    for (int i = 0; i < directory.length; i++) {
      ForgeRecipe recipe = new ForgeRecipeReader().readRecipe(directory[i]);
      recipes.add(recipe);
      recipesMap.put(recipe.getName(), recipe);
    }
    createRecipePages();
  }

  /**
   * Creates pages of recipes.
   */
  public void createRecipePages() {
    int numberOfRecipes = getRecipes().size();
    int numberOfPages = calculateNumberOfPages(numberOfRecipes);
    setNumberOfPages(numberOfPages);

    int startIndex = 0;
    int endIndex = Math.min(numberOfRecipes, 45);

    for (int page = 0; page < numberOfPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54, "Forge Recipe Page");
      // i = recipes index
      // j = inventory slot index

      // Recipes begin on the second row
      int j = 9;
      for (int i = startIndex; i < endIndex; i++) {
        ArrayList<ItemStack> results = recipes.get(i).getResults();
        inv.setItem(j, createResultsDisplay(results.get(0), results));
        j++;
      }
      getRecipePages().add(inv);

      // Indices to use for the next page (if it exists)
      startIndex += 45;
      endIndex = Math.min(numberOfRecipes, endIndex + 45);
    }
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
        recipeResults.add(ChatColor.AQUA + "x" + item.getAmount() +
            ChatColor.WHITE + " " + metaReader.getItemName(item));
      }

      ItemStack itemDisplay = displayItem.clone();
      ItemMeta meta = itemDisplay.getItemMeta();
      meta.setLore(recipeResults);
      itemDisplay.setItemMeta(meta);
      return itemDisplay;
    } else {
      return displayItem;
    }
  }

  public ArrayList<ForgeRecipe> getRecipes() {
    return this.recipes;
  }

  public HashMap<String, ForgeRecipe> getRecipesMap() {
    return this.recipesMap;
  }

  public ArrayList<Inventory> getRecipePages() {
    return this.recipePages;
  }

  public int getNumberOfPages() {
    return this.numberOfPages;
  }

  private void setNumberOfPages(int numberOfForgeRecipePages) {
    this.numberOfPages = numberOfForgeRecipePages;
  }
}
