package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.KeyHeader;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Represents an item's Minecraft and Aethel attribute lore generation.
 *
 * @author Danny Nguyen
 * @version 1.16.0
 * @since 1.13.2
 */
class ItemAttributeLore {
  /**
   * ItemStack whose attributes are being totalled.
   */
  private final ItemStack item;

  /**
   * ItemStack's meta.
   */
  private final ItemMeta meta;

  /**
   * ItemStack's lore.
   */
  private final List<String> lore;

  /**
   * ItemStack's total Minecraft and Aethel attribute
   * values categorized by equipment slot.
   */
  private final Map<String, Map<String, Double>> attributeValues;

  /**
   * Associates an ItemStack with its Aethel attribute list.
   *
   * @param item interacting item
   */
  protected ItemAttributeLore(@NotNull ItemStack item) {
    this.item = Objects.requireNonNull(item, "Null item");
    this.meta = item.getItemMeta();
    if (meta.hasLore()) {
      this.lore = meta.getLore();
    } else {
      this.lore = new ArrayList<>();
    }
    this.attributeValues = totalAttributeValues();
  }

  /**
   * Totals the item's Minecraft and Aethel attributes together.
   *
   * @return ItemStack's total attribute values
   */
  private Map<String, Map<String, Double>> totalAttributeValues() {
    Map<String, Map<String, Double>> attributeValues = new HashMap<>();
    if (meta.hasAttributeModifiers()) {
      sortMinecraftAttributes(attributeValues);
    }
    sortAethelAttributes(attributeValues);
    return attributeValues;
  }

  /**
   * Adds attribute headers to the item's lore.
   */
  protected void addAttributeHeaders() {
    addAttributeHeader("head");
    addAttributeHeader("chest");
    addAttributeHeader("legs");
    addAttributeHeader("feet");
    addAttributeHeader("necklace");
    addAttributeHeader("ring");
    addAttributeHeader("hand");
    addAttributeHeader("off_hand");
    meta.setLore(lore);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    item.setItemMeta(meta);
  }

  /**
   * Adds an attribute header if it exists for the
   * equipment slot with its associated attribute values.
   *
   * @param eSlot equipment slot
   */
  private void addAttributeHeader(String eSlot) {
    if (attributeValues.containsKey(eSlot)) {
      List<String> attributeHeader = new ArrayList<>(List.of(""));
      switch (eSlot) {
        case "head" -> attributeHeader.add(ChatColor.GRAY + "When on Head:");
        case "chest" -> attributeHeader.add(ChatColor.GRAY + "When on Chest:");
        case "legs" -> attributeHeader.add(ChatColor.GRAY + "When on Legs:");
        case "feet" -> attributeHeader.add(ChatColor.GRAY + "When on Feet:");
        case "necklace" -> attributeHeader.add(ChatColor.GRAY + "When on Necklace:");
        case "ring" -> attributeHeader.add(ChatColor.GRAY + "When on Ring:");
        case "hand" -> attributeHeader.add(ChatColor.GRAY + "When in Main Hand:");
        case "off_hand" -> attributeHeader.add(ChatColor.GRAY + "When in Off Hand:");
      }
      DecimalFormat df3 = new DecimalFormat();
      df3.setMaximumFractionDigits(3);
      for (String attribute : attributeValues.get(eSlot).keySet()) {
        switch (attribute) {
          case "critical_chance", "counter_chance", "dodge_chance", "critical_damage", "item_damage", "item_cooldown" -> attributeHeader.add(ChatColor.DARK_GREEN + "+" + df3.format(attributeValues.get(eSlot).get(attribute)) + "% " + TextFormatter.capitalizePhrase(attribute));
          default -> attributeHeader.add(ChatColor.DARK_GREEN + "+" + df3.format(attributeValues.get(eSlot).get(attribute)) + " " + TextFormatter.capitalizePhrase(attribute));
        }
      }
      lore.addAll(attributeHeader);
    }
  }

  /**
   * Sorts Minecraft attributes by their equipment slot.
   *
   * @param attributeValues equipment slot : (attribute : value)
   */
  private void sortMinecraftAttributes(Map<String, Map<String, Double>> attributeValues) {
    for (Attribute attribute : meta.getAttributeModifiers().keySet()) {
      for (AttributeModifier attributeModifier : meta.getAttributeModifiers(attribute)) {
        String attributeSlot = attributeModifier.getSlot().name().toLowerCase();
        String attributeName;
        switch (attribute) {
          case GENERIC_MAX_HEALTH -> attributeName = "max_hp";
          case GENERIC_ARMOR_TOUGHNESS -> attributeName = "toughness";
          default -> attributeName = attribute.name().substring(8).toLowerCase();
        }
        if (attributeValues.containsKey(attributeSlot)) {
          if (attributeValues.get(attributeSlot).containsKey(attributeName)) {
            attributeValues.get(attributeSlot).put(attributeName, attributeValues.get(attributeSlot).get(attributeName) + attributeModifier.getAmount());
          } else {
            attributeValues.get(attributeSlot).put(attributeName, attributeModifier.getAmount());
          }
        } else {
          attributeValues.put(attributeSlot, new HashMap<>(Map.of(attributeName, attributeModifier.getAmount())));
        }
      }
    }
  }

  /**
   * Sorts Aethel attributes by their equipment slot.
   *
   * @param attributeValues equipment slot : (attribute : value)
   */
  private void sortAethelAttributes(Map<String, Map<String, Double>> attributeValues) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    for (String attribute : meta.getPersistentDataContainer().get(PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
      String attributeSlot = attribute.substring(0, attribute.indexOf("."));
      String attributeName = attribute.substring(attribute.indexOf(".") + 1);
      NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute);
      if (attributeValues.containsKey(attributeSlot)) {
        if (attributeValues.get(attributeSlot).containsKey(attributeName)) {
          attributeValues.get(attributeSlot).put(attributeName, attributeValues.get(attributeSlot).get(attributeName) + dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
        } else {
          attributeValues.get(attributeSlot).put(attributeName, dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
        }
      } else {
        attributeValues.put(attributeSlot, new HashMap<>(Map.of(attributeName, dataContainer.get(attributeKey, PersistentDataType.DOUBLE))));
      }
    }
  }
}
