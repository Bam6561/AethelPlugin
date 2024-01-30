package me.dannynguyen.aethel.commands.playerstats.object;

/**
 * PlayerStatsCategory is an object relating player statistics with their category.
 *
 * @author Danny Nguyen
 * @version 1.8.3
 * @since 1.4.9
 */
public record PlayerStatsCategory(String name, String[] stats) {

  public String getName() {
    return this.name;
  }

  public String[] getStats() {
    return this.stats;
  }
}
