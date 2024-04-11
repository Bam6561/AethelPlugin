package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents entity damage mitigation.
 *
 * @author Danny Nguyen
 * @version 1.22.10
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
   * Player's enchantments.
   */
  private Map<Enchantment, Integer> enchantments = null;

  /**
   * Entity's {@link Status statuses}.
   */
  private final Map<StatusType, Status> statuses;

  /**
   * Associates the damage calculator with an entity.
   *
   * @param defender damagee
   */
  public DamageMitigation(@NotNull LivingEntity defender) {
    this.defender = Objects.requireNonNull(defender, "Null damagee");
    this.uuid = defender.getUniqueId();
    RpgSystem rpgSystem = Plugin.getData().getRpgSystem();
    if (defender instanceof Player) {
      this.enchantments = rpgSystem.getRpgPlayers().get(uuid).getEquipment().getEnchantments().getTotalEnchantments();
    }
    this.statuses = rpgSystem.getStatuses().get(uuid);
  }

  /**
   * Mitigates fall damage taken based on the player's fall protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateFall(double damage) {
    if (enchantments == null) {
      return damage;
    }
    return damage - (damage * (enchantments.get(Enchantment.PROTECTION_FALL) * .2));
  }

  /**
   * Mitigates fire damage based taken on the player's fire protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateFire(double damage) {
    if (enchantments == null) {
      return damage;
    }
    return damage - (damage * (enchantments.get(Enchantment.PROTECTION_FIRE) * .1));
  }

  /**
   * Mitigates explosion damage taken based on the player's explosion protection levels.
   *
   * @param damage damage done
   * @return damage taken
   */
  public double mitigateExplosion(double damage) {
    if (enchantments == null) {
      return damage;
    }
    damage = damage - (damage * (enchantments.get(Enchantment.PROTECTION_EXPLOSIONS) * .1));
    if (damage <= 0) {
      Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getHealth().heal(damage * .2);
      Player player = (Player) defender;
      player.setFoodLevel(20);
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
    if (enchantments == null) {
      return damage;
    }
    return damage - (damage * (Math.min(enchantments.get(Enchantment.PROTECTION_PROJECTILE) * .05, .5)));
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
   * Mitigates physical damage taken based on the player's armor and protection levels.
   *
   * @param damage initial damage
   * @return damage taken
   */
  public double mitigateArmorProtection(double damage) {
    if (enchantments == null) {
      return damage;
    }
    int armor = (int) defender.getAttribute(Attribute.GENERIC_ARMOR).getValue();
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
    if (enchantments == null) {
      return damage;
    }
    int armor = (int) defender.getAttribute(Attribute.GENERIC_ARMOR).getValue();
    if (statuses != null && statuses.containsKey(StatusType.FRACTURE)) {
      armor = armor - statuses.get(StatusType.FRACTURE).getStackAmount();
    }
    double mitigationValue = Math.min(armor * 0.02, .4) + Math.min(enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL) * 0.01, .2);
    damage = damage - (damage * mitigationValue);
    if (defender.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      int resistance = defender.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
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
    if (enchantments == null) {
      return damage;
    }
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
    if (enchantments == null) {
      return damage;
    }
    int protection = enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL);
    damage = damage - (damage * (Math.min(protection * .04, .8)));
    if (defender.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      int resistance = defender.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
      damage = damage - (damage * (resistance * 0.05));
    }
    return damage;
  }
}
