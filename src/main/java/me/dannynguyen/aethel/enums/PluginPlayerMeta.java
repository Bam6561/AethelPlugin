package me.dannynguyen.aethel.enums;

/**
 * PluginPlayerMeta is an enum collection containing the plugin's accessed player metadata types.
 *
 * @author Danny Nguyen
 * @version 1.8.4
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
}
