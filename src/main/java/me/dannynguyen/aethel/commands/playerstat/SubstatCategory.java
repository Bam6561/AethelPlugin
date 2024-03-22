package me.dannynguyen.aethel.commands.playerstat;

import org.jetbrains.annotations.NotNull;

/**
 * Player substatistic categories.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.17.14
 */
enum SubstatCategory {
  /**
   * Entity types.
   */
  ENTITY_TYPES("Entity Types"),

  /**
   * Material types.
   */
  MATERIALS("Materials");

  /**
   * Category's proper name.
   */
  private final String properName;

  /***
   * Associates the category with a proper name.
   * @param properName proper name
   */
  SubstatCategory(String properName) {
    this.properName = properName;
  }

  /**
   * Gets the category's proper name.
   *
   * @return category's proper name
   */
  @NotNull
  public String getProperName() {
    return this.properName;
  }
}
