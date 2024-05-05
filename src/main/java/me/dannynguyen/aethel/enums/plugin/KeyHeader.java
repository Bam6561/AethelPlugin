package me.dannynguyen.aethel.enums.plugin;

import org.jetbrains.annotations.NotNull;

/**
 * Plugin key headers.
 *
 * @author Danny Nguyen
 * @version 1.24.12
 * @since 1.15.3
 */
public enum KeyHeader {
  /**
   * Plugin.
   */
  AETHEL("aethel."),

  /**
   * Tags describing an item's meta.
   */
  ITEM(AETHEL.getHeader() + "item."),

  /**
   * {@link me.dannynguyen.aethel.commands.aethelitem.ItemCommand}
   */
  AETHELITEM(AETHEL.getHeader() + "aethelitem."),

  /**
   * {@link me.dannynguyen.aethel.commands.forge.ForgeCommand}
   */
  FORGE(AETHEL.getHeader() + "forge."),

  /**
   * RPG values.
   */
  RPG(AETHEL.getHeader() + "rpg."),

  /**
   * {@link Key#ATTRIBUTE_LIST}
   */
  ATTRIBUTE(AETHEL.getHeader() + "attribute."),

  /**
   * Enchantment.
   */
  ENCHANTMENT(AETHEL.getHeader() + "enchantment."),

  /**
   * Item {@link Key#PASSIVE_LIST}
   */
  PASSIVE(AETHEL.getHeader() + "passive."),

  /**
   * Item {@link Key#ACTIVE_LIST}
   */
  ACTIVE(AETHEL.getHeader() + "active.");

  /**
   * Key header.
   */
  private final String header;

  /**
   * Associates a key header ID with its header.
   *
   * @param header key header
   */
  KeyHeader(@NotNull String header) {
    this.header = header;
  }

  /**
   * Gets the key header.
   *
   * @return key header
   */
  @NotNull
  public String getHeader() {
    return this.header;
  }
}
