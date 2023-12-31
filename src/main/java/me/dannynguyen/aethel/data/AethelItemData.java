package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.inventories.PageCalculator;
import me.dannynguyen.aethel.objects.AethelItem;
import me.dannynguyen.aethel.objects.AethelItemCategory;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * AethelItem contains information about Aethel items stored in memory.
 *
 * @author Danny Nguyen
 * @version 1.5.0
 * @since 1.3.2
 */
public class AethelItemData {
  private final HashMap<String, AethelItem> itemsMap = new HashMap<>();
  private final HashMap<String, AethelItemCategory> itemCategoriesMap = new HashMap<>();

  /**
   * Loads Aethel items into memory.
   */
  public void loadItems() {
    HashMap<String, AethelItem> itemsMap = getItemsMap();
    HashMap<String, AethelItemCategory> itemCategoriesMap = getItemCategoriesMap();

    itemsMap.clear();
    itemCategoriesMap.clear();

    ArrayList<ItemStack> allItems = new ArrayList<>();
    HashMap<String, ArrayList<ItemStack>> categorizedItems = new HashMap<>();

    File[] directory = new File(AethelResources.aethelItemDirectory).listFiles();
    Collections.sort(Arrays.asList(directory));
    for (File file : directory) {
      if (file.getName().endsWith("_itm.txt")) {
        AethelItem item = readItemFile(file);
        itemsMap.put(item.getName(), item);
        allItems.add(item.getItem());
        categorizeItem(item.getItem(), categorizedItems);
      }
    }

    if (!itemsMap.isEmpty()) {
      createAllItemPages(allItems, itemCategoriesMap);
      createItemCategoryPages(categorizedItems, itemCategoriesMap);
    }
  }

  /**
   * Reads an item file.
   *
   * @param file
   * @return decoded item
   * @throws IOException file not found
   */
  private AethelItem readItemFile(File file) {
    try {
      Scanner scanner = new Scanner(file);
      ItemStack item = ItemReader.decodeItem(scanner.nextLine());
      return new AethelItem(file, ItemReader.readItemName(item), item);
    } catch (IOException ex) {
      return null;
    }
  }

  /**
   * Puts an item into a category if it has an item category tag.
   *
   * @param item             interacting item
   * @param categorizedItems items sorted by category
   */
  private void categorizeItem(ItemStack item, HashMap<String, ArrayList<ItemStack>> categorizedItems) {
    NamespacedKey itemCategoryKey =
        new NamespacedKey(AethelPlugin.getInstance(), "aethel.aitemcat");
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();

    if (dataContainer.has(itemCategoryKey, PersistentDataType.STRING)) {
      String itemCategory = dataContainer.get(itemCategoryKey, PersistentDataType.STRING);

      if (categorizedItems.containsKey(itemCategory)) {
        categorizedItems.get(itemCategory).add(item);
      } else {
        ArrayList<ItemStack> items = new ArrayList();
        items.add(item);
        categorizedItems.put(itemCategory, items);
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
                                  HashMap<String, AethelItemCategory> itemCategoriesMap) {
    int numberOfItems = allItems.size();
    int numberOfPages = PageCalculator.calculateNumberOfPages(numberOfItems);

    ArrayList<Inventory> pages = createItemPages(allItems, numberOfItems, numberOfPages);

    itemCategoriesMap.put("all", new AethelItemCategory("all", pages, numberOfPages));
  }

  /**
   * Creates pages of items by category.
   *
   * @param categorizedItems  items sorted by category
   * @param itemCategoriesMap item category pages
   */
  private void createItemCategoryPages(HashMap<String, ArrayList<ItemStack>> categorizedItems,
                                       HashMap<String, AethelItemCategory> itemCategoriesMap) {
    for (ArrayList<ItemStack> items : categorizedItems.values()) {
      int numberOfItems = items.size();
      int numberOfPages = PageCalculator.calculateNumberOfPages(numberOfItems);

      ArrayList<Inventory> pages = createItemPages(items, numberOfItems, numberOfPages);

      NamespacedKey itemCategoryKey =
          new NamespacedKey(AethelPlugin.getInstance(), "aethel.aitemcat");
      PersistentDataContainer dataContainer = items.get(0).getItemMeta().getPersistentDataContainer();
      String itemCategory = dataContainer.get(itemCategoryKey, PersistentDataType.STRING);

      itemCategoriesMap.put(itemCategory, new AethelItemCategory(itemCategory, pages, numberOfPages));
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
      // i = item index
      // j = inventory slot index

      // Items begin on the second row
      int j = 9;
      for (int i = startIndex; i < endIndex; i++) {
        inv.setItem(j, items.get(i));
        j++;
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

  public HashMap<String, AethelItemCategory> getItemCategoriesMap() {
    return this.itemCategoriesMap;
  }
}
