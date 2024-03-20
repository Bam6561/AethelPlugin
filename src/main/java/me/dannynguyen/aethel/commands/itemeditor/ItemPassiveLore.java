package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.KeyHeader;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.rpg.ability.PassiveAbilityEffect;
import me.dannynguyen.aethel.systems.rpg.ability.PassiveAbilityType;
import me.dannynguyen.aethel.systems.rpg.ability.Trigger;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's passive ability lore generation.
 *
 * @author Danny Nguyen
 * @version 1.16.13
 * @since 1.15.15
 */
class ItemPassiveLore {
  /**
   * ItemStack whose passive abilities are being written.
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
   * ItemStack's passive abilities categorized by equipment slot.
   */
  private final Map<String, List<String>> passiveAbilities;

  /**
   * Associates an ItemStack with its passive ability list.
   *
   * @param item interacting item
   */
  protected ItemPassiveLore(@NotNull ItemStack item) {
    this.item = Objects.requireNonNull(item, "Null item");
    this.meta = item.getItemMeta();
    this.dataContainer = meta.getPersistentDataContainer();
    if (meta.hasLore()) {
      this.lore = meta.getLore();
    } else {
      this.lore = new ArrayList<>();
    }
    this.passiveAbilities = sortPassiveAbilities();
  }

  /**
   * Adds passive ability headers to the item's lore.
   */
  public void addPassiveHeaders() {
    addPassiveHeader("head");
    addPassiveHeader("chest");
    addPassiveHeader("legs");
    addPassiveHeader("feet");
    addPassiveHeader("necklace");
    addPassiveHeader("ring");
    addPassiveHeader("hand");
    addPassiveHeader("off_hand");
    meta.setLore(lore);
    item.setItemMeta(meta);
  }

  /**
   * Adds a passive ability header if it exists for the
   * equipment slot with its associated ability values.
   *
   * @param eSlot equipment slot
   */
  private void addPassiveHeader(String eSlot) {
    if (passiveAbilities.containsKey(eSlot)) {
      List<String> passiveHeader = new ArrayList<>(List.of(""));
      switch (eSlot) {
        case "head" -> passiveHeader.add(ChatColor.GRAY + "Head (Passive):");
        case "chest" -> passiveHeader.add(ChatColor.GRAY + "Chest (Passive):");
        case "legs" -> passiveHeader.add(ChatColor.GRAY + "Legs (Passive):");
        case "feet" -> passiveHeader.add(ChatColor.GRAY + "Feet (Passive):");
        case "necklace" -> passiveHeader.add(ChatColor.GRAY + "Necklace (Passive):");
        case "ring" -> passiveHeader.add(ChatColor.GRAY + "Ring (Passive):");
        case "hand" -> passiveHeader.add(ChatColor.GRAY + "Main Hand (Passive):");
        case "off_hand" -> passiveHeader.add(ChatColor.GRAY + "Off Hand (Passive):");
      }
      for (String ability : passiveAbilities.get(eSlot)) {
        passiveHeader.add(ability);
      }
      lore.addAll(passiveHeader);
    }
  }

  /**
   * Sorts passive abilities by their equipment slot.
   *
   * @return equipment slot : passive ability
   */
  private Map<String, List<String>> sortPassiveAbilities() {
    Map<String, List<String>> passiveAbilities = new HashMap<>();
    for (String passive : dataContainer.get(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
      String[] passiveMeta = passive.split("\\.");
      String slot = passiveMeta[0];
      String condition = passiveMeta[1];
      String type = passiveMeta[2];

      Trigger trigger = Trigger.valueOf(condition.toUpperCase());
      PassiveAbilityEffect abilityEffect = PassiveAbilityType.valueOf(type.toUpperCase()).getEffect();

      String[] abilityData = dataContainer.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + slot + "." + condition + "." + type), PersistentDataType.STRING).split(" ");
      StringBuilder abilityLore = new StringBuilder();

      abilityLore.append(ChatColor.DARK_AQUA);
      switch (trigger.getCondition()) {
        case CHANCE_COOLDOWN -> {
          addTriggerLore(abilityLore, trigger);
          // Chance
          if (!abilityData[0].equals("100.0")) {
            abilityLore.append(ChatColor.WHITE).append(abilityData[0]).append("% ");
          }
          // Cooldown
          if (!abilityData[1].equals("0")) {
            abilityLore.append(ChatColor.WHITE).append("(").append(convertTicksToSeconds(abilityData[1])).append("s) ");
          }
          switch (abilityEffect) {
            case STACK_INSTANCE -> abilityLore.append(ChatColor.WHITE).append("Apply ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(TextFormatter.capitalizePhrase(type)).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(convertTicksToSeconds(abilityData[4])).append("s)");
            case CHAIN_DAMAGE -> abilityLore.append(ChatColor.WHITE).append("Deal ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(TextFormatter.capitalizePhrase(type)).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(abilityData[4]).append("m)");
          }
        }
        case HEALTH_COOLDOWN -> {
          abilityLore.append("Below ").append(abilityData[0]).append("% HP: ");
          addTriggerLore(abilityLore, trigger);
          // Cooldown
          if (!abilityData[1].equals("0")) {
            abilityLore.append(ChatColor.WHITE).append("(").append(convertTicksToSeconds(abilityData[1])).append("s) ");
          }
          switch (abilityEffect) {
            case STACK_INSTANCE -> abilityLore.append(ChatColor.WHITE).append("Apply ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(TextFormatter.capitalizePhrase(type)).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(convertTicksToSeconds(abilityData[4])).append("s)");
            case CHAIN_DAMAGE -> abilityLore.append(ChatColor.WHITE).append("Deal ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(TextFormatter.capitalizePhrase(type)).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(abilityData[4]).append("m)");
          }
        }
      }
      if (passiveAbilities.containsKey(slot)) {
        passiveAbilities.get(slot).add(abilityLore.toString());
      } else {
        passiveAbilities.put(slot, new ArrayList<>(List.of(abilityLore.toString())));
      }
    }
    return passiveAbilities;
  }

  /**
   * Adds ability trigger lore.
   *
   * @param abilityLore ability lore
   * @param trigger     trigger
   */
  private void addTriggerLore(StringBuilder abilityLore, Trigger trigger) {
    switch (trigger) {
      case DAMAGE_DEALT -> abilityLore.append("Damage Dealt: ");
      case DAMAGE_TAKEN -> abilityLore.append("Damage Taken: ");
      case ON_KILL -> abilityLore.append("On Kill: ");
    }
  }

  /**
   * Gets a time duration in ticks and converts it to seconds.
   *
   * @param ticks ticks
   * @return seconds
   */
  private String convertTicksToSeconds(String ticks) {
    return String.valueOf(Double.parseDouble(ticks) / 20);
  }
}
