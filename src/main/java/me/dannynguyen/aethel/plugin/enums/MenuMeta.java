package me.dannynguyen.aethel.plugin.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Plugin menu metadata.
 *
 * @author Danny Nguyen
 * @version 1.15.0
 * @since 1.10.1
 */
public enum MenuMeta {
  /**
   * View {@link me.dannynguyen.aethel.commands.aethelitem.PersistentItem item} categories.
   */
  AETHELITEM_CATEGORY("aethelitem.category"),

  /**
   * Get {@link me.dannynguyen.aethel.commands.aethelitem.PersistentItem items}.
   */
  AETHELITEM_GET("aethelitem.get"),

  /**
   * Remove {@link me.dannynguyen.aethel.commands.aethelitem.PersistentItem items}.
   */
  AETHELITEM_REMOVE("aethelitem.remove"),

  /**
   * Interact with {@link me.dannynguyen.aethel.commands.character.SheetMenu}.
   */
  CHARACTER_SHEET("character.sheet"),

  /**
   * View quests.
   */
  CHARACTER_QUESTS("character.quests"),

  /**
   * View collectibles.
   */
  CHARACTER_COLLECTIBLES("character.collectibles"),

  /**
   * Interact with {@link me.dannynguyen.aethel.rpg.system.Settings RPG settings}.
   */
  CHARACTER_SETTINGS("character.settings"),

  /**
   * View {@link me.dannynguyen.aethel.commands.forge.PersistentRecipe recipe} categories.
   */
  FORGE_CATEGORY("forge.category"),

  /**
   * Craft {@link me.dannynguyen.aethel.commands.forge.PersistentRecipe recipes}.
   */
  FORGE_CRAFT("forge.craft"),

  /**
   * Craft {@link me.dannynguyen.aethel.commands.forge.PersistentRecipe recipe} operation.
   */
  FORGE_CRAFT_RECIPE("forge.craft-recipe"),

  /**
   * Edit {@link me.dannynguyen.aethel.commands.forge.PersistentRecipe recipes}.
   */
  FORGE_EDIT("forge.edit"),

  /**
   * Remove {@link me.dannynguyen.aethel.commands.forge.PersistentRecipe recipes}.
   */
  FORGE_REMOVE("forge.remove"),

  /**
   * Save {@link me.dannynguyen.aethel.commands.forge.PersistentRecipe recipes}.
   */
  FORGE_SAVE("forge.save"),

  /**
   * Edit item cosmetics.
   */
  ITEMEDITOR_COSMETIC("itemeditor.cosmetic"),

  /**
   * Edit item Minecraft attributes.
   */
  ITEMEDITOR_MINECRAFT_ATTRIBUTE("itemeditor.minecraft_attribute"),

  /**
   * Edit item {@link me.dannynguyen.aethel.rpg.enums.AethelAttributeType Aethel attributes}.
   */
  ITEMEDITOR_AETHEL_ATTRIBUTE("itemeditor.aethel_attribute"),

  /**
   * Edit item enchantments.
   */
  ITEMEDITOR_ENCHANTMENT("itemeditor.enchantment"),

  /**
   * Edit item potion effects.
   */
  ITEMEDITOR_POTION("itemeditor.potion"),

  /**
   * Edit item {@link me.dannynguyen.aethel.rpg.enums.PassiveAbilityType passive abilities}.
   */
  ITEMEDITOR_PASSIVE("itemeditor.passive"),

  /**
   * Edit item {@link me.dannynguyen.aethel.rpg.enums.ActiveAbilityType active abilities}.
   */
  ITEMEDITOR_ACTIVE("itemeditor.active"),

  /**
   * Edit item {@link PluginNamespacedKey Aethel tags}.
   */
  ITEMEDITOR_TAG("itemeditor.tag"),

  /**
   * View stat categories.
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
  @NotNull
  public String getMeta() {
    return this.meta;
  }
}
