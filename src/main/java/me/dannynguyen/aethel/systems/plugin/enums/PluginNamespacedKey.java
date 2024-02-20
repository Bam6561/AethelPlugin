package me.dannynguyen.aethel.systems.plugin.enums;

import me.dannynguyen.aethel.Plugin;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Plugin namespaced keys.
 *
 * @author Danny Nguyen
 * @version 1.11.9
 * @since 1.11.9
 **/
public enum PluginNamespacedKey {
  /**
   * Aethel attribute list.
   */
  ATTRIBUTE_LIST(new NamespacedKey(Plugin.getInstance(), "aethel.attribute.list")),

  /**
   * Item category.
   */
  ITEM_CATEGORY(new NamespacedKey(Plugin.getInstance(), "aethel.aethelitem.category")),

  /**
   * Recipe category.
   */
  RECIPE_CATEGORY(new NamespacedKey(Plugin.getInstance(), "aethel.forge.category")),

  /**
   * Recipe id.
   */
  RECIPE_ID(new NamespacedKey(Plugin.getInstance(), "aethel.forge.id"));

  /**
   * Namespaced key.
   */
  private final NamespacedKey namespacedKey;

  /**
   * Associates a NamespacedKey with its id.
   *
   * @param namespacedKey namespaced key
   */
  PluginNamespacedKey(@NotNull NamespacedKey namespacedKey) {
    this.namespacedKey = Objects.requireNonNull(namespacedKey, "Null namespaced key");
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