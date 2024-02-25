package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.AethelAttribute;
import me.dannynguyen.aethel.systems.rpg.RpgPlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Player damage done, taken, and healed listener.
 *
 * @author Danny Nguyen
 * @version 1.12.10
 * @since 1.9.4
 */
public class PlayerDamage implements Listener {
  /**
   * Handled damage causes.
   */
  private static final Set<EntityDamageEvent.DamageCause> handledDamageCauses = Set.of(
      EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.ENTITY_ATTACK,
      EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK,
      EntityDamageEvent.DamageCause.FALLING_BLOCK, EntityDamageEvent.DamageCause.LIGHTNING,
      EntityDamageEvent.DamageCause.PROJECTILE, EntityDamageEvent.DamageCause.KILL);

  /**
   * Calculates damage taken by players from non-entity attacks.
   *
   * @param e entity damage event
   */
  @EventHandler
  private void onGeneralDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player damagee && !handledDamageCauses.contains(e.getCause())) {
      Map<Enchantment, Integer> totalEquipmentEnchantments = PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).getTotalEquipmentEnchantments();
      EntityDamageEvent.DamageCause cause = e.getCause();
      if (mitigateEnvironmentalDamage(e, cause, totalEquipmentEnchantments)) {
        e.setCancelled(true);
        return;
      }
      double finalDamage = e.getDamage();
      e.setDamage(0);
      switch (cause) {
        case BLOCK_EXPLOSION, CONTACT, FIRE, HOT_FLOOR, LAVA -> damageArmorDurability(damagee, finalDamage);
      }
      PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).damageHealthBar(finalDamage);
    }
  }

  /**
   * Calculates damage done or taken by players from other entities.
   *
   * @param e entity damaged by entity event
   */
  @EventHandler
  private void onEntityDamage(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player || e.getEntity() instanceof Player) {
      if (e.getDamager() instanceof Player damager && !(e.getEntity() instanceof Player)) { // PvE
        calculateDamageDone(e, damager);
      } else {
        Player damagee = (Player) e.getEntity();
        if (e.getDamager() instanceof Player) { // PvP, otherwise EvP
          calculateDamageDone(e, (Player) e.getDamager());
        }
        calculateDamageTaken(e, damagee);
      }
    }
  }

  /**
   * Calculates damage healed by players.
   *
   * @param e entity regain health event
   */
  @EventHandler
  private void onRegainHealth(EntityRegainHealthEvent e) {
    if (e.getEntity() instanceof Player player) {
      e.setCancelled(true);
      PluginData.rpgSystem.getRpgPlayers().get(player.getUniqueId()).healHealthBar(e.getAmount());
    }
  }

  /**
   * Mitigates the damage based on the player's environmental protection enchantments.
   *
   * @param e                          entity damage event
   * @param cause                      damage cause
   * @param totalEquipmentEnchantments player's equipment enchantments
   * @return if no damage is taken
   */
  private boolean mitigateEnvironmentalDamage(EntityDamageEvent e, EntityDamageEvent.DamageCause cause, Map<Enchantment, Integer> totalEquipmentEnchantments) {
    switch (cause) {
      case FALL -> {
        int fallProtection = totalEquipmentEnchantments.get(Enchantment.PROTECTION_FALL);
        if (fallProtection > 0) {
          double damage = e.getDamage();
          e.setDamage(damage - (damage * (fallProtection * .2)));
        }
      }
      case FIRE, FIRE_TICK, HOT_FLOOR, LAVA -> {
        int fireProtection = totalEquipmentEnchantments.get(Enchantment.PROTECTION_FIRE);
        if (fireProtection > 0) {
          double damage = e.getDamage();
          e.setDamage(damage - (damage * (fireProtection * .1)));
        }
      }
      case DRAGON_BREATH, FLY_INTO_WALL, MAGIC, POISON, WITHER -> {
        int protection = totalEquipmentEnchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL);
        if (protection > 0) {
          protection = Math.max(protection, 20);
          double damage = e.getDamage();
          e.setDamage(damage - (damage * (protection * .04)));
        }
      }
      case BLOCK_EXPLOSION -> {
        int explosionProtection = totalEquipmentEnchantments.get(Enchantment.PROTECTION_EXPLOSIONS);
        if (explosionProtection >= 10) {
          Player player = (Player) e.getEntity();
          PluginData.rpgSystem.getRpgPlayers().get(player.getUniqueId()).healHealthBar(e.getDamage() * .2);
          player.setFoodLevel(20);
          return true;
        } else if (explosionProtection > 0) {
          double damage = e.getDamage();
          e.setDamage(Math.max(damage - (damage * (explosionProtection * .1)), 0));
        }
      }
    }
    return false;
  }

  /**
   * Calculates damage done to the target by the player.
   *
   * @param e       entity damage by entity event
   * @param damager interacting player
   */
  private void calculateDamageDone(EntityDamageByEntityEvent e, Player damager) {
    Map<AethelAttribute, Double> attributes = PluginData.rpgSystem.getRpgPlayers().get(damager.getUniqueId()).getAethelAttributes();
    Random random = new Random();
    double damage = e.getDamage();
    damage = ifCriticallyHit(attributes, random, damage);
    e.setDamage(damage);
  }

  /**
   * Calculates damage taken by the player.
   *
   * @param e       entity damage by entity event
   * @param damagee interacting player
   */
  public void calculateDamageTaken(EntityDamageByEntityEvent e, Player damagee) {
    RpgPlayer rpgPlayer = PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId());
    switch (e.getDamager().getType()) {
      case ARROW, DRAGON_FIREBALL, EGG, ENDER_PEARL, FIREBALL, FIREWORK,
          FISHING_HOOK, LLAMA_SPIT, SHULKER_BULLET, SMALL_FIREBALL, SNOWBALL,
          SPECTRAL_ARROW, THROWN_EXP_BOTTLE, TRIDENT, WITHER_SKULL -> {
        int projectileProtection = rpgPlayer.getTotalEquipmentEnchantments().get(Enchantment.PROTECTION_PROJECTILE);
        if (projectileProtection > 0) {
          if (projectileProtection >= 10) {
            // TO DO
          } else if (projectileProtection > 0) {
            projectileProtection = Math.max(projectileProtection, 10);
            double damage = e.getDamage();
            e.setDamage(damage - (damage * (projectileProtection * .05)));
          }
        }
      }
      case SPLASH_POTION -> {
        int protection = rpgPlayer.getTotalEquipmentEnchantments().get(Enchantment.PROTECTION_ENVIRONMENTAL);
        if (protection > 0) {
          protection = Math.max(protection, 20);
          double damage = e.getDamage();
          e.setDamage(damage - (damage * (protection * .04)));
          double finalDamage = e.getDamage();
          e.setDamage(0);
          rpgPlayer.damageHealthBar(finalDamage);
          return;
        }
      }
      case PRIMED_TNT -> {
        if (mitigatePrimedTNTDamage(e, damagee)) {
          e.setCancelled(true);
          return;
        }
      }
    }

    Map<AethelAttribute, Double> attributes = rpgPlayer.getAethelAttributes();
    Random random = new Random();

    if (ifCountered(damagee, attributes, random, e.getDamager())) {
      e.setCancelled(true);
      return;
    } else if (ifDodged(attributes, random)) {
      e.setCancelled(true);
      return;
    } else if (ifTougher(e, damagee, attributes)) {
      e.setCancelled(true);
      return;
    }
    mitigateArmorProtection(e, damagee);
    mitigateResistance(e, damagee);

    double finalDamage = e.getDamage();
    e.setDamage(0);
    damageArmorDurability(damagee, finalDamage);
    rpgPlayer.damageHealthBar(finalDamage);
  }

  /**
   * If the player dealt a critical hit, multiply the damage by its modifier.
   *
   * @param attributes player's attributes
   * @param random     rng
   * @param damage     damage dealt
   * @return damage dealt
   */
  private double ifCriticallyHit(Map<AethelAttribute, Double> attributes, Random random, double damage) {
    if (attributes.get(AethelAttribute.CRITICAL_CHANCE) > random.nextDouble() * 100) {
      return (damage * (1.25 + (attributes.get(AethelAttribute.CRITICAL_DAMAGE) / 100)));
    }
    return damage;
  }

  /**
   * Ignores damage taken if the player killed the damager by counterattacks.
   *
   * @param damagee    player taking damage
   * @param attributes player's attributes
   * @param random     rng
   * @param damager    damager
   * @return if the damager died
   */
  private boolean ifCountered(Player damagee, Map<AethelAttribute, Double> attributes, Random random, Entity damager) {
    if (damager instanceof LivingEntity attacker) {
      if (attributes.get(AethelAttribute.COUNTER_CHANCE) > random.nextDouble() * 100) {
        attacker.damage((int) damagee.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue() * damagee.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());
        return attacker.getHealth() <= 0.0;
      }
    }
    return false;
  }

  /**
   * Ignore damage taken if the player dodged.
   *
   * @param attributes player's attributes
   * @param random     rng
   * @return if damage taken ignored
   */
  private boolean ifDodged(Map<AethelAttribute, Double> attributes, Random random) {
    return attributes.get(AethelAttribute.DODGE_CHANCE) > random.nextDouble() * 100;
  }

  /**
   * Ignore damage taken if the player's toughness is higher,
   * otherwise toughness mitigates damage by a flat amount.
   *
   * @param e          entity damage by entity event
   * @param damagee    player taking damage
   * @param attributes player's attributes
   * @return if damage taken ignored
   */
  private boolean ifTougher(EntityDamageByEntityEvent e, Player damagee, Map<AethelAttribute, Double> attributes) {
    double toughness = damagee.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + attributes.get(AethelAttribute.TOUGHNESS);
    e.setDamage(Math.max(e.getDamage() - (toughness / 2), 0));
    return e.getDamage() == 0;
  }

  /**
   * Mitigates the damage based on the player's armor value and protection enchantments.
   *
   * @param e       entity damage by entity event
   * @param damagee player taking damage
   */
  private void mitigateArmorProtection(EntityDamageByEntityEvent e, Player damagee) {
    int armor = Math.min((int) damagee.getAttribute(Attribute.GENERIC_ARMOR).getValue(), 20);
    int protection = Math.min(PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).getTotalEquipmentEnchantments().get(Enchantment.PROTECTION_ENVIRONMENTAL), 20);
    double damage = e.getDamage();
    e.setDamage(damage - (damage * (armor * 0.02 + protection * 0.01)));
  }

  /**
   * Mitigates the damage based on the player's resistance effect.
   *
   * @param e       entity damage by entity event
   * @param damagee player taking damage
   */
  private void mitigateResistance(EntityDamageByEntityEvent e, Player damagee) {
    int resistance = 0;
    if (damagee.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      resistance = damagee.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
    }
    double damage = e.getDamage();
    e.setDamage(damage - (damage * (resistance * 0.05)));
  }

  /**
   * Damages the worn armors' durability based on the damage taken.
   *
   * @param damagee player taking damage
   * @param damage  damage taken
   */
  private void damageArmorDurability(Player damagee, double damage) {
    DurabilityDamage durabilityDamage = new DurabilityDamage(damagee, damage);
    durabilityDamage.damageDurability(EquipmentSlot.HEAD);
    durabilityDamage.damageDurability(EquipmentSlot.CHEST);
    durabilityDamage.damageDurability(EquipmentSlot.LEGS);
    durabilityDamage.damageDurability(EquipmentSlot.FEET);
  }

  /**
   * Mitigates damage done by primed TNT based on the
   * player's total blast protection enchantment.
   *
   * @param e       entity damage by entity event
   * @param damagee player taking damage
   * @return if no damage was taken
   */
  private boolean mitigatePrimedTNTDamage(EntityDamageByEntityEvent e, Player damagee) {
    int explosionProtection = PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).getTotalEquipmentEnchantments().get(Enchantment.PROTECTION_EXPLOSIONS);
    if (explosionProtection >= 10) {
      Player player = (Player) e.getEntity();
      PluginData.rpgSystem.getRpgPlayers().get(player.getUniqueId()).healHealthBar(e.getDamage() * .2);
      player.setFoodLevel(20);
      return true;
    } else if (explosionProtection > 0) {
      double damage = e.getDamage();
      e.setDamage(Math.max(damage - (damage * (explosionProtection * .1)), 0));
    }
    return false;
  }
}
