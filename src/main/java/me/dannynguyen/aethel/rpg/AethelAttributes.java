package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.Bukkit;
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
 * @version 1.22.7
 * @since 1.17.9
 */
public class AethelAttributes {
  /**
   * Player's {@link AethelAttribute} values.
   */
  private final PersistentDataContainer playerContainer;

  /**
   * {@link AethelAttribute} values on {@link RpgEquipmentSlot}.
   */
  private final Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> slotAttributes = new HashMap<>();

  /**
   * Associates the player's persistent tags with Aethel attributes.
   *
   * @param playerContainer player's persistent tags
   */
  public AethelAttributes(@NotNull PersistentDataContainer playerContainer) {
    this.playerContainer = playerContainer;
  }

  /**
   * Checks if the item is in the correct {@link RpgEquipmentSlot}
   * before updating the player's {@link AethelAttribute} values.
   *
   * @param eSlot         {@link RpgEquipmentSlot}
   * @param dataContainer item's persistent tags
   */
  public void readAttributes(@NotNull RpgEquipmentSlot eSlot, @NotNull PersistentDataContainer dataContainer) {
    String[] attributes = Objects.requireNonNull(dataContainer, "Null data container").get(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String attribute : attributes) {
      RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(attribute.substring(0, attribute.indexOf("."))));
      if (slot == Objects.requireNonNull(eSlot, "Null slot")) {
        addAttributes(eSlot, dataContainer, attribute);
      }
    }
  }

  /**
   * Adds new {@link Equipment} {@link AethelAttribute} modifiers.
   *
   * @param eSlot         {@link RpgEquipmentSlot}
   * @param itemContainer item's persistent tags
   * @param attribute     attribute modifier
   */
  private void addAttributes(RpgEquipmentSlot eSlot, PersistentDataContainer itemContainer, String attribute) {
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute);
    AethelAttribute attributeType = AethelAttribute.valueOf(TextFormatter.formatEnum(attribute.substring(attribute.indexOf(".") + 1)));
    slotAttributes.get(eSlot).put(attributeType, itemContainer.get(attributeKey, PersistentDataType.DOUBLE));

    NamespacedKey setAttributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attributeType.getId());
    double attributeValue = playerContainer.getOrDefault(setAttributeKey, PersistentDataType.DOUBLE, 0.0);
    playerContainer.set(setAttributeKey, PersistentDataType.DOUBLE, attributeValue + itemContainer.get(attributeKey, PersistentDataType.DOUBLE));
  }

  /**
   * Removes existing {@link Equipment} {@link AethelAttribute} modifiers at a {@link RpgEquipmentSlot}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   */
  public void removeAttributes(@NotNull RpgEquipmentSlot eSlot) {
    for (AethelAttribute attribute : slotAttributes.get(Objects.requireNonNull(eSlot, "Null slot")).keySet()) {
      NamespacedKey setAttributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute.getId());
      double attributeValue = playerContainer.getOrDefault(setAttributeKey, PersistentDataType.DOUBLE, 0.0);
      playerContainer.set(setAttributeKey, PersistentDataType.DOUBLE, attributeValue - slotAttributes.get(eSlot).get(attribute));
      slotAttributes.get(eSlot).put(attribute, 0.0);
    }
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
