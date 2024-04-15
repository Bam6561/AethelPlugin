package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.interfaces.DataRegistry;
import me.dannynguyen.aethel.utils.InventoryPages;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Represents items in memory.
 * <p>
 * After the registry's creation, {@link #loadData() loadData} must be
 * called in order to load {@link Item items} from its associated directory.
 *
 * @author Danny Nguyen
 * @version 1.23.4
 * @since 1.3.2
 */
public class ItemRegistry implements DataRegistry {
  /**
   * Item file directory.
   */
  private final File directory;

  /**
   * Loaded {@link Item items}.
   */
  private final Map<String, Item> items = new HashMap<>();

  /**
   * Loaded {@link Item} categories represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   */
  private final Map<String, List<Inventory>> itemCategories = new HashMap<>();

  /**
   * Sorted {@link Item} category names.
   */
  private final List<String> itemCategoryNames = new ArrayList<>();

  /**
   * Associates an ItemRegistry with the provided directory.
   *
   * @param directory directory containing item files
   * @throws IllegalArgumentException if provided file is not a directory
   */
  public ItemRegistry(@NotNull File directory) throws IllegalArgumentException {
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
   * Loads data by reading {@link Item} files from the provided directory.
   */
  public void loadData() {
    File[] files = directory.listFiles();
    if (files == null) {
      return;
    }

    items.clear();
    itemCategories.clear();
    itemCategoryNames.clear();

    if (files.length == 0) {
      return;
    }

    Arrays.sort(files);
    Map<String, List<ItemStack>> categories = new HashMap<>(Map.of("All", new ArrayList<>()));
    for (File file : files) {
      if (file.getName().endsWith("_itm.txt")) {
        readFile(file, categories);
      }
    }

    if (items.isEmpty()) {
      return;
    }

    for (String category : categories.keySet()) {
      itemCategories.put(category, createPages(categories.get(category)));
      itemCategoryNames.add(category);
    }
    Collections.sort(itemCategoryNames);
  }

  /**
   * Deserializes bytes from designated item file into an
   * {@link Item} that is then sorted into a category.
   *
   * @param file       item file
   * @param categories item categories
   */
  private void readFile(File file, Map<String, List<ItemStack>> categories) {
    try {
      Scanner scanner = new Scanner(file);
      ItemStack item = ItemReader.decodeItem(scanner.nextLine());
      scanner.close();

      if (ItemReader.isNotNullOrAir(item)) {
        Item pItem = new Item(file, item);
        items.put(pItem.getName(), pItem);
        categories.get("All").add(item);
        sortItem(categories, item);
      } else {
        Bukkit.getLogger().warning(Message.INVALID_FILE.getMessage() + file.getName());
      }
    } catch (FileNotFoundException ex) {
      Bukkit.getLogger().warning(Message.UNABLE_TO_READ_FILE.getMessage() + file.getName());
    }
  }

  /**
   * Creates an {@link Item} category's pages.
   *
   * @param items items from an {@link Item} category
   * @return {@link Item} category's pages
   */
  private List<Inventory> createPages(List<ItemStack> items) {
    int totalItems = items.size();
    int totalPages = InventoryPages.getTotalPages(totalItems);

    List<Inventory> pages = new ArrayList<>();
    int pageStart = 0;
    int pageEnd = Math.min(totalItems, 45);

    for (int page = 0; page < totalPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54);

      int invSlot = 9;
      for (int itemIndex = pageStart; itemIndex < pageEnd; itemIndex++) {
        inv.setItem(invSlot, items.get(itemIndex));
        invSlot++;
      }
      pages.add(inv);

      // Indices to use for the next page (if it exists)
      pageStart += 45;
      pageEnd = Math.min(totalItems, pageEnd + 45);
    }
    return pages;
  }

  /**
   * Sorts an item into a category based on its {@link Key#ITEM_CATEGORY}.
   *
   * @param categories {@link Item} categories
   * @param item       interacting item
   */
  private void sortItem(Map<String, List<ItemStack>> categories, ItemStack item) {
    PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
    if (!data.has(Key.ITEM_CATEGORY.getNamespacedKey(), PersistentDataType.STRING)) {
      return;
    }

    String category = data.get(Key.ITEM_CATEGORY.getNamespacedKey(), PersistentDataType.STRING);
    if (categories.containsKey(category)) {
      categories.get(category).add(item);
    } else {
      categories.put(category, new ArrayList<>(List.of(item)));
    }
  }

  /**
   * Gets loaded {@link Item items}.
   *
   * @return loaded {@link Item items}
   */
  @NotNull
  protected Map<String, Item> getItems() {
    return this.items;
  }

  /**
   * Gets loaded {@link Item} categories.
   *
   * @return loaded {@link Item} categories
   */
  @NotNull
  protected Map<String, List<Inventory>> getItemCategories() {
    return this.itemCategories;
  }


  /**
   * Gets sorted {@link Item} category names.
   *
   * @return sorted {@link Item} category names
   */
  @NotNull
  protected List<String> getItemCategoryNames() {
    return this.itemCategoryNames;
  }

  /**
   * Represents an ItemStack stored in the file system.
   * <p>
   * Loaded into memory when {@link #loadData()} is called.
   *
   * @author Danny Nguyen
   * @version 1.17.12
   * @since 1.3.2
   */
  static class Item {
    /**
     * Item file.
     * <ul>
     *  <li>May be deleted from file system.
     *  <li>Path persists until data is reloaded.
     * </ul>
     */
    private final File file;

    /**
     * ItemStack.
     */
    private final ItemStack item;

    /**
     * Effective item name.
     */
    private final String name;

    /**
     * Associates an ItemStack with its file.
     *
     * @param file item file
     * @param item ItemStack
     */
    Item(File file, ItemStack item) {
      this.file = file;
      this.item = item;
      this.name = ItemReader.readName(item);
    }

    /**
     * Deletes the item file from the file system.
     */
    protected void delete() {
      file.delete();
    }

    /**
     * Gets the ItemStack.
     *
     * @return ItemStack
     */
    @NotNull
    protected ItemStack getItem() {
      return this.item;
    }

    /**
     * Gets the ItemStack's effective name.
     *
     * @return item name
     */
    @NotNull
    protected String getName() {
      return this.name;
    }
  }
}
