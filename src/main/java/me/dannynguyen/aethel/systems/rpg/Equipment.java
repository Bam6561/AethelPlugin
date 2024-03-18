package me.dannynguyen.aethel.systems.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.Directory;
import me.dannynguyen.aethel.systems.plugin.KeyHeader;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.rpg.ability.*;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Represents an RPG player's equipment.
 *
 * @author Danny Nguyen
 * @version 1.17.1
 * @since 1.13.4
 */
public class Equipment {
  /**
   * Tracked equipment enchantments.
   */
  private static final Set<Enchantment> trackedEnchantments = Set.of(
      Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS,
      Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE);

  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * Total Aethel attributes.
   */
  private final Map<AethelAttribute, Double> aethelAttributes;

  /**
   * Equipment Aethel attributes.
   */
  private final Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> attributes = new HashMap<>();

  /**
   * Total equipment enchantments.
   */
  private final Map<Enchantment, Integer> totalEnchantments = createBlankTotalEnchantments();

  /**
   * Equipment enchantments.
   */
  private final Map<RpgEquipmentSlot, Map<Enchantment, Integer>> enchantments = new HashMap<>();

  /**
   * Equipment passive abilities by slot.
   */
  private final Map<RpgEquipmentSlot, List<TriggerAbility>> slotPassives = new HashMap<>();

  /**
   * Equipment passive abilities by trigger.
   */
  private final Map<Trigger, Map<SlotAbility, PassiveAbility>> triggerPassives = createBlankPassiveTriggers();

  /**
   * Equipment passive abilities on cooldown.
   */
  private final Map<Trigger, Set<SlotAbility>> onCooldownPassives = createBlankCooldownTriggers();

  /**
   * Jewelry slots.
   */
  private final ItemStack[] jewelry = new ItemStack[2];

  /**
   * Held item.
   */
  private ItemStack heldItem;

