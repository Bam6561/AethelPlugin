package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.commands.forge.object.ForgeRecipe;
import me.dannynguyen.aethel.commands.forge.object.ForgeRecipeCategory;
import me.dannynguyen.aethel.enums.PluginDirectory;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * ForgeRecipeData stores forge recipes in memory.
 *
 * @author Danny Nguyen
 * @version 1.8.2
 * @since 1.1.11
 */
public class ForgeData {
  private final Map<String, ForgeRecipe> recipesMap = new HashMap<>();
  private final Map<String, ForgeRecipeCategory> recipeCategoriesMap = new HashMap<>();

  /**
   * Loads forge recipes into memory.
   */
  public void loadRecipes() {
    File[] directory = PluginDirectory.FORGE.file.listFiles();
    if (directory != null) {
      Arrays.sort(directory);

      recipesMap.clear();
      recipeCategoriesMap.clear();

      List<ForgeRecipe> allRecipes = new ArrayList<>();
      Map<String, List<ForgeRecipe>> sortedRecipes = new HashMap<>();
      NamespacedKey categoryKey = PluginNamespacedKey.FORGE_CATEGORY.namespacedKey;

      for (File file : directory) {
        if (file.getName().endsWith("_rcp.txt")) {
          ForgeRecipe recipe = readRecipeFile(file);
          recipesMap.put(recipe.getName(), recipe);
          allRecipes.add(recipe);
          sortRecipes(recipe, categoryKey, sortedRecipes);
        }
      }

      if (!recipesMap.isEmpty()) {
        createAllRecipePages(allRecipes, recipeCategoriesMap);
        createRecipeCategoryPages(categoryKey, sortedRecipes, recipeCategoriesMap);
      }
    }
  }

  /**
   * Reads a recipe file.
   * <p>
   * Data is stored in two lines of text, represented by the variable dataType.
   * - [1] Results
   * - [2] Components
   * </p>
   *
   * @param file recipe file
   * @return decoded recipe
   * @throws FileNotFoundException file not found
   */
  private ForgeRecipe readRecipeFile(File file) {
    List<ItemStack> results = new ArrayList<>();
    List<ItemStack> components = new ArrayList<>();
    int dataType = 1;

    try {
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        readLine(scanner.nextLine(), dataType, results, components);
        dataType++;
      }
      return new ForgeRecipe(file, ItemReader.readName(results.get(0)), results, components);
    } catch (FileNotFoundException ex) {
      return null;
    }
  }

  /**
   * Reads a line of text from the file and adds decoded items to the recipe.
   * <p>
   * Individual encoded items are separated by spaces.
   * </p>
   *
   * @param line       text line
   * @param dataType   [1] Results | [2] Components
   * @param results    recipe results
   * @param components recipe components
   */
  private void readLine(String line, int dataType,
                        List<ItemStack> results, List<ItemStack> components) {
    String[] data = line.split(" ");
    for (String encodedItem : data) {
      ItemStack item = ItemReader.decodeItem(encodedItem);
      if (item != null) {
        switch (dataType) {
          case 1 -> results.add(item);
          case 2 -> components.add(item);
        }
      }
    }
  }

  /**
   * Puts a recipe into a category if its first result item has a forge category tag.
   *
   * @param recipe        interacting recipe
   * @param categoryKey   forge category tag
   * @param sortedRecipes recipes sorted by category
   */
  private void sortRecipes(ForgeRecipe recipe, NamespacedKey categoryKey,
                           Map<String, List<ForgeRecipe>> sortedRecipes) {
    ItemStack item = recipe.getResults().get(0);
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();

    if (dataContainer.has(categoryKey, PersistentDataType.STRING)) {
      String recipeCategory = dataContainer.get(categoryKey, PersistentDataType.STRING);

      if (sortedRecipes.containsKey(recipeCategory)) {
        sortedRecipes.get(recipeCategory).add(recipe);
      } else {
        List<ForgeRecipe> recipes = new ArrayList<>();
        recipes.add(recipe);
        sortedRecipes.put(recipeCategory, recipes);
      }
    }
  }

  /**
   * Creates pages of all recipes, regardless of category.
   *
   * @param allRecipes          all recipes
   * @param recipeCategoriesMap recipe category pages
   */
  private void createAllRecipePages(List<ForgeRecipe> allRecipes,
                                    Map<String, ForgeRecipeCategory> recipeCategoriesMap) {
    int numberOfRecipes = allRecipes.size();
    int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfRecipes);

    List<Inventory> pages = createRecipePages(allRecipes, numberOfRecipes, numberOfPages);

    recipeCategoriesMap.put("All", new ForgeRecipeCategory("All", pages, numberOfPages));
  }


  /**
   * Creates pages of recipes by category.
   *
   * @param forgeCategoryKey    forge category tag
   * @param sortedRecipes       recipes sorted by category
   * @param recipeCategoriesMap recipe category pages
   */
  private void createRecipeCategoryPages(NamespacedKey forgeCategoryKey,
                                         Map<String, List<ForgeRecipe>> sortedRecipes,
                                         Map<String, ForgeRecipeCategory> recipeCategoriesMap) {
    for (List<ForgeRecipe> recipes : sortedRecipes.values()) {
      int numberOfRecipes = recipes.size();
      int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfRecipes);

      List<Inventory> pages = createRecipePages(recipes, numberOfRecipes, numberOfPages);

      PersistentDataContainer dataContainer = recipes.get(0).getResults().
          get(0).getItemMeta().getPersistentDataContainer();
      String recipeCategory = dataContainer.get(forgeCategoryKey, PersistentDataType.STRING);

      recipeCategoriesMap.put(recipeCategory, new ForgeRecipeCategory(recipeCategory, pages, numberOfPages));
    }
  }

  /**
   * Creates pages of recipes.
   *
   * @param recipes         recipes
   * @param numberOfRecipes number of recipes
   * @param numberOfPages   number of pages
   * @return pages of recipes
   */
  private List<Inventory> createRecipePages(List<ForgeRecipe> recipes,
                                            int numberOfRecipes, int numberOfPages) {
    int startIndex = 0;
    int endIndex = Math.min(numberOfRecipes, 45);

    List<Inventory> pages = new ArrayList<>();
    for (int page = 0; page < numberOfPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54, "Forge Recipe Category Page");

      int invSlot = 9;
      for (int itemIndex = startIndex; itemIndex < endIndex; itemIndex++) {
        List<ItemStack> results = recipes.get(itemIndex).getResults();
        inv.setItem(invSlot, createResultsDisplay(results.get(0), results));
        invSlot++;
      }
      pages.add(inv);

      // Indices to use for the next page (if it exists)
      startIndex += 45;
      endIndex = Math.min(numberOfRecipes, endIndex + 45);
    }
    return pages;
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
  private ItemStack createResultsDisplay(ItemStack displayItem, List<ItemStack> results) {
    if (results.size() > 1) {
      List<String> resultsLore = new ArrayList<>();
      for (ItemStack item : results) {
        resultsLore.add(ChatColor.AQUA + "x" + item.getAmount() +
            ChatColor.WHITE + " " + ItemReader.readName(item));
      }

      ItemStack item = displayItem.clone();
      ItemMeta meta = item.getItemMeta();
      meta.setLore(resultsLore);
      item.setItemMeta(meta);
      return item;
    } else {
      return displayItem;
    }
  }

  public Map<String, ForgeRecipe> getRecipesMap() {
    return this.recipesMap;
  }

  public Map<String, ForgeRecipeCategory> getRecipeCategoriesMap() {
    return this.recipeCategoriesMap;
  }
}