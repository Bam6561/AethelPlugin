package me.dannynguyen.aethel.objects;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * AethelItemCategory is an object that relates Aethel items with their category pages.
 *
 * @author Danny Nguyen
 * @version 1.5.0
 * @since 1.5.0
 */
public class AethelItemCategory {
  private final String name;
  private final ArrayList<Inventory> pages;
  private final int numberOfPages;

  public AethelItemCategory(String name, ArrayList<Inventory> pages, int numberOfPages) {
    this.name = name;
    this.pages = pages;
    this.numberOfPages = numberOfPages;
  }

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
