package me.dannynguyen.aethel.systems;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.object.RpgCharacter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * RpgData stores RPG characters in memory.
 *
 * @author Danny Nguyen
 * @version 1.9.1
 * @since 1.8.10
 */
public class RpgData {
  private Map<Player, RpgCharacter> rpgCharacters = new HashMap<>();
  private Map<Player, ItemStack> playerHeldItemMap = new HashMap<>();

  /**
   * Loads an RPG character into memory.
   *
   * @param player player
   */
  public void loadRpgCharacter(Player player) {
    Map<String, Map<String, Double>> equipment = new HashMap<>();
    Map<String, Double> aethelAttributes = createBlankAethelAttributes();

    loadEquipmentAttributes(player.getInventory(), equipment, aethelAttributes);
    rpgCharacters.put(player, new RpgCharacter(player, equipment, aethelAttributes));
  }

  /**
   * Loads the player's equipment-related Aethel attribute modifiers into memory.
   *
   * @param inv       player's inventory
   * @param equipment slot : attribute and value
   */
  private void loadEquipmentAttributes(PlayerInventory inv,
                                       Map<String, Map<String, Double>> equipment,
                                       Map<String, Double> aethelAttributes) {
    readEquipmentSlot(equipment, aethelAttributes, inv.getItemInMainHand(), Slot.HAND.slot);
    readEquipmentSlot(equipment, aethelAttributes, inv.getItemInOffHand(), Slot.OFF_HAND.slot);
    readEquipmentSlot(equipment, aethelAttributes, inv.getHelmet(), Slot.HEAD.slot);
    readEquipmentSlot(equipment, aethelAttributes, inv.getChestplate(), Slot.CHEST.slot);
    readEquipmentSlot(equipment, aethelAttributes, inv.getLeggings(), Slot.LEGS.slot);
    readEquipmentSlot(equipment, aethelAttributes, inv.getBoots(), Slot.FEET.slot);
  }

  /**
   * Checks if the item has Aethel attribute modifiers before
   * checking whether the item is in the correct equipment slot.
   *
   * @param equipment        slot : attribute and value
   * @param aethelAttributes attribute : total value
   * @param item             interacting item
   * @param slot             slot type
   */
  public void readEquipmentSlot(Map<String, Map<String, Double>> equipment,
                                Map<String, Double> aethelAttributes,
                                ItemStack item, String slot) {
    if (item != null && item.getType() != Material.AIR) {

      if (equipment.containsKey(slot)) {
        removeExistingEquipmentAttributes(equipment, aethelAttributes, slot);
      }

      PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
      NamespacedKey listKey = PluginNamespacedKey.AETHEL_ATTRIBUTE_LIST.namespacedKey;

      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        equipment.put(slot, new HashMap<>());
        readEquipmentMeta(equipment, aethelAttributes, slot, dataContainer, listKey);
      }
    } else {
      if (equipment.containsKey(slot)) {
        removeExistingEquipmentAttributes(equipment, aethelAttributes, slot);
      }
    }
  }

  /**
   * Checks if the item is in the correct equipment slot before updating the player's attribute values.
   *
   * @param equipment        slot : attribute and value
   * @param aethelAttributes attribute : total value
   * @param slot             slot type
   * @param dataContainer    item's persistent tags
   * @param listKey          attribute list key
   */
  private void readEquipmentMeta(Map<String, Map<String, Double>> equipment,
                                 Map<String, Double> aethelAttributes,
                                 String slot,
                                 PersistentDataContainer dataContainer, NamespacedKey listKey) {
    String[] attributes = dataContainer.get(listKey, PersistentDataType.STRING).split(" ");
    for (String attribute : attributes) {

      String attributeSlot = attribute.substring(attribute.indexOf(".") + 1);
      if (attributeSlot.equals(slot)) {
        addNewEquipmentAttributes(equipment, aethelAttributes, slot, dataContainer, attribute);
      }
    }
  }

  /**
   * Creates a blank set of Aethel attributes.
   *
   * @return blank set of Aethel attributes
   */
  private Map<String, Double> createBlankAethelAttributes() {
    Map<String, Double> aethelAttributes = new HashMap<>();
    for (AethelAttributeId attribute : AethelAttributeId.values()) {
      aethelAttributes.put(attribute.id, 0.0);
    }
    return aethelAttributes;
  }

  /**
   * Adds new equipment attribute modifiers.
   *
   * @param equipment        slot : attribute and value
   * @param aethelAttributes attribute : total value
   * @param slot             slot type
   * @param dataContainer    item's persistent tags
   * @param attribute        attribute modifier
   */
  private void addNewEquipmentAttributes(Map<String, Map<String, Double>> equipment,
                                         Map<String, Double> aethelAttributes,
                                         String slot,
                                         PersistentDataContainer dataContainer,
                                         String attribute) {
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + attribute);
    String attributeType = attribute.substring(0, attribute.indexOf("."));

    equipment.get(slot).put(attributeType, dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
    aethelAttributes.put(attributeType,
        aethelAttributes.get(attributeType) + dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
  }

  /**
   * Removes existing equipment attribute modifiers.
   *
   * @param equipment        slot : attribute and value
   * @param aethelAttributes attribute : total value
   * @param slot             slot type
   */
  public void removeExistingEquipmentAttributes(Map<String, Map<String, Double>> equipment,
                                                Map<String, Double> aethelAttributes,
                                                String slot) {
    for (String attribute : equipment.get(slot).keySet()) {
      aethelAttributes.put(attribute, aethelAttributes.get(attribute) - equipment.get(slot).get(attribute));
    }
    equipment.remove(slot);
  }

  public Map<Player, RpgCharacter> getRpgCharacters() {
    return this.rpgCharacters;
  }

  public Map<Player, ItemStack> getPlayerHeldItemMap() {
    return this.playerHeldItemMap;
  }

  private enum Slot {
    HAND("hand"),
    OFF_HAND("off_hand"),
    HEAD("head"),
    CHEST("chest"),
    LEGS("legs"),
    FEET("feet");

    public final String slot;

    Slot(String slot) {
      this.slot = slot;
    }
  }

  private enum AethelAttributeId {
    CRITICAL_CHANCE("critical_chance"),
    CRITICAL_DAMAGE("critical_damage"),
    BLOCK("block"),
    PARRY_CHANCE("parry_chance"),
    PARRY_DEFLECT("parry_deflect"),
    DODGE_CHANCE("dodge_chance"),
    ABILITY_DAMAGE("ability_damage"),
    ABILITY_COOLDOWN("ability_cooldown"),
    APPLY_STATUS("apply_status");

    public final String id;

    AethelAttributeId(String attribute) {
      this.id = attribute;
    }
  }
}
