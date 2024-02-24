package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.AethelAttribute;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
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
 * @version 1.12.5
 * @since 1.9.4
 */
public class PlayerDamage implements Listener {
  /**
   * Handled damage causes.
   */
  private static final Set<EntityDamageEvent.DamageCause> handledDamageCause = Set.of(
      EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.ENTITY_ATTACK,
      EntityDamageEvent.DamageCause.KILL);

  /**
   * Calculates damage taken by players from non-entity attacks.
   *
   * @param e entity damage event
   */
  @EventHandler
  private void onGeneralDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player damagee && !handledDamageCause.contains(e.getCause())) {
      e.setCancelled(true);
      if (damagee.getNoDamageTicks() == 0) {
        damagee.damage(0.01);
        damagee.setNoDamageTicks(10);
        PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).damageHealthBar(e.getDamage());
      }
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
        processDamageDone(e, damager);
      } else {
        e.setCancelled(true);
        Player damagee = (Player) e.getEntity();
        if (damagee.getNoDamageTicks() == 0) {
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

    if (ifCountered(damagee, attributes, random, (LivingEntity) e.getDamager())) {
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
    damagee.damage(0.01);
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
  private boolean ifCountered(Player damagee, Map<AethelAttribute, Double> attributes, Random random, LivingEntity damager) {
    if (attributes.get(AethelAttribute.COUNTER_CHANCE) > random.nextDouble() * 100) {
      damager.damage((int) damagee.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue() * damagee.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());
      return damager.getHealth() <= 0.0;
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
    int protection = Math.min(totalProtectionLevels(damagee.getInventory()), 20);
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
   * Totals the worn armors' protection enchantment levels.
   *
   * @param pInv player inventory
   * @return total protection enchantment levels
   */
  private int totalProtectionLevels(PlayerInventory pInv) {
    int protection = 0;
    ItemStack helmet = pInv.getHelmet();
    if (ItemReader.isNotNullOrAir(helmet)) {
      protection += helmet.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
    }
    ItemStack chestplate = pInv.getChestplate();
    if (ItemReader.isNotNullOrAir(chestplate)) {
      protection += chestplate.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
    }
    ItemStack leggings = pInv.getLeggings();
    if (ItemReader.isNotNullOrAir(leggings)) {
      protection += leggings.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
    }
    ItemStack boots = pInv.getBoots();
    if (ItemReader.isNotNullOrAir(boots)) {
      protection += boots.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
    }
    return protection;
  }

  /**
   * Damages the worn armors' durability based on the damage taken.
   *
   * @param pInv   player inventory
   * @param damage damage taken
   */
  private void processArmorDurabilityDamage(PlayerInventory pInv, double damage) {
    int durabilityLoss = (int) Math.max(damage / 8, 1);
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
