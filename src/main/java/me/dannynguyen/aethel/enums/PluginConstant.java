package me.dannynguyen.aethel.enums;

import org.bukkit.NamespacedKey;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PluginConstant is an enum-like storage containing various data type constants.
 *
 * @author Danny Nguyen
 * @version 1.9.1
 * @since 1.7.13
 */
public class PluginConstant {
  public static final NamespacedKey[] aethelTags = {
      PluginNamespacedKey.ITEM_CATEGORY.getNamespacedKey(),
      PluginNamespacedKey.RECIPE_CATEGORY.getNamespacedKey(),
      PluginNamespacedKey.RECIPE_ID.getNamespacedKey()};

  public static final Map<String, String[]> aethelAttributesMap = Map.of(
      "offense", new String[]{
          "Attack Damage", "Attack Speed",
          "Critical Chance", "Critical Damage"},
      "defense", new String[]{
          "Max Health", "Armor", "Armor Toughness", "Movement Speed",
          "Block", "Parry Chance", "Parry Deflect", "Dodge Chance"},
      "other", new String[]{
          "Ability Damage", "Ability Cooldown",
          "Apply Status", "Knockback Resistance", "Luck"});

  public static final Set<String> minecraftAttributes = new HashSet<>(List.of(
      "Attack Damage", "Attack Speed", "Max Health", "Armor", "Armor Toughness",
      "Movement Speed", "Knockback Resistance", "Luck"));
}
