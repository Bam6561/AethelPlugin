package me.dannynguyen.aethel.enums;

/**
 * PluginPermission is an enum containing the plugin's permission nodes.
 *
 * @author Danny Nguyen
 * @version 1.7.6
 * @since 1.7.6
 */
public enum PluginPermission {
  AETHELITEMS("aethel.aethelitems"),
  AETHELTAGS("aethel.aetheltags"),
  CHARACTER("aethel.character"),
  DEVELOPERMODE("aethel.developermode"),
  FORGE("aethel.forge"),
  FORGE_EDITOR("aethel.forge.editor"),
  ITEMEDITOR("aethel.itemeditor"),
  PING("aethel.ping"),
  PLAYERSTATS("aethel.playerstats"),
  SHOWITEM("aethel.showitem"),
  TEMPLATE("");

  public final String permission;

  PluginPermission(String permission) {
    this.permission = permission;
  }
}
