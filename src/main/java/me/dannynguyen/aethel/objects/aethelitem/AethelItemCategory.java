package me.dannynguyen.aethel.objects.aethelitem;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * AethelItemCategory is an object that relates Aethel items with their category pages.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.5.0
 */
public record AethelItemCategory(String name, ArrayList<Inventory> pages,
                                 int numberOfPages) {

  public String getName() {
    return name;
  }

  public ArrayList<Inventory> getPages() {
    return pages;
  }

  public int getNumberOfPages() {
    return numberOfPages;
  }
}
