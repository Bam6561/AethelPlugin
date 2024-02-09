package me.dannynguyen.aethel.enums;

/**
 * PluginPlayerMeta is a collection of enums containing the plugin's accessed player metadata types.
 *
 * @author Danny Nguyen
 * @version 1.9.7
 * @since 1.7.6
 */
public enum PluginPlayerMeta {
  CATEGORY("category"),
  DEVELOPER("developer"),
  FUTURE("future"),
  INVENTORY("inventory"),
  MESSAGE("message"),
  PAGE("page"),
  PLAYER("player"),
  SLOT("slot"),
  TYPE("type");

  private final String meta;

  PluginPlayerMeta(String meta) {
    this.meta = meta;
  }

  public String getMeta() {
    return this.meta;
  }
}

