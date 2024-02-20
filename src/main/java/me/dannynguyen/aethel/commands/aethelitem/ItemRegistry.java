package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.plugin.PluginEnum;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
 * After the registry's creation, {@link #loadData() loadData} must
 * be called in order to load items from its associated directory.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.9.21
 * @since 1.3.2
 */
public class ItemRegistry {
  /**
   * Item file directory.
   */
  private final File directory;

  /**
   * Loaded items.
   */
  private final Map<String, PersistentItem> itemMap = new HashMap<>();

  /**
   * Loaded item categories represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   * </p>
   */
  private final Map<String, List<Inventory>> categoryMap = new HashMap<>();

  /**
   * Associates an ItemRegistry with the provided directory.
   *
   * @param directory directory containing item files
   * @throws IllegalArgumentException provided file is not a directory
   */
  public ItemRegistry(@NotNull File directory) throws IllegalArgumentException {
    if (directory.isDirectory()) {
      this.directory = Objects.requireNonNull(directory, "Null directory");
    } else {
      throw new IllegalArgumentException("Non-directory");
    }
  }

  /**
   * Loads data by reading item files from the provided directory.
   */
  public void loadData() {
    File[] files = directory.listFiles();
    if (files != null) {
      itemMap.clear();
      categoryMap.clear();

      if (files.length > 0) {
        Arrays.sort(files);

        Map<String, List<ItemStack>> categories = new HashMap<>(Map.of("All", new ArrayList<>()));
        for (File file : files) {
          if (file.getName().endsWith("_itm.txt")) {
            readFile(file, categories);
          }
        }

        if (!itemMap.isEmpty()) {
          for (String category : categories.keySet()) {
            categoryMap.put(category, createPages(categories.get(category)));
          }
        }
      }
    }
  }

  /**
   * Deserializes bytes from designated item file into
   * an ItemStack that is then sorted into a category.
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
        PersistentItem pItem = new PersistentItem(file, item);
        itemMap.put(pItem.getName(), pItem);
        categories.get("All").add(item);
        sortItem(categories, item);
      } else {
        Bukkit.getLogger().warning("[Aethel] Invalid file: " + file.getName());
      }
    } catch (FileNotFoundException ex) {
      Bukkit.getLogger().warning("[Aethel] Unable to read file: " + file.getName());
    }
  }

  /**
   * Creates an item category's pages.
   *
   * @param items items from an item category
   * @return item category's pages
   */
  private List<Inventory> createPages(List<ItemStack> items) {
    int totalItems = items.size();
    int totalPages = InventoryPages.calculateTotalPages(totalItems);

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
   * Sorts an item into a category based on its item category id.
   *
   * @param categories item categories
   * @param item       interacting item
   */
  private void sortItem(Map<String, List<ItemStack>> categories, ItemStack item) {
    PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
    NamespacedKey itemId = PluginEnum.Key.ITEM_CATEGORY.getNamespacedKey();
    if (data.has(itemId, PersistentDataType.STRING)) {
      String category = data.get(itemId, PersistentDataType.STRING);
      if (categories.containsKey(category)) {
        categories.get(category).add(item);
      } else {
        categories.put(category, new ArrayList<>(List.of(item)));
      }
    }
  }

  /**
   * Gets loaded items.
   *
   * @return loaded items
   */
  @NotNull
  protected Map<String, PersistentItem> getItemMap() {
    return this.itemMap;
  }

  /**
   * Gets loaded item categories.
   *
   * @return loaded item categories
   */
  @NotNull
  protected Map<String, List<Inventory>> getCategoryMap() {
    return this.categoryMap;
  }
}
