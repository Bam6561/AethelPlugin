package me.dannynguyen.aethel.commands.playerstat;

import org.jetbrains.annotations.NotNull;

/**
 * Types of substatistics.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.17.14
 */
enum SubstatisticType {
  /**
   * Entity types.
   */
  ENTITY_TYPES("Entity Types"),

  /**
   * Material types.
   */
  MATERIALS("Materials");

  /**
   * Type's proper name.
   */
  private final String properName;

  /***
   * Associates the type with a proper name.
   * @param properName proper name
   */
  SubstatisticType(String properName) {
    this.properName = properName;
  }

  /**
   * Gets the type's proper name.
   *
   * @return type's proper name
   */
  @NotNull
  public String getProperName() {
    return this.properName;
  }
}
