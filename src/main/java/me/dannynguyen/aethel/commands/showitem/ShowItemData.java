package me.dannynguyen.aethel.commands.showitem;

import me.dannynguyen.aethel.commands.showitem.object.ItemOwner;

import java.util.ArrayList;

/**
 * ShowItemData stores past shown items in memory.
 *
 * @author Danny Nguyen
 * @version 1.5.0
 * @since 1.4.5
 */
public class ShowItemData {
  private final ArrayList<ItemOwner> pastItems = new ArrayList<>();

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
