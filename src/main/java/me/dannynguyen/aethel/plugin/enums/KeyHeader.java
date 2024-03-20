package me.dannynguyen.aethel.plugin.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Plugin key headers.
 *
 * @author Danny Nguyen
 * @version 1.15.5
 * @since 1.15.3
 */
public enum KeyHeader {
  /**
   * Plugin.
   */
  AETHEL("aethel."),

  /**
   * {@link me.dannynguyen.aethel.commands.aethelitem.ItemCommand AethelItem} command.
   */
  AETHELITEM(AETHEL.getHeader() + "aethelitem."),

  /**
   * {@link me.dannynguyen.aethel.commands.forge.ForgeCommand Forge} command.
   */
  FORGE(AETHEL.getHeader() + "forge."),

  /**
   * {@link me.dannynguyen.aethel.rpg.enums.AethelAttributeType Aethel attribute}.
   */
  ATTRIBUTE(AETHEL.getHeader() + "attribute."),

  /**
   * Item {@link me.dannynguyen.aethel.rpg.enums.PassiveAbilityType passive ability}.
   */
  PASSIVE(AETHEL.getHeader() + "passive."),

  /**
   * Item {@link me.dannynguyen.aethel.rpg.enums.ActiveAbilityType active ability}.
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
