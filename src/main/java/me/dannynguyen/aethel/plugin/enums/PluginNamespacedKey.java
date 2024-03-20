package me.dannynguyen.aethel.plugin.enums;

import me.dannynguyen.aethel.Plugin;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin namespaced keys.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.11.9
 **/
public enum PluginNamespacedKey {
  /**
   * Item {@link me.dannynguyen.aethel.rpg.system.AethelAttributes} list.
   */
  ATTRIBUTE_LIST(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + "list")),

  /**
   * Item {@link me.dannynguyen.aethel.rpg.ability.PassiveAbility} list.
   */
  PASSIVE_LIST(new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + "list")),

  /**
   * Item {@link me.dannynguyen.aethel.rpg.ability.ActiveAbility} list.
   */
  ACTIVE_LIST(new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + "list")),

  /**
   * {@link me.dannynguyen.aethel.commands.aethelitem.PersistentItem Item} category.
   */
  ITEM_CATEGORY(new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHELITEM.getHeader() + "category")),

  /**
   * {@link me.dannynguyen.aethel.commands.forge.PersistentRecipe Recipe} category.
   */
  RECIPE_CATEGORY(new NamespacedKey(Plugin.getInstance(), KeyHeader.FORGE.getHeader() + "category")),

  /**
   * {@link me.dannynguyen.aethel.commands.forge.PersistentRecipe Recipe} Forge ID.
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