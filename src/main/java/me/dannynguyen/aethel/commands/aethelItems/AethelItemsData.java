package me.dannynguyen.aethel.commands.aethelItems;

import me.dannynguyen.aethel.commands.aethelItems.object.AethelItem;
import me.dannynguyen.aethel.commands.aethelItems.object.AethelItemsCategory;
import me.dannynguyen.aethel.enums.PluginDirectory;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * AethelItemsData stores Aethel items in memory.
 *
 * @author Danny Nguyen
 * @version 1.9.3
 * @since 1.3.2
 */
public class AethelItemsData {
  private final Map<String, AethelItem> itemsMap = new HashMap<>();
  private final Map<String, AethelItemsCategory> itemCategoriesMap = new HashMap<>();

  /**
   * Loads Aethel items into memory.
   */
  public void loadItems() {
    File[] directory = PluginDirectory.AETHELITEMS.file.listFiles();
    if (directory != null) {
      Arrays.sort(directory);

      itemsMap.clear();
      itemCategoriesMap.clear();

      List<ItemStack> allItems = new ArrayList<>();
      Map<String, List<ItemStack>> sortedItems = new HashMap<>();
      NamespacedKey categoryKey = PluginNamespacedKey.AETHELITEM_CATEGORY.namespacedKey;

      for (File file : directory) {
        if (file.getName().endsWith("_itm.txt")) {
          AethelItem item = readItemFile(file);
          if (item != null) {
            itemsMap.put(item.getName(), item);
            allItems.add(item.getItem());
            sortItems(item.getItem(), categoryKey, sortedItems);
          }
        }
      }

      if (!itemsMap.isEmpty()) {
        createAllItemPages(allItems, itemCategoriesMap);
        createItemCategoryPages(categoryKey, sortedItems, itemCategoriesMap);
      }
    }
  }

  /**
   * Reads an item file.
   *
   * @param file item file
   * @return decoded item
   * @throws IOException file not found
   */
  private AethelItem readItemFile(File file) {
    try {
      Scanner scanner = new Scanner(file);
      ItemStack item = ItemReader.decodeItem(scanner.nextLine(), "aethelitems");
      if (ItemReader.isNotNullOrAir(item)) {
        return new AethelItem(file, ItemReader.readName(item), item);
      } else {
        return null;
      }
    } catch (FileNotFoundException ex) {
      return null;
    }
  }

  /**
   * Puts an item into a category if it has an item category tag.
   *
   * @param item        interacting item
   * @param categoryKey item category tag
   * @param sortedItems items sorted by category
   */
  private void sortItems(ItemStack item, NamespacedKey categoryKey,
                         Map<String, List<ItemStack>> sortedItems) {
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();

    if (dataContainer.has(categoryKey, PersistentDataType.STRING)) {
      String itemCategory = dataContainer.get(categoryKey, PersistentDataType.STRING);

      if (sortedItems.containsKey(itemCategory)) {
        sortedItems.get(itemCategory).add(item);
      } else {
        sortedItems.put(itemCategory, new ArrayList<>(List.of(item)));
      }
    }
  }

  /**
   * Creates pages of all items, regardless of category.
   *
   * @param allItems          all items
   * @param itemCategoriesMap item category pages
   */
  private void createAllItemPages(List<ItemStack> allItems,
                                  Map<String, AethelItemsCategory> itemCategoriesMap) {
    int numberOfItems = allItems.size();
    int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfItems);

    List<Inventory> pages = createItemPages(allItems, numberOfItems, numberOfPages);

    itemCategoriesMap.put("All", new AethelItemsCategory("All", pages, numberOfPages));
  }

  /**
   * Creates pages of items by category.
   *
   * @param categoryKey       item category tag
   * @param sortedItems       items sorted by category
   * @param itemCategoriesMap item category pages
   */
  private void createItemCategoryPages(NamespacedKey categoryKey,
                                       Map<String, List<ItemStack>> sortedItems,
                                       Map<String, AethelItemsCategory> itemCategoriesMap) {
    for (List<ItemStack> items : sortedItems.values()) {
      int numberOfItems = items.size();
      int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfItems);

      List<Inventory> pages = createItemPages(items, numberOfItems, numberOfPages);

      PersistentDataContainer dataContainer = items.get(0).getItemMeta().getPersistentDataContainer();
      String itemCategory = dataContainer.get(categoryKey, PersistentDataType.STRING);

      itemCategoriesMap.put(itemCategory, new AethelItemsCategory(itemCategory, pages, numberOfPages));
    }
  }

  /**
   * Creates pages of items.
   *
   * @param items         items
   * @param numberOfItems number of items
   * @param numberOfPages number of pages
   * @return pages of items
   */
  private List<Inventory> createItemPages(List<ItemStack> items,
                                          int numberOfItems, int numberOfPages) {
    int startIndex = 0;
    int endIndex = Math.min(numberOfItems, 45);

    List<Inventory> pages = new ArrayList<>();
    for (int page = 0; page < numberOfPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54, "Aethel Item Category Page");

      int invSlot = 9;
      for (int itemIndex = startIndex; itemIndex < endIndex; itemIndex++) {
        inv.setItem(invSlot, items.get(itemIndex));
        invSlot++;
      }
      pages.add(inv);

      // Indices to use for the next page (if it exists)
      startIndex += 45;
      endIndex = Math.min(numberOfItems, endIndex + 45);
    }
    return pages;
  }

  public Map<String, AethelItem> getItemsMap() {
    return this.itemsMap;
  }

  public Map<String, AethelItemsCategory> getItemCategoriesMap() {
    return this.itemCategoriesMap;
  }
}
