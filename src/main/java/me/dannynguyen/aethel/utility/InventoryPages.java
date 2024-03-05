package me.dannynguyen.aethel.utility;

import me.dannynguyen.aethel.systems.plugin.PluginPlayerHead;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Provides double chest sized inventories with useful methods.
 *
 * @author Danny Nguyen
 * @version 1.13.7
 * @since 1.4.2
 */
public class InventoryPages {
  /**
   * Utility methods only.
   */
  private InventoryPages() {
  }

  /**
   * Determines how many pages of items exist and whether there are partially filled pages.
   *
   * @param numberOfItems number of items
   * @return number of pages
   */
  public static int calculateTotalPages(int numberOfItems) {
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
   * @param requestedPage requested page
   * @return interpreted page to view
   */
  public static int calculatePageViewed(int numberOfPages, int requestedPage) {
    if (numberOfPages > 0) {
      if (requestedPage >= numberOfPages) {
        requestedPage = numberOfPages - 1;
      } else if (requestedPage < 0) {
        requestedPage = 0;
      }
      return requestedPage;
    }
    return 0;
  }

  /**
   * Returns the player to the categories page.
   *
   * @param inv     interacting inventory
   * @param invSlot inventory slot
   */
  public static void addBackButton(@NotNull Inventory inv, int invSlot) {
    Objects.requireNonNull(inv, "Null inventory");
    inv.setItem(invSlot, ItemCreator.createPluginPlayerHead(PluginPlayerHead.CHISELED_BOOKSHELF.getHead(), ChatColor.AQUA + "Back"));
  }

  /**
   * Adds previous and next page buttons based on the page number.
   *
   * @param inv           interacting inventory
   * @param numberOfPages number of pages
   * @param pageViewed    page viewed
   */
  public static void addPageButtons(@NotNull Inventory inv, int numberOfPages, int pageViewed) {
    Objects.requireNonNull(inv, "Null inventory");
    if (pageViewed > 0) {
      inv.setItem(0, ItemCreator.createPluginPlayerHead(PluginPlayerHead.BACKWARD_RED.getHead(), ChatColor.AQUA + "Previous Page"));
    }
    if (numberOfPages - 1 > pageViewed) {
      inv.setItem(8, ItemCreator.createPluginPlayerHead(PluginPlayerHead.FORWARD_LIME.getHead(), ChatColor.AQUA + "Next Page"));
    }
  }
}
