package me.dannynguyen.aethel.rpg.system;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.KeyHeader;
import me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.rpg.enums.AethelAttributeType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an RPG player's Aethel attributes.
 *
 * @author Danny Nguyen
 * @version 1.17.9
 * @since 1.17.9
 */
public class AethelAttributes {
  /**
   * Total Aethel attributes.
   */
  private final Map<AethelAttributeType, Double> attributes = createBlankAethelAttributes();

  /**
   * Aethel attributes by slot.
   */
  private final Map<RpgEquipmentSlot, Map<AethelAttributeType, Double>> slotAttributes = new HashMap<>();

  /**
   * Associates Aethel attributes together.
   */
  public AethelAttributes() {
  }

  /**
   * Creates a blank map of Aethel attributes.
   *
   * @return blank Aethel attributes
   */
  private Map<AethelAttributeType, Double> createBlankAethelAttributes() {
    Map<AethelAttributeType, Double> aethelAttributes = new HashMap<>();
    for (AethelAttributeType attribute : AethelAttributeType.values()) {
      aethelAttributes.put(attribute, 0.0);
    }
    return aethelAttributes;
  }

  /**
   * Checks if the item is in the correct equipment slot before updating the player's attribute values.
   *
   * @param eSlot         slot type
   * @param dataContainer item's persistent tags
   */
  public void readAttributes(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer) {
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
    AethelAttributeType attributeType = AethelAttributeType.valueOf(attribute.substring(attribute.indexOf(".") + 1).toUpperCase());
    slotAttributes.get(eSlot).put(attributeType, dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
    attributes.put(attributeType, attributes.get(attributeType) + dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
  }

  /**
   * Removes existing equipment attribute modifiers at an equipment slot.
   *
   * @param eSlot equipment slot
   */
  public void removeAttributes(@NotNull RpgEquipmentSlot eSlot) {
    for (AethelAttributeType attribute : slotAttributes.get(Objects.requireNonNull(eSlot, "Null slot")).keySet()) {
      attributes.put(attribute, attributes.get(attribute) - slotAttributes.get(eSlot).get(attribute));
      slotAttributes.get(eSlot).put(attribute, 0.0);
    }
  }

  /**
   * Gets total Aethel attributes.
   *
   * @return total Aethel attributes
   */
  @NotNull
  public Map<AethelAttributeType, Double> getAttributes() {
    return this.attributes;
  }

  /**
   * Gets Aethel attributes by slot.
   *
   * @return Aethel attributes by slot
   */
  @NotNull
  public Map<RpgEquipmentSlot, Map<AethelAttributeType, Double>> getSlotAttributes() {
    return this.slotAttributes;
  }
}
