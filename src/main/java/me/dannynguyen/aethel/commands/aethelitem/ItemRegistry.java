package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.interfaces.DataRegistry;
import me.dannynguyen.aethel.utils.InventoryPages;
import me.dannynguyen.aethel.utils.item.ItemReader;
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
 * Represents items in memory.
 * <p>
 * After the registry's creation, {@link #loadData() loadData} must be
 * called in order to load {@link Item items} from its associated directory.
 *
 * @author Danny Nguyen
 * @version 1.25.13
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
   * {@link Item} category icons.
   */
  private final Map<String, ItemStack> itemCategoryIcons = new HashMap<>();

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
    Map<String, List<ItemStack>> categories = new HashMap<>();
    parseDirectory(files, categories);

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
   * Recursively parses the directory and reads item files.
   *
   * @param directory  item directory
   * @param categories item categories
   */
  private void parseDirectory(File[] directory, Map<String, List<ItemStack>> categories) {
    for (File file : directory) {
      if (file.isFile()) {
        if (file.getName().endsWith("_itm.txt")) {
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

        String category = file.getParentFile().getName();
        if (categories.containsKey(category)) {
          categories.get(category).add(item);
        } else {
          categories.put(category, new ArrayList<>(List.of(item)));
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
   * to set a {@link Item} category's icon.
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
      itemCategoryIcons.put(category, icon);
    } catch (IOException ex) {
      Bukkit.getLogger().warning(Message.UNABLE_TO_READ_FILE + file.getName());
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
   * Gets {@link Item} category icons.
   *
   * @return {@link Item} category icons
   */
  @NotNull
  protected Map<String, ItemStack> getItemCategoryIcons() {
    return this.itemCategoryIcons;
  }

  /**
   * Represents an ItemStack stored in the file system.
   * <p>
   * Loaded into memory when {@link #loadData()} is called.
   *
   * @author Danny Nguyen
   * @version 1.24.7
   * @since 1.3.2
   */
  protected static class Item {
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
    private Item(File file, ItemStack item) {
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
