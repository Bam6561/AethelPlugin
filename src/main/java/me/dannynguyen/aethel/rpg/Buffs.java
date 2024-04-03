package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an {@link RpgPlayer}'s temporary attribute stat changes.
 *
 * @author Danny Nguyen
 * @version 1.20.10
 * @since 1.20.9
 */
public class Buffs {
  /**
   * Temporary {@link Attribute} values.
   */
  private final Map<Attribute, Double> attributes = createAttributes();

  /**
   * Temporary {@link AethelAttribute} values.
   */
  private final Map<AethelAttribute, Double> aethelAttributes = createAethelAttributes();

  /**
   * No parameter constructor.
   */
  public Buffs() {
  }

  /**
   * Creates a blank map of Minecraft attribute values.
   *
   * @return blank Minecraft attribute values
   */
  private Map<Attribute, Double> createAttributes() {
    Map<Attribute, Double> attributes = new HashMap<>();
    for (Attribute attribute : Attribute.values()) {
      attributes.put(attribute, 0.0);
    }
    return attributes;
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
   * Gets attribute buffs.
   *
   * @return attribute buffs
   */
  @NotNull
  public Map<Attribute, Double> getAttributes() {
    return this.attributes;
  }

  /**
   * Gets {@link AethelAttribute} buffs.
   *
   * @return {@link AethelAttribute} buffs
   */
  @NotNull
  public Map<AethelAttribute, Double> getAethelAttributes() {
    return this.aethelAttributes;
  }
}
