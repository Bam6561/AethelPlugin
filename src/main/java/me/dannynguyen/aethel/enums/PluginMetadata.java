package me.dannynguyen.aethel.enums;

/**
 * PluginMetadata is an enum containing the plugin's commonly accessed player metadata.
 *
 * @author Danny Nguyen
 * @version 1.7.6
 * @since 1.7.6
 */
public enum PluginMetadata {
  // Metadata
  INVENTORY("inventory"),
  CATEGORY("category"),
  PAGE("page"),
  DEVELOPER("developer"),
  FUTURE("future"),
  PLAYER("player"),

  // Value
  AETHELITEMS_CATEGORY("aethelitems.category"),
  CHARACTER_SHEET("character.sheet"),
  FORGE_CATEGORY("forge.category"),
  ITEMEDITOR_MENU("itemeditor.menu"),
  PLAYERSTATS_CATEGORY("playerstats.category"),
  PLAYERSTATS_PAST("playerstats.past"),
  SHOWITEM_PAST("showitem.past");

  public final String data;

  PluginMetadata(String metadata) {
    this.data = metadata;
  }
}
