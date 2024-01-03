package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.AethelItem;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * AethelItem contains information about Aethel items stored in memory.
 *
 * @author Danny Nguyen
 * @verison 1.3.2
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
    /*ArrayList<AethelItem> items = getItems();
    HashMap<String, AethelItem> itemsMap = getItemsMap();

    items.clear();
    itemsMap.clear();

    File[] directory = new File(AethelPlugin.getInstance().getResources().getAethelItemDirectory()).listFiles();
    Collections.sort(Arrays.asList(directory));
    for (int i = 0; i < directory.length; i++) {
      AethelItem item = new AethelItemReader().readItem(directory[i]);
      items.add(item);
      itemsMap.put(item.getName(), item);
    }
    createItemPages();*/
  }

  private ArrayList<AethelItem> getItems() {
    return this.items;
  }

  private HashMap<String, AethelItem> getItemsMap() {
    return this.itemsMap;
  }

  private ArrayList<Inventory> getItemPages() {
    return this.itemPages;
  }

  private int getNumberOfPages() {
    return this.numberOfPages;
  }

  private void setNumberOfPages(int numberOfPages) {
    this.numberOfPages = numberOfPages;
  }
}
