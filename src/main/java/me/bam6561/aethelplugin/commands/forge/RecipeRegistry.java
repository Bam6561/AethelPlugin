package me.bam6561.aethelplugin.commands.forge;

import me.bam6561.aethelplugin.enums.plugin.Message;
import me.bam6561.aethelplugin.interfaces.DataRegistry;
import me.bam6561.aethelplugin.utils.InventoryPages;
import me.bam6561.aethelplugin.utils.item.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Represents {@link Recipe recipes} in memory.
 * <p>
 * After the registry's creation, {@link #loadData() loadData} must be called
 * in order to load {@link Recipe recipes} from its associated directory.
 *
 * @author Danny Nguyen
 * @version 1.26.5
 * @since 1.1.11
 */
public class RecipeRegistry implements DataRegistry {
  /**
   * Recipe file directory.
   */
  private final File directory;

  /**
   * Loaded {@link Recipe recipes}.
   */
  private final Map<String, Recipe> recipes = new HashMap<>();

  /**
   * Loaded {@link Recipe} categories represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   */
  private final Map<String, List<Inventory>> recipeCategories = new HashMap<>();

  /**
   * Sorted {@link Recipe} category names.
   */
  private final List<String> recipeCategoryNames = new ArrayList<>();

  /**
   * {@link Recipe} category icons.
   */
  private final Map<String, ItemStack> recipeCategoryIcons = new HashMap<>();

  /**
   * {@link Recipe} search terms.
   */
  private final Map<String, List<String>> recipeSearchTerms = new HashMap<>();

  /**
   * Associates a RecipeRegistry with the provided directory.
   *
   * @param directory directory containing recipe files
   * @throws IllegalArgumentException if provided file is not a directory
   */
  public RecipeRegistry(@NotNull File directory) {
    if (!Objects.requireNonNull(directory, "Null directory").exists()) {
      this.directory = directory;
      directory.mkdirs();
      return;
    }

    if (directory.isDirectory()) {
      this.directory = directory;
    } else {
      throw new IllegalArgumentException("Non-directory");
    }
  }

  /**
   * Loads {@link Recipe recipes} into memory.
   */
  public void loadData() {
    File[] files = directory.listFiles();
    if (files == null) {
      return;
    }

    recipes.clear();
    recipeCategories.clear();
    recipeCategoryNames.clear();
    recipeCategoryIcons.clear();
    recipeSearchTerms.clear();

    if (files.length == 0) {
      return;
    }

    Arrays.sort(files);
    Map<String, List<List<ItemStack>>> categories = new HashMap<>();
    parseDirectory(files, categories);

    if (recipes.isEmpty()) {
      return;
    }

    for (String category : categories.keySet()) {
      recipeCategories.put(category, createPages(categories.get(category)));
      recipeCategoryNames.add(category);
    }
    Collections.sort(recipeCategoryNames);
  }

  /**
   * Recursively parses the directory and reads recipe files.
   *
   * @param directory  recipe directory
   * @param categories recipe categories
   */
  private void parseDirectory(File[] directory, Map<String, List<List<ItemStack>>> categories) {
    for (File file : directory) {
      if (file.isFile()) {
        if (file.getName().endsWith("_rcp.txt")) {
          readFile(file, categories);
        } else if (file.getName().endsWith("_icon.txt")) {
          readIcon(file);
        }
      } else {
        File[] subdirectory = file.listFiles();
        if (subdirectory.length == 0) {
          file.delete();
        } else {
          Arrays.sort(subdirectory);
          parseDirectory(subdirectory, categories);
        }
      }
    }
  }

  /**
   * Deserializes bytes from designated recipe file into a
   * {@link Recipe} that is then sorted into a category.
   * <p>
   * Data is stored in two lines of text, represented by the variable dataType.
   * <ul>
   *  <li>[1] Results
   *  <li>[2] Materials
   * </ul>
   *
   * @param file       recipe file
   * @param categories {@link Recipe} categories
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
        Recipe pRecipe = new Recipe(file, results, materials);
        recipes.put(pRecipe.getName(), pRecipe);

        String recipeName = ChatColor.stripColor(pRecipe.getName());
        for (String term : recipeName.split(" ")) {
          term = term.toLowerCase();
          if (recipeSearchTerms.containsKey(term)) {
            recipeSearchTerms.get(term).add(pRecipe.getName());
          } else {
            recipeSearchTerms.put(term, new ArrayList<>(List.of(pRecipe.getName())));
          }
        }

        String category = file.getParentFile().getName();
        if (categories.containsKey(category)) {
          categories.get(category).add(results);
        } else {
          categories.put(category, new ArrayList<>(List.of(results)));
        }
      } else {
        Bukkit.getLogger().warning(Message.INVALID_FILE.getMessage() + file.getName());
      }
    } catch (FileNotFoundException ex) {
      Bukkit.getLogger().warning(Message.UNABLE_TO_READ_FILE.getMessage() + file.getName());
    }
  }

  /**
   * Deserializes bytes from designated icon file
   * to set a {@link Recipe} category's icon.
   *
   * @param file icon file
   */
  private void readIcon(File file) {
    try {
      Scanner scanner = new Scanner(file);
      ItemStack icon = ItemReader.decodeItem(scanner.nextLine());
      ItemMeta iconMeta = icon.getItemMeta();
      String category = file.getParentFile().getName();
      iconMeta.setDisplayName(ChatColor.WHITE + category);
      icon.setItemMeta(iconMeta);
      recipeCategoryIcons.put(category, icon);
    } catch (IOException ex) {
      Bukkit.getLogger().warning(Message.UNABLE_TO_READ_FILE + file.getName());
    }
  }

