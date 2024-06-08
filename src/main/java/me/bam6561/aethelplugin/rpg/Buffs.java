package me.bam6561.aethelplugin.rpg;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.rpg.AethelAttribute;
import me.bam6561.aethelplugin.utils.entity.HealthChange;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an entity's temporary attribute stat changes.
 *
 * @author Danny Nguyen
 * @version 1.22.20
 * @since 1.20.9
 */
public class Buffs {
  /**
   * Buffs owner.
   */
  private final UUID uuid;

  /**
   * Attribute buff instances represented by their timer ID.
   * <p>
   * Aethel attribute buff timers are not necessary to be
   * tracked since they do not modify the entity's original
   * stat values directly and can be removed freely without issues.
   */
  private final Set<Integer> attributeBuffs = new HashSet<>();

  /**
   * Temporary {@link Attribute} values.
   */
  private final Map<Attribute, Double> attributes = new HashMap<>();

  /**
   * Temporary {@link AethelAttribute} values.
   */
  private final Map<AethelAttribute, Double> aethelAttributes = new HashMap<>();

  /**
   * Associates buffs with its owner.
   *
   * @param uuid owner's UUID
   */
  public Buffs(@NotNull UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid, "Null UUID");
  }

  /**
   * Adds an attribute stat buff.
   *
   * @param attribute interacting attribute
   * @param value     value
   * @param duration  duration in ticks
   */
  public void addAttribute(@NotNull Attribute attribute, double value, int duration) {
    LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
    AttributeInstance entityAttribute = entity.getAttribute(Objects.requireNonNull(attribute, "Null attribute"));
    if (entityAttribute == null) {
      return;
    }
    if (attribute == Attribute.GENERIC_ATTACK_SPEED && entityAttribute.getBaseValue() + value <= 0) {
      return;
    }

    entityAttribute.setBaseValue(entityAttribute.getBaseValue() + value);
    attributes.put(attribute, attributes.getOrDefault(attribute, 0.0) + value);

    int taskID;
    if (attribute == Attribute.GENERIC_MAX_HEALTH) {
      new HealthChange(entity).updateDisplays();
      taskID = Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        entityAttribute.setBaseValue(entityAttribute.getBaseValue() - value);
        attributes.put(attribute, attributes.get(attribute) - value);
        new HealthChange(entity).updateDisplays();
      }, duration).getTaskId();
    } else {
      taskID = Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        entityAttribute.setBaseValue(entityAttribute.getBaseValue() - value);
        attributes.put(attribute, attributes.get(attribute) - value);
      }, duration).getTaskId();
    }
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> removeAttributeIfEmpty(taskID, attribute), duration);
    attributeBuffs.add(taskID);
  }

  /**
   * Adds an {@link AethelAttribute} stat buff.
   *
   * @param aethelAttribute {@link AethelAttribute}
   * @param value           value
   * @param duration        duration in ticks
   */
  public void addAethelAttribute(@NotNull AethelAttribute aethelAttribute, double value, int duration) {
    aethelAttributes.put(aethelAttribute, aethelAttributes.getOrDefault(aethelAttribute, 0.0) + value);

    if (aethelAttribute == AethelAttribute.MAX_HEALTH) {
      LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
      new HealthChange(entity).updateDisplays();
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        aethelAttributes.put(aethelAttribute, aethelAttributes.get(aethelAttribute) - value);
        new HealthChange(entity).updateDisplays();
      }, duration);
    } else {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> aethelAttributes.put(aethelAttribute, aethelAttributes.get(aethelAttribute) - value), duration);
    }

    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> removeAethelAttributeIfEmpty(aethelAttribute), duration);
  }

  /**
   * Removes the attribute after its timer ends.
   *
   * @param taskID    task ID to be removed
   * @param attribute attribute to be removed
   */
  private void removeAttributeIfEmpty(int taskID, Attribute attribute) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      attributeBuffs.remove(taskID);
      if (attributes.get(attribute) == 0.0) {
        attributes.remove(attribute);
        if (attributes.isEmpty() && aethelAttributes.isEmpty()) {
          Plugin.getData().getRpgSystem().getBuffs().remove(uuid);
        }
      }
    }, 1);
  }

  /**
   * Removes the {@link AethelAttribute} after its timer ends.
   *
   * @param aethelAttribute {@link AethelAttribute} to be removed
   */
  private void removeAethelAttributeIfEmpty(AethelAttribute aethelAttribute) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      if (aethelAttributes.get(aethelAttribute) == 0.0) {
        aethelAttributes.remove(aethelAttribute);
        if (aethelAttributes.isEmpty() && attributes.isEmpty()) {
          Plugin.getData().getRpgSystem().getBuffs().remove(uuid);
        }
      }
    }, 1);
  }

  /**
   * Gets the attribute buff value.
   *
   * @param attribute attribute
   * @return attribute buff value
   */
  public double getAttribute(@NotNull Attribute attribute) {
    return attributes.getOrDefault(Objects.requireNonNull(attribute), 0.0);
  }

  /**
   * Gets the {@link AethelAttribute} buff value.
   *
   * @param aethelAttribute {@link AethelAttribute}
   * @return {@link AethelAttribute} buff value
   */
  public double getAethelAttribute(@NotNull AethelAttribute aethelAttribute) {
    return aethelAttributes.getOrDefault(Objects.requireNonNull(aethelAttribute), 0.0);
  }

  /**
   * Gets buffed attributes.
   *
   * @return buffed attributes
   */
  @NotNull
  public Set<Attribute> getAttributeBuffs() {
    return attributes.keySet();
  }

  /**
   * Gets buffed {@link AethelAttribute Aethel attributes}.
   *
   * @return buffed {@link AethelAttribute Aethel attributes}
   */
  @NotNull
  public Set<AethelAttribute> getAethelAttributeBuffs() {
    return aethelAttributes.keySet();
  }

  /**
   * Safely removes all buffs from the entity.
   */
  public void removeAllBuffs() {
    for (int taskID : attributeBuffs) {
      Bukkit.getScheduler().cancelTask(taskID);
    }
    LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
    for (Attribute attribute : attributes.keySet()) {
      AttributeInstance attributeInstance = entity.getAttribute(attribute);
      attributeInstance.setBaseValue(attributeInstance.getBaseValue() - attributes.get(attribute));
    }
    Plugin.getData().getRpgSystem().getBuffs().remove(uuid);
  }
}
