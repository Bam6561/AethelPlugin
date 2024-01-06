package me.dannynguyen.aethel.objects;

import java.util.ArrayList;

/**
 * PlayerStatCategory is an object relating statistics with their category.
 *
 * @author Danny Nguyen
 * @version 1.4.9
 * @since 1.4.9
 */
public class PlayerStatCategory {
  private String name;
  private ArrayList<String> stats;

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
