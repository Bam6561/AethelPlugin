package me.dannynguyen.aethel;

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
 * AethelResources represents the plugin's resources loaded in memory as an object.
 *
 * @author Danny Nguyen
 * @version 1.1.10
 * @since 1.1.7
 */
public class AethelResources {
  private String resourceDirectory = "./plugins/Aethel";
  private String forgeRecipeDirectory = resourceDirectory + "/forge";
  private ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>();
  private HashMap<String, ForgeRecipe> forgeRecipesMap = new HashMap<>();
  private ArrayList<Inventory> forgeRecipePages = new ArrayList<>();
  private int numberOfForgeRecipePages = 0;

  /**
   * (Re)loads forge recipes into memory.
   */
  public void loadForgeRecipes() {
    ArrayList<ForgeRecipe> forgeRecipes = getForgeRecipes();
    HashMap<String, ForgeRecipe> forgeRecipesMap = getForgeRecipesMap();
    ArrayList<Inventory> forgeRecipePages = getForgeRecipePages();
    forgeRecipes.clear();
    forgeRecipesMap.clear();
    forgeRecipePages.clear();
    setNumberOfForgeRecipePages(0);

    File[] forgeRecipeDirectory = new File(getForgeRecipeDirectory()).listFiles();
    Collections.sort(Arrays.asList(forgeRecipeDirectory));
    for (int i = 0; i < forgeRecipeDirectory.length; i++) {
      ForgeRecipe forgeRecipe = new ForgeRecipeReader().readRecipe(forgeRecipeDirectory[i]);
      forgeRecipes.add(forgeRecipe);
      forgeRecipesMap.put(forgeRecipe.getRecipeName(), forgeRecipe);
    }
    createForgeRecipePages();
  }

  /**
   * Creates pages of recipes.
   */
  public void createForgeRecipePages() {
    int numberOfRecipes = getForgeRecipes().size();
    int numberOfPages = calculateNumberOfPages(numberOfRecipes);
    setNumberOfForgeRecipePages(numberOfPages);

    int startIndex = 0;
    int endIndex = Math.min(numberOfRecipes, 45);

    for (int page = 0; page < numberOfPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54, "Forge Recipe Page");

      // i = recipes index
      // j = inventory slot index

      // Recipes begin on the second row
      int j = 9;
      for (int i = startIndex; i < endIndex; i++) {
        ArrayList<ItemStack> results = forgeRecipes.get(i).getResults();
        inv.setItem(j, createResultsDisplay(results.get(0), results));
        j++;
      }
      startIndex += 45;
      endIndex = Math.min(numberOfRecipes, endIndex + 45);
      getForgeRecipePages().add(inv);
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


  public String getResourceDirectory() {
    return this.resourceDirectory;
  }

  public String getForgeRecipeDirectory() {
    return this.forgeRecipeDirectory;
  }

  public ArrayList<ForgeRecipe> getForgeRecipes() {
    return this.forgeRecipes;
  }

  public HashMap<String, ForgeRecipe> getForgeRecipesMap() {
    return this.forgeRecipesMap;
  }

  public ArrayList<Inventory> getForgeRecipePages() {
    return this.forgeRecipePages;
  }

  public int getNumberOfForgeRecipePages() {
    return this.numberOfForgeRecipePages;
  }

  private void setNumberOfForgeRecipePages(int numberOfForgeRecipePages) {
    this.numberOfForgeRecipePages = numberOfForgeRecipePages;
  }
}
