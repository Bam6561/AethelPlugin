package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Represents Forge recipes in memory.
 * <p>
 * After the registry's creation, {@link #loadData() loadData} must
 * be called in order to load recipes from its associated directory.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.12.1
 * @since 1.1.11
 */
public class RecipeRegistry {
  /**
   * Forge recipe file directory.
   */
  private final File directory;

  /**
   * Loaded Forge recipes.
   */
  private final Map<String, PersistentRecipe> recipeMap = new HashMap<>();

  /**
   * Loaded Forge recipe categories represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   * </p>
   */
  private final Map<String, List<Inventory>> categoryMap = new HashMap<>();

  /**
   * Associates a RecipeRegistry with the provided directory.
   *
   * @param directory directory containing recipe files
   * @throws IllegalArgumentException provided file is not a directory
   */
  public RecipeRegistry(@NotNull File directory) {
    if (directory.exists()) {
      if (directory.isDirectory()) {
        this.directory = Objects.requireNonNull(directory, "Null directory");
      } else {
        throw new IllegalArgumentException("Non-directory");
      }
    } else {
      this.directory = directory;
      directory.mkdirs();
    }
  }

  /**
   * Loads Forge recipes into memory.
   */
  public void loadData() {
    File[] files = directory.listFiles();
    if (files != null) {
      recipeMap.clear();
      categoryMap.clear();

      if (files.length > 0) {
        Arrays.sort(files);

        Map<String, List<List<ItemStack>>> categories = new HashMap<>(Map.of("All", new ArrayList<>()));
        for (File file : files) {
          if (file.getName().endsWith("_rcp.txt")) {
            readFile(file, categories);
          }
        }

        if (!recipeMap.isEmpty()) {
          for (String category : categories.keySet()) {
            categoryMap.put(category, createPages(categories.get(category)));
          }
        }
      }
    }
  }

  /**
   * Deserializes bytes from designated recipe file into
   * a Forge recipe that is then sorted into a category.
   * <p>
   * Data is stored in two lines of text, represented by the variable dataType.
   * - [1] Results
   * - [2] Materials
   * </p>
   *
   * @param file       recipe file
   * @param categories recipe categories
   */
  private void readFile(File file, Map<String, List<List<ItemStack>>> categories) {
    List<ItemStack> results = new ArrayList<>();
    List<ItemStack> materials = new ArrayList<>();

    try {
      Scanner scanner = new Scanner(file);
      String[] lines = new String[2];
      lines[0] = scanner.nextLine();
      lines[1] = scanner.nextLine();
      scanner.close();

      readLines(lines, results, materials);
      if (!results.isEmpty()) {
        PersistentRecipe pRecipe = new PersistentRecipe(file, results, materials);
        recipeMap.put(pRecipe.getName(), pRecipe);
        categories.get("All").add(results);
        sortRecipe(categories, results);
      } else {
        Bukkit.getLogger().warning("[Aethel] Invalid file: " + file.getName());
      }
    } catch (FileNotFoundException ex) {
      Bukkit.getLogger().warning("[Aethel] Unable to read file: " + file.getName());
    }
  }

  /**
   * Creates a recipe category's pages.
   *
   * @param recipes recipes from a recipe category
   * @return recipe category's pages
   */
  private List<Inventory> createPages(List<List<ItemStack>> recipes) {
    int totalRecipes = recipes.size();
    int totalPages = InventoryPages.calculateTotalPages(totalRecipes);

    List<Inventory> pages = new ArrayList<>();
    int pageStart = 0;
    int pageEnd = Math.min(totalRecipes, 45);

    for (int page = 0; page < totalPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54);

      int invSlot = 9;
      for (int recipeIndex = pageStart; recipeIndex < pageEnd; recipeIndex++) {
        inv.setItem(invSlot, createResultsDisplay(recipes.get(recipeIndex).get(0), recipes.get(recipeIndex)));
        invSlot++;
      }
      pages.add(inv);

      // Indices to use for the next page (if it exists)
      pageStart += 45;
      pageEnd = Math.min(totalRecipes, pageEnd + 45);
    }
    return pages;
  }

  /**
   * Reads lines of text from the file and adds decoded items to the recipe.
   * <p>
   * Individual encoded ITemStacks are separated by spaces.
   * </p>
   *
   * @param lines     text lines
   * @param results   recipe results
   * @param materials recipe materials
   */
  private void readLines(String[] lines, List<ItemStack> results, List<ItemStack> materials) {
    int dataType = 1;
    for (String line : lines) {
      String[] data = line.split(" ");
      for (String encodedItem : data) {
        ItemStack item = ItemReader.decodeItem(encodedItem);
        if (ItemReader.isNotNullOrAir(item)) {
          switch (dataType) {
            case 1 -> results.add(item);
            case 2 -> materials.add(item);
          }
        } else {
          results.clear();
          return;
        }
      }
      dataType++;
    }
  }

  /**
   * Sorts a recipe into a category based on its recipe category id.
   *
   * @param categories recipe categories
   * @param results    interacting recipe
   */
  private void sortRecipe(Map<String, List<List<ItemStack>>> categories, List<ItemStack> results) {
    PersistentDataContainer data = results.get(0).getItemMeta().getPersistentDataContainer();
    if (data.has(PluginNamespacedKey.RECIPE_CATEGORY.getNamespacedKey(), PersistentDataType.STRING)) {
      String category = data.get(PluginNamespacedKey.RECIPE_CATEGORY.getNamespacedKey(), PersistentDataType.STRING);
      if (categories.containsKey(category)) {
        categories.get(category).add(results);
      } else {
        categories.put(category, new ArrayList<>(List.of(results)));
      }
    }
  }

  /**
   * Creates an item display for recipes with multiple results.
   * <p>
   * Format:
   * xAmount Item
   * ...
   * </p>
   *
   * @param displayItem item to be shown
   * @param results     recipe results
   * @return display item labeled with its result(s)
   */
  private ItemStack createResultsDisplay(ItemStack displayItem, List<ItemStack> results) {
    if (results.size() > 1) {
      List<String> lore = new ArrayList<>();
      for (ItemStack result : results) {
        lore.add(ChatColor.AQUA + "x" + result.getAmount() + ChatColor.WHITE + " " + ItemReader.readName(result));
      }

      ItemStack item = displayItem.clone();
      ItemMeta meta = item.getItemMeta();
      meta.setLore(lore);
      item.setItemMeta(meta);
      return item;
    } else {
      return displayItem;
    }
  }

  /**
   * Gets loaded recipe.
   *
   * @return loaded recipes
   */
  @NotNull
  public Map<String, PersistentRecipe> getRecipeMap() {
    return this.recipeMap;
  }

  /**
   * Gets loaded recipe categories.
   *
   * @return loaded recipe categories
   */
  @NotNull
  public Map<String, List<Inventory>> getCategoryMap() {
    return this.categoryMap;
  }
}