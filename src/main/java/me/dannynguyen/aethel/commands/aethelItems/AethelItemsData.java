package me.dannynguyen.aethel.commands.aethelItems;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.inventories.utility.InventoryPages;
import me.dannynguyen.aethel.commands.aethelItems.objects.AethelItem;
import me.dannynguyen.aethel.commands.aethelItems.objects.AethelItemsCategory;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * AethelItemsData stores Aethel items in memory.
 *
 * @author Danny Nguyen
 * @version 1.7.8
 * @since 1.3.2
 */
public class AethelItemsData {
  private final HashMap<String, AethelItem> itemsMap = new HashMap<>();
  private final HashMap<String, AethelItemsCategory> itemCategoriesMap = new HashMap<>();

  /**
   * Loads Aethel items into memory.
   */
  public void loadItems() {
    File[] directory = new File(PluginData.aethelItemsDirectory).listFiles();
    if (directory.length > 0) {
      Arrays.sort(directory);

      itemsMap.clear();
      itemCategoriesMap.clear();

      ArrayList<ItemStack> allItems = new ArrayList<>();
      HashMap<String, ArrayList<ItemStack>> sortedItems = new HashMap<>();
      NamespacedKey categoryKey = PluginNamespacedKey.AETHELITEM_CATEGORY.namespacedKey;

      for (File file : directory) {
        if (file.getName().endsWith("_itm.txt")) {
          AethelItem item = readItemFile(file);
          itemsMap.put(item.getName(), item);
          allItems.add(item.getItem());
          sortItem(item.getItem(), categoryKey, sortedItems);
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
      ItemStack item = ItemReader.decodeItem(scanner.nextLine());
      return new AethelItem(file, ItemReader.readName(item), item);
    } catch (IOException ex) {
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
  private void sortItem(ItemStack item, NamespacedKey categoryKey,
                        HashMap<String, ArrayList<ItemStack>> sortedItems) {
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();

    if (dataContainer.has(categoryKey, PersistentDataType.STRING)) {
      String itemCategory = dataContainer.get(categoryKey, PersistentDataType.STRING);

      if (sortedItems.containsKey(itemCategory)) {
        sortedItems.get(itemCategory).add(item);
      } else {
        ArrayList<ItemStack> newItemCategory = new ArrayList<>();
        newItemCategory.add(item);
        sortedItems.put(itemCategory, newItemCategory);
      }
    }
  }

  /**
   * Creates pages of all items, regardless of category.
   *
   * @param allItems          all items
   * @param itemCategoriesMap item category pages
   */
  private void createAllItemPages(ArrayList<ItemStack> allItems,
                                  HashMap<String, AethelItemsCategory> itemCategoriesMap) {
    int numberOfItems = allItems.size();
    int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfItems);

    ArrayList<Inventory> pages = createItemPages(allItems, numberOfItems, numberOfPages);

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
                                       HashMap<String, ArrayList<ItemStack>> sortedItems,
                                       HashMap<String, AethelItemsCategory> itemCategoriesMap) {
    for (ArrayList<ItemStack> items : sortedItems.values()) {
      int numberOfItems = items.size();
      int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfItems);

      ArrayList<Inventory> pages = createItemPages(items, numberOfItems, numberOfPages);

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
  private ArrayList<Inventory> createItemPages(ArrayList<ItemStack> items,
                                               int numberOfItems, int numberOfPages) {
    int startIndex = 0;
    int endIndex = Math.min(numberOfItems, 45);

    ArrayList<Inventory> pages = new ArrayList<>();
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

  public HashMap<String, AethelItem> getItemsMap() {
    return this.itemsMap;
  }

  public HashMap<String, AethelItemsCategory> getItemCategoriesMap() {
    return this.itemCategoriesMap;
  }
}
