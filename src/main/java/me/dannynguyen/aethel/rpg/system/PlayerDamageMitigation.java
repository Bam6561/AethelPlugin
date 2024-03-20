package me.dannynguyen.aethel.rpg.system;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.rpg.enums.StatusType;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents player damage mitigation.
 *
 * @author Danny Nguyen
 * @version 1.17.2
 * @since 1.16.14
 */
public class PlayerDamageMitigation {

  /**
   * Player taking damage.
   */
  private final Player damagee;

  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * Player's enchantments.
   */
  private final Map<Enchantment, Integer> enchantments;

  /**
   * Player's statuses.
   */
  private final Map<StatusType, Status> statuses;

  /**
   * Associates the damage calculator with a player.
   *
   * @param damagee damagee
   */
  public PlayerDamageMitigation(@NotNull Player damagee) {
    this.damagee = Objects.requireNonNull(damagee, "Null damagee");
    this.uuid = damagee.getUniqueId();
    RpgSystem rpgSystem = Plugin.getData().getRpgSystem();
    this.enchantments = rpgSystem.getRpgPlayers().get(uuid).getEquipment().getTotalEnchantments();
    this.statuses = rpgSystem.getStatuses().get(uuid);
  }

  /**
   * Mitigates fall damage taken based on the player's fall protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateFall(double damage) {
    return damage - (damage * (enchantments.get(Enchantment.PROTECTION_FALL) * .2));
  }

  /**
   * Mitigates fire damage based taken on the player's fire protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateFire(double damage) {
    return damage - (damage * (enchantments.get(Enchantment.PROTECTION_FIRE) * .1));
  }

  /**
   * Mitigates explosion damage taken based on the player's explosion protection levels.
   *
   * @param damage damage done
   * @return damage taken
   */
  public double mitigateExplosion(double damage) {
    damage = damage - (damage * (enchantments.get(Enchantment.PROTECTION_EXPLOSIONS) * .1));
    if (damage <= 0) {
      Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getHealth().heal(damage * .2);
      damagee.setFoodLevel(20);
    }
    return damage;
  }

  /**
   * Mitigates projectile damage based on the player's projectile protection levels.
   *
   * @param damage damage done
   * @return damage taken
   */
  public double mitigateProjectile(double damage) {
    return damage - (damage * (Math.min(enchantments.get(Enchantment.PROTECTION_PROJECTILE) * .05, .5)));
  }

  /**
   * Mitigates damage taken based on the player's resistance amplifier.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateResistance(double damage) {
    if (damagee.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      int resistance = damagee.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
      damage = damage - (damage * (resistance * 0.05));
    }
    return damage;
  }

  /**
   * Mitigates physical damage taken based on the player's armor and protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateArmorProtection(double damage) {
    int armor = (int) damagee.getAttribute(Attribute.GENERIC_ARMOR).getValue();
    int protection = enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL);
    if (statuses != null && statuses.containsKey(StatusType.FRACTURE)) {
      armor = armor - statuses.get(StatusType.FRACTURE).getStackAmount();
    }
    double mitigationValue = Math.min(armor * 0.02, .4) + Math.min(protection * 0.01, .2);
    return damage - (damage * mitigationValue);
  }

  /**
   * Mitigates physical damage taken based on the player's
   * armor, protection levels, and resistance amplifier.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateArmorProtectionResistance(double damage) {
    int armor = (int) damagee.getAttribute(Attribute.GENERIC_ARMOR).getValue();
    if (statuses != null && statuses.containsKey(StatusType.FRACTURE)) {
      armor = armor - statuses.get(StatusType.FRACTURE).getStackAmount();
    }
    double mitigationValue = Math.min(armor * 0.02, .4) + Math.min(enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL) * 0.01, .2);
    damage = damage - (damage * mitigationValue);
    if (damagee.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      int resistance = damagee.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
      damage = damage - (damage * (resistance * 0.05));
    }
    return damage;
  }

  /**
   * Mitigates magical damage taken on the player's protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateProtection(double damage) {
    return damage - (damage * (Math.min(enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL) * .04, .8)));
  }

  /**
   * Mitigates magical damage taken based on the player's
   * protection levels and resistance amplifier.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateProtectionResistance(double damage) {
    int protection = enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL);
    damage = damage - (damage * (Math.min(protection * .04, .8)));
    if (damagee.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      int resistance = damagee.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
      damage = damage - (damage * (resistance * 0.05));
    }
    return damage;
  }
}
