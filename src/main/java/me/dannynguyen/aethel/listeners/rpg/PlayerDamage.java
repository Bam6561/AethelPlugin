package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.AethelAttribute;
import me.dannynguyen.aethel.utility.ItemReader;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Player damage done, taken, and healed listener.
 *
 * @author Danny Nguyen
 * @version 1.12.9
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
   * Processes damage taken by players from non-entity attacks.
   *
   * @param e entity damage event
   */
  @EventHandler
  private void onGeneralDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player damagee && !handledDamageCauses.contains(e.getCause())) {
      e.setCancelled(true);
      if (damagee.getNoDamageTicks() == 0) {
        damagee.damage(0.01);
        damagee.setNoDamageTicks(10);
        Map<Enchantment, Integer> totalEquipmentEnchantments = PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).getTotalEquipmentEnchantments();
        EntityDamageEvent.DamageCause cause = e.getCause();
        if (mitigateEnvironmentalDamage(e, cause, totalEquipmentEnchantments)) {
          return;
        }
        double finalDamage = e.getDamage();
        switch (cause) {
          case BLOCK_EXPLOSION, CONTACT, FIRE, HOT_FLOOR, LAVA -> processArmorDurabilityDamage(damagee.getInventory(), finalDamage);
        }
        PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).damageHealthBar(finalDamage);
      }
    }
  }

  /**
   * Processes damage done or taken by players from other entities.
   *
   * @param e entity damaged by entity event
   */
  @EventHandler
  private void onEntityDamage(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player || e.getEntity() instanceof Player) {
      if (e.getDamager() instanceof Player damager && !(e.getEntity() instanceof Player)) { // PvE
        processDamageDone(e, damager);
      } else {
        e.setCancelled(true);
        Player damagee = (Player) e.getEntity();
        if (damagee.getNoDamageTicks() == 0) {
          damagee.damage(0.01);
          damagee.setNoDamageTicks(10);
          if (e.getDamager() instanceof Player) { // PvP, otherwise EvP
            processDamageDone(e, (Player) e.getDamager());
          }
          processDamageTaken(e, damagee);
        }
      }
    }
  }

  /**
   * Processes damage healed by players.
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
      case FLY_INTO_WALL, MAGIC, POISON, WITHER -> {
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
  private void processDamageDone(EntityDamageByEntityEvent e, Player damager) {
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
  private void processDamageTaken(EntityDamageByEntityEvent e, Player damagee) {
    Map<AethelAttribute, Double> attributes = PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).getAethelAttributes();
    Random random = new Random();

    if (ifCountered(damagee, attributes, random, e.getDamager())) {
      return;
    } else if (ifDodged(attributes, random)) {
      return;
    } else if (ifTougher(e, damagee, attributes)) {
      return;
    }
    mitigateArmorProtection(e, damagee);
    mitigateResistance(e, damagee);

    double finalDamage = e.getDamage();
    processArmorDurabilityDamage(damagee.getInventory(), finalDamage);
    PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).damageHealthBar(finalDamage);
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
   * @param pInv   player inventory
   * @param damage damage taken
   */
  private void processArmorDurabilityDamage(PlayerInventory pInv, double damage) {
    int durabilityLoss = (int) Math.max(damage / 4, 1);
    ItemStack helmet = pInv.getHelmet();
    if (ItemReader.isNotNullOrAir(helmet)) {
      Damageable helmetDurability = (Damageable) helmet.getItemMeta();
      helmetDurability.setDamage(helmetDurability.getDamage() + durabilityLoss);
      helmet.setItemMeta(helmetDurability);
    }
    ItemStack chestplate = pInv.getChestplate();
    if (ItemReader.isNotNullOrAir(chestplate)) {
      Damageable chestplateDurability = (Damageable) chestplate.getItemMeta();
      chestplateDurability.setDamage(chestplateDurability.getDamage() + durabilityLoss);
      chestplate.setItemMeta(chestplateDurability);
    }
    ItemStack leggings = pInv.getLeggings();
    if (ItemReader.isNotNullOrAir(leggings)) {
      Damageable leggingsDurability = (Damageable) leggings.getItemMeta();
      leggingsDurability.setDamage(leggingsDurability.getDamage() + durabilityLoss);
      leggings.setItemMeta(leggingsDurability);
    }
    ItemStack boots = pInv.getBoots();
    if (ItemReader.isNotNullOrAir(boots)) {
      Damageable bootsDurability = (Damageable) boots.getItemMeta();
      bootsDurability.setDamage(bootsDurability.getDamage() + durabilityLoss);
      boots.setItemMeta(bootsDurability);
    }
  }
}
