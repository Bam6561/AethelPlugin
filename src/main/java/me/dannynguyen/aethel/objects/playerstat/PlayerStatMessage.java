package me.dannynguyen.aethel.objects.playerstat;

import java.util.List;

/**
 * PlayerStatMessage is an object relating statistics with their message.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.4.10
 */
public record PlayerStatMessage(String statName, List<String> stats) {

  public String getStatName() {
    return this.statName;
  }

  public List<String> getStats() {
    return this.stats;
  }
}
