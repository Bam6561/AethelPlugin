package me.dannynguyen.aethel.objects.playerstat;

import java.util.ArrayList;

/**
 * PlayerStatCategory is an object relating statistics with their category.
 *
 * @author Danny Nguyen
 * @version 1.4.12
 * @since 1.4.9
 */
public class PlayerStatCategory {
  private final String name;
  private final ArrayList<String> stats;

  public PlayerStatCategory(String name, ArrayList<String> stats) {
    this.name = name;
    this.stats = stats;
  }

  public String getName() {
    return this.name;
  }

  public ArrayList<String> getStats() {
    return this.stats;
  }
}