  /**
   * Creates a {@link Recipe} category's pages.
   *
   * @param recipes {@link Recipe recipes} from a {@link Recipe} category
   * @return {@link Recipe} category's pages
   */
  private List<Inventory> createPages(List<List<ItemStack>> recipes) {
    int totalRecipes = recipes.size();
    int totalPages = InventoryPages.getTotalPages(totalRecipes);

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
   * Reads lines of text from the file and adds decoded items to the {@link Recipe}.
   * <p>
   * Individual encoded ItemStacks are separated by spaces.
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
   * Creates an item display for {@link Recipe recipes} with multiple results.
   * <p>
   * Format:
   * <ul>
   *  <li>xAmount Item
   *  <li>...
   * </ul>
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
   * Gets loaded {@link Recipe}.
   *
   * @return loaded {@link Recipe recipes}
   */
  @NotNull
  protected Map<String, Recipe> getRecipes() {
    return this.recipes;
  }

  /**
   * Gets loaded {@link Recipe} categories.
   *
   * @return loaded {@link Recipe} categories
   */
  @NotNull
  protected Map<String, List<Inventory>> getRecipeCategories() {
    return this.recipeCategories;
  }

  /**
   * Gets sorted {@link Recipe} category names.
   *
   * @return sorted {@link Recipe} category names
   */
  @NotNull
  protected List<String> getRecipeCategoryNames() {
    return this.recipeCategoryNames;
  }

  /**
   * Gets {@link Recipe} category icons.
   *
   * @return {@link Recipe} category icons
   */
  @NotNull
  protected Map<String, ItemStack> getRecipeCategoryIcons() {
    return this.recipeCategoryIcons;
  }

  /**
   * Gets {@link Recipe} search terms.
   *
   * @return {@link Recipe} search terms
   */
  @NotNull
  protected Map<String, List<String>> getRecipeSearchTerms() {
    return this.recipeSearchTerms;
  }

  /**
   * Represents a recipe stored in the file system.
   * <p>
   * Loaded into memory when {@link #loadData()} is called.
   *
   * @author Danny Nguyen
   * @version 1.24.7
   * @since 1.0.3
   */
  protected static class Recipe {
    /**
     * Recipe file.
     * <ul>
     *  <li>May be deleted from file system.
     *  <li>Path persists until data is reloaded.
     * </ul>
     */
    private final File file;

    /**
     * Recipe results.
     */
    private final List<ItemStack> results;

    /**
     * Recipe materials.
     */
    private final List<ItemStack> materials;

    /**
     * Effective recipe name.
     */
    private final String name;

    /**
     * Associates a recipe with its file.
     *
     * @param file      recipe file
     * @param results   recipe results
     * @param materials recipe materials
     * @throws IllegalArgumentException if provided file is not a file
     */
    private Recipe(File file, List<ItemStack> results, List<ItemStack> materials) {
      this.file = file;
      this.results = results;
      this.materials = materials;
      this.name = ItemReader.readName(results.get(0));
    }

    /**
     * Gets the recipe's file.
     *
     * @return recipe's file
     */
    protected File getFile() {
      return this.file;
    }

    /**
     * Deletes the recipe file from the file system.
     */
    protected void delete() {
      file.delete();
    }

    /**
     * Gets the recipe's results.
     *
     * @return recipe results
     */
    @NotNull
    protected List<ItemStack> getResults() {
      return this.results;
    }

    /**
     * Gets the recipe's materials.
     *
     * @return recipe materials
     */
    @NotNull
    protected List<ItemStack> getMaterials() {
      return this.materials;
    }

    /**
     * Gets the recipe's effective name.
     *
     * @return recipe name
     */
    @NotNull
    protected String getName() {
      return this.name;
    }
  }
}