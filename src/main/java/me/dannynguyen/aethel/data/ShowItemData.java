package me.dannynguyen.aethel.data;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * ShowItemData contains information about the past shown items to chat.
 *
 * @author Danny Nguyen
 * @version 1.4.10
 * @since 1.4.5
 */
public class ShowItemData {
  private ArrayList<ItemStack> pastItems = new ArrayList<>();

  /**
   * Ensures the number of past items never exceeds 9 (the ShowItemPast inventory size).
   *
   * @param item interacting item
   */
  public void addPastItem(ItemStack item) {
    ArrayList<ItemStack> pastItems = getPastItems();
    if (pastItems.size() == 9) {
      pastItems.remove(0);
    }
    pastItems.add(item);
  }

  public ArrayList<ItemStack> getPastItems() {
    return this.pastItems;
  }
}
