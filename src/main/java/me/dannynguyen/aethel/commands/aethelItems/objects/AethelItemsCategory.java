package me.dannynguyen.aethel.commands.aethelItems.objects;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * AethelItemsCategory is an object that relates Aethel items with their category pages.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.5.0
 */
public record AethelItemsCategory(String name, ArrayList<Inventory> pages,
                                  int numberOfPages) {

  public String getName() {
    return this.name;
  }

  public ArrayList<Inventory> getPages() {
    return this.pages;
  }

  public int getNumberOfPages() {
    return this.numberOfPages;
  }
}
