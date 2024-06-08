package me.bam6561.aethelplugin.utils.item;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Key;
import me.bam6561.aethelplugin.enums.plugin.KeyHeader;
import me.bam6561.aethelplugin.enums.rpg.RpgEquipmentSlot;
import me.bam6561.aethelplugin.enums.rpg.abilities.ActiveAbilityType;
import me.bam6561.aethelplugin.enums.rpg.abilities.PassiveAbilityType;
import me.bam6561.aethelplugin.enums.rpg.abilities.PassiveTriggerType;
import me.bam6561.aethelplugin.utils.TextFormatter;
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
 * Represents an item's Minecraft and {@link Key#ATTRIBUTE_LIST Aethel attribute},
 * {@link Key#PASSIVE_LIST passive ability}, {@link Key#ACTIVE_EQUIPMENT_LIST equipment active ability},
 * and {@link Key#ACTIVE_EDIBLE_LIST edible active ability} lore generation.
 *
 * @author Danny Nguyen
 * @version 1.27.1
 * @since 1.17.13
 */
public class LoreGeneration {
  /**
   * Order of headers by {@link RpgEquipmentSlot}.
   */
  private static final List<String> headerOrder = List.of(
      RpgEquipmentSlot.HEAD.getId(), RpgEquipmentSlot.CHEST.getId(),
      RpgEquipmentSlot.LEGS.getId(), RpgEquipmentSlot.FEET.getId(),
      RpgEquipmentSlot.NECKLACE.getId(), RpgEquipmentSlot.RING.getId(),
      RpgEquipmentSlot.HAND.getId(), RpgEquipmentSlot.OFF_HAND.getId());

  /**
   * Interacting user.
   */
  private final Player user;

  /**
   * Interacting item.
   */
  private final ItemStack item;

  /**
   * Interacting menu.
   */
  private final Inventory menu;

  /**
   * Item meta.
   */
  private final ItemMeta meta;

  /**
   * ItemStack's persistent tags.
   */
  private final PersistentDataContainer itemTags;

  /**
   * ItemStack's lore.
   */
  private final List<String> lore;

  /**
   * ItemStack's total Minecraft and {@link Key#ATTRIBUTE_LIST Aethel attribute}
   * values categorized by {@link RpgEquipmentSlot}.
   */
  private Map<String, Map<String, Double>> attributeValues;

  /**
   * ItemStack's {@link Key#PASSIVE_LIST passive abilities}
   * categorized by {@link RpgEquipmentSlot}.
   */
  private Map<String, List<String>> passiveAbilities;

  /**
   * ItemStack's {@link me.bam6561.aethelplugin.rpg.Equipment}
   * {@link Key#ACTIVE_EQUIPMENT_LIST active abilities} categorized by {@link RpgEquipmentSlot}.
   */
  private Map<String, List<String>> activeEquipmentAbilities;

  /**
   * ItemStack's edible {@link Key#ACTIVE_EDIBLE_LIST active abilities}.
   */
  private List<String> activeEdibleAbilities;

  /**
   * If the item's lore was generated.
   */
  private boolean generatedLore = false;

  /**
   * Associates lore generation with a user, item, and menu.
   *
   * @param user interacting user
   * @param item interacting item
   * @param menu interacting menu
   */
  public LoreGeneration(@NotNull Player user, @NotNull ItemStack item, @NotNull Inventory menu) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = Objects.requireNonNull(item, "Null item");
    this.menu = Objects.requireNonNull(menu, "Null menu");
    this.meta = item.getItemMeta();
    this.itemTags = meta.getPersistentDataContainer();
    if (meta.hasLore()) {
      this.lore = meta.getLore();
    } else {
      this.lore = new ArrayList<>();
    }
  }

  /**
   * Generates an item's lore based on its {@link Key plugin-related data}.
   */
  public void generateLore() {
    if (meta.hasAttributeModifiers() || itemTags.has(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      this.attributeValues = totalAttributeValues();
      addAttributeHeaders();
      if (meta.hasAttributeModifiers()) {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        menu.setItem(42, ItemCreator.createItem(Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Hide Attributes", List.of(ChatColor.GREEN + "True")));
      }
    }

    if (itemTags.has(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      this.passiveAbilities = sortPassiveAbilities();
      addPassiveHeaders();
    }

    if (itemTags.has(Key.ACTIVE_EQUIPMENT_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      this.activeEquipmentAbilities = sortActiveEquipmentAbilities();
      addActiveEquipmentHeaders();
    }

    if (itemTags.has(Key.ACTIVE_EDIBLE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      this.activeEdibleAbilities = sortActiveEdibleAbilities();
      addActiveEdibleHeader();
    }

    if (itemTags.has(Key.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING)) {
      if (generatedLore) {
        lore.add("");
      }
      if (itemTags.has(Key.UNUSABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        lore.add(ChatColor.DARK_GRAY + "Unusable");
      }
      if (itemTags.has(Key.NON_PLACEABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        lore.add(ChatColor.DARK_GRAY + "Non-Placeable");
      }
      if (itemTags.has(Key.NON_EDIBLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        lore.add(ChatColor.DARK_GRAY + "Non-Edible");
      }
      generatedLore = true;
      lore.add(ChatColor.DARK_GRAY + "aethel:" + itemTags.get(Key.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING));
    } else {
      if (itemTags.has(Key.UNUSABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        if (generatedLore) {
          lore.add("");
        }
        generatedLore = true;
        lore.add(ChatColor.DARK_GRAY + "Unusable");
      }
      if (itemTags.has(Key.NON_PLACEABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        if (generatedLore) {
          lore.add("");
        }
        generatedLore = true;
        lore.add(ChatColor.DARK_GRAY + "Non-Placeable");
      }
      if (itemTags.has(Key.NON_EDIBLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        if (generatedLore) {
          lore.add("");
        }
        generatedLore = true;
        lore.add(ChatColor.DARK_GRAY + "Non-Edible");
      }
    }

    boolean hasReinforcementTags = itemTags.has(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER) && itemTags.has(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
    if (hasReinforcementTags) {
      if (generatedLore) {
        lore.add("");
      }
      generatedLore = true;
      int reinforcement = itemTags.get(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
      int maxReinforcement = itemTags.get(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
      lore.add(ChatColor.WHITE + "Reinforcement: " + reinforcement + " / " + maxReinforcement);
    }

    if (generatedLore) {
      meta.setLore(lore);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Generated Lore]");
    } else {
      user.sendMessage(ChatColor.RED + "Not modified by plugin.");
    }
  }

  /**
   * Totals the item's Minecraft and {@link Key#ATTRIBUTE_LIST Aethel attributes} together.
   *
   * @return ItemStack's total attribute values
   */
  private Map<String, Map<String, Double>> totalAttributeValues() {
    Map<String, Map<String, Double>> attributeValues = new HashMap<>();
    if (meta.hasAttributeModifiers()) {
      sortMinecraftAttributes(attributeValues);
    }
    if (itemTags.has(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      sortAethelAttributes(attributeValues);
    }
    return attributeValues;
  }

  /**
   * Adds attribute {@link RpgEquipmentSlot} headers to the item's lore.
   */
  private void addAttributeHeaders() {
    for (String eSlot : headerOrder) {
      if (attributeValues.containsKey(eSlot)) {
        List<String> header = new ArrayList<>(List.of(""));
        switch (eSlot) {
          case "head" -> header.add(ChatColor.GRAY + "When on Head:");
          case "chest" -> header.add(ChatColor.GRAY + "When on Body:");
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
          StringBuilder line = new StringBuilder();
          double attributeValue = attributeValues.get(eSlot).get(attribute);

          line.append(ChatColor.BLUE);
          if (attribute.equals("item_cooldown")) {
            if (attributeValue >= 0) {
              line.append("-");
            } else {
              line.append("+");
            }
          } else if (attributeValue > 0) {
            line.append("+");
          }
          switch (attribute) {
            case "critical_chance", "counter_chance", "dodge_chance", "critical_damage", "item_damage", "item_cooldown",
                 "tenacity" ->
                line.append(df3.format(attributeValue)).append("% ").append(TextFormatter.capitalizePhrase(attribute));
            case "knockback_resistance" ->
                line.append(df3.format(attributeValue * 10)).append(" ").append(TextFormatter.capitalizePhrase(attribute));
            default ->
                line.append(df3.format(attributeValue)).append(" ").append(TextFormatter.capitalizePhrase(attribute));
          }
          header.add(line.toString());
        }
        lore.addAll(header);
      }
    }
  }

  /**
   * Sorts {@link Key#PASSIVE_LIST passive abilities}
   * by their {@link RpgEquipmentSlot}.
   *
   * @return {@link RpgEquipmentSlot} : {@link Key#PASSIVE_LIST passive ability}
   */
  private Map<String, List<String>> sortPassiveAbilities() {
    Map<String, List<String>> passiveAbilities = new HashMap<>();
    for (String passive : itemTags.get(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
      String[] passiveMeta = passive.split("\\.");
      String slot = passiveMeta[0];
      String condition = passiveMeta[1];
      String type = passiveMeta[2];

      PassiveAbilityType abilityType = PassiveAbilityType.valueOf(TextFormatter.formatEnum(type));
      PassiveTriggerType triggerType = PassiveTriggerType.valueOf(TextFormatter.formatEnum(condition));
      PassiveAbilityType.Effect effect = PassiveAbilityType.valueOf(TextFormatter.formatEnum(type)).getEffect();

      String[] abilityData = itemTags.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + slot + "." + condition + "." + type), PersistentDataType.STRING).split(" ");
      StringBuilder abilityLore = new StringBuilder();

      abilityLore.append(ChatColor.DARK_AQUA);
      switch (triggerType.getCondition()) {
        case COOLDOWN -> {
          addTriggerLore(abilityLore, triggerType);
          abilityLore.append(ChatColor.WHITE);
          // Cooldown
          if (!abilityData[0].equals("0")) {
            abilityLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[0])).append("s) ");
          }
          switch (effect) {
            case BUFF -> {
              String attributeName = abilityData[2];
              if (attributeName.startsWith("generic_") || attributeName.startsWith("player_")) {
                attributeName = attributeName.substring(attributeName.indexOf("_") + 1);
              }
              abilityLore.append("Gain ").append(TextFormatter.capitalizePhrase(attributeName)).append(" (");
              if (attributeName.equals("item_cooldown") && Double.parseDouble(abilityData[3]) >= 0) {
                abilityLore.append("-");
              }
              switch (attributeName) {
                case "knockback_resistance" -> abilityLore.append(Double.parseDouble(abilityData[4]) * 10);
                default -> abilityLore.append(abilityData[4]);
              }
              switch (attributeName) {
                case "critical_chance", "counter_chance", "dodge_chance", "critical_damage", "item_damage",
                     "item_cooldown", "tenacity" -> abilityLore.append("%) ");
                default -> abilityLore.append(") ");
              }
              abilityLore.append(ChatColor.AQUA).append("Buff ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[4])).append("s) [").append(abilityData[1].equals("true") ? "Self]" : "Target]");
            }
            case STACK_INSTANCE ->
                abilityLore.append(ChatColor.WHITE).append("Apply ").append(abilityData[2]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[1].equals("true") ? "Self] (" : "Target] (").append(ticksToSeconds(abilityData[3])).append("s)");
            case CHAIN_DAMAGE ->
                abilityLore.append(ChatColor.WHITE).append("Deal ").append(abilityData[2]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[1].equals("true") ? "Self] (" : "Target] (").append(abilityData[3]).append("m)");
            case POTION_EFFECT -> {
              int amplifier = Integer.parseInt(abilityData[3]) + 1;
              abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[2]))).append(" ").append(amplifier).append(ChatColor.AQUA).append(" Effect ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[4])).append("s) [").append(abilityData[1].equals("true") ? "Self]" : "Target]");
            }
          }
        }
        case CHANCE_COOLDOWN -> {
          addTriggerLore(abilityLore, triggerType);
          abilityLore.append(ChatColor.WHITE);
          // Chance
          if (!abilityData[0].equals("100.0")) {
            abilityLore.append(abilityData[0]).append("% ");
          }
          // Cooldown
          if (!abilityData[1].equals("0")) {
            abilityLore.append("(").append(ticksToSeconds(abilityData[1])).append("s) ");
          }
          switch (effect) {
            case BUFF -> {
              String attributeName = abilityData[3];
              if (attributeName.startsWith("generic_") || attributeName.startsWith("player_")) {
                attributeName = attributeName.substring(attributeName.indexOf("_") + 1);
              }
              abilityLore.append("Gain ").append(TextFormatter.capitalizePhrase(attributeName)).append(" (");
              if (attributeName.equals("item_cooldown") && Double.parseDouble(abilityData[4]) >= 0) {
                abilityLore.append("-");
              }
              switch (attributeName) {
                case "knockback_resistance" -> abilityLore.append(Double.parseDouble(abilityData[4]) * 10);
                default -> abilityLore.append(abilityData[4]);
              }
              switch (attributeName) {
                case "critical_chance", "counter_chance", "dodge_chance", "critical_damage", "item_damage",
                     "item_cooldown", "tenacity" -> abilityLore.append("%) ");
                default -> abilityLore.append(") ");
              }
              abilityLore.append(ChatColor.AQUA).append("Buff ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[5])).append("s) [").append(abilityData[2].equals("true") ? "Self]" : "Target]");
            }
            case STACK_INSTANCE ->
                abilityLore.append(ChatColor.WHITE).append("Apply ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(ticksToSeconds(abilityData[4])).append("s)");
            case CHAIN_DAMAGE ->
                abilityLore.append(ChatColor.WHITE).append("Deal ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(abilityData[4]).append("m)");
            case POTION_EFFECT -> {
              int amplifier = Integer.parseInt(abilityData[4]) + 1;
              abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[3]))).append(" ").append(amplifier).append(ChatColor.AQUA).append(" Effect ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[5])).append("s) [").append(abilityData[2].equals("true") ? "Self]" : "Target]");
            }
          }
        }
        case HEALTH_COOLDOWN -> {
          abilityLore.append("Below ").append(abilityData[0]).append("% HP: ");
          addTriggerLore(abilityLore, triggerType);
          abilityLore.append(ChatColor.WHITE);
          // Cooldown
          if (!abilityData[1].equals("0")) {
            abilityLore.append("(").append(ticksToSeconds(abilityData[1])).append("s) ");
          }
          switch (effect) {
            case BUFF -> {
              String attributeName = abilityData[3];
              if (attributeName.startsWith("generic_") || attributeName.startsWith("player_")) {
                attributeName = attributeName.substring(attributeName.indexOf("_") + 1);
              }
              abilityLore.append("Gain ").append(TextFormatter.capitalizePhrase(attributeName)).append(" (");
              if (attributeName.equals("item_cooldown") && Double.parseDouble(abilityData[4]) >= 0) {
                abilityLore.append("-");
              }
              switch (attributeName) {
                case "knockback_resistance" -> abilityLore.append(Double.parseDouble(abilityData[4]) * 10);
                default -> abilityLore.append(abilityData[4]);
              }
              switch (attributeName) {
                case "critical_chance", "counter_chance", "dodge_chance", "critical_damage", "item_damage",
                     "item_cooldown", "tenacity" -> abilityLore.append("%) ");
                default -> abilityLore.append(") ");
              }
              abilityLore.append(ChatColor.AQUA).append("Buff ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[5])).append("s) [").append(abilityData[2].equals("true") ? "Self]" : "Target]");
            }
            case STACK_INSTANCE ->
                abilityLore.append(ChatColor.WHITE).append("Apply ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(ticksToSeconds(abilityData[4])).append("s)");
            case CHAIN_DAMAGE ->
                abilityLore.append(ChatColor.WHITE).append("Deal ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(abilityData[4]).append("m)");
            case POTION_EFFECT -> {
              int amplifier = Integer.parseInt(abilityData[4]) + 1;
              abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[3]))).append(" ").append(amplifier).append(ChatColor.AQUA).append(" Effect ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[5])).append("s) [").append(abilityData[2].equals("true") ? "Self]" : "Target]");
            }
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
   * Adds passive ability {@link RpgEquipmentSlot} headers to the item's lore.
   */
  private void addPassiveHeaders() {
    for (String eSlot : headerOrder) {
      if (passiveAbilities.containsKey(eSlot)) {
        List<String> header = new ArrayList<>(List.of(""));
        String tag = ChatColor.GREEN + "Passives";
        switch (eSlot) {
          case "head" -> header.add(ChatColor.GRAY + "Head " + tag);
          case "chest" -> header.add(ChatColor.GRAY + "Chest " + tag);
          case "legs" -> header.add(ChatColor.GRAY + "Legs " + tag);
          case "feet" -> header.add(ChatColor.GRAY + "Feet " + tag);
          case "necklace" -> header.add(ChatColor.GRAY + "Necklace " + tag);
          case "ring" -> header.add(ChatColor.GRAY + "Ring " + tag);
          case "hand" -> header.add(ChatColor.GRAY + "Main Hand " + tag);
          case "off_hand" -> header.add(ChatColor.GRAY + "Off Hand " + tag);
        }
        header.addAll(passiveAbilities.get(eSlot));
        lore.addAll(header);
      }
    }
  }

  /**
   * Sorts {@link me.bam6561.aethelplugin.rpg.Equipment}
   * {@link Key#ACTIVE_EQUIPMENT_LIST active abilities} by their {@link RpgEquipmentSlot}.
   *
   * @return {@link RpgEquipmentSlot} : {@link Key#ACTIVE_EQUIPMENT_LIST}
   */
  private Map<String, List<String>> sortActiveEquipmentAbilities() {
    Map<String, List<String>> activeAbilities = new HashMap<>();
    for (String active : itemTags.get(Key.ACTIVE_EQUIPMENT_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
      String slot = active.substring(0, active.indexOf("."));
      String type = active.substring(active.indexOf(".") + 1);

      ActiveAbilityType abilityType = ActiveAbilityType.valueOf(TextFormatter.formatEnum(type));
      ActiveAbilityType.Effect abilityEffect = abilityType.getEffect();

      String[] abilityData = itemTags.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE_EQUIPMENT.getHeader() + slot + "." + type), PersistentDataType.STRING).split(" ");
      StringBuilder abilityLore = new StringBuilder();

      if (!abilityData[0].equals("0")) {
        abilityLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[0])).append("s) ");
      }
      abilityLore.append(ChatColor.WHITE);
      switch (abilityEffect) {
        case BUFF -> {
          String attributeName = abilityData[1];
          if (attributeName.startsWith("generic_") || attributeName.startsWith("player_")) {
            attributeName = attributeName.substring(attributeName.indexOf("_") + 1);
          }
          abilityLore.append("Gain ").append(TextFormatter.capitalizePhrase(attributeName)).append(" (");
          if (attributeName.equals("item_cooldown") && Double.parseDouble(abilityData[2]) >= 0) {
            abilityLore.append("-");
          }
          switch (attributeName) {
            case "knockback_resistance" -> abilityLore.append(Double.parseDouble(abilityData[2]) * 10);
            default -> abilityLore.append(abilityData[2]);
          }
          switch (attributeName) {
            case "critical_chance", "counter_chance", "dodge_chance", "critical_damage", "item_damage", "item_cooldown",
                 "tenacity" -> abilityLore.append("%) ");
            default -> abilityLore.append(") ");
          }
          abilityLore.append(ChatColor.AQUA).append("Buff ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[3])).append("s)");
        }
        case CLEAR_STATUS -> abilityLore.append(ChatColor.AQUA).append(abilityType.getProperName());
        case DISPLACEMENT ->
            abilityLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" ").append(abilityData[1]).append("% (").append(abilityData[2]).append("m)");
        case DISTANCE_DAMAGE ->
            abilityLore.append("Deal ").append(abilityData[1]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" Damage").append(" (").append(abilityData[2]).append("m)");
        case MOVEMENT ->
            abilityLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" (").append(abilityData[1]).append("%)");
        case POTION_EFFECT -> {
          int amplifier = Integer.parseInt(abilityData[2]) + 1;
          abilityLore.append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[1]))).append(" ").append(amplifier).append(ChatColor.AQUA).append(" Effect").append(ChatColor.WHITE).append(" (").append(ticksToSeconds(abilityData[3])).append("s)");
        }
        case PROJECTION ->
            abilityLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" (").append(abilityData[1]).append("m) Return after (").append(ticksToSeconds(abilityData[2])).append("s)");
        case SHATTER, TELEPORT ->
            abilityLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" (").append(abilityData[1]).append("m)");
      }
      if (activeAbilities.containsKey(slot)) {
        activeAbilities.get(slot).add(abilityLore.toString());
      } else {
        activeAbilities.put(slot, new ArrayList<>(List.of(abilityLore.toString())));
      }
    }
    return activeAbilities;
  }

  /**
   * Adds active ability {@link RpgEquipmentSlot} headers to the item's lore.
   */
  private void addActiveEquipmentHeaders() {
    for (String eSlot : headerOrder) {
      if (activeEquipmentAbilities.containsKey(eSlot)) {
        List<String> header = new ArrayList<>(List.of(""));
        String tag = ChatColor.YELLOW + "Actives";
        switch (eSlot) {
          case "head" -> header.add(ChatColor.GRAY + "Head " + tag);
          case "chest" -> header.add(ChatColor.GRAY + "Chest " + tag);
          case "legs" -> header.add(ChatColor.GRAY + "Legs " + tag);
          case "feet" -> header.add(ChatColor.GRAY + "Feet " + tag);
          case "necklace" -> header.add(ChatColor.GRAY + "Necklace " + tag);
          case "ring" -> header.add(ChatColor.GRAY + "Ring " + tag);
          case "hand" -> header.add(ChatColor.GRAY + "Main Hand " + tag);
          case "off_hand" -> header.add(ChatColor.GRAY + "Off Hand " + tag);
        }
        header.addAll(activeEquipmentAbilities.get(eSlot));
        lore.addAll(header);
      }
    }
  }

  /**
   * Sorts edible {@link Key#ACTIVE_EDIBLE_LIST active abilities}
   * by their {@link RpgEquipmentSlot}.
   *
   * @return {@link RpgEquipmentSlot} : {@link Key#ACTIVE_EDIBLE_LIST}
   */
  private List<String> sortActiveEdibleAbilities() {
    List<String> activeAbilities = new ArrayList<>();
    for (String active : itemTags.get(Key.ACTIVE_EDIBLE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
      ActiveAbilityType abilityType = ActiveAbilityType.valueOf(TextFormatter.formatEnum(active));
      ActiveAbilityType.Effect abilityEffect = abilityType.getEffect();

      String[] abilityData = itemTags.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE_EDIBLE.getHeader() + active), PersistentDataType.STRING).split(" ");
      StringBuilder abilityLore = new StringBuilder();

      if (!abilityData[0].equals("0")) {
        abilityLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[0])).append("s) ");
      }
      abilityLore.append(ChatColor.WHITE);
      switch (abilityEffect) {
        case BUFF -> {
          String attributeName = abilityData[1];
          if (attributeName.startsWith("generic_") || attributeName.startsWith("player_")) {
            attributeName = attributeName.substring(attributeName.indexOf("_") + 1);
          }
          abilityLore.append("Gain ").append(TextFormatter.capitalizePhrase(attributeName)).append(" (");
          if (attributeName.equals("item_cooldown") && Double.parseDouble(abilityData[2]) >= 0) {
            abilityLore.append("-");
          }
          switch (attributeName) {
            case "knockback_resistance" -> abilityLore.append(Double.parseDouble(abilityData[2]) * 10);
            default -> abilityLore.append(abilityData[2]);
          }
          switch (attributeName) {
            case "critical_chance", "counter_chance", "dodge_chance", "critical_damage", "item_damage", "item_cooldown",
                 "tenacity" -> abilityLore.append("%) ");
            default -> abilityLore.append(") ");
          }
          abilityLore.append(ChatColor.AQUA).append("Buff ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[3])).append("s)");
        }
        case CLEAR_STATUS -> abilityLore.append(ChatColor.AQUA).append(abilityType.getProperName());
        case DISPLACEMENT ->
            abilityLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" ").append(abilityData[1]).append("% (").append(abilityData[2]).append("m)");
        case DISTANCE_DAMAGE ->
            abilityLore.append("Deal ").append(abilityData[1]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" Damage").append(" (").append(abilityData[2]).append("m)");
        case MOVEMENT ->
            abilityLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" (").append(abilityData[1]).append("%)");
        case POTION_EFFECT -> {
          int amplifier = Integer.parseInt(abilityData[2]) + 1;
          abilityLore.append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[1]))).append(" ").append(amplifier).append(ChatColor.AQUA).append(" Effect").append(ChatColor.WHITE).append(" (").append(ticksToSeconds(abilityData[3])).append("s)");
        }
        case PROJECTION ->
            abilityLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" (").append(abilityData[1]).append("m) Return after (").append(ticksToSeconds(abilityData[2])).append("s)");
        case SHATTER, TELEPORT ->
            abilityLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" (").append(abilityData[1]).append("m)");
      }
      activeAbilities.add(abilityLore.toString());
    }
    return activeAbilities;
  }

  /**
   * Adds the edible active ability header to the item's lore.
   */
  private void addActiveEdibleHeader() {
    List<String> header = new ArrayList<>(List.of(""));
    header.add(ChatColor.GRAY + "Edible " + ChatColor.LIGHT_PURPLE + "Actives");
    header.addAll(activeEdibleAbilities);
    lore.addAll(header);
  }

  /**
   * Sorts Minecraft attributes by their {@link RpgEquipmentSlot}.
   *
   * @param attributeValues {@link RpgEquipmentSlot} : (attribute : value)
   */
  private void sortMinecraftAttributes(Map<String, Map<String, Double>> attributeValues) {
    for (Attribute attribute : meta.getAttributeModifiers().keySet()) {
      for (AttributeModifier attributeModifier : meta.getAttributeModifiers(attribute)) {
        String slot = attributeModifier.getSlot().name().toLowerCase();
        String name;
        switch (attribute) {
          case GENERIC_ARMOR -> name = "armor";
          case GENERIC_MAX_HEALTH -> name = "max_health";
          case GENERIC_ARMOR_TOUGHNESS -> name = "armor_toughness";
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
   * Sorts {@link Key#ATTRIBUTE_LIST Aethel attributes}
   * by their {@link RpgEquipmentSlot}.
   *
   * @param attributeValues {@link RpgEquipmentSlot} : (attribute : value)
   */
  private void sortAethelAttributes(Map<String, Map<String, Double>> attributeValues) {
    PersistentDataContainer itemTags = meta.getPersistentDataContainer();
    for (String attribute : meta.getPersistentDataContainer().get(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
      String slot = attribute.substring(0, attribute.indexOf("."));
      String name = attribute.substring(attribute.indexOf(".") + 1);
      NamespacedKey key = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute);
      if (attributeValues.containsKey(slot)) {
        if (attributeValues.get(slot).containsKey(name)) {
          attributeValues.get(slot).put(name, attributeValues.get(slot).get(name) + itemTags.get(key, PersistentDataType.DOUBLE));
        } else {
          attributeValues.get(slot).put(name, itemTags.get(key, PersistentDataType.DOUBLE));
        }
      } else {
        attributeValues.put(slot, new HashMap<>(Map.of(name, itemTags.get(key, PersistentDataType.DOUBLE))));
      }
    }
  }

  /**
   * Adds ability {@link PassiveTriggerType} lore.
   *
   * @param abilityLore        ability lore
   * @param passiveTriggerType {@link PassiveTriggerType}
   */
  private void addTriggerLore(StringBuilder abilityLore, PassiveTriggerType passiveTriggerType) {
    switch (passiveTriggerType) {
      case DAMAGE_DEALT -> abilityLore.append("Damage Dealt: ");
      case DAMAGE_TAKEN -> abilityLore.append("Damage Taken: ");
      case INTERVAL -> abilityLore.append("Interval: ");
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

  /**
   * Gets the potion effect type as an ID.
   *
   * @param potionEffect potion effect name
   * @return potion effect ID
   */
  private String getPotionEffectTypeAsId(String potionEffect) {
    potionEffect = potionEffect.toLowerCase();
    switch (potionEffect) {
      case "confusion" -> potionEffect = "nausea";
      case "damage_resistance" -> potionEffect = "resistance";
      case "fast_digging" -> potionEffect = "haste";
      case "harm" -> potionEffect = "instant_damage";
      case "heal" -> potionEffect = "instant_health";
      case "increase_damage" -> potionEffect = "strength";
      case "jump" -> potionEffect = "leap_boost";
      case "slow" -> potionEffect = "slowness";
      case "slow_digging" -> potionEffect = "mining_fatigue";
    }
    return potionEffect;
  }
}
