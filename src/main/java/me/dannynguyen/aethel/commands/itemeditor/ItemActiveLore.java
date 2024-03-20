package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.KeyHeader;
import me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.rpg.enums.ActiveAbilityEffect;
import me.dannynguyen.aethel.rpg.enums.ActiveAbilityType;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link PluginNamespacedKey#ACTIVE_LIST active ability} lore generation.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.15.16
 */
class ItemActiveLore {
  /**
   * ItemStack whose {@link PluginNamespacedKey#ACTIVE_LIST active abilities} are being written.
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
   * ItemStack's {@link PluginNamespacedKey#ACTIVE_LIST active abilities}
   * categorized by {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}.
   */
  private final Map<String, List<String>> activeAbilities;

  /**
   * Associates an ItemStack with its {@link PluginNamespacedKey#ACTIVE_LIST active ability list}.
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
   * Adds active ability {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot} headers to the item's lore.
   */
  protected void addActiveHeaders() {
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
   * Adds an active ability {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   * header if it exists for the its associated ability values.
   *
   * @param eSlot {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   */
  private void addActiveHeader(String eSlot) {
    if (activeAbilities.containsKey(eSlot)) {
      List<String> activeHeader = new ArrayList<>(List.of(""));
      switch (eSlot) {
        case "head" -> activeHeader.add(ChatColor.GRAY + "Head (Active):");
        case "chest" -> activeHeader.add(ChatColor.GRAY + "Chest (Active):");
        case "legs" -> activeHeader.add(ChatColor.GRAY + "Legs (Active):");
        case "feet" -> activeHeader.add(ChatColor.GRAY + "Feet (Active):");
        case "necklace" -> activeHeader.add(ChatColor.GRAY + "Necklace (Active):");
        case "ring" -> activeHeader.add(ChatColor.GRAY + "Ring (Active):");
        case "hand" -> activeHeader.add(ChatColor.GRAY + "Main Hand (Active):");
        case "off_hand" -> activeHeader.add(ChatColor.GRAY + "Off Hand (Active):");
      }
      for (String ability : activeAbilities.get(eSlot)) {
        activeHeader.add(ability);
      }
      lore.addAll(activeHeader);
    }
  }

  /**
   * Sorts {@link PluginNamespacedKey#ACTIVE_LIST active abilities}
   * by their {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}.
   *
   * @return {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot} : {@link PluginNamespacedKey#ACTIVE_LIST}
   */
  private Map<String, List<String>> sortActiveAbilities() {
    Map<String, List<String>> activeAbilities = new HashMap<>();
    for (String active : dataContainer.get(PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
      String slot = active.substring(0, active.indexOf("."));
      String type = active.substring(active.indexOf(".") + 1);

      ActiveAbilityType abilityType = ActiveAbilityType.valueOf(type.toUpperCase());
      ActiveAbilityEffect abilityEffect = abilityType.getEffect();

      String[] abilityData = dataContainer.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + slot + "." + type), PersistentDataType.STRING).split(" ");
      StringBuilder activeLore = new StringBuilder();

      activeLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[0])).append("s) ");
      switch (abilityEffect) {
        case MOVEMENT -> {
          switch (abilityType) {
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
