package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an {@link RpgPlayer}'s temporary stat changes.
 *
 * @author Danny Nguyen
 * @version 1.20.9
 * @since 1.20.9
 */
public class Buffs {
  /**
   * Buffs owner.
   */
  private final UUID uuid;

  /**
   * Temporary {@link Attribute} values.
   */
  private final Map<Attribute, Double> attributes = createAttributes();

  /**
   * Temporary {@link AethelAttribute} values.
   */
  private final Map<AethelAttribute, Double> aethelAttributes = createAethelAttributes();

  /**
   * Associates buffs with its owning player.
   */
  public Buffs(UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid, "Null UUID");
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
   * Removes attribute buffs.
   */
  public void removeAttributeBuffs() {
    Player player = Bukkit.getPlayer(uuid);
    for (Attribute attribute : attributes.keySet()) {
      AttributeInstance playerAttribute = player.getAttribute(attribute);
      playerAttribute.setBaseValue(playerAttribute.getBaseValue() - attributes.get(attribute));
    }
    attributes.replaceAll((a, v) -> 0.0);
  }

  /**
   * Removes {@link AethelAttribute} buffs.
   */
  public void removeAethelAttributeBuffs() {
    aethelAttributes.replaceAll((a, v) -> 0.0);
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
