package me.dannynguyen.aethel.commands.showitem;

import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents past shown items.
 *
 * @author Danny Nguyen
 * @version 1.9.12
 * @since 1.4.5
 */
public class PastItemHistory {
  /**
   * Past shown items.
   */
  private final Queue<ItemStack> pastItems = new LinkedList<>();

  /**
   * Ensures the number of past items never exceeds 27 (ShowItemPast menu's size).
   *
   * @param item past item
   */
  public void addPastItem(ItemStack item) {
    if (pastItems.size() == 27) {
      pastItems.remove();
    }
    pastItems.add(item);
  }

  /**
   * Gets past shown items.
   *
   * @return past shown items
   */
  public Queue<ItemStack> getPastItems() {
    return this.pastItems;
  }
}
