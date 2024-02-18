package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Player damage done, taken, and healed listener.
 *
 * @author Danny Nguyen
 * @version 1.10.8
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
  public void onGeneralDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player damagee && !handledDamageCause.contains(e.getCause())) {
      e.setCancelled(true);
      if (damagee.getNoDamageTicks() == 0) {
        damagee.damage(0.01);
        damagee.setNoDamageTicks(10);
        PluginData.rpgSystem.getRpgProfiles().get(damagee).damageHealthBar(e.getDamage());
      }
    }
  }

  /**
   * Calculates damage done or taken by players from other entities.
   *
   * @param e entity damaged by entity event
   */
  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent e) {
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
   */
  @EventHandler
  private void onRegainHealth(EntityRegainHealthEvent e) {
    if (e.getEntity() instanceof Player player) {
      e.setCancelled(true);
      PluginData.rpgSystem.getRpgProfiles().get(player).healHealthBar(e.getAmount());
    }
  }

  /**
   * Calculates damage done to the target by the player.
   *
   * @param e       entity damage by entity event
   * @param damager interacting player
   */
  private void processDamageDone(EntityDamageByEntityEvent e, Player damager) {
    Map<String, Double> aethelAttributes = PluginData.rpgSystem.getRpgProfiles().get(damager).getAethelAttributes();
    Random random = new Random();
    double finalDamage = e.getDamage();
    finalDamage = calculateIfCriticallyHit(aethelAttributes, random, finalDamage);
    e.setDamage(finalDamage);
  }

  /**
   * Calculates damage taken by the player.
   *
   * @param e       entity damage by entity event
   * @param damagee interacting player
   */
  private void processDamageTaken(EntityDamageByEntityEvent e, Player damagee) {
    Map<String, Double> aethelAttributes = PluginData.rpgSystem.getRpgProfiles().get(damagee).getAethelAttributes();
    Random random = new Random();
    double finalDamage = e.getDamage();
    if (calculateIfDodged(aethelAttributes, random)) {
      return;
    } else if (calculateIfParried(aethelAttributes, random, finalDamage, (LivingEntity) e.getDamager())) {
      return;
    } else if (calculateIfBlocked(aethelAttributes, finalDamage)) {
      return;
    }
    damagee.damage(0.01);
    PluginData.rpgSystem.getRpgProfiles().get(damagee).damageHealthBar(finalDamage);
  }

  /**
   * Checks if the player deals a critical hit, then multiplies the damage by its modifier.
   *
   * @param aethelAttributes player's attributes
   * @param random           rng
   * @param damage           damage dealt
   */
  private double calculateIfCriticallyHit(Map<String, Double> aethelAttributes, Random random, double damage) {
    if (aethelAttributes.get("critical_chance") > random.nextDouble() * 100) {
      return (damage * (1.25 + (aethelAttributes.get("critical_damage") / 100)));
    }
    return damage;
  }

  /**
   * Checks if the player dodged, then negates the damage taken.
   *
   * @param aethelAttributes player's attributes
   * @param random           rng
   * @return if damage taken dodged
   */
  private boolean calculateIfDodged(Map<String, Double> aethelAttributes, Random random) {
    if (aethelAttributes.get("dodge_chance") > random.nextDouble() * 100) {
      return true;
    }
    return false;
  }

  /**
   * Checks if the player parried, then modifies the damage taken by
   * the percentage parried and deflects the remainder to the attacker.
   *
   * @param damager          damager
   * @param aethelAttributes player's attributes
   * @param random           rng
   * @param damage           damage taken
   * @return if damage taken completely parried
   */
  private boolean calculateIfParried(Map<String, Double> aethelAttributes, Random random, Double damage, LivingEntity damager) {
    if (aethelAttributes.get("parry_chance") > random.nextDouble() * 100) {
      double damageDeflected = damage * (aethelAttributes.get("parry_deflect") / 100);
      damage = damage - damageDeflected;
      damager.damage(damageDeflected);
      if (damage < 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the player blocked, then subtracts from the damage taken.
   *
   * @param aethelAttributes player's attributes
   * @param damage           damage taken
   * @return if damage taken completely blocked
   */
  private boolean calculateIfBlocked(Map<String, Double> aethelAttributes, double damage) {
    damage = damage - aethelAttributes.get("block");
    if (damage < 0) {
      return true;
    }
    return false;
  }
}
