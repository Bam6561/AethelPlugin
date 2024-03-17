package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.KeyHeader;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.rpg.ability.ActiveAbilityEffect;
import me.dannynguyen.aethel.systems.rpg.ability.ActiveAbilityType;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's active ability lore generation.
 *
 * @author Danny Nguyen
 * @version 1.16.0
 * @since 1.15.16
 */
class ItemActiveLore {
  /**
   * ItemStack whose active abilities are being written.
   */
  private final ItemStack item;

  /**
   * ItemStack's meta.
   */
  private final ItemMeta meta;

  /**
   * ItemStack's persistent tags.
   */
  private final PersistentDataContainer dataContainer;

  /**
   * ItemStack's lore.
   */
  private final List<String> lore;

  /**
   * ItemStack's active abilities categorized by equipment slot.
   */
  private final Map<String, List<String>> activeAbilities;

  /**
   * Associates an ItemStack with its active ability list.
   *
   * @param item interacting item
   */
  protected ItemActiveLore(@NotNull ItemStack item) {
    this.item = Objects.requireNonNull(item, "Null item");
    this.meta = item.getItemMeta();
    this.dataContainer = meta.getPersistentDataContainer();
    if (meta.hasLore()) {
      this.lore = meta.getLore();
    } else {
      this.lore = new ArrayList<>();
    }
    this.activeAbilities = sortActiveAbilities();
  }

  /**
   * Adds active ability headers to the item's lore.
   */
  public void addActiveHeaders() {
    addActiveHeader("head");
    addActiveHeader("chest");
    addActiveHeader("legs");
    addActiveHeader("feet");
    addActiveHeader("necklace");
    addActiveHeader("ring");
    addActiveHeader("hand");
    addActiveHeader("off_hand");
    meta.setLore(lore);
    item.setItemMeta(meta);
  }

  /**
   * Adds an active ability header if it exists for the
   * equipment slot with its associated ability values.
   *
   * @param slot equipment slot
   */
  private void addActiveHeader(String slot) {
    if (activeAbilities.containsKey(slot)) {
      List<String> activeHeader = new ArrayList<>(List.of(""));
      switch (slot) {
        case "head" -> activeHeader.add(ChatColor.GRAY + "Head (Active):");
        case "chest" -> activeHeader.add(ChatColor.GRAY + "Chest (Active):");
        case "legs" -> activeHeader.add(ChatColor.GRAY + "Legs (Active):");
        case "feet" -> activeHeader.add(ChatColor.GRAY + "Feet (Active):");
        case "necklace" -> activeHeader.add(ChatColor.GRAY + "Necklace (Active):");
        case "ring" -> activeHeader.add(ChatColor.GRAY + "Ring (Active):");
        case "hand" -> activeHeader.add(ChatColor.GRAY + "Main Hand (Active):");
        case "off_hand" -> activeHeader.add(ChatColor.GRAY + "Off Hand (Active):");
      }
      for (String ability : activeAbilities.get(slot)) {
        activeHeader.add(ability);
      }
      lore.addAll(activeHeader);
    }
  }

  /**
   * Sorts active abilities by their equipment slot.
   *
   * @return equipment slot: active ability
   */
  private Map<String, List<String>> sortActiveAbilities() {
    Map<String, List<String>> activeAbilities = new HashMap<>();
    for (String active : dataContainer.get(PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
      String slot = active.substring(0, active.indexOf("."));
      String type = active.substring(active.indexOf(".") + 1);

      ActiveAbilityType ability = ActiveAbilityType.valueOf(type.toUpperCase());
      ActiveAbilityEffect abilityEffect = ability.getEffect();

      String[] abilityData = dataContainer.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + slot + "." + type), PersistentDataType.STRING).split(" ");
      StringBuilder activeLore = new StringBuilder();

      activeLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[0])).append("s) ");
      switch (abilityEffect) {
        case MOVEMENT -> {
          switch (ability) {
            case BLINK -> activeLore.append(ChatColor.AQUA).append("Blink ");
            case DASH -> activeLore.append(ChatColor.AQUA).append("Dash ");
          }
          activeLore.append(ChatColor.WHITE).append("(").append(abilityData[1]).append("m)");
        }
        case PROJECTION -> activeLore.append(ChatColor.AQUA).append("Projection ").append(ChatColor.WHITE).append("(").append(abilityData[1]).append("m) Return after (").append(ticksToSeconds(abilityData[2])).append("s)");
        case SHATTER -> activeLore.append(ChatColor.AQUA).append("Shatter ").append(ChatColor.WHITE).append("(").append(abilityData[1]).append("m)");
      }
      if (activeAbilities.containsKey(slot)) {
        activeAbilities.get(slot).add(activeLore.toString());
      } else {
        activeAbilities.put(slot, new ArrayList<>(List.of(activeLore.toString())));
      }
    }
    return activeAbilities;
  }

  /**
   * Gets a time duration in ticks and converts it to seconds.
   *
   * @param ticks ticks
   * @return seconds
   */
  private String ticksToSeconds(String ticks) {
    return String.valueOf(Double.parseDouble(ticks) / 20);
  }
}
