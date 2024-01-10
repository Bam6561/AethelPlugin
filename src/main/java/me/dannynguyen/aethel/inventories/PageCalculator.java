package me.dannynguyen.aethel.inventories;

/**
 * PageCalculator is a supporting object for double chest sized inventories with pagination.
 *
 * @author Danny Nguyen
 * @version 1.4.12
 * @since 1.4.2
 */
public class PageCalculator {
  /**
   * Determines how many pages of items exist and whether there are partially filled pages.
   *
   * @param numberOfItems number of items
   * @return number of pages
   */
  public static int calculateNumberOfPages(int numberOfItems) {
    int numberOfPages = numberOfItems / 45;
    boolean partiallyFilledPage = (numberOfItems % 45) > 0;
    if (partiallyFilledPage) {
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
      boolean requestMoreThanTotalPages = pageRequest >= numberOfPages;
      boolean requestNegativePageNumber = pageRequest < 0;
      if (requestMoreThanTotalPages) {
        pageRequest = numberOfPages - 1;
      } else if (requestNegativePageNumber) {
        pageRequest = 0;
      }
      return pageRequest;
    }
    return 0;
  }
}
