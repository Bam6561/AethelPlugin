package me.dannynguyen.aethel.systems.plugin;

import me.dannynguyen.aethel.Plugin;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Plugin namespaced keys.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.11.9
 **/
public enum PluginNamespacedKey {
  /**
   * Aethel attribute list.
   */
  ATTRIBUTE_LIST(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + "list")),

  /**
   * Item passive ability list.
   */
  PASSIVE_LIST(new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + "list")),

  /**
   * Item active ability list.
   */
  ACTIVE_LIST(new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + "list")),

  /**
   * Item category.
   */
  ITEM_CATEGORY(new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHELITEM.getHeader() + "category")),

  /**
   * Recipe category.
   */
  RECIPE_CATEGORY(new NamespacedKey(Plugin.getInstance(), KeyHeader.FORGE.getHeader() + "category")),

  /**
   * Recipe Forge ID.
   */
  RECIPE_FORGE_ID(new NamespacedKey(Plugin.getInstance(), KeyHeader.FORGE.getHeader() + "id"));

  /**
   * Namespaced key.
   */
  private final NamespacedKey namespacedKey;

  /**
   * Associates a NamespacedKey with its ID.
   *
   * @param namespacedKey namespaced key
   */
  PluginNamespacedKey(NamespacedKey namespacedKey) {
    this.namespacedKey = namespacedKey;
  }

  /**
   * Gets the namespaced key.
   *
   * @return namespaced key
   */
  @NotNull
  public NamespacedKey getNamespacedKey() {
    return this.namespacedKey;
  }
}