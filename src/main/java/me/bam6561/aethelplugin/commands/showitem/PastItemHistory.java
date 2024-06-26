package me.bam6561.aethelplugin.commands.showitem;

import me.bam6561.aethelplugin.utils.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * Represents past shown items in memory.
 *
 * @author Danny Nguyen
 * @version 1.9.19
 * @since 1.4.5
 */
public class PastItemHistory {
  /**
   * Past shown items.
   */
  private final Queue<ItemStack> pastItems = new LinkedList<>();

  /**
   * No parameter constructor.
   */
  public PastItemHistory() {
  }

  /**
   * Adds the item to past item history and ensures the number of
   * past items never exceeds 27 ({@link PastItemMenu}'s size).
   *
   * @param user item owner
   * @param item original item
   */
  protected void addPastItem(@NotNull Player user, @NotNull ItemStack item) {
    ItemStack pastItem = Objects.requireNonNull(item, "Null item").clone();
    ItemMeta meta = pastItem.getItemMeta();
    meta.setDisplayName(ChatColor.DARK_PURPLE + Objects.requireNonNull(user, "Null user").getName() + ChatColor.WHITE + " " + ItemReader.readName(pastItem));
    pastItem.setItemMeta(meta);

    if (pastItems.size() == 27) {
      pastItems.remove();
    }
    pastItems.add(pastItem);
  }

  /**
   * Gets past shown items.
   *
   * @return past shown items
   */
  @NotNull
  protected Queue<ItemStack> getPastItems() {
    return this.pastItems;
  }
}
