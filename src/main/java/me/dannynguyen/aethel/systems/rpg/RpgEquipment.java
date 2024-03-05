package me.dannynguyen.aethel.systems.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.PluginDirectory;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Represents an RPG player's equipment.
 *
 * @author Danny Nguyen
 * @version 1.14.2
 * @since 1.13.4
 */
public class RpgEquipment {
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
   * Total equipment enchantments.
   */
  private final Map<Enchantment, Integer> totalEnchantments = createBlankTotalEnchantments();

  /**
   * Equipment enchantments.
   */
  private final Map<RpgEquipmentSlot, Map<Enchantment, Integer>> enchantments = new HashMap<>();

  /**
   * Equipment Aethel attributes.
   */
  private final Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> attributes = new HashMap<>();

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
  public RpgEquipment(@NotNull Player player, @NotNull Map<AethelAttribute, Double> aethelAttributes) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.aethelAttributes = Objects.requireNonNull(aethelAttributes, "Null Aethel Attributes");
    this.heldItem = player.getInventory().getItemInMainHand();
    initializeEquipmentAttributes(player);
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
   * Initializes the player's equipment-related Aethel attribute modifiers.
   *
   * @param player interacting player
   */
  private void initializeEquipmentAttributes(Player player) {
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
    File file = new File(PluginDirectory.JEWELRY.getFile().getPath() + "/" + uuid.toString() + "_jwl.txt");
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
   * Checks if the item has enchantments and Aethel attribute modifiers
   * before checking whether the item is in the correct equipment slot.
   * <p>
   * A 2 tick delay is added to max health updates due to items containing
   * Minecraft's Generic Health attribute requiring 1 tick to update.
   * </p>
   *
   * @param item interacting item
   * @param slot slot type
   */
  public void readSlot(ItemStack item, @NotNull RpgEquipmentSlot slot, boolean maxHealthUpdate) {
    Objects.requireNonNull(slot, "Null RPG equipment slot");
    if (ItemReader.isNotNullOrAir(item)) {
      if (enchantments.containsKey(slot)) {
        removeEnchantments(slot);
      }
      if (item.getItemMeta().hasEnchants()) {
        enchantments.put(slot, new HashMap<>());
        addEnchantments(slot, item);
      }

      if (attributes.containsKey(slot)) {
        removeAttributes(slot);
      }
      PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
      NamespacedKey listKey = PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey();
      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        attributes.put(slot, new HashMap<>());
        readAttributes(slot, dataContainer, listKey);
      }
    } else {
      if (enchantments.containsKey(slot)) {
        removeEnchantments(slot);
      }
      if (attributes.containsKey(slot)) {
        removeAttributes(slot);
      }
    }
    if (maxHealthUpdate) {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> PluginData.rpgSystem.getRpgPlayers().get(uuid).getHealth().updateMaxHealth(), 2);
    }
  }

  /**
   * Saves the player's jewelry items to a file.
   */
  public void saveJewelry() {
    File file = new File(PluginDirectory.JEWELRY.getFile().getPath() + "/" + uuid + "_jwl.txt");
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
   * Adds new equipment enchantments.
   *
   * @param slot slot type
   * @param item interacting item
   */
  public void addEnchantments(RpgEquipmentSlot slot, ItemStack item) {
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
  public void removeEnchantments(RpgEquipmentSlot slot) {
    for (Enchantment enchantment : enchantments.get(slot).keySet()) {
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
      PluginData.rpgSystem.getSufficientEnchantments().get(enchantment).add(uuid);
    } else {
      PluginData.rpgSystem.getSufficientEnchantments().get(enchantment).remove(uuid);
    }
  }

  /**
   * Checks if the item is in the correct equipment slot before updating the player's attribute values.
   *
   * @param slot          slot type
   * @param dataContainer item's persistent tags
   * @param listKey       attributes list
   */
  private void readAttributes(RpgEquipmentSlot slot, PersistentDataContainer dataContainer, NamespacedKey listKey) {
    String[] attributes = dataContainer.get(listKey, PersistentDataType.STRING).split(" ");
    for (String attribute : attributes) {
      RpgEquipmentSlot equipmentSlot = RpgEquipmentSlot.valueOf(attribute.substring(attribute.indexOf(".") + 1).toUpperCase());
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
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + attribute);
    AethelAttribute attributeType = AethelAttribute.valueOf(attribute.substring(0, attribute.indexOf(".")).toUpperCase());
    attributes.get(slot).put(attributeType, dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
    aethelAttributes.put(attributeType, aethelAttributes.get(attributeType) + dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
  }

  /**
   * Removes existing equipment attribute modifiers at an equipment slot.
   *
   * @param slot slot type
   */
  public void removeAttributes(RpgEquipmentSlot slot) {
    for (AethelAttribute attribute : attributes.get(slot).keySet()) {
      aethelAttributes.put(attribute, aethelAttributes.get(attribute) - attributes.get(slot).get(attribute));
      attributes.get(slot).put(attribute, 0.0);
    }
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
   * Sets the player's held item tracked by RPG system.
   *
   * @param item item to be set
   */
  public void setHeldItem(ItemStack item) {
    this.heldItem = item;
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
   * Gets the player's equipment Aethel attributes.
   *
   * @return equipment Aethel attributes
   */
  @NotNull
  public Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> getAttributes() {
    return this.attributes;
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
}
