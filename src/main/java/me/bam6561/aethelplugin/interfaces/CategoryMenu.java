package me.bam6561.aethelplugin.interfaces;

import org.bukkit.inventory.Inventory;

/**
 * Plugin menu that contains pages of categories.
 *
 * @author Danny Nguyen
 * @version 1.17.5
 * @since 1.17.5
 */
public interface CategoryMenu extends Menu {
  /**
   * Populates the menu with items belonging to the category and page.
   *
   * @param requestedCategory requested category
   * @param requestedPage     requested page
   * @return category page
   */
  Inventory getCategoryPage(String requestedCategory, int requestedPage);
}
