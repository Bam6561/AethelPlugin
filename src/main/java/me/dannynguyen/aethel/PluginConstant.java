package me.dannynguyen.aethel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PluginConstant is an enum-like storage containing various data type constants.
 *
 * @author Danny Nguyen
 * @version 1.9.21
 * @since 1.7.13
 */
public class PluginConstant {
  public static final Set<String> minecraftAttributes = new HashSet<>(List.of(
      "Attack Damage", "Attack Speed", "Max Health", "Armor", "Armor Toughness",
      "Movement Speed", "Knockback Resistance", "Luck"));
}
