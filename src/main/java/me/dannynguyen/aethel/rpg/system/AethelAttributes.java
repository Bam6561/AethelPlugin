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
 * Represents an {@link RpgPlayer} {@link AethelAttributeType}.
 *
 * @author Danny Nguyen
 * @version 1.17.9
 * @since 1.17.9
 */
public class AethelAttributes {
  /**
   * Total {@link AethelAttributeType Aethel attributes}.
   */
  private final Map<AethelAttributeType, Double> attributes = createBlankAethelAttributes();

  /**
   * {@link AethelAttributeType Aethel attributes} by {@link RpgEquipmentSlot slot}.
   */
  private final Map<RpgEquipmentSlot, Map<AethelAttributeType, Double>> slotAttributes = new HashMap<>();

  /**
   * No parameter constructor.
   */
  public AethelAttributes() {
  }

  /**
   * Creates a blank map of {@link AethelAttributeType Aethel attributes}.
   *
   * @return blank {@link AethelAttributeType Aethel attributes}
   */
  private Map<AethelAttributeType, Double> createBlankAethelAttributes() {
    Map<AethelAttributeType, Double> aethelAttributes = new HashMap<>();
    for (AethelAttributeType attribute : AethelAttributeType.values()) {
      aethelAttributes.put(attribute, 0.0);
    }
    return aethelAttributes;
  }

  /**
   * Checks if the item is in the correct {@link RpgEquipmentSlot slot}
   * before updating the player's {@link AethelAttributeType attributes} values.
   *
   * @param eSlot         {@link RpgEquipmentSlot equipment slot}
   * @param dataContainer item's persistent tags
   */
  public void readAttributes(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer) {
    String[] attributes = dataContainer.get(PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String attribute : attributes) {
      RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(attribute.substring(0, attribute.indexOf(".")).toUpperCase());
      if (slot == eSlot) {
        addAttributes(eSlot, dataContainer, attribute);
      }
    }
  }

  /**
   * Adds new equipment {@link AethelAttributeType attribute} modifiers.
   *
   * @param eSlot         {@link RpgEquipmentSlot equipment slot}
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
   * Removes existing equipment {@link AethelAttributeType attribute} modifiers at a {@link RpgEquipmentSlot slot}.
   *
   * @param eSlot {@link RpgEquipmentSlot equipment slot}
   */
  public void removeAttributes(@NotNull RpgEquipmentSlot eSlot) {
    for (AethelAttributeType attribute : slotAttributes.get(Objects.requireNonNull(eSlot, "Null slot")).keySet()) {
      attributes.put(attribute, attributes.get(attribute) - slotAttributes.get(eSlot).get(attribute));
      slotAttributes.get(eSlot).put(attribute, 0.0);
    }
  }

  /**
   * Gets total {@link AethelAttributeType Aethel attributes}.
   *
   * @return total {@link AethelAttributeType Aethel attributes}
   */
  @NotNull
  public Map<AethelAttributeType, Double> getAttributes() {
    return this.attributes;
  }

  /**
   * Gets {@link AethelAttributeType Aethel attributes} by {@link RpgEquipmentSlot slot}.
   *
   * @return {@link AethelAttributeType Aethel attributes} by {@link RpgEquipmentSlot slot}
   */
  @NotNull
  public Map<RpgEquipmentSlot, Map<AethelAttributeType, Double>> getSlotAttributes() {
    return this.slotAttributes;
  }
}
