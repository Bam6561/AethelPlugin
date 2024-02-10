package me.dannynguyen.aethel.commands.playerstat;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a player statistic category.
 *
 * @author Danny Nguyen
 * @version 1.9.13
 * @since 1.4.9
 */
public record PlayerStatCategory(@NotNull String name, @NotNull String[] stats) {
  /**
   * Associates player statistics into a category.
   *
   * @param name  category name
   * @param stats player statistics
   */
  public PlayerStatCategory(@NotNull String name, @NotNull String[] stats) {
    this.name = Objects.requireNonNull(name, "Null name");
    this.stats = Objects.requireNonNull(stats, "Null stats");
  }

  /**
   * Gets the category's name.
   *
   * @return category name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the category's player statistics.
   *
   * @return player statistics
   */
  public String[] getStats() {
    return this.stats;
  }
}
