package me.dannynguyen.aethel.objects.playerstats;

import java.util.ArrayList;

/**
 * PlayerStatsCategory is an object relating statistics with their category.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.4.9
 */
public record PlayerStatsCategory(String name, ArrayList<String> stats) {

  public String getName() {
    return this.name;
  }

  public ArrayList<String> getStats() {
    return this.stats;
  }
}
