package me.dannynguyen.aethel.objects.playerstat;

import java.util.List;

/**
 * PlayerStatMessage is an object relating statistics with their values.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.4.10
 */
public record PlayerStatValues(String name, List<String> values) {

  public String getName() {
    return this.name;
  }

  public List<String> getValues() {
    return this.values;
  }
}
