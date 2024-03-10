package me.dannynguyen.aethel.systems.plugin;

import org.jetbrains.annotations.NotNull;

/**
 * Plugin key headers.
 *
 * @author Danny Nguyen
 * @version 1.15.3
 * @since 1.15.3
 */
public enum KeyHeader {
  /**
   * Plugin.
   */
  AETHEL("aethel."),

  /**
   * AethelItem command.
   */
  AETHELITEM(AETHEL.getHeader() + "aethelitem."),

  /**
   * Forge command.
   */
  FORGE(AETHEL.getHeader() + "forge."),

  /**
   * Aethel attribute.
   */
  ATTRIBUTE(AETHEL.getHeader() + "attribute."),

  /**
   * Item passive ability.
   */
  PASSIVE(AETHEL.getHeader() + "passive."),

  /**
   * Item active ability.
   */
  ACTIVE(AETHEL.getHeader() + "active.");

  /**
   * Key header.
   */
  private String header;

  /**
   * Associates a key header ID with its header.
   *
   * @param header
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