  /**
   * Associates RPG equipment with a player.
   *
   * @param player           interacting player
   * @param aethelAttributes total Aethel attributes
   */
  public Equipment(@NotNull Player player, @NotNull Map<AethelAttribute, Double> aethelAttributes) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.aethelAttributes = Objects.requireNonNull(aethelAttributes, "Null Aethel Attributes");
    this.heldItem = player.getInventory().getItemInMainHand();
    initializeEquipment(player);
    initializeJewelrySlots();
  }

  /**
   * Creates a blank map of total enchantment levels.
   *
   * @return blank total enchantment levels
   */
  private Map<Enchantment, Integer> createBlankTotalEnchantments() {
    Map<Enchantment, Integer> enchantments = new HashMap<>();
    for (Enchantment enchantment : trackedEnchantments) {
      enchantments.put(enchantment, 0);
    }
    return enchantments;
  }

  /**
   * Creates a blank map of ability triggers.
   *
   * @return blank ability triggers
   */
  private Map<Trigger, Map<SlotAbility, PassiveAbility>> createBlankPassiveTriggers() {
    Map<Trigger, Map<SlotAbility, PassiveAbility>> triggers = new HashMap<>();
    for (Trigger trigger : Trigger.values()) {
      triggers.put(trigger, new HashMap<>());
    }
    return triggers;
  }

  /**
   * Creates a blank map of ability cooldown triggers.
   *
   * @return blank ability cooldown triggers
   */
  private Map<Trigger, Set<SlotAbility>> createBlankCooldownTriggers() {
    Map<Trigger, Set<SlotAbility>> triggers = new HashMap<>();
    for (Trigger trigger : Trigger.values()) {
      triggers.put(trigger, new HashSet<>());
    }
    return triggers;
  }

  /**
   * Initializes the player's equipment-related Aethel tags.
   *
   * @param player interacting player
   */
  private void initializeEquipment(Player player) {
    PlayerInventory pInv = player.getInventory();
    readSlot(pInv.getItemInMainHand(), RpgEquipmentSlot.HAND, false);
    readSlot(pInv.getItemInOffHand(), RpgEquipmentSlot.OFF_HAND, false);
    readSlot(pInv.getHelmet(), RpgEquipmentSlot.HEAD, false);
    readSlot(pInv.getChestplate(), RpgEquipmentSlot.CHEST, false);
    readSlot(pInv.getLeggings(), RpgEquipmentSlot.LEGS, false);
    readSlot(pInv.getBoots(), RpgEquipmentSlot.FEET, false);
  }

  /**
   * Initializes the player's equipped jewelry from a file if it exists.
   */
  private void initializeJewelrySlots() {
    File file = new File(Directory.JEWELRY.getFile().getPath() + "/" + uuid.toString() + "_jwl.txt");
    if (file.exists()) {
      try {
        Scanner scanner = new Scanner(file);
        jewelry[0] = ItemReader.decodeItem(scanner.nextLine());
        jewelry[1] = ItemReader.decodeItem(scanner.nextLine());
        scanner.close();
      } catch (IOException ex) {
        Bukkit.getLogger().warning("[Aethel] Unable to read file: " + file.getName());
      }
    }
  }

  /**
   * Checks if the item has Aethel attribute modifiers, enchantments, and abilities
   * before checking whether the item is in the correct equipment slot.
   * <p>
   * A 2 tick delay is added to max health updates due to items containing
   * Minecraft's Generic Health attribute requiring 1 tick to update.
   * </p>
   *
   * @param item            interacting item
   * @param slot            slot type
   * @param maxHealthUpdate whether to update the player's max health
   */
  public void readSlot(@Nullable ItemStack item, @NotNull RpgEquipmentSlot slot, boolean maxHealthUpdate) {
    Objects.requireNonNull(slot, "Null RPG equipment slot");
    if (ItemReader.isNotNullOrAir(item)) {
      removeSlotData(slot);
      setSlotData(slot, item);
    } else {
      removeSlotData(slot);
    }
    if (maxHealthUpdate) {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getHealth().updateMaxHealth(), 2);
    }
  }

  /**
   * Saves the player's jewelry items to a file.
   */
  public void saveJewelry() {
    File file = new File(Directory.JEWELRY.getFile().getPath() + "/" + uuid + "_jwl.txt");
    String encodedJewelry = encodeJewelry();
    try {
      FileWriter fw = new FileWriter(file);
      fw.write(encodedJewelry);
      fw.close();
    } catch (IOException ex) {
      Bukkit.getLogger().warning("[Aethel] Failed to write " + uuid + "'s jewelry items to file.");
    }
  }

  /**
   * Sets new data stored about an equipment slot.
   *
   * @param slot slot type
   * @param item interacting item
   */
  private void setSlotData(RpgEquipmentSlot slot, ItemStack item) {
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
    if (dataContainer.has(PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      attributes.put(slot, new HashMap<>());
      readAttributes(slot, dataContainer);
    }
    if (item.getItemMeta().hasEnchants()) {
      enchantments.put(slot, new HashMap<>());
      addEnchantments(slot, item);
    }
    if (dataContainer.has(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      slotPassives.put(slot, new ArrayList<>());
      readPassives(slot, dataContainer);
    }
  }

  /**
   * Removes all data stored about an equipment slot.
   *
   * @param slot slot type
   */
  private void removeSlotData(RpgEquipmentSlot slot) {
    if (attributes.containsKey(slot)) {
      removeAttributes(slot);
    }
    if (enchantments.containsKey(slot)) {
      removeEnchantments(slot);
    }
    if (slotPassives.containsKey(slot)) {
      removePassives(slot);
    }
  }

  /**
   * Checks if the item is in the correct equipment slot before updating the player's attribute values.
   *
   * @param slot          slot type
   * @param dataContainer item's persistent tags
   */
  private void readAttributes(RpgEquipmentSlot slot, PersistentDataContainer dataContainer) {
    String[] attributes = dataContainer.get(PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String attribute : attributes) {
      RpgEquipmentSlot equipmentSlot = RpgEquipmentSlot.valueOf(attribute.substring(0, attribute.indexOf(".")).toUpperCase());
      if (equipmentSlot == slot) {
        addAttributes(slot, dataContainer, attribute);
      }
    }
  }

  /**
   * Adds new equipment attribute modifiers.
   *
   * @param slot          slot type
   * @param dataContainer item's persistent tags
   * @param attribute     attribute modifier
   */
  private void addAttributes(RpgEquipmentSlot slot, PersistentDataContainer dataContainer, String attribute) {
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute);
    AethelAttribute attributeType = AethelAttribute.valueOf(attribute.substring(attribute.indexOf(".") + 1).toUpperCase());
    attributes.get(slot).put(attributeType, dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
    aethelAttributes.put(attributeType, aethelAttributes.get(attributeType) + dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
  }

  /**
   * Removes existing equipment attribute modifiers at an equipment slot.
   *
   * @param slot slot type
   */
  public void removeAttributes(@NotNull RpgEquipmentSlot slot) {
    for (AethelAttribute attribute : attributes.get(Objects.requireNonNull(slot, "Null slot")).keySet()) {
      aethelAttributes.put(attribute, aethelAttributes.get(attribute) - attributes.get(slot).get(attribute));
      attributes.get(slot).put(attribute, 0.0);
    }
  }

  /**
   * Adds new equipment enchantments.
   *
   * @param slot slot type
   * @param item interacting item
   */
  public void addEnchantments(@NotNull RpgEquipmentSlot slot, @NotNull ItemStack item) {
    Objects.requireNonNull(slot, "Null slot");
    Objects.requireNonNull(item, "Null item");
    for (Enchantment enchantment : trackedEnchantments) {
      enchantments.get(slot).put(enchantment, item.getEnchantmentLevel(enchantment));
      totalEnchantments.put(enchantment, totalEnchantments.get(enchantment) + item.getEnchantmentLevel(enchantment));
    }
    readEnchantmentLevel(Enchantment.PROTECTION_FALL, 5);
    readEnchantmentLevel(Enchantment.PROTECTION_FIRE, 10);
  }

  /**
   * Removes existing equipment enchantments at an equipment slot.
   *
   * @param slot slot type
   */
  public void removeEnchantments(@NotNull RpgEquipmentSlot slot) {
    for (Enchantment enchantment : enchantments.get(Objects.requireNonNull(slot, "Null slot")).keySet()) {
      totalEnchantments.put(enchantment, totalEnchantments.get(enchantment) - enchantments.get(slot).get(enchantment));
      enchantments.get(slot).put(enchantment, 0);
    }
    readEnchantmentLevel(Enchantment.PROTECTION_FALL, 5);
    readEnchantmentLevel(Enchantment.PROTECTION_FIRE, 10);
  }

  /**
   * Checks if the player has met a certain enchantment level.
   *
   * @param enchantment enchantment to be checked
   * @param requirement required level to be sufficient
   */
  private void readEnchantmentLevel(Enchantment enchantment, int requirement) {
    if (totalEnchantments.get(enchantment) >= requirement) {
      Plugin.getData().getRpgSystem().getSufficientEnchantments().get(enchantment).add(uuid);
    } else {
      Plugin.getData().getRpgSystem().getSufficientEnchantments().get(enchantment).remove(uuid);
    }
  }

  /**
   * Checks if the item is in the correct equipment slot before updating the player's passive abilities.
   *
   * @param slot          slot type
   * @param dataContainer item's persistent tags
   */
  private void readPassives(RpgEquipmentSlot slot, PersistentDataContainer dataContainer) {
    String[] passives = dataContainer.get(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String passive : passives) {
      RpgEquipmentSlot equipmentSlot = RpgEquipmentSlot.valueOf(passive.substring(0, passive.indexOf(".")).toUpperCase());
      if (equipmentSlot == slot) {
        addPassives(slot, dataContainer, passive);
      }
    }
  }

  /**
   * Adds new equipment passive abilities.
   *
   * @param slot          slot type
   * @param dataContainer item's persistent tags
   * @param passive       ability data
   */
  private void addPassives(RpgEquipmentSlot slot, PersistentDataContainer dataContainer, String passive) {
    NamespacedKey passiveKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + passive);
    String[] abilityMeta = passive.split("\\.");
    Trigger trigger = Trigger.valueOf(abilityMeta[1].toUpperCase());
    PassiveAbilityType ability = PassiveAbilityType.valueOf(abilityMeta[2].toUpperCase());
    slotPassives.get(slot).add(new TriggerAbility(trigger, ability));
    triggerPassives.get(trigger).put(new SlotAbility(slot, ability), new PassiveAbility(onCooldownPassives, slot, trigger, ability, dataContainer.get(passiveKey, PersistentDataType.STRING).split(" ")));
  }

  /**
   * Removes existing equipment passive abilities at an equipment slot.
   *
   * @param slot slot type
   */
  public void removePassives(@NotNull RpgEquipmentSlot slot) {
    List<TriggerAbility> abilitiesToRemove = new ArrayList<>();
    for (TriggerAbility triggerAbility : slotPassives.get(Objects.requireNonNull(slot, "Null slot"))) {
      triggerPassives.get(triggerAbility.getTrigger()).remove(new SlotAbility(slot, triggerAbility.getAbility()));
      abilitiesToRemove.add(triggerAbility);
    }
    slotPassives.get(slot).removeAll(abilitiesToRemove);
  }

  /**
   * Encodes the player's jewelry items.
   *
   * @return encoded jewelry string
   */
  private String encodeJewelry() {
    StringBuilder encodedJewelry = new StringBuilder();
    for (ItemStack jewelrySlot : jewelry) {
      if (ItemReader.isNotNullOrAir(jewelrySlot)) {
        encodedJewelry.append(ItemCreator.encodeItem(jewelrySlot));
      } else {
        encodedJewelry.append("NULL");
      }
      encodedJewelry.append("\n");
    }
    return encodedJewelry.toString();
  }

  /**
   * Gets the player's equipment Aethel attributes.
   *
   * @return equipment Aethel attributes
   */
  @NotNull
  public Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> getAttributes() {
    return this.attributes;
  }

  /**
   * Gets the player's total equipment enchantments.
   *
   * @return total equipment enchantments
   */
  @NotNull
  public Map<Enchantment, Integer> getTotalEnchantments() {
    return this.totalEnchantments;
  }

  /**
   * Gets the player's equipment enchantments.
   *
   * @return equipment enchantments
   */
  @NotNull
  public Map<RpgEquipmentSlot, Map<Enchantment, Integer>> getEnchantments() {
    return this.enchantments;
  }

  /**
   * Gets the player's equipment passive abilities by slot.
   *
   * @return equipment passive abilities
   */
  @NotNull
  public Map<RpgEquipmentSlot, List<TriggerAbility>> getSlotPassives() {
    return this.slotPassives;
  }

  /**
   * Gets the player's equipment passive abilities by trigger.
   *
   * @return equipment passive abilities
   */
  @NotNull
  public Map<Trigger, Map<SlotAbility, PassiveAbility>> getTriggerPassives() {
    return this.triggerPassives;
  }

  /**
   * Gets the player's equipment passive abilities on cooldown.
   *
   * @return equipment passive abilities on cooldown
   */
  @NotNull
  public Map<Trigger, Set<SlotAbility>> getOnCooldownPassives() {
    return this.onCooldownPassives;
  }

  /**
   * Gets the player's jewelry.
   *
   * @return jewelry slots
   */
  @NotNull
  public ItemStack[] getJewelry() {
    return this.jewelry;
  }

  /**
   * Gets the player's held item tracked by the RPG system.
   * <p>
   * Held item is not synchronous with in-game events,
   * as it is only updated every 10 tick interval.
   * </p>
   *
   * @return held item tracked by the RPG system
   */
  @NotNull
  public ItemStack getHeldItem() {
    return this.heldItem;
  }

  /**
   * Sets the player's held item tracked by RPG system.
   *
   * @param item item to be set
   */
  public void setHeldItem(@Nullable ItemStack item) {
    this.heldItem = item;
  }
}
