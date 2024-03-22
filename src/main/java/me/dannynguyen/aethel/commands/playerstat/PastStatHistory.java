package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.util.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
   * of past stats never exceeds 27 ({@link PastStatMenu}'s size).
   *
   * @param name   stat owner
   * @param values stat values
   */
  protected void addPastStat(@NotNull String name, @NotNull List<String> values) {
    ItemStack stat = ItemCreator.createItem(Material.PAPER, Objects.requireNonNull(name, "Null name"), Objects.requireNonNull(values, "Null values"));
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
  @NotNull
  protected Queue<ItemStack> getPastStats() {
    return this.pastStats;
  }
}
