package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.KeyHeader;
import me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.rpg.enums.*;
import me.dannynguyen.aethel.util.TextFormatter;
import me.dannynguyen.aethel.util.item.ItemCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Represents an item's Minecraft and {@link PluginNamespacedKey#ATTRIBUTE_LIST Aethel attribute},
 * {@link PluginNamespacedKey#PASSIVE_LIST passive ability}, and
 * {@link PluginNamespacedKey#ACTIVE_LIST active ability} lore generation.
 * <p>
 * Used with {@link ItemEditorMenuClick}.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.17.13
 */
class ItemLoreGeneration {
  /**
   * Order of headers by {@link RpgEquipmentSlot}.
   */
  private static final List<String> headerOrder = new ArrayList<>(List.of(
      RpgEquipmentSlot.HEAD.getId(), RpgEquipmentSlot.CHEST.getId(),
      RpgEquipmentSlot.LEGS.getId(), RpgEquipmentSlot.FEET.getId(),
      RpgEquipmentSlot.NECKLACE.getId(), RpgEquipmentSlot.RING.getId(),
      RpgEquipmentSlot.HAND.getId(), RpgEquipmentSlot.OFF_HAND.getId()));

  /**
   * Interacting user.
   */
  private final Player user;

  /**
   * ItemStack whose lore is being generated.
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
   * Interacting menu generating the lore.
   */
  private final Inventory menu;

  /**
   * ItemStack's total Minecraft and {@link PluginNamespacedKey#ATTRIBUTE_LIST Aethel attribute}
   * values categorized by {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}.
   */
  private Map<String, Map<String, Double>> attributeValues;

  /**
   * ItemStack's {@link PluginNamespacedKey#PASSIVE_LIST passive abilities}
   * categorized by {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}.
   */
  private Map<String, List<String>> passiveAbilities;

  /**
   * ItemStack's {@link PluginNamespacedKey#ACTIVE_LIST active abilities}
   * categorized by {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}.
   */
  private Map<String, List<String>> activeAbilities;

  /**
   * If the item's lore was generated.
   */
  private boolean generatedLore = false;

  /**
   * Associates an item's lore generation with its user and menu being interacted with.
   *
   * @param user user
   * @param menu interacting menu
   * @param item interacting item
   */
  ItemLoreGeneration(@NotNull Player user, @NotNull Inventory menu, @NotNull ItemStack item) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.menu = Objects.requireNonNull(menu, "Null menu");
    this.item = Objects.requireNonNull(item, "Null item");
    this.meta = item.getItemMeta();
    this.dataContainer = meta.getPersistentDataContainer();
    if (meta.hasLore()) {
      this.lore = meta.getLore();
    } else {
      this.lore = new ArrayList<>();
    }
  }

  /**
   * Generates an item's lore based on its {@link PluginNamespacedKey plugin-related data}.
   */
  protected void generateLore() {
    if (dataContainer.has(PluginNamespacedKey.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      displayForgeId();
    }
    if (dataContainer.has(PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      this.attributeValues = totalAttributeValues();
      addAttributeHeaders();
      menu.setItem(42, ItemCreator.createItem(Material.GREEN_DYE, ChatColor.AQUA + "Hide Attributes", List.of(ChatColor.GREEN + "True")));
    }
    if (dataContainer.has(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      this.passiveAbilities = sortPassiveAbilities();
      addPassiveHeaders();
    }
    if (dataContainer.has(PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      this.activeAbilities = sortActiveAbilities();
      addActiveHeaders();
    }
    if (generatedLore) {
      user.sendMessage(ChatColor.GREEN + "[Generated Lore]");
    } else {
      user.sendMessage(ChatColor.RED + "Not modified by plugin.");
    }
  }

  /**
   * Adds the Forge ID to the item's lore.
   */
  private void displayForgeId() {
    lore.add(ChatColor.DARK_GRAY + "Forge ID: " + dataContainer.get(PluginNamespacedKey.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING));
    meta.setLore(lore);
    item.setItemMeta(meta);
  }

  /**
   * Totals the item's Minecraft and {@link PluginNamespacedKey#ATTRIBUTE_LIST Aethel attributes} together.
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
   * Adds attribute {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot} headers to the item's lore.
   */
  private void addAttributeHeaders() {
    for (String eSlot : headerOrder) {
      addAttributeHeader(eSlot);
    }
    meta.setLore(lore);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    item.setItemMeta(meta);
  }

  /**
   * Sorts {@link PluginNamespacedKey#PASSIVE_LIST passive abilities}
   * by their {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}.
   *
   * @return {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot} : {@link PluginNamespacedKey#PASSIVE_LIST passive ability}
   */
  private Map<String, List<String>> sortPassiveAbilities() {
    Map<String, List<String>> passiveAbilities = new HashMap<>();
    for (String passive : dataContainer.get(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
      String[] passiveMeta = passive.split("\\.");
      String slot = passiveMeta[0];
      String condition = passiveMeta[1];
      String type = passiveMeta[2];

      Trigger trigger = Trigger.valueOf(TextFormatter.formatEnum(condition));
      PassiveAbilityEffect abilityEffect = PassiveAbilityType.valueOf(TextFormatter.formatEnum(type)).getEffect();

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
            abilityLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[1])).append("s) ");
          }
          switch (abilityEffect) {
            case STACK_INSTANCE -> abilityLore.append(ChatColor.WHITE).append("Apply ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(TextFormatter.capitalizePhrase(type)).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(ticksToSeconds(abilityData[4])).append("s)");
            case CHAIN_DAMAGE -> abilityLore.append(ChatColor.WHITE).append("Deal ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(TextFormatter.capitalizePhrase(type)).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(abilityData[4]).append("m)");
          }
        }
        case HEALTH_COOLDOWN -> {
          abilityLore.append("Below ").append(abilityData[0]).append("% HP: ");
          addTriggerLore(abilityLore, trigger);
          // Cooldown
          if (!abilityData[1].equals("0")) {
            abilityLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[1])).append("s) ");
          }
          switch (abilityEffect) {
            case STACK_INSTANCE -> abilityLore.append(ChatColor.WHITE).append("Apply ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(TextFormatter.capitalizePhrase(type)).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(ticksToSeconds(abilityData[4])).append("s)");
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
   * Adds passive ability {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot} headers to the item's lore.
   */
  private void addPassiveHeaders() {
    for (String eSlot : headerOrder) {
      addPassiveHeader(eSlot);
    }
    meta.setLore(lore);
    item.setItemMeta(meta);
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

      ActiveAbilityType abilityType = ActiveAbilityType.valueOf(TextFormatter.formatEnum(type));
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
   * Adds active ability {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot} headers to the item's lore.
   */
  protected void addActiveHeaders() {
    for (String eSlot : headerOrder) {
      addActiveHeader(eSlot);
    }
    meta.setLore(lore);
    item.setItemMeta(meta);
  }

  /**
   * Adds an attribute {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   * header if it exists for its associated attribute values.
   *
   * @param eSlot {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   */
  private void addAttributeHeader(String eSlot) {
    if (attributeValues.containsKey(eSlot)) {
      List<String> header = new ArrayList<>(List.of(""));
      switch (eSlot) {
        case "head" -> header.add(ChatColor.GRAY + "When on Head:");
        case "chest" -> header.add(ChatColor.GRAY + "When on Chest:");
        case "legs" -> header.add(ChatColor.GRAY + "When on Legs:");
        case "feet" -> header.add(ChatColor.GRAY + "When on Feet:");
        case "necklace" -> header.add(ChatColor.GRAY + "When on Necklace:");
        case "ring" -> header.add(ChatColor.GRAY + "When on Ring:");
        case "hand" -> header.add(ChatColor.GRAY + "When in Main Hand:");
        case "off_hand" -> header.add(ChatColor.GRAY + "When in Off Hand:");
      }
      DecimalFormat df3 = new DecimalFormat();
      df3.setMaximumFractionDigits(3);
      for (String attribute : attributeValues.get(eSlot).keySet()) {
        switch (attribute) {
          case "critical_chance", "counter_chance", "dodge_chance", "critical_damage", "item_damage", "item_cooldown" -> header.add(ChatColor.DARK_GREEN + "+" + df3.format(attributeValues.get(eSlot).get(attribute)) + "% " + TextFormatter.capitalizePhrase(attribute));
          default -> header.add(ChatColor.DARK_GREEN + "+" + df3.format(attributeValues.get(eSlot).get(attribute)) + " " + TextFormatter.capitalizePhrase(attribute));
        }
      }
      lore.addAll(header);
    }
  }

  /**
   * Adds a passive ability {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   * header if it exists with its associated ability values.
   *
   * @param eSlot {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   */
  private void addPassiveHeader(String eSlot) {
    if (passiveAbilities.containsKey(eSlot)) {
      List<String> header = new ArrayList<>(List.of(""));
      switch (eSlot) {
        case "head" -> header.add(ChatColor.GRAY + "Head (Passive):");
        case "chest" -> header.add(ChatColor.GRAY + "Chest (Passive):");
        case "legs" -> header.add(ChatColor.GRAY + "Legs (Passive):");
        case "feet" -> header.add(ChatColor.GRAY + "Feet (Passive):");
        case "necklace" -> header.add(ChatColor.GRAY + "Necklace (Passive):");
        case "ring" -> header.add(ChatColor.GRAY + "Ring (Passive):");
        case "hand" -> header.add(ChatColor.GRAY + "Main Hand (Passive):");
        case "off_hand" -> header.add(ChatColor.GRAY + "Off Hand (Passive):");
      }
      header.addAll(passiveAbilities.get(eSlot));
      lore.addAll(header);
    }
  }

  /**
   * Adds an active ability {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   * header if it exists for its associated ability values.
   *
   * @param eSlot {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   */
  private void addActiveHeader(String eSlot) {
    if (activeAbilities.containsKey(eSlot)) {
      List<String> header = new ArrayList<>(List.of(""));
      switch (eSlot) {
        case "head" -> header.add(ChatColor.GRAY + "Head (Active):");
        case "chest" -> header.add(ChatColor.GRAY + "Chest (Active):");
        case "legs" -> header.add(ChatColor.GRAY + "Legs (Active):");
        case "feet" -> header.add(ChatColor.GRAY + "Feet (Active):");
        case "necklace" -> header.add(ChatColor.GRAY + "Necklace (Active):");
        case "ring" -> header.add(ChatColor.GRAY + "Ring (Active):");
        case "hand" -> header.add(ChatColor.GRAY + "Main Hand (Active):");
        case "off_hand" -> header.add(ChatColor.GRAY + "Off Hand (Active):");
      }
      header.addAll(activeAbilities.get(eSlot));
      lore.addAll(header);
    }
  }

  /**
   * Sorts Minecraft attributes by their {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}.
   *
   * @param attributeValues {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot} : (attribute : value)
   */
  private void sortMinecraftAttributes(Map<String, Map<String, Double>> attributeValues) {
    for (Attribute attribute : meta.getAttributeModifiers().keySet()) {
      for (AttributeModifier attributeModifier : meta.getAttributeModifiers(attribute)) {
        String slot = attributeModifier.getSlot().name().toLowerCase();
        String name;
        switch (attribute) {
          case GENERIC_MAX_HEALTH -> name = "max_hp";
          case GENERIC_ARMOR_TOUGHNESS -> name = "toughness";
          default -> name = attribute.name().substring(8).toLowerCase();
        }
        if (attributeValues.containsKey(slot)) {
          if (attributeValues.get(slot).containsKey(name)) {
            attributeValues.get(slot).put(name, attributeValues.get(slot).get(name) + attributeModifier.getAmount());
          } else {
            attributeValues.get(slot).put(name, attributeModifier.getAmount());
          }
        } else {
          attributeValues.put(slot, new HashMap<>(Map.of(name, attributeModifier.getAmount())));
        }
      }
    }
  }

  /**
   * Sorts {@link PluginNamespacedKey#ATTRIBUTE_LIST Aethel attributes}
   * by their {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}.
   *
   * @param attributeValues {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot} : (attribute : value)
   */
  private void sortAethelAttributes(Map<String, Map<String, Double>> attributeValues) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    for (String attribute : meta.getPersistentDataContainer().get(PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
      String slot = attribute.substring(0, attribute.indexOf("."));
      String name = attribute.substring(attribute.indexOf(".") + 1);
      NamespacedKey key = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute);
      if (attributeValues.containsKey(slot)) {
        if (attributeValues.get(slot).containsKey(name)) {
          attributeValues.get(slot).put(name, attributeValues.get(slot).get(name) + dataContainer.get(key, PersistentDataType.DOUBLE));
        } else {
          attributeValues.get(slot).put(name, dataContainer.get(key, PersistentDataType.DOUBLE));
        }
      } else {
        attributeValues.put(slot, new HashMap<>(Map.of(name, dataContainer.get(key, PersistentDataType.DOUBLE))));
      }
    }
  }

  /**
   * Adds ability {@link Trigger} lore.
   *
   * @param abilityLore ability lore
   * @param trigger     {@link Trigger}
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
  private String ticksToSeconds(String ticks) {
    return String.valueOf(Double.parseDouble(ticks) / 20);
  }
}
