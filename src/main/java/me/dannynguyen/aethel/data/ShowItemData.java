package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.objects.ItemOwner;

import java.util.ArrayList;

/**
 * ShowItemData contains information about the past shown items to chat.
 *
 * @author Danny Nguyen
 * @version 1.4.14
 * @since 1.4.5
 */
public class ShowItemData {
  private ArrayList<ItemOwner> pastItems = new ArrayList<>();

  /**
   * Ensures the number of past items never exceeds 9 (the ShowItemPast inventory size).
   *
   * @param itemOwner past item
   */
  public void addPastItem(ItemOwner itemOwner) {
    ArrayList<ItemOwner> pastItems = getPastItems();
    if (pastItems.size() == 9) {
      pastItems.remove(0);
    }
    pastItems.add(itemOwner);
  }

  public ArrayList<ItemOwner> getPastItems() {
    return this.pastItems;
  }
}
