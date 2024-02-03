package me.dannynguyen.aethel.enums;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PluginItems is an enum collection containing the plugin's item categories.
 *
 * @author Danny Nguyen
 * @version 1.9.2
 * @since 1.9.2
 */
public class PluginItems {
  public enum WornItems {
    ALL(new HashSet<>(List.of(
        "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS",
        "CHAINMAIL_HELMET", "CHAINMAIL_CHESTPLATE", "CHAINMAIL_LEGGINGS", "CHAINMAIL_BOOTS",
        "IRON_HELMET", "IRON_CHESTPLATE", "IRON_LEGGINGS", "IRON_BOOTS",
        "GOLDEN_HELMET", "GOLDEN_CHESTPLATE", "GOLDEN_LEGGINGS", "GOLDEN_BOOTS",
        "DIAMOND_HELMET", "DIAMOND_CHESTPLATE", "DIAMOND_LEGGINGS", "DIAMOND_BOOTS",
        "NETHERITE_HELMET", "NETHERITE_CHESTPLATE", "NETHERITE_LEGGINGS", "NETHERITE_BOOTS",
        "CREEPER_HEAD", "ZOMBIE_HEAD", "SKELETON_SKULL", "WITHER_SKELETON_SKULL", "PLAYER_HEAD",
        "DRAGON_HEAD", "TURTLE_HELMET", "PUMPKIN", "ELYTRA", "SHIELD"))),
    HEAD(new HashSet<>(List.of(
        "LEATHER_HELMET", "CHAINMAIL_HELMET", "IRON_HELMET",
        "GOLDEN_HELMET", "DIAMOND_HELMET", "NETHERITE_HELMET",
        "CREEPER_HEAD", "ZOMBIE_HEAD", "SKELETON_SKULL", "WITHER_SKELETON_SKULL", "PLAYER_HEAD",
        "DRAGON_HEAD", "TURTLE_HELMET", "PUMPKIN"))),
    CHEST(new HashSet<>(List.of(
        "LEATHER_CHESTPLATE", "CHAINMAIL_CHESTPLATE", "IRON_CHESTPLATE",
        "GOLDEN_CHESTPLATE", "DIAMOND_CHESTPLATE", "NETHERITE_CHESTPLATE",
        "ELYTRA"))),
    LEGS(new HashSet<>(List.of(
        "LEATHER_LEGGINGS", "CHAINMAIL_LEGGINGS", "IRON_LEGGINGS",
        "GOLDEN_LEGGINGS", "DIAMOND_LEGGINGS", "NETHERITE_LEGGINGS"))),
    FEET(new HashSet<>(List.of(
        "LEATHER_BOOTS", "CHAINMAIL_BOOTS", "IRON_BOOTS",
        "GOLDEN_BOOTS", "DIAMOND_BOOTS", "NETHERITE_BOOTS"
    )));

    public Set<String> items;

    WornItems(Set<String> items) {
      this.items = items;
    }
  }
}
