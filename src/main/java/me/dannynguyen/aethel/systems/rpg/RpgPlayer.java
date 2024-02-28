package me.dannynguyen.aethel.systems.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.enums.PluginDirectory;
import me.dannynguyen.aethel.systems.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
import java.text.DecimalFormat;
import java.util.*;

/**
 * Represents a player's RPG metadata.
 *
 * @author Danny Nguyen
 * @version 1.13.1
 * @since 1.8.9
 */
public class RpgPlayer {
  /**
   * Tracked equipment enchantments.
   */
  private static final Set<Enchantment> trackedEquipmentEnchantments = Set.of(
      Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS,
      Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE);

  /**
   * RPG player's UUID.
   */
  private final UUID uuid;

  /**
   * Total equipment enchantments.
   */
  private final Map<Enchantment, Integer> totalEquipmentEnchantments = createBlankTotalEquipmentEnchantments();

  /**
   * Equipment enchantments.
   */
  private final Map<RpgEquipmentSlot, Map<Enchantment, Integer>> equipmentEnchantments = new HashMap<>();

  /**
   * Total Aethel attributes.
   */
  private final Map<AethelAttribute, Double> aethelAttributes = createBlankAethelAttributes();

  /**
   * Equipment Aethel attributes.
   */
  private final Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> equipmentAttributes = new HashMap<>();

  /**
   * Jewelry slots.
   */
  private final ItemStack[] jewelrySlots = new ItemStack[2];

  /**
   * Player health bar.
   */
  private final BossBar healthBar = Bukkit.createBossBar("Health", BarColor.RED, BarStyle.SEGMENTED_10);

  /**
   * Player health.
   */
  private double currentHealth;

  /**
   * Player max health.
   */
  private double maxHealth;

