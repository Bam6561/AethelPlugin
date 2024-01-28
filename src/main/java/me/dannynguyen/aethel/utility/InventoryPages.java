package me.dannynguyen.aethel.utility;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

/**
 * InventoryPages is a utility class that supports
 * double chest sized inventories with useful methods.
 *
 * @author Danny Nguyen
 * @version 1.7.11
 * @since 1.4.2
 */
public final class InventoryPages {
  private InventoryPages() {
  }

  /**
   * Determines how many pages of items exist and whether there are partially filled pages.
   *
   * @param numberOfItems number of items
   * @return number of pages
   */
  public static int calculateNumberOfPages(int numberOfItems) {
    int numberOfPages = numberOfItems / 45;
    if ((numberOfItems % 45) > 0) {
      numberOfPages += 1;
    }
    return numberOfPages;
  }

  /**
   * Determines which page is viewed.
   *
   * @param numberOfPages number of pages
   * @param pageRequest   page to view
   * @return interpreted page to view
   */
  public static int calculatePageViewed(int numberOfPages, int pageRequest) {
    if (numberOfPages > 0) {
      if (pageRequest >= numberOfPages) {
        pageRequest = numberOfPages - 1;
      } else if (pageRequest < 0) {
        pageRequest = 0;
      }
      return pageRequest;
    }
    return 0;
  }

  /**
   * Returns the player to the categories page.
   *
   * @param inv interacting inventory
   */
  public static void addBackButton(Inventory inv, int invSlot) {
    inv.setItem(invSlot, ItemCreator.createPlayerHeadTexture(
        "CHISELED_BOOKSHELF", ChatColor.AQUA + "Back"));
  }

  /**
   * Adds previous and next page buttons based on the page number.
   *
   * @param inv           interacting inventory
   * @param numberOfPages number of pages
   * @param pageViewed    page viewed
   */
  public static void addPageButtons(Inventory inv, int numberOfPages, int pageViewed) {
    if (pageViewed > 0) {
      inv.setItem(0, ItemCreator.createPlayerHeadTexture(
          "RED_BACKWARD", ChatColor.AQUA + "Previous Page"));
    }
    if (numberOfPages - 1 > pageViewed) {
      inv.setItem(8, ItemCreator.
          createPlayerHeadTexture(
              "LIME_FORWARD", ChatColor.AQUA + "Next Page"));
    }
  }
}
