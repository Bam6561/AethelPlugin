package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.util.item.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents past shared player stats in memory.
 *
 * @author Danny Nguyen
 * @version 1.9.19
 * @since 1.4.5
 */
public class PastStatHistory {
  /**
   * Past shared player stats.
   */
  private final Queue<ItemStack> pastStats = new LinkedList<>();

  /**
   * No parameter constructor.
   */
  public PastStatHistory() {
  }

  /**
   * Adds the stat to past stat history and ensures the number
   * of past stats never exceeds 27 (PastStat menu's size).
   *
   * @param name   stat owner
   * @param values stat values
   */
  protected void addPastStat(String name, List<String> values) {
    ItemStack stat = ItemCreator.createItem(Material.PAPER, name, values);
    if (pastStats.size() == 27) {
      pastStats.remove();
    }
    pastStats.add(stat);
  }

  /**
   * Gets past shown stats.
   *
   * @return past shown stats
   */
  protected Queue<ItemStack> getPastStats() {
    return this.pastStats;
  }
}
