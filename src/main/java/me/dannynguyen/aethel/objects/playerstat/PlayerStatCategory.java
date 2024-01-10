package me.dannynguyen.aethel.objects.playerstat;

import java.util.ArrayList;

/**
 * PlayerStatCategory is an object relating statistics with their category.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.4.9
 */
public record PlayerStatCategory(String name, ArrayList<String> stats) {

  public String getName() {
    return this.name;
  }

  public ArrayList<String> getStats() {
    return this.stats;
  }
}
