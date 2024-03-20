package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.plugin.interfaces.DataRegistry;
import me.dannynguyen.aethel.util.InventoryPages;
import me.dannynguyen.aethel.util.item.ItemReader;
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
 * After the registry's creation, {@link #loadData() loadData} must be called in
 * order to load {@link PersistentItem items} from its associated directory.
 *
 * @author Danny Nguyen
 * @version 1.17.5
 * @since 1.3.2
 */
public class ItemRegistry implements DataRegistry {
  /**
   * Item file directory.
   */
  private final File directory;

  /**
   * Loaded {@link PersistentItem items}.
   */
  private final Map<String, PersistentItem> items = new HashMap<>();

  /**
   * Loaded {@link PersistentItem item} categories represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   */
  private final Map<String, List<Inventory>> itemCategories = new HashMap<>();

  /**
   * Associates an ItemRegistry with the provided directory.
   *
   * @param directory directory containing item files
   * @throws IllegalArgumentException if provided file is not a directory
   */
  public ItemRegistry(@NotNull File directory) throws IllegalArgumentException {
    if (Objects.requireNonNull(directory, "Null directory").exists()) {
      if (directory.isDirectory()) {
        this.directory = directory;
      } else {
        throw new IllegalArgumentException("Non-directory");
      }
    } else {
      this.directory = directory;
      directory.mkdirs();
    }
  }

  /**
   * Loads data by reading {@link PersistentItem item} files from the provided directory.
   */
  public void loadData() {
    File[] files = directory.listFiles();
    if (files != null) {
      items.clear();
      itemCategories.clear();

      if (files.length > 0) {
        Arrays.sort(files);

        Map<String, List<ItemStack>> categories = new HashMap<>(Map.of("All", new ArrayList<>()));
        for (File file : files) {
          if (file.getName().endsWith("_itm.txt")) {
            readFile(file, categories);
          }
        }

        if (!items.isEmpty()) {
          for (String category : categories.keySet()) {
            itemCategories.put(category, createPages(categories.get(category)));
          }
        }
      }
    }
  }

  /**
   * Deserializes bytes from designated item file into an
   * {@link PersistentItem item} that is then sorted into a category.
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
        items.put(pItem.getName(), pItem);
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
   * Creates an {@link PersistentItem item} category's pages.
   *
   * @param items items from an {@link PersistentItem item} category
   * @return {@link PersistentItem item} category's pages
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
   * Sorts an item into a category based on its {@link PluginNamespacedKey#ITEM_CATEGORY}.
   *
   * @param categories {@link PersistentItem item} categories
   * @param item       interacting item
   */
  private void sortItem(Map<String, List<ItemStack>> categories, ItemStack item) {
    PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
    NamespacedKey itemCategory = PluginNamespacedKey.ITEM_CATEGORY.getNamespacedKey();
    if (data.has(itemCategory, PersistentDataType.STRING)) {
      String category = data.get(itemCategory, PersistentDataType.STRING);
      if (categories.containsKey(category)) {
        categories.get(category).add(item);
      } else {
        categories.put(category, new ArrayList<>(List.of(item)));
      }
    }
  }

  /**
   * Gets loaded {@link PersistentItem items}.
   *
   * @return loaded {@link PersistentItem items}
   */
  @NotNull
  protected Map<String, PersistentItem> getItems() {
    return this.items;
  }

  /**
   * Gets loaded {@link PersistentItem item} categories.
   *
   * @return loaded {@link PersistentItem item} categories
   */
  @NotNull
  protected Map<String, List<Inventory>> getItemCategories() {
    return this.itemCategories;
  }
}
