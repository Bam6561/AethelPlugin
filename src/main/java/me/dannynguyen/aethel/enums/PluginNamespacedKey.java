package me.dannynguyen.aethel.enums;

import me.dannynguyen.aethel.Plugin;
import org.bukkit.NamespacedKey;

/**
 * PluginNamespacedKey is an enum containing the plugin's namespaced keys.
 *
 * @author Danny Nguyen
 * @version 1.9.7
 * @since 1.7.8
 */
public enum PluginNamespacedKey {
  AETHEL_ATTRIBUTE_LIST(new NamespacedKey(Plugin.getInstance(), "aethel.attribute.list")),
  ITEM_CATEGORY(new NamespacedKey(Plugin.getInstance(), "aethel.aethelitem.category")),
  RECIPE_CATEGORY(new NamespacedKey(Plugin.getInstance(), "aethel.forge.category")),
  RECIPE_ID(new NamespacedKey(Plugin.getInstance(), "aethel.forge.id"));

  private final NamespacedKey namespacedKey;

  PluginNamespacedKey(NamespacedKey namespacedKey) {
    this.namespacedKey = namespacedKey;
  }

  public NamespacedKey getNamespacedKey() {
    return this.namespacedKey;
  }
}
