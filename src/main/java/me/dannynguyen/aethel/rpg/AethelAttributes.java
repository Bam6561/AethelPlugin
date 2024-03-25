package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an {@link RpgPlayer}'s total {@link AethelAttribute} values.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.17.9
 */
public class AethelAttributes {
  /**
   * Total {@link AethelAttribute} values.
   */
  private final Map<AethelAttribute, Double> attributes = createAethelAttributes();

  /**
   * {@link AethelAttribute} values on {@link RpgEquipmentSlot}.
   */
  private final Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> slotAttributes = new HashMap<>();

  /**
   * No parameter constructor.
   */
  public AethelAttributes() {
  }

  /**
   * Creates a blank map of {@link AethelAttribute} values.
   *
   * @return blank {@link AethelAttribute} values
   */
  private Map<AethelAttribute, Double> createAethelAttributes() {
    Map<AethelAttribute, Double> aethelAttributes = new HashMap<>();
    for (AethelAttribute attribute : AethelAttribute.values()) {
      aethelAttributes.put(attribute, 0.0);
    }
    return aethelAttributes;
  }

  /**
   * Checks if the item is in the correct {@link RpgEquipmentSlot}
   * before updating the player's {@link AethelAttribute} values.
   *
   * @param eSlot         {@link RpgEquipmentSlot}
   * @param dataContainer item's persistent tags
   */
  public void readAttributes(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer) {
    String[] attributes = dataContainer.get(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String attribute : attributes) {
      RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(attribute.substring(0, attribute.indexOf("."))));
      if (slot == eSlot) {
        addAttributes(eSlot, dataContainer, attribute);
      }
    }
  }

  /**
   * Adds new {@link Equipment} {@link AethelAttribute} modifiers.
   *
   * @param eSlot         {@link RpgEquipmentSlot}
   * @param dataContainer item's persistent tags
   * @param attribute     attribute modifier
   */
  private void addAttributes(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer, String attribute) {
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute);
    AethelAttribute attributeType = AethelAttribute.valueOf(TextFormatter.formatEnum(attribute.substring(attribute.indexOf(".") + 1)));
    slotAttributes.get(eSlot).put(attributeType, dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
    attributes.put(attributeType, attributes.get(attributeType) + dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
  }

  /**
   * Removes existing {@link Equipment} {@link AethelAttribute} modifiers at a {@link RpgEquipmentSlot}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   */
  public void removeAttributes(@NotNull RpgEquipmentSlot eSlot) {
    for (AethelAttribute attribute : slotAttributes.get(Objects.requireNonNull(eSlot, "Null slot")).keySet()) {
      attributes.put(attribute, attributes.get(attribute) - slotAttributes.get(eSlot).get(attribute));
      slotAttributes.get(eSlot).put(attribute, 0.0);
    }
  }

  /**
   * Gets total {@link AethelAttribute} values.
   *
   * @return total {@link AethelAttribute} values
   */
  @NotNull
  public Map<AethelAttribute, Double> getAttributes() {
    return this.attributes;
  }

  /**
   * Gets {@link Equipment} {@link AethelAttribute} values on {@link RpgEquipmentSlot}.
   *
   * @return {@link Equipment} {@link AethelAttribute} values on {@link RpgEquipmentSlot}
   */
  @NotNull
  public Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> getSlotAttributes() {
    return this.slotAttributes;
  }
}
