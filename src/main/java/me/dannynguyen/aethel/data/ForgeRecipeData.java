package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.inventories.utility.InventoryPages;
import me.dannynguyen.aethel.objects.forge.ForgeRecipe;
import me.dannynguyen.aethel.objects.forge.ForgeRecipeCategory;
import me.dannynguyen.aethel.readers.ItemReader;
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
 * @version 1.7.2
 * @since 1.1.11
 */
public class ForgeRecipeData {
  private final HashMap<String, ForgeRecipe> recipesMap = new HashMap<>();
  private final HashMap<String, ForgeRecipeCategory> recipeCategoriesMap = new HashMap<>();

  /**
   * Loads forge recipes into memory.
   */
  public void loadRecipes() {
    HashMap<String, ForgeRecipe> recipesMap = getRecipesMap();
    HashMap<String, ForgeRecipeCategory> recipeCategoriesMap = getRecipeCategoriesMap();

    recipesMap.clear();
    recipeCategoriesMap.clear();

    ArrayList<ForgeRecipe> allRecipes = new ArrayList<>();
    HashMap<String, ArrayList<ForgeRecipe>> categorizedRecipes = new HashMap<>();
    NamespacedKey forgeCategoryKey =
        new NamespacedKey(AethelPlugin.getInstance(), "aethel.forge.category");

    File[] directory = new File(AethelResources.forgeRecipesDirectory).listFiles();
    Arrays.sort(directory);
    for (File file : directory) {
      if (file.getName().endsWith("_rcp.txt")) {
        ForgeRecipe recipe = readRecipeFile(file);
        recipesMap.put(recipe.getName(), recipe);
        allRecipes.add(recipe);
        categorizeRecipe(recipe, forgeCategoryKey, categorizedRecipes);
      }
    }

    if (!recipesMap.isEmpty()) {
      createAllRecipePages(allRecipes, recipeCategoriesMap);
      createRecipeCategoryPages(forgeCategoryKey, categorizedRecipes, recipeCategoriesMap);
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
    ArrayList<ItemStack> results = new ArrayList<>();
    ArrayList<ItemStack> components = new ArrayList<>();
    int dataType = 1;

    try {
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        readLine(scanner.nextLine(), dataType, results, components);
        dataType++;
      }
      return new ForgeRecipe(file, ItemReader.readItemName(results.get(0)), results, components);
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
                        ArrayList<ItemStack> results, ArrayList<ItemStack> components) {
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
   * @param recipe             interacting recipe
   * @param forgeCategoryKey   forge category tag
   * @param categorizedRecipes recipes sorted by category
   */
  private void categorizeRecipe(ForgeRecipe recipe, NamespacedKey forgeCategoryKey,
                                HashMap<String, ArrayList<ForgeRecipe>> categorizedRecipes) {
    ItemStack item = recipe.getResults().get(0);
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();

    if (dataContainer.has(forgeCategoryKey, PersistentDataType.STRING)) {
      String recipeCategory = dataContainer.get(forgeCategoryKey, PersistentDataType.STRING);

      if (categorizedRecipes.containsKey(recipeCategory)) {
        categorizedRecipes.get(recipeCategory).add(recipe);
      } else {
        ArrayList<ForgeRecipe> recipes = new ArrayList<>();
        recipes.add(recipe);
        categorizedRecipes.put(recipeCategory, recipes);
      }
    }
  }

  /**
   * Creates pages of all recipes, regardless of category.
   *
   * @param allRecipes          all recipes
   * @param recipeCategoriesMap recipe category pages
   */
  private void createAllRecipePages(ArrayList<ForgeRecipe> allRecipes,
                                    HashMap<String, ForgeRecipeCategory> recipeCategoriesMap) {
    int numberOfRecipes = allRecipes.size();
    int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfRecipes);

    ArrayList<Inventory> pages = createRecipePages(allRecipes, numberOfRecipes, numberOfPages);

    recipeCategoriesMap.put("All", new ForgeRecipeCategory("All", pages, numberOfPages));
  }


  /**
   * Creates pages of recipes by category.
   *
   * @param forgeCategoryKey    forge category tag
   * @param categorizedRecipes  recipes sorted by category
   * @param recipeCategoriesMap recipe category pages
   */
  private void createRecipeCategoryPages(NamespacedKey forgeCategoryKey,
                                         HashMap<String, ArrayList<ForgeRecipe>> categorizedRecipes,
                                         HashMap<String, ForgeRecipeCategory> recipeCategoriesMap) {
    for (ArrayList<ForgeRecipe> recipes : categorizedRecipes.values()) {
      int numberOfRecipes = recipes.size();
      int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfRecipes);

      ArrayList<Inventory> pages = createRecipePages(recipes, numberOfRecipes, numberOfPages);

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
  private ArrayList<Inventory> createRecipePages(ArrayList<ForgeRecipe> recipes,
                                                 int numberOfRecipes, int numberOfPages) {
    int startIndex = 0;
    int endIndex = Math.min(numberOfRecipes, 45);

    ArrayList<Inventory> pages = new ArrayList<>();
    for (int page = 0; page < numberOfPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54, "Forge Recipe Category Page");

      int invSlot = 9;
      for (int itemIndex = startIndex; itemIndex < endIndex; itemIndex++) {
        ArrayList<ItemStack> results = recipes.get(itemIndex).getResults();
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
  private ItemStack createResultsDisplay(ItemStack displayItem, ArrayList<ItemStack> results) {
    if (results.size() > 1) {
      List<String> recipeResults = new ArrayList<>();

      for (ItemStack item : results) {
        recipeResults.add(ChatColor.AQUA + "x" + item.getAmount() +
            ChatColor.WHITE + " " + ItemReader.readItemName(item));
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

  public HashMap<String, ForgeRecipe> getRecipesMap() {
    return this.recipesMap;
  }

  public HashMap<String, ForgeRecipeCategory> getRecipeCategoriesMap() {
    return this.recipeCategoriesMap;
  }
}