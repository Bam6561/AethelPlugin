package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.PageCalculator;
import me.dannynguyen.aethel.objects.AethelItem;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * AethelItem contains information about Aethel items stored in memory.
 *
 * @author Danny Nguyen
 * @version 1.4.12
 * @since 1.3.2
 */
public class AethelItemData {
  private ArrayList<AethelItem> items = new ArrayList<>();
  private HashMap<String, AethelItem> itemsMap = new HashMap<>();
  private ArrayList<Inventory> itemPages = new ArrayList<>();
  private int numberOfPages = 0;

  /**
   * Loads Aethel items into memory.
   */
  public void loadItems() {
    ArrayList<AethelItem> items = getItems();
    HashMap<String, AethelItem> itemsMap = getItemsMap();

    items.clear();
    itemsMap.clear();
    getItemPages().clear();

    File[] directory = new File(AethelPlugin.getInstance().getResources().getAethelItemDirectory()).listFiles();
    Collections.sort(Arrays.asList(directory));
    for (File file : directory) {
      if (file.getName().endsWith("_itm.txt")) {
        AethelItem item = readItemFile(file);
        items.add(item);
        itemsMap.put(item.getName(), item);
      }
    }
    createItemPages();
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
   * Creates pages of items.
   */
  private void createItemPages() {
    ArrayList<AethelItem> items = getItems();
    ArrayList<Inventory> itemPages = getItemPages();

    int numberOfItems = items.size();
    int numberOfPages = PageCalculator.calculateNumberOfPages(numberOfItems);
    setNumberOfPages(numberOfPages);

    int startIndex = 0;
    int endIndex = Math.min(numberOfItems, 45);

    for (int page = 0; page < numberOfPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54, "Aethel Item Page");
      // i = item index
      // j = inventory slot index

      // Items begin on the second row
      int j = 9;
      for (int i = startIndex; i < endIndex; i++) {
        inv.setItem(j, items.get(i).getItem());
        j++;
      }
      itemPages.add(inv);

      // Indices to use for the next page (if it exists)
      startIndex += 45;
      endIndex = Math.min(numberOfItems, endIndex + 45);
    }
  }

  public ArrayList<AethelItem> getItems() {
    return this.items;
  }

  public HashMap<String, AethelItem> getItemsMap() {
    return this.itemsMap;
  }

  public ArrayList<Inventory> getItemPages() {
    return this.itemPages;
  }

  public int getNumberOfPages() {
    return this.numberOfPages;
  }

  private void setNumberOfPages(int numberOfPages) {
    this.numberOfPages = numberOfPages;
  }
}
