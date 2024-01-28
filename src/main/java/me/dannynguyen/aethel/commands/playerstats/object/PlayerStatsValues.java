package me.dannynguyen.aethel.commands.playerstats.object;

import java.util.List;

/**
 * PlayerStatsValues is an object relating player statistics with their values.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.4.10
 */
public record PlayerStatsValues(String name, List<String> values) {

  public String getName() {
    return this.name;
  }

  public List<String> getValues() {
    return this.values;
  }
}
