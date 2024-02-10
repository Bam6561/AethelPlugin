package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.utility.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents past shared player stats in memory.
 *
 * @author Danny Nguyen
 * @version 1.9.13
 * @since 1.4.5
 */
public class PastStatHistory {
  /**
   * Past shared player stats.
   */
  private final Queue<ItemStack> pastStats = new LinkedList<>();

  /**
   * Adds the stat to past stat history and ensures the number
   * of past stats never exceeds 27 (PlayerStatPast menu's size).
   *
   * @param name   stat owner
   * @param values stat values
   */
  public void addPastStat(String name, List<String> values) {
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
  public Queue<ItemStack> getPastStats() {
    return this.pastStats;
  }
}
