package me.dannynguyen.aethel.commands.forge.object;

import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * ForgeRecipeCategory is an object relating forge recipes with their category and page number.
 *
 * @author Danny Nguyen
 * @version 1.8.2
 * @since 1.5.4
 */
public record ForgeRecipeCategory(String name, List<Inventory> pages,
                                  int numberOfPages) {

  public String getName() {
    return this.name;
  }

  public List<Inventory> getPages() {
    return this.pages;
  }

  public int getNumberOfPages() {
    return this.numberOfPages;
  }
}
