package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents entity damage mitigation.
 *
 * @author Danny Nguyen
 * @version 1.22.20
 * @since 1.16.14
 */
public class DamageMitigation {
  /**
   * Defending entity.
   */
  private final LivingEntity defender;

  /**
   * Entity's UUID.
   */
  private final UUID uuid;

  /**
   * Entity's persistent tags.
   */
  private final PersistentDataContainer entityTags;

  /**
   * Entity's {@link Status statuses}.
   */
  private final Map<StatusType, Status> statuses;

  /**
   * Associates the damage mitigation with an entity.
   *
   * @param defender defending entity
   */
  public DamageMitigation(@NotNull LivingEntity defender) {
    this.defender = Objects.requireNonNull(defender, "Null damagee");
    this.uuid = defender.getUniqueId();
    this.entityTags = defender.getPersistentDataContainer();
    this.statuses = Plugin.getData().getRpgSystem().getStatuses().get(uuid);
  }

  /**
   * Mitigates fall damage taken based on the entity's fall protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateFall(double damage) {
    int featherFallingBase = entityTags.getOrDefault(Key.ENCHANTMENT_FEATHER_FALLING.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    return damage - (damage * (featherFallingBase * .2));
  }

  /**
   * Mitigates fire damage based taken on the entity's fire protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateFire(double damage) {
    int fireProtectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_FIRE_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    return damage - (damage * (fireProtectionBase * .1));
  }

  /**
   * Mitigates explosion damage taken based on the entity's explosion protection levels.
   *
   * @param damage damage done
   * @return damage taken
   */
  public double mitigateExplosion(double damage) {
    int blastProtectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_BLAST_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    final double finalDamage = damage - (damage * (blastProtectionBase * .1));
    if (finalDamage <= 0) {
      new HealthChange(defender).heal(damage * .2);
      if (defender instanceof Player player) {
        player.setFoodLevel(20);
      }
    }
    return finalDamage;
  }

  /**
   * Mitigates projectile damage based on the entity's projectile protection levels.
   *
   * @param damage damage done
   * @return damage taken
   */
  public double mitigateProjectile(double damage) {
    int projectileProtectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_PROJECTILE_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    return damage - (damage * (Math.min(projectileProtectionBase * .05, .5)));
  }

  /**
   * Mitigates damage taken based on the entity's resistance amplifier.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateResistance(double damage) {
    if (defender.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      int resistance = defender.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
      damage = damage - (damage * (resistance * 0.05));
    }
    return damage;
  }

  /**
   * Mitigates physical damage taken based on the entity's armor and protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateArmorProtection(double damage) {
    int armor = (int) defender.getAttribute(Attribute.GENERIC_ARMOR).getValue();
    if (statuses != null && statuses.containsKey(StatusType.FRACTURE)) {
      armor = armor - statuses.get(StatusType.FRACTURE).getStackAmount();
    }
    int protectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    double mitigationValue = Math.min(armor * 0.02, .4) + Math.min(protectionBase * 0.01, .2);
    return damage - (damage * mitigationValue);
  }

  /**
   * Mitigates physical damage taken based on the entity's
   * armor, protection levels, and resistance amplifier.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateArmorProtectionResistance(double damage) {
    int armor = (int) defender.getAttribute(Attribute.GENERIC_ARMOR).getValue();
    if (statuses != null && statuses.containsKey(StatusType.FRACTURE)) {
      armor = armor - statuses.get(StatusType.FRACTURE).getStackAmount();
    }
    int protectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    double mitigationValue = Math.min(armor * 0.02, .4) + Math.min(protectionBase * 0.01, .2);
    damage = damage - (damage * mitigationValue);
    if (defender.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      int resistance = defender.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
      damage = damage - (damage * (resistance * 0.05));
    }
    return damage;
  }

  /**
   * Mitigates magical damage taken on the entity's protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateProtection(double damage) {
    int protectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    return damage - (damage * (Math.min(protectionBase * .04, .8)));
  }

  /**
   * Mitigates magical damage taken based on the entity's
   * protection levels and resistance amplifier.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateProtectionResistance(double damage) {
    int protectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    damage = damage - (damage * (Math.min(protectionBase * .04, .8)));
    if (defender.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      int resistance = defender.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
      damage = damage - (damage * (resistance * 0.05));
    }
    return damage;
  }
}
