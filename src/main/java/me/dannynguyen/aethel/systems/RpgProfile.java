package me.dannynguyen.aethel.systems;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginEnum;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a player's RPG metadata.
 *
 * @author Danny Nguyen
 * @version 1.11.1
 * @since 1.8.9
 */
public class RpgProfile {
  /**
   * Aethel attribute IDs.
   */
  private final static String[] aethelAttributeId = new String[]{
      "critical_chance", "critical_damage", "block", "parry_chance", "parry_deflect",
      "dodge_chance", "ability_damage", "ability_cooldown", "apply_status"
  };

  /**
   * RPG profile owner.
   */
  private final Player player;

  /**
   * Player health bar.
   */
  private final BossBar healthBar;

  /**
   * Player max health;
   */
  private double maxHealth;

  /**
   * Player health.
   */
  private double currentHealth;

  /**
   * Total Aethel attributes.
   */
  private final Map<String, Double> aethelAttributes;

  /**
   * Equipment Aethel attributes.
   */
  private final Map<String, Map<String, Double>> equipmentAttributes;

  /**
   * Jewelry slots.
   */
  private final ItemStack[] jewelrySlots;

  /**
   * Associates a player with a new RPG profile.
   *
   * @param player player
   */
  public RpgProfile(@NotNull Player player) {
    this.player = Objects.requireNonNull(player, "Null player");
    this.healthBar = Bukkit.createBossBar("Health", BarColor.RED, BarStyle.SEGMENTED_10);
    this.maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    this.currentHealth = player.getHealth();
    this.aethelAttributes = createBlankAethelAttributes();
    this.equipmentAttributes = new HashMap<>();
    this.jewelrySlots = new ItemStack[2];
  }

  /**
   * Creates a blank set of Aethel attributes.
   *
   * @return blank set of Aethel attributes
   */
  private Map<String, Double> createBlankAethelAttributes() {
    Map<String, Double> aethelAttributes = new HashMap<>();
    for (String attributeId : aethelAttributeId) {
      aethelAttributes.put(attributeId, 0.0);
    }
    return aethelAttributes;
  }

  /**
   * Loads the player's equipment-related Aethel attribute modifiers.
   */
  public void loadEquipmentAttributes() {
    PlayerInventory pInv = player.getInventory();
    readEquipmentSlot(pInv.getItemInMainHand(), "hand");
    readEquipmentSlot(pInv.getItemInOffHand(), "off_hand");
    readEquipmentSlot(pInv.getHelmet(), "head");
    readEquipmentSlot(pInv.getChestplate(), "chest");
    readEquipmentSlot(pInv.getLeggings(), "legs");
    readEquipmentSlot(pInv.getBoots(), "feet");
  }

  /**
   * Loads the player's health bar.
   */
  public void loadHealthBar() {
    updateHealthBar();
    healthBar.addPlayer(player);
  }

  /**
   * Checks if the item has Aethel attribute modifiers before
   * checking whether the item is in the correct equipment slot.
   *
   * @param item interacting item
   * @param slot slot type
   */
  public void readEquipmentSlot(ItemStack item, String slot) {
    if (ItemReader.isNotNullOrAir(item)) {
      if (equipmentAttributes.containsKey(slot)) {
        removeEquipmentAttributes(slot);
      }
      PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
      NamespacedKey listKey = PluginEnum.Key.ATTRIBUTE_LIST.getNamespacedKey();
      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        equipmentAttributes.put(slot, new HashMap<>());
        readEquipmentMeta(slot, dataContainer, listKey);
      }
    } else {
      if (equipmentAttributes.containsKey(slot)) {
        removeEquipmentAttributes(slot);
      }
    }
  }

  /**
   * Updates the player's health bar based on their max health attribute.
   */
  public void updateHealthBar() {
    currentHealth = currentHealth + player.getAbsorptionAmount();
    player.setAbsorptionAmount(0);
    maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    processHealthBarProgress();
  }

  /**
   * Damages the player by an amount.
   *
   * @param damage damage amount
   */
  public void damageHealthBar(double damage) {
    currentHealth = currentHealth - damage;
    if (currentHealth > 0) {
      processHealthBarProgress();
    } else {
      DecimalFormat dc = new DecimalFormat();
      dc.setMaximumFractionDigits(2);
      currentHealth = 0;
      player.setHealth(currentHealth);
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
      double healthTotal = currentHealth + heal;
      if (maxHealth >= healthTotal) {
        currentHealth = healthTotal;
      } else {
        currentHealth = maxHealth;
      }
      processHealthBarProgress();
    }
  }

  /**
   * Resets the player's health bar.
   */
  public void resetHealthBar() {
    currentHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    processHealthBarProgress();
  }

  /**
   * Checks if the item is in the correct equipment slot before updating the player's attribute values.
   *
   * @param slot          slot type
   * @param dataContainer item's persistent tags
   * @param listKey       attributes list
   */
  private void readEquipmentMeta(String slot, PersistentDataContainer dataContainer, NamespacedKey listKey) {
    String[] attributes = dataContainer.get(listKey, PersistentDataType.STRING).split(" ");
    for (String attribute : attributes) {
      String attributeSlot = attribute.substring(attribute.indexOf(".") + 1);
      if (attributeSlot.equals(slot)) {
        addNewEquipmentAttributes(slot, dataContainer, attribute);
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
  private void addNewEquipmentAttributes(String slot, PersistentDataContainer dataContainer, String attribute) {
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + attribute);
    String attributeType = attribute.substring(0, attribute.indexOf("."));
    equipmentAttributes.get(slot).put(attributeType, dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
    aethelAttributes.put(attributeType, aethelAttributes.get(attributeType) + dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
  }

  /**
   * Removes existing equipment attribute modifiers.
   *
   * @param slot slot type
   */
  public void removeEquipmentAttributes(String slot) {
    for (String attribute : equipmentAttributes.get(slot).keySet()) {
      aethelAttributes.put(attribute, aethelAttributes.get(attribute) - equipmentAttributes.get(slot).get(attribute));
    }
    equipmentAttributes.remove(slot);
  }

  /**
   * Sets the progress of the health bar based on the player's health : max health.
   */
  private void processHealthBarProgress() {
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
   * Gets the player the RPG profile belongs to.
   *
   * @return RPG profile owner
   */
  @NotNull
  public Player getPlayer() {
    return this.player;
  }

  /**
   * Gets the player's health bar.
   *
   * @return player's health bar
   */
  public BossBar getHealthBar() {
    return this.healthBar;
  }

  /**
   * Gets the player's max health.
   *
   * @return player's max health
   */
  public double getMaxHealth() {
    return this.maxHealth;
  }

  /**
   * Gets the player's health.
   *
   * @return player's health
   */
  public double getCurrentHealth() {
    return this.currentHealth;
  }

  /**
   * Gets the profile's equipment Aethel attributes.
   *
   * @return equipment Aethel attributes
   */
  @NotNull
  public Map<String, Map<String, Double>> getEquipmentAttributes() {
    return this.equipmentAttributes;
  }

  /**
   * Gets the profile's total Aethel attributes.
   *
   * @return total Aethel attributes
   */
  @NotNull
  public Map<String, Double> getAethelAttributes() {
    return this.aethelAttributes;
  }

  /**
   * Gets the profile's jewelry slots
   *
   * @return jewelry slots
   */
  @NotNull
  public ItemStack[] getJewelrySlots() {
    return this.jewelrySlots;
  }
}
