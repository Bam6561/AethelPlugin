package me.dannynguyen.aethel.enums.plugin;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.rpg.AethelAttributes;
import me.dannynguyen.aethel.rpg.abilities.ActiveAbility;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin namespaced keys.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.11.9
 */
public enum Key {
  /**
   * Item {@link AethelAttributes} list.
   */
  ATTRIBUTE_LIST(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + "list")),

  /**
   * Item {@link PassiveAbility} list.
   */
  PASSIVE_LIST(new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + "list")),

  /**
   * Item {@link ActiveAbility} list.
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
  Key(NamespacedKey namespacedKey) {
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