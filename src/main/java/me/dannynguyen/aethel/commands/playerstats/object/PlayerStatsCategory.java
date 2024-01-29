package me.dannynguyen.aethel.commands.playerstats.object;

import java.util.List;

/**
 * PlayerStatsCategory is an object relating player statistics with their category.
 *
 * @author Danny Nguyen
 * @version 1.8.1
 * @since 1.4.9
 */
public record PlayerStatsCategory(String name, List<String> stats) {

  public String getName() {
    return this.name;
  }

  public List<String> getStats() {
    return this.stats;
  }
}
