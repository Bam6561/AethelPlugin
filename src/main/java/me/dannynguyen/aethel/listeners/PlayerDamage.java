package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Player damage done and taken listener.
 *
 * @author Danny Nguyen
 * @version 1.10.6
 * @since 1.9.4
 */
public class PlayerDamage implements Listener {
  /**
   * Handled damage causes.
   */
  private static final Set<EntityDamageEvent.DamageCause> handledDamageCause = Set.of(
      EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.ENTITY_ATTACK);

  /**
   * Calculates damage taken by players from non-entity attacks.
   *
   * @param e entity damage event
   */
  @EventHandler
  public void onDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player player && !handledDamageCause.contains(e.getCause())) {
      e.setCancelled(true);
      if (player.getNoDamageTicks() == 0) {
        player.damage(0.01);
        player.setNoDamageTicks(10);
        PluginData.rpgSystem.getRpgProfiles().get(player).damage(e.getDamage());
      }
    }
  }

  /**
   * Calculates damage done or taken by players from other entities.
   *
   * @param e entity damaged by entity event
   */
  @EventHandler
  public void onDamage(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player || e.getEntity() instanceof Player) {
      if (e.getDamager() instanceof Player player && !(e.getEntity() instanceof Player)) { // PvE
        processDamageDone(e, player);
      } else if (!(e.getDamager() instanceof Player)) { // EvP
        e.setCancelled(true);
        Player player = (Player) e.getEntity();
        if (player.getNoDamageTicks() == 0) {
          player.damage(0.01);
          player.setNoDamageTicks(10);
          processDamageTaken(e, player);
        }
      } else { // PvP
        // TO DO
      }
    }
  }

  /**
   * Calculates damage done to the target by the player.
   *
   * @param e      entity damage by entity event
   * @param player interacting player
   */
  private void processDamageDone(EntityDamageByEntityEvent e, Player player) {
    calculateIfCriticallyHit(e, player);
  }

  /**
   * Calculates damage taken by the player.
   *
   * @param e      entity damage by entity event
   * @param player interacting player
   */
  private void processDamageTaken(EntityDamageByEntityEvent e, Player player) {
    Map<String, Double> aethelAttributes = PluginData.rpgSystem.getRpgProfiles().get(player).getAethelAttributes();
    Random random = new Random();
    Double damage = e.getDamage();
    if (calculateIfDodged(e, aethelAttributes, random)) {
      return;
    } else if (calculateIfParried(e, aethelAttributes, random, damage)) {
      return;
    } else if (calculateIfBlocked(e, aethelAttributes, damage)) {
      return;
    }
    PluginData.rpgSystem.getRpgProfiles().get(player).damage(damage);
  }

  /**
   * Checks if the player deals a critical hit, then multiplies the damage by its modifier.
   *
   * @param e      entity damage by entity event
   * @param player interacting player
   */
  private void calculateIfCriticallyHit(EntityDamageByEntityEvent e, Player player) {
    Map<String, Double> aethelAttributes = PluginData.rpgSystem.getRpgProfiles().get(player).getAethelAttributes();
    if (aethelAttributes.get("critical_chance") > new Random().nextDouble() * 100) {
      e.setDamage(e.getDamage() * (1.25 + (aethelAttributes.get("critical_damage") / 100)));
    }
  }

  /**
   * Checks if the player dodged, then negates the damage taken.
   *
   * @param e                entity damage by entity event
   * @param aethelAttributes player's attributes
   * @param random           rng
   * @return if damage taken dodged
   */
  private boolean calculateIfDodged(EntityDamageByEntityEvent e, Map<String, Double> aethelAttributes, Random random) {
    if (aethelAttributes.get("dodge_chance") > random.nextDouble() * 100) {
      return true;
    }
    return false;
  }

  /**
   * Checks if the player parried, then modifies the damage taken by
   * the percentage parried and deflects the remainder to the attacker.
   *
   * @param e                entity damage by entity event
   * @param aethelAttributes player's attributes
   * @param random           rng
   * @param damage           damage taken
   * @return if damage taken completely parried
   */
  private boolean calculateIfParried(EntityDamageByEntityEvent e, Map<String, Double> aethelAttributes, Random random, Double damage) {
    if (aethelAttributes.get("parry_chance") > random.nextDouble() * 100) {
      Double damageDeflected = damage * (aethelAttributes.get("parry_deflect") / 100);
      damage = damage - damageDeflected;
      ((LivingEntity) e.getDamager()).damage(damageDeflected);
      if (damage < 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the player blocked, then subtracts from the damage taken.
   *
   * @param e                entity damage by entity event
   * @param aethelAttributes player's attributes
   * @param damage           damage taken
   * @return if damage taken completely blocked
   */
  private boolean calculateIfBlocked(EntityDamageByEntityEvent e, Map<String, Double> aethelAttributes, Double damage) {
    damage = damage - aethelAttributes.get("block");
    if (damage < 0) {
      return true;
    }
    return false;
  }
}
