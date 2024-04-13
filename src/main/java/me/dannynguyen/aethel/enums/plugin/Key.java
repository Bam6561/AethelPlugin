package me.dannynguyen.aethel.enums.plugin;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.rpg.abilities.ActiveAbility;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin namespaced keys.
 *
 * @author Danny Nguyen
 * @version 1.22.20
 * @since 1.11.9
 */
public enum Key {
  /**
   * Item {@link AethelAttribute} list.
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
   * {@link me.dannynguyen.aethel.commands.aethelitem.ItemRegistry Item} category.
   */
  ITEM_CATEGORY(new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHELITEM.getHeader() + "category")),

  /**
   * {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry Recipe} category.
   */
  RECIPE_CATEGORY(new NamespacedKey(Plugin.getInstance(), KeyHeader.FORGE.getHeader() + "category")),

  /**
   * {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry Recipe} Forge ID.
   */
  RECIPE_FORGE_ID(new NamespacedKey(Plugin.getInstance(), KeyHeader.FORGE.getHeader() + "id")),

  /**
   * RPG current health.
   */
  RPG_CURRENT_HEALTH(new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + "rpg.current_health")),

  /**
   * {@link AethelAttribute#MAX_HEALTH}
   */
  ATTRIBUTE_MAX_HEALTH(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.MAX_HEALTH.getId())),

  /**
   * {@link AethelAttribute#CRITICAL_CHANCE}
   */
  ATTRIBUTE_CRITICAL_CHANCE(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.CRITICAL_CHANCE.getId())),

  /**
   * {@link AethelAttribute#CRITICAL_DAMAGE}
   */
  ATTRIBUTE_CRITICAL_DAMAGE(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.CRITICAL_DAMAGE.getId())),

  /**
   * {@link AethelAttribute#FEINT_SKILL}
   */
  ATTRIBUTE_FEINT_SKILL(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.FEINT_SKILL.getId())),

  /**
   * {@link AethelAttribute#ACCURACY_SKILL}
   */
  ATTRIBUTE_ACCURACY_SKILL(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.ACCURACY_SKILL.getId())),

  /**
   * {@link AethelAttribute#COUNTER_CHANCE}
   */
  ATTRIBUTE_COUNTER_CHANCE(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.COUNTER_CHANCE.getId())),

  /**
   * {@link AethelAttribute#DODGE_CHANCE}
   */
  ATTRIBUTE_DODGE_CHANCE(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.DODGE_CHANCE.getId())),

  /**
   * {@link AethelAttribute#ARMOR_TOUGHNESS}
   */
  ATTRIBUTE_ARMOR_TOUGHNESS(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.ARMOR_TOUGHNESS.getId())),

  /**
   * {@link AethelAttribute#ITEM_DAMAGE}
   */
  ATTRIBUTE_ITEM_DAMAGE(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.ITEM_DAMAGE.getId())),

  /**
   * {@link AethelAttribute#ITEM_COOLDOWN}
   */
  ATTRIBUTE_ITEM_COOLDOWN(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.ITEM_COOLDOWN.getId())),

  /**
   * {@link AethelAttribute#TENACITY}
   */
  ATTRIBUTE_TENACITY(new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + AethelAttribute.TENACITY.getId())),

  /**
   * Protection.
   */
  ENCHANTMENT_PROTECTION(new NamespacedKey(Plugin.getInstance(), KeyHeader.ENCHANTMENT.getHeader() + "protection")),

  /**
   * Blast protection.
   */
  ENCHANTMENT_BLAST_PROTECTION(new NamespacedKey(Plugin.getInstance(), KeyHeader.ENCHANTMENT.getHeader() + "blast_protection")),

  /**
   * Fire protection.
   */
  ENCHANTMENT_FIRE_PROTECTION(new NamespacedKey(Plugin.getInstance(), KeyHeader.ENCHANTMENT.getHeader() + "fire_protection")),

  /**
   * Projectile protection.
   */
  ENCHANTMENT_PROJECTILE_PROTECTION(new NamespacedKey(Plugin.getInstance(), KeyHeader.ENCHANTMENT.getHeader() + "projectile_protection")),

  /**
   * Feather falling.
   */
  ENCHANTMENT_FEATHER_FALLING(new NamespacedKey(Plugin.getInstance(), KeyHeader.ENCHANTMENT.getHeader() + "feather_falling"));

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