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
 * @version 1.17.4
 * @since 1.13.4
 */
public class Equipment {
  /**
   * Tracked enchantments.
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
   * Aethel attributes by slot.
   */
  private final Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> attributes = new HashMap<>();

  /**
   * Total enchantments.
   */
  private final Map<Enchantment, Integer> totalEnchantments = createBlankTotalEnchantments();

  /**
   * Enchantments by slot.
   */
  private final Map<RpgEquipmentSlot, Map<Enchantment, Integer>> enchantments = new HashMap<>();

  /**
   * Passive abilities by slot.
   */
  private final Map<RpgEquipmentSlot, List<TriggerPassiveAbility>> slotPassives = new HashMap<>();

  /**
   * Passive abilities by trigger method..
   */
  private final Map<Trigger, Map<SlotPassiveAbility, PassiveAbility>> triggerPassives = createBlankPassiveTriggers();

  /**
   * Passive abilities on cooldown.
   */
  private final Map<Trigger, Set<SlotPassiveAbility>> onCooldownPassives = createBlankCooldownTriggers();

  /**
   * Active abilities by slot.
   */
  private final Map<RpgEquipmentSlot, List<ActiveAbilityType>> slotActives = new HashMap<>();

  /**
   * Active abilities by trigger method.
   */
  private final Map<RpgEquipmentSlot, List<ActiveAbility>> triggerActives = new HashMap<>();

