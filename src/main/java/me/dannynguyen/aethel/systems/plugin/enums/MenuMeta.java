package me.dannynguyen.aethel.systems.plugin.enums;

/**
 * Plugin menu metadata.
 *
 * @author Danny Nguyen
 * @version 1.10.5
 * @since 1.10.1
 */
public enum MenuMeta {
  /**
   * View item categories.
   */
  AETHELITEM_CATEGORY("aethelitem.category"),

  /**
   * Get items.
   */
  AETHELITEM_GET("aethelitem.get"),

  /**
   * Remove items.
   */
  AETHELITEM_REMOVE("aethelitem.remove"),

  /**
   * Interact with character sheet.
   */
  CHARACTER_SHEET("character.sheet"),

  /**
   * Interact with RPG settings.
   */
  CHARACTER_SETTINGS("character.settings"),

  /**
   * View recipe categories.
   */
  FORGE_CATEGORY("forge.category"),

  /**
   * Craft recipes.
   */
  FORGE_CRAFT("forge.craft"),

  /**
   * Craft recipe operation.
   */
  FORGE_CRAFT_RECIPE("forge.craft-recipe"),

  /**
   * Edit recipes.
   */
  FORGE_EDIT("forge.edit"),

  /**
   * Remove recipes.
   */
  FORGE_REMOVE("forge.remove"),

  /**
   * Save recipes.
   */
  FORGE_SAVE("forge.save"),

  /**
   * Edit item attributes.
   */
  ITEMEDITOR_ATTRIBUTE("itemeditor.attribute"),

  /**
   * Edit item cosmetics.
   */
  ITEMEDITOR_COSMETIC("itemeditor.cosmetic"),

  /**
   * Edit item enchantments.
   */
  ITEMEDITOR_ENCHANTMENT("itemeditor.enchantment"),

  /**
   * Edit item tags.
   */
  ITEMEDITOR_TAG("itemeditor.tag"),

  /**
   * View categories.
   */
  PLAYERSTAT_CATEGORY("playerstat.category"),

  /**
   * View past stats.
   */
  PLAYERSTAT_PAST("playerstat.past"),

  /**
   * Interact with statistics.
   */
  PLAYERSTAT_STAT("playerstat.stat"),

  /**
   * Interact with sub-statistics.
   */
  PLAYERSTAT_SUBSTAT("playerstat.substat"),

  /**
   * View past shown items.
   */
  SHOWITEM_PAST("showitem.past");

  /**
   * Metadata value.
   */
  private final String meta;

  /**
   * Associates a menu metadata with its value.
   *
   * @param meta metadata value
   */
  MenuMeta(String meta) {
    this.meta = meta;
  }

  /**
   * Gets the metadata value.
   *
   * @return metadata value
   */
  public String getMeta() {
    return this.meta;
  }
}
