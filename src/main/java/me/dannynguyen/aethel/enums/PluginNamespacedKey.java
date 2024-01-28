package me.dannynguyen.aethel.enums;

import me.dannynguyen.aethel.Plugin;
import org.bukkit.NamespacedKey;

/**
 * PluginNamespacedKey is an enum containing the plugin's namespaced keys.
 *
 * @author Danny Nguyen
 * @version 1.7.8
 * @since 1.7.8
 */
public enum PluginNamespacedKey {
  AETHELITEM_CATEGORY(new NamespacedKey(Plugin.getInstance(), "aethel.aethelitem.category")),
  FORGE_CATEGORY(new NamespacedKey(Plugin.getInstance(), "aethel.forge.category")),
  FORGE_ID(new NamespacedKey(Plugin.getInstance(), "aethel.forge.id"));

  public final NamespacedKey namespacedKey;

  PluginNamespacedKey(NamespacedKey namespacedKey) {
    this.namespacedKey = namespacedKey;
  }
}