  /**
   * Associates a player with a new RPG player.
   *
   * @param player interacting player
   */
  public RpgPlayer(@NotNull Player player) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    initializeEquipmentAttributes(player);
    initializeJewelrySlots();
    this.currentHealth = player.getHealth();
    this.maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + aethelAttributes.get(AethelAttribute.MAX_HP);
    initializeHealthBar(player);
  }

  /**
   * Creates a blank map of total enchantment levels.
   *
   * @return blank total enchantment levels
   */
  private Map<Enchantment, Integer> createBlankTotalEquipmentEnchantments() {
    Map<Enchantment, Integer> enchantments = new HashMap<>();
    for (Enchantment enchantment : trackedEquipmentEnchantments) {
      enchantments.put(enchantment, 0);
    }
    return enchantments;
  }

  /**
   * Creates a blank map of Aethel attributes.
   *
   * @return blank Aethel attributes
   */
  private Map<AethelAttribute, Double> createBlankAethelAttributes() {
    Map<AethelAttribute, Double> aethelAttributes = new HashMap<>();
    for (AethelAttribute attribute : AethelAttribute.values()) {
      aethelAttributes.put(attribute, 0.0);
    }
    return aethelAttributes;
  }

  /**
   * Initializes the player's equipment-related Aethel attribute modifiers.
   *
   * @param player interacting player
   */
  private void initializeEquipmentAttributes(Player player) {
    PlayerInventory pInv = player.getInventory();
    readEquipmentSlot(pInv.getItemInMainHand(), RpgEquipmentSlot.HAND);
    readEquipmentSlot(pInv.getItemInOffHand(), RpgEquipmentSlot.OFF_HAND);
    readEquipmentSlot(pInv.getHelmet(), RpgEquipmentSlot.HEAD);
    readEquipmentSlot(pInv.getChestplate(), RpgEquipmentSlot.CHEST);
    readEquipmentSlot(pInv.getLeggings(), RpgEquipmentSlot.LEGS);
    readEquipmentSlot(pInv.getBoots(), RpgEquipmentSlot.FEET);
  }

  /**
   * Initializes the player's equipped jewelry from a file if it exists.
   */
  private void initializeJewelrySlots() {
    File file = new File(PluginDirectory.JEWELRY.getFile().getPath() + "/" + uuid.toString() + "_jwl.txt");
    if (file.exists()) {
      try {
        Scanner scanner = new Scanner(file);
        jewelrySlots[0] = ItemReader.decodeItem(scanner.nextLine());
        jewelrySlots[1] = ItemReader.decodeItem(scanner.nextLine());
        scanner.close();
      } catch (IOException ex) {
        Bukkit.getLogger().warning("[Aethel] Unable to read file: " + file.getName());
      }
    }
  }

  /**
   * Initializes the player's health bar.
   *
   * @param player interacting player
   */
  private void initializeHealthBar(Player player) {
    double minecraftMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    double healthScale = (aethelAttributes.get(AethelAttribute.MAX_HP) + minecraftMaxHealth) / minecraftMaxHealth;
    setCurrentHealth(currentHealth * healthScale);
    updateHealthBarProgress();
    healthBar.addPlayer(player);
  }

  /**
   * Checks if the item has enchantments and Aethel attribute modifiers
   * before checking whether the item is in the correct equipment slot.
   *
   * @param item interacting item
   * @param slot slot type
   */
  public void readEquipmentSlot(ItemStack item, RpgEquipmentSlot slot) {
    if (ItemReader.isNotNullOrAir(item)) {
      if (equipmentEnchantments.containsKey(slot)) {
        removeEquipmentEnchantments(slot);
      }
      if (item.getItemMeta().hasEnchants()) {
        equipmentEnchantments.put(slot, new HashMap<>());
        addEquipmentEnchantments(slot, item);
      }

      if (equipmentAttributes.containsKey(slot)) {
        removeEquipmentAttributes(slot);
      }
      PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
      NamespacedKey listKey = PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey();
      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        equipmentAttributes.put(slot, new HashMap<>());
        readEquipmentAttributes(slot, dataContainer, listKey);
      }
    } else {
      if (equipmentEnchantments.containsKey(slot)) {
        removeEquipmentEnchantments(slot);
      }
      if (equipmentAttributes.containsKey(slot)) {
        removeEquipmentAttributes(slot);
      }
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
   * Updates the health bar and display.
   */
  public void updateHealthBar() {
    Player player = Bukkit.getPlayer(uuid);
    setCurrentHealth(currentHealth + player.getAbsorptionAmount());
    player.setAbsorptionAmount(0);
    setMaxHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + aethelAttributes.get(AethelAttribute.MAX_HP));
    updateHealthBarProgress();
  }

  /**
   * Damages the player by an amount.
   *
   * @param damage damage amount
   */
  public void damageHealthBar(double damage) {
    setCurrentHealth(currentHealth - damage);
    if (currentHealth > 0) {
      updateHealthBarProgress();
    } else {
      DecimalFormat dc = new DecimalFormat();
      dc.setMaximumFractionDigits(2);
      setCurrentHealth(0.0);
      Bukkit.getPlayer(uuid).setHealth(currentHealth);
      healthBar.setProgress(0.0);
      healthBar.setTitle(0 + " / " + dc.format(maxHealth) + " HP");
    }
  }

  /**
   * Heals the player by an amount.
   *
   * @param heal heal amount
   */
  public void healHealthBar(double heal) {
    if (!(currentHealth > maxHealth)) {
      setCurrentHealth(Math.min(maxHealth, currentHealth + heal));
      updateHealthBarProgress();
    }
  }

  /**
   * Resets the health bar.
   */
  public void resetHealthBar() {
    setCurrentHealth(20.0);
    setMaxHealth(20.0);
    updateHealthBarProgress();
  }

  /**
   * Decays a player's current health.
   * <p>
   * This method should only be used when a player's overshield
   * (current health > max health) exceeds x1.2 their max health.
   * </p>
   */
  public void decayOvershield() {
    double overshieldCap = maxHealth * 1.2;
    double decayRate = Math.max((currentHealth - overshieldCap) / 40, 0.25);
    setCurrentHealth(Math.max(overshieldCap, currentHealth - decayRate));
    updateHealthBarProgress();
  }

  /**
   * Toggles the visibility of the health bar.
   */
  public void toggleHealthBar() {
    healthBar.setVisible(!healthBar.isVisible());
  }

  /**
   * Adds new equipment enchantments.
   *
   * @param slot slot type
   * @param item interacting item
   */
  public void addEquipmentEnchantments(RpgEquipmentSlot slot, ItemStack item) {
    for (Enchantment enchantment : trackedEquipmentEnchantments) {
      equipmentEnchantments.get(slot).put(enchantment, item.getEnchantmentLevel(enchantment));
      totalEquipmentEnchantments.put(enchantment, totalEquipmentEnchantments.get(enchantment) + item.getEnchantmentLevel(enchantment));
    }
  }

  /**
   * Removes existing equipment enchantments at an equipment slot.
   *
   * @param slot slot type
   */
  public void removeEquipmentEnchantments(RpgEquipmentSlot slot) {
    for (Enchantment enchantment : equipmentEnchantments.get(slot).keySet()) {
      totalEquipmentEnchantments.put(enchantment, totalEquipmentEnchantments.get(enchantment) - equipmentEnchantments.get(slot).get(enchantment));
      equipmentEnchantments.get(slot).put(enchantment, 0);
    }
  }

  /**
   * Checks if the item is in the correct equipment slot before updating the player's attribute values.
   *
   * @param slot          slot type
   * @param dataContainer item's persistent tags
   * @param listKey       attributes list
   */
  private void readEquipmentAttributes(RpgEquipmentSlot slot, PersistentDataContainer dataContainer, NamespacedKey listKey) {
    String[] attributes = dataContainer.get(listKey, PersistentDataType.STRING).split(" ");
    for (String attribute : attributes) {
      RpgEquipmentSlot equipmentSlot = RpgEquipmentSlot.valueOf(attribute.substring(attribute.indexOf(".") + 1).toUpperCase());
      if (equipmentSlot == slot) {
        addEquipmentAttributes(slot, dataContainer, attribute);
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
  private void addEquipmentAttributes(RpgEquipmentSlot slot, PersistentDataContainer dataContainer, String attribute) {
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + attribute);
    AethelAttribute attributeType = AethelAttribute.valueOf(attribute.substring(0, attribute.indexOf(".")).toUpperCase());
    equipmentAttributes.get(slot).put(attributeType, dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
    aethelAttributes.put(attributeType, aethelAttributes.get(attributeType) + dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
  }

  /**
   * Removes existing equipment attribute modifiers at an equipment slot.
   *
   * @param slot slot type
   */
  public void removeEquipmentAttributes(RpgEquipmentSlot slot) {
    for (AethelAttribute attribute : equipmentAttributes.get(slot).keySet()) {
      aethelAttributes.put(attribute, aethelAttributes.get(attribute) - equipmentAttributes.get(slot).get(attribute));
      equipmentAttributes.get(slot).put(attribute, 0.0);
    }
  }

  /**
   * Encodes the player's jewelry items.
   *
   * @return encoded jewelry string
   */
  private String encodeJewelry() {
    StringBuilder encodedJewelry = new StringBuilder();
    for (ItemStack jewelrySlot : jewelrySlots) {
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
   * Sets the progress of the health bar based on the current health : max health.
   */
  private void updateHealthBarProgress() {
    Player player = Bukkit.getPlayer(uuid);
    double maxHealthScale = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    if (currentHealth < maxHealth) {
      double lifeRatio = currentHealth / maxHealth;
      player.setHealth(lifeRatio * maxHealthScale);
      healthBar.setProgress(lifeRatio);
      healthBar.setColor(BarColor.RED);
    } else if (currentHealth == maxHealth) {
      player.setHealth(maxHealthScale);
      healthBar.setProgress(1.0);
      healthBar.setColor(BarColor.RED);
    } else if (currentHealth > maxHealth) {
      player.setHealth(maxHealthScale);
      healthBar.setProgress(1.0);
      healthBar.setColor(BarColor.YELLOW);
    }
    DecimalFormat dc = new DecimalFormat();
    dc.setMaximumFractionDigits(2);
    healthBar.setTitle(dc.format(currentHealth) + " / " + dc.format(maxHealth) + " HP");
  }

  /**
   * Gets the UUID the RPG player belongs to.
   *
   * @return RPG player owner
   */
  @NotNull
  public UUID getUUID() {
    return this.uuid;
  }

  /**
   * Gets the player's total equipment enchantments.
   *
   * @return total equipment enchantments
   */
  @NotNull
  public Map<Enchantment, Integer> getTotalEquipmentEnchantments() {
    return this.totalEquipmentEnchantments;
  }

  /**
   * Gets the player's equipment enchantments.
   *
   * @return equipment enchantments
   */
  @NotNull
  public Map<RpgEquipmentSlot, Map<Enchantment, Integer>> getEquipmentEnchantments() {
    return this.equipmentEnchantments;
  }

  /**
   * Gets the player's total Aethel attributes.
   *
   * @return total Aethel attributes
   */
  @NotNull
  public Map<AethelAttribute, Double> getAethelAttributes() {
    return this.aethelAttributes;
  }

  /**
   * Gets the player's equipment Aethel attributes.
   *
   * @return equipment Aethel attributes
   */
  @NotNull
  public Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> getEquipmentAttributes() {
    return this.equipmentAttributes;
  }

  /**
   * Gets the player's jewelry slots,
   *
   * @return jewelry slots
   */
  @NotNull
  public ItemStack[] getJewelrySlots() {
    return this.jewelrySlots;
  }

  /**
   * Gets the health bar.
   *
   * @return player's health bar
   */
  @NotNull
  public BossBar getHealthBar() {
    return this.healthBar;
  }

  /**
   * Gets the current health.
   *
   * @return player's current health
   */
  public double getCurrentHealth() {
    return this.currentHealth;
  }

  /**
   * Gets the max health.
   *
   * @return player's max health
   */
  public double getMaxHealth() {
    return this.maxHealth;
  }


  /**
   * Sets the player's current health.
   *
   * @param currentHealth new current health value
   */
  private void setCurrentHealth(Double currentHealth) {
    this.currentHealth = currentHealth;
  }

  /**
   * Sets the player's max health.
   *
   * @param maxHealth new max health value
   */
  private void setMaxHealth(Double maxHealth) {
    this.maxHealth = maxHealth;
  }
}
