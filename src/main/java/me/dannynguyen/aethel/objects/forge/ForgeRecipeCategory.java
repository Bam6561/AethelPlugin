package me.dannynguyen.aethel.objects.forge;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * ForgeRecipeCategory is an object that relates forge recipes with their category pages.
 *
 * @author Danny Nguyen
 * @version 1.5.4
 * @since 1.5.4
 */
public class ForgeRecipeCategory {
  private final String name;
  private final ArrayList<Inventory> pages;
  private final int numberOfPages;

  public ForgeRecipeCategory(String name, ArrayList<Inventory> pages, int numberOfPages) {
    this.name = name;
    this.pages = pages;
    this.numberOfPages = numberOfPages;
  }

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
