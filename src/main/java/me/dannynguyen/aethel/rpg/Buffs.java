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
 * @version 1.21.2
 * @since 1.20.9
 */
public class Buffs {
  /**
   * Temporary {@link Attribute} values.
   */
  private final Map<Attribute, Double> attributes = new HashMap<>();

  /**
   * Temporary {@link AethelAttribute} values.
   */
  private final Map<AethelAttribute, Double> aethelAttributes = new HashMap<>();

  /**
   * No parameter constructor.
   */
  public Buffs() {
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
