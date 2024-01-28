package me.dannynguyen.aethel.enums;

/**
 * PluginPlayerMeta is an enum collection containing the plugin's commonly accessed player metadata.
 *
 * @author Danny Nguyen
 * @version 1.7.11
 * @since 1.7.6
 */
public class PluginPlayerMeta {
  /**
   * Metadata namespaces.
   */
  public enum Namespace {
    CATEGORY("category"),
    DEVELOPER("developer"),
    FUTURE("future"),
    INVENTORY("inventory"),
    MESSAGE("message"),
    PAGE("page"),
    PLAYER("player"),
    SLOT("slot"),
    TYPE("type");

    public final String namespace;

    Namespace(String namespace) {
      this.namespace = namespace;
    }
  }

  /**
   * Currently open inventory.
   */
  public enum Inventory {
    AETHELITEMS_CATEGORY("aethelitems.category"),
    AETHELITEMS_GET("aethelitems.get"),
    AETHELITEMS_REMOVE("aethelitems.remove"),
    CHARACTER_SHEET("character.sheet"),
    FORGE_CATEGORY("forge.category"),
    FORGE_CRAFT("forge.craft"),
    FORGE_CRAFT_CONFIRM("forge.craft-confirm"),
    FORGE_EDIT("forge.edit"),
    FORGE_REMOVE("forge.remove"),
    FORGE_SAVE("forge.save"),
    ITEMEDITOR_ATTRIBUTES("itemeditor.attributes"),
    ITEMEDITOR_COSMETICS("itemeditor.cosmetics"),
    ITEMEDITOR_ENCHANTS("itemeditor.enchants"),
    ITEMEDITOR_TAGS("itemeditor.tags"),
    PLAYERSTATS_CATEGORY("playerstats.category"),
    PLAYERSTATS_PAST("playerstats.past"),
    PLAYERSTATS_STAT("playerstats.stat"),
    PLAYERSTATS_SUBSTAT("playerstats.substat"),
    SHOWITEM_PAST("showitem.past");

    public final String inventory;

    Inventory(String inventory) {
      this.inventory = inventory;
    }
  }
}
