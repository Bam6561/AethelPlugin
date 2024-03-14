package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.KeyHeader;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.rpg.PassiveAbility;
import me.dannynguyen.aethel.systems.rpg.PassiveAbilityEffect;
import me.dannynguyen.aethel.systems.rpg.Trigger;
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
 * @version 1.15.15
 * @since 1.15.15
 */
class ItemPassiveLore {
  /**
   * ItemStack whose passives are being written.
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
   * ItemStack's passive abilities.
   */
  private final List<String> passives;

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
    this.passives = new ArrayList<>(List.of(dataContainer.get(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")));
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
   * @param slot equipment slot
   */
  private void addPassiveHeader(String slot) {
    if (passiveAbilities.containsKey(slot)) {
      List<String> passiveHeader = new ArrayList<>(List.of(""));
      switch (slot) {
        case "head" -> passiveHeader.add(ChatColor.GRAY + "Head (Passive):");
        case "chest" -> passiveHeader.add(ChatColor.GRAY + "Chest (Passive):");
        case "legs" -> passiveHeader.add(ChatColor.GRAY + "Legs (Passive):");
        case "feet" -> passiveHeader.add(ChatColor.GRAY + "Feet (Passive):");
        case "necklace" -> passiveHeader.add(ChatColor.GRAY + "Necklace (Passive):");
        case "ring" -> passiveHeader.add(ChatColor.GRAY + "Ring (Passive):");
        case "hand" -> passiveHeader.add(ChatColor.GRAY + "Main Hand (Passive):");
        case "off_hand" -> passiveHeader.add(ChatColor.GRAY + "Off Hand (Passive):");
      }
      for (String ability : passiveAbilities.get(slot)) {
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
    for (String passive : passives) {
      String[] passiveMeta = passive.split("\\.");
      String slot = passiveMeta[0];
      String condition = passiveMeta[1];
      String type = passiveMeta[2];

      Trigger trigger = Trigger.valueOf(condition.toUpperCase());
      PassiveAbilityEffect abilityEffect = PassiveAbility.valueOf(type.toUpperCase()).getEffect();

      String[] abilityData = dataContainer.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + slot + "." + condition + "." + type), PersistentDataType.STRING).split(" ");
      StringBuilder abilityLore = new StringBuilder(ChatColor.GRAY + "");

      switch (trigger.getCondition()) {
        case CHANCE_COOLDOWN -> {
          // Chance
          if (!abilityData[0].equals("100.0")) {
            abilityLore.append(abilityData[0]).append("% ");
          }
          // Cooldown
          if (!abilityData[1].equals("0")) {
            abilityLore.append("(").append(convertTicksToSeconds(abilityData[1])).append("s) ");
          }
          addTriggerLore(abilityLore, trigger);
          switch (abilityEffect) {
            case STACK_INSTANCE -> abilityLore.append("Apply ").append(abilityData[2]).append(" ").append(TextFormatter.capitalizePhrase(type)).append(" (").append(convertTicksToSeconds(abilityData[3])).append("s)");
            case SPARK -> abilityLore.append("Spark ").append(abilityData[2]).append(" damage (").append(abilityData[3]).append("m radius)");
          }
        }
        case HP_CHANCE_COOLDOWN -> {
          // Chance
          if (!abilityData[1].equals("100.0")) {
            abilityLore.append(abilityData[1]).append("% ");
          }
          // Cooldown
          if (!abilityData[2].equals("0")) {
            abilityLore.append("(").append(convertTicksToSeconds(abilityData[2])).append("s) ");
          }
          abilityLore.append("Below ").append(abilityData[0]).append("% HP ");
          addTriggerLore(abilityLore, trigger);
          switch (abilityEffect) {
            case STACK_INSTANCE -> abilityLore.append("Apply ").append(abilityData[3]).append(" ").append(TextFormatter.capitalizePhrase(type)).append(" (").append(convertTicksToSeconds(abilityData[4])).append("s)");
            case SPARK -> abilityLore.append("Spark ").append(abilityData[3]).append(" damage (").append(abilityData[4]).append("m radius)");
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
      case DEAL_DAMAGE -> abilityLore.append("Dealing Damage ");
      case TAKE_DAMAGE -> abilityLore.append("Taking Damage ");
      case KILL -> abilityLore.append("On Kill ");
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