  /**
   * Active abilities on cooldown.
   */
  private final Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives = new HashMap<>();

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
  private Map<Trigger, Map<SlotPassiveAbility, PassiveAbility>> createBlankPassiveTriggers() {
    Map<Trigger, Map<SlotPassiveAbility, PassiveAbility>> triggers = new HashMap<>();
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
  private Map<Trigger, Set<SlotPassiveAbility>> createBlankCooldownTriggers() {
    Map<Trigger, Set<SlotPassiveAbility>> triggers = new HashMap<>();
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
   * @param eSlot           slot type
   * @param maxHealthUpdate whether to update the player's max health
   */
  public void readSlot(@Nullable ItemStack item, @NotNull RpgEquipmentSlot eSlot, boolean maxHealthUpdate) {
    Objects.requireNonNull(eSlot, "Null RPG equipment slot");
    if (ItemReader.isNotNullOrAir(item)) {
      removeSlotData(eSlot);
      setSlotData(eSlot, item);
    } else {
      removeSlotData(eSlot);
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
   * @param eSlot equipment slot
   * @param item  interacting item
   */
  private void setSlotData(RpgEquipmentSlot eSlot, ItemStack item) {
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
    if (dataContainer.has(PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      attributes.put(eSlot, new HashMap<>());
      readAttributes(eSlot, dataContainer);
    }
    if (item.getItemMeta().hasEnchants()) {
      enchantments.put(eSlot, new HashMap<>());
      addEnchantments(eSlot, item);
    }
    if (dataContainer.has(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      slotPassives.put(eSlot, new ArrayList<>());
      readPassives(eSlot, dataContainer);
    }
  }

  /**
   * Removes all data stored about an equipment slot.
   *
   * @param eSlot equipment slot
   */
  private void removeSlotData(RpgEquipmentSlot eSlot) {
    if (attributes.containsKey(eSlot)) {
      removeAttributes(eSlot);
    }
    if (enchantments.containsKey(eSlot)) {
      removeEnchantments(eSlot);
    }
    if (slotPassives.containsKey(eSlot)) {
      removePassives(eSlot);
    }
  }

  /**
   * Checks if the item is in the correct equipment slot before updating the player's attribute values.
   *
   * @param eSlot         slot type
   * @param dataContainer item's persistent tags
   */
  private void readAttributes(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer) {
    String[] attributes = dataContainer.get(PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String attribute : attributes) {
      RpgEquipmentSlot equipmentSlot = RpgEquipmentSlot.valueOf(attribute.substring(0, attribute.indexOf(".")).toUpperCase());
      if (equipmentSlot == eSlot) {
        addAttributes(eSlot, dataContainer, attribute);
      }
    }
  }

  /**
   * Adds new equipment attribute modifiers.
   *
   * @param eSlot         slot type
   * @param dataContainer item's persistent tags
   * @param attribute     attribute modifier
   */
  private void addAttributes(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer, String attribute) {
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute);
    AethelAttribute attributeType = AethelAttribute.valueOf(attribute.substring(attribute.indexOf(".") + 1).toUpperCase());
    attributes.get(eSlot).put(attributeType, dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
    aethelAttributes.put(attributeType, aethelAttributes.get(attributeType) + dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
  }

  /**
   * Removes existing equipment attribute modifiers at an equipment slot.
   *
   * @param eSlot equipment slot
   */
  public void removeAttributes(@NotNull RpgEquipmentSlot eSlot) {
    for (AethelAttribute attribute : attributes.get(Objects.requireNonNull(eSlot, "Null slot")).keySet()) {
      aethelAttributes.put(attribute, aethelAttributes.get(attribute) - attributes.get(eSlot).get(attribute));
      attributes.get(eSlot).put(attribute, 0.0);
    }
  }

  /**
   * Adds new equipment enchantments.
   *
   * @param eSlot equipment slot
   * @param item  interacting item
   */
  public void addEnchantments(@NotNull RpgEquipmentSlot eSlot, @NotNull ItemStack item) {
    Objects.requireNonNull(eSlot, "Null slot");
    Objects.requireNonNull(item, "Null item");
    for (Enchantment enchantment : trackedEnchantments) {
      enchantments.get(eSlot).put(enchantment, item.getEnchantmentLevel(enchantment));
      totalEnchantments.put(enchantment, totalEnchantments.get(enchantment) + item.getEnchantmentLevel(enchantment));
    }
    readEnchantmentLevel(Enchantment.PROTECTION_FALL, 5);
    readEnchantmentLevel(Enchantment.PROTECTION_FIRE, 10);
  }

  /**
   * Removes existing equipment enchantments at an equipment slot.
   *
   * @param eSlot equipment slot
   */
  public void removeEnchantments(@NotNull RpgEquipmentSlot eSlot) {
    for (Enchantment enchantment : enchantments.get(Objects.requireNonNull(eSlot, "Null slot")).keySet()) {
      totalEnchantments.put(enchantment, totalEnchantments.get(enchantment) - enchantments.get(eSlot).get(enchantment));
      enchantments.get(eSlot).put(enchantment, 0);
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
   * @param eSlot         slot type
   * @param dataContainer item's persistent tags
   */
  private void readPassives(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer) {
    String[] passives = dataContainer.get(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String passive : passives) {
      RpgEquipmentSlot equipmentSlot = RpgEquipmentSlot.valueOf(passive.substring(0, passive.indexOf(".")).toUpperCase());
      if (equipmentSlot == eSlot) {
        addPassives(eSlot, dataContainer, passive);
      }
    }
  }

  /**
   * Adds new equipment passive abilities.
   *
   * @param eSlot         slot type
   * @param dataContainer item's persistent tags
   * @param passive       ability data
   */
  private void addPassives(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer, String passive) {
    NamespacedKey passiveKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + passive);
    String[] abilityMeta = passive.split("\\.");
    Trigger trigger = Trigger.valueOf(abilityMeta[1].toUpperCase());
    PassiveAbilityType abilityType = PassiveAbilityType.valueOf(abilityMeta[2].toUpperCase());
    slotPassives.get(eSlot).add(new TriggerPassiveAbility(trigger, abilityType));
    triggerPassives.get(trigger).put(new SlotPassiveAbility(eSlot, abilityType), new PassiveAbility(onCooldownPassives, eSlot, trigger, abilityType, dataContainer.get(passiveKey, PersistentDataType.STRING).split(" ")));
  }

  /**
   * Removes existing equipment passive abilities at an equipment slot.
   *
   * @param eSlot equipment slot
   */
  public void removePassives(@NotNull RpgEquipmentSlot eSlot) {
    List<TriggerPassiveAbility> abilitiesToRemove = new ArrayList<>();
    for (TriggerPassiveAbility triggerPassiveAbility : slotPassives.get(Objects.requireNonNull(eSlot, "Null slot"))) {
      triggerPassives.get(triggerPassiveAbility.getTrigger()).remove(new SlotPassiveAbility(eSlot, triggerPassiveAbility.getAbilityType()));
      abilitiesToRemove.add(triggerPassiveAbility);
    }
    slotPassives.get(eSlot).removeAll(abilitiesToRemove);
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
  public Map<RpgEquipmentSlot, List<TriggerPassiveAbility>> getSlotPassives() {
    return this.slotPassives;
  }

  /**
   * Gets the player's equipment passive abilities by trigger.
   *
   * @return equipment passive abilities
   */
  @NotNull
  public Map<Trigger, Map<SlotPassiveAbility, PassiveAbility>> getTriggerPassives() {
    return this.triggerPassives;
  }

  /**
   * Gets the player's equipment active abilities by slot.
   *
   * @return equipment active abilities
   */
  @NotNull
  public Map<RpgEquipmentSlot, List<ActiveAbilityType>> getSlotActives() {
    return this.slotActives;
  }

  /**
   * Gets the player's equipment passive ability triggers by slot.
   *
   * @return equipment active abilities
   */
  @NotNull
  public Map<RpgEquipmentSlot, List<ActiveAbility>> getTriggerActives() {
    return this.triggerActives;
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
