package me.dannynguyen.aethel.enums;

/**
 * PluginPlayerMeta is an enum collection containing the plugin's commonly accessed player metadata.
 *
 * @author Danny Nguyen
 * @version 1.7.10
 * @since 1.7.6
 */
public class PluginPlayerMeta {
  public enum Container {
    INVENTORY("inventory"),
    CATEGORY("category"),
    PAGE("page"),
    DEVELOPER("developer"),
    FUTURE("future"),
    PLAYER("player");

    public final String name;

    Container(String name) {
      this.name = name;
    }
  }

  public enum Value {
    AETHELITEMS_CATEGORY("aethelitems.category"),
    AETHELITEMS_GET("aethelitems.get"),
    AETHELITEMS_REMOVE("aethelitems.remove"),
    CHARACTER_SHEET("character.sheet"),
    FORGE_CATEGORY("forge.category"),
    ITEMEDITOR_MENU("itemeditor.menu"),
    PLAYERSTATS_CATEGORY("playerstats.category"),
    PLAYERSTATS_PAST("playerstats.past"),
    SHOWITEM_PAST("showitem.past");

    public final String value;

    Value(String value) {
      this.value = value;
    }
  }
}
