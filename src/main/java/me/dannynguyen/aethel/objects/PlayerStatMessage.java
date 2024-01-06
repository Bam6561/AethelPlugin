package me.dannynguyen.aethel.objects;

import java.util.List;

/**
 * PlayerStatMessage is an object relating statistics with their message.
 *
 * @author Danny Nguyen
 * @version 1.4.10
 * @since 1.4.10
 */
public class PlayerStatMessage {
  private String statName;
  private List<String> stats;

  public PlayerStatMessage(String statName, List<String> stats) {
    this.statName = statName;
    this.stats = stats;
  }

  public String getStatName() {
    return this.statName;
  }

  public List<String> getStats() {
    return this.stats;
  }
}
