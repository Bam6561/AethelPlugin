package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * ForgeRecipeData contains information about forge recipes loaded in memory.
 *
 * @author Danny Nguyen
 * @version 1.4.1
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
      ForgeRecipe recipe = readRecipe(directory[i]);
      recipes.add(recipe);
      recipesMap.put(recipe.getName(), recipe);
    }
    createRecipePages();
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
  private ForgeRecipe readRecipe(File file) {
    ArrayList<ItemStack> results = new ArrayList<>();
    ArrayList<ItemStack> components = new ArrayList<>();
    int dataType = 1;

    try {
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        readLine(scanner.nextLine(), dataType, results, components);
        dataType++;
      }
      return new ForgeRecipe(file, new ItemReader().readItemName(results.get(0)), results, components);
    } catch (FileNotFoundException ex) {
      return null;
    }
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
        ArrayList<ItemStack> results = getRecipes().get(i).getResults();
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
  private void readLine(String line, int dataType, ArrayList<ItemStack> results, ArrayList<ItemStack> components) {
    String[] data = line.split(" ");
    ItemReader itemReader = new ItemReader();
    for (String encodedItem : data) {
      ItemStack item = itemReader.decodeItem(encodedItem);
      if (item != null) {
        switch (dataType) {
          case 1 -> results.add(item);
          case 2 -> components.add(item);
        }
      }
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
      ItemReader itemReader = new ItemReader();

      for (ItemStack item : results) {
        recipeResults.add(ChatColor.AQUA + "x" + item.getAmount() +
            ChatColor.WHITE + " " + itemReader.readItemName(item));
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

  private void setNumberOfPages(int numberOfPages) {
    this.numberOfPages = numberOfPages;
  }
}
