package me.dannynguyen.aethel.commands.forge.objects;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * ForgeRecipeCategory is an object that relates forge recipes with their category and page number.
 *
 * @author Danny Nguyen
 * @version 1.7.13
 * @since 1.5.4
 */
public record ForgeRecipeCategory(String name, ArrayList<Inventory> pages,
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
