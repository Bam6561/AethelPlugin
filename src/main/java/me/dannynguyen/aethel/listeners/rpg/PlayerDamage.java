package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.AethelAttribute;
import me.dannynguyen.aethel.systems.rpg.RpgPlayer;
import me.dannynguyen.aethel.utility.ItemDurability;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Player damage done, taken, and healed listener.
 *
 * @author Danny Nguyen
 * @version 1.13.1
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
      EntityDamageEvent.DamageCause cause = e.getCause();
      if (mitigateEnvironmentalDamage(e, cause, damagee)) {
        e.setCancelled(true);
        return;
      }

      double finalDamage = e.getDamage();
      switch (cause) {
        case BLOCK_EXPLOSION, CONTACT, FIRE, HOT_FLOOR, LAVA -> damageArmorDurability(damagee, finalDamage);
      }
      PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).damageHealthBar(finalDamage);
      e.setDamage(0);
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
        calculatePlayerDamageDone(e, damager);
      } else {
        Player damagee = (Player) e.getEntity();
        if (e.getDamager() instanceof Player) { // PvP, otherwise EvP
          calculatePlayerDamageDone(e, (Player) e.getDamager());
        }
        calculatePlayerDamageTaken(e, damagee);
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
   * Calculates damage done to the entity by the player.
   *
   * @param e       entity damage by entity event
   * @param damager interacting player
   */
  private void calculatePlayerDamageDone(EntityDamageByEntityEvent e, Player damager) {
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
  public void calculatePlayerDamageTaken(EntityDamageByEntityEvent e, Player damagee) {
    RpgPlayer rpgPlayer = PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId());
    Map<Enchantment, Integer> enchantments = rpgPlayer.getTotalEquipmentEnchantments();
    Entity damager = e.getDamager();

    if (mitigateSpecificEntityTypeDamage(e, enchantments, damager, damagee)) {
      e.setCancelled(true);
      return;
    }

    Map<AethelAttribute, Double> attributes = rpgPlayer.getAethelAttributes();
    Random random = new Random();

    if (damager instanceof Projectile) {
      if (mitigateProjectileEntityDamage(e, enchantments, damager, damagee)) {
        e.setDamage(0);
        return;
      }
    } else if (ifCountered(attributes, random, damager, damagee)) {
      e.setCancelled(true);
      return;
    }

    if (ifDodged(attributes, random)) {
      e.setCancelled(true);
      return;
    } else if (ifTougher(e, attributes, damagee)) {
      e.setCancelled(true);
      return;
    }

    mitigateArmorProtection(e, damagee);
    mitigateResistance(e, damagee);

    double finalDamage = e.getDamage();
    damageArmorDurability(damagee, finalDamage);
    rpgPlayer.damageHealthBar(finalDamage);
    e.setDamage(0);
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
   * Mitigates environmental damage taken based on the player's environmental protection enchantments.
   *
   * @param e       entity damage event
   * @param cause   damage cause
   * @param damagee player taking damage
   * @return if no damage is taken
   */
  private boolean mitigateEnvironmentalDamage(EntityDamageEvent e, EntityDamageEvent.DamageCause cause, Player damagee) {
    Map<Enchantment, Integer> enchantments = PluginData.rpgSystem.getRpgPlayers().get(e.getEntity().getUniqueId()).getTotalEquipmentEnchantments();
    switch (cause) {
      case FALL -> {
        int fallProtection = enchantments.get(Enchantment.PROTECTION_FALL);
        if (fallProtection > 0) {
          double damage = e.getDamage();
          e.setDamage(damage - (damage * (fallProtection * .2)));
        }
      }
      case DRAGON_BREATH, FLY_INTO_WALL, MAGIC, POISON, WITHER -> {
        int protection = enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL);
        if (protection > 0) {
          protection = Math.max(protection, 20);
          double damage = e.getDamage();
          e.setDamage(damage - (damage * (protection * .04)));
        }
      }
      case FIRE, FIRE_TICK, HOT_FLOOR, LAVA -> {
        int fireProtection = enchantments.get(Enchantment.PROTECTION_FIRE);
        if (fireProtection > 0) {
          double damage = e.getDamage();
          e.setDamage(damage - (damage * (fireProtection * .1)));
        }
      }
      case BLOCK_EXPLOSION -> {
        int explosionProtection = enchantments.get(Enchantment.PROTECTION_EXPLOSIONS);
        if (explosionProtection >= 10) {
          PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).healHealthBar(e.getDamage() * .2);
          damagee.setFoodLevel(20);
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
   * Mitigates specific entity type damage taken based on their respective protection enchantment.
   *
   * @param e            entity damage by entity event
   * @param enchantments player's enchantments
   * @param damager      damaging entity
   * @param damagee      player taking damage
   * @return if no damage taken or magic damage was taken/mitigated
   */
  private boolean mitigateSpecificEntityTypeDamage(EntityDamageByEntityEvent e, Map<Enchantment, Integer> enchantments, Entity damager, Player damagee) {
    switch (damager.getType()) {
      case PRIMED_TNT, ENDER_CRYSTAL -> {
        int explosionProtection = enchantments.get(Enchantment.PROTECTION_EXPLOSIONS);
        if (explosionProtection >= 10) {
          PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).healHealthBar(e.getDamage() * .2);
          damagee.setFoodLevel(20);
          return true;
        } else if (explosionProtection > 0) {
          double damage = e.getDamage();
          e.setDamage(Math.max(damage - (damage * (explosionProtection * .1)), 0));
        }
      }
      case AREA_EFFECT_CLOUD -> {
        int protection = enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL);
        if (protection > 0) {
          protection = Math.max(protection, 20);
          double damage = e.getDamage();
          e.setDamage(damage - (damage * (protection * .04)));
        }
        PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).damageHealthBar(e.getDamage());
        e.setDamage(0);
        return true;
      }
    }
    return false;
  }

  /**
   * Mitigates projectile damage taken based on the player's Projectile Protection enchantment.
   *
   * @param e            entity damage by entity event
   * @param enchantments player's enchantments
   * @param damager      damaging entity
   * @param damagee      player taking damage
   * @return if splash potion projectile damage mitigated
   */
  private boolean mitigateProjectileEntityDamage(EntityDamageByEntityEvent e, Map<Enchantment, Integer> enchantments, Entity damager, Player damagee) {
    EntityType damagerType = damager.getType();
    switch (damagerType) {
      case ARROW, DRAGON_FIREBALL, EGG, ENDER_PEARL, FIREBALL, FIREWORK,
          FISHING_HOOK, LLAMA_SPIT, SHULKER_BULLET, SMALL_FIREBALL, SNOWBALL,
          SPECTRAL_ARROW, THROWN_EXP_BOTTLE, TRIDENT, WITHER_SKULL -> {
        int projectileProtection = enchantments.get(Enchantment.PROTECTION_PROJECTILE);
        if (projectileProtection >= 10) {
          PlayerInventory pInv = damagee.getInventory();
          switch (damagerType) {
            case ARROW -> {
              PotionType potionType = ((Arrow) damager).getBasePotionType();
              if (potionType == PotionType.UNCRAFTABLE) {
                pInv.addItem(new ItemStack(Material.ARROW));
              } else {
                ItemStack item = new ItemStack(Material.TIPPED_ARROW);
                PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                potionMeta.setBasePotionType(potionType);
                item.setItemMeta(potionMeta);
                pInv.addItem(item);
              }
            }
            case FIREBALL, SMALL_FIREBALL -> pInv.addItem(new ItemStack(Material.FIRE_CHARGE));
            case SNOWBALL -> pInv.addItem(new ItemStack(Material.SNOWBALL));
            case SPECTRAL_ARROW -> pInv.addItem(new ItemStack(Material.SPECTRAL_ARROW));
          }
        } else if (projectileProtection > 0) {
          projectileProtection = Math.max(projectileProtection, 10);
          double damage = e.getDamage();
          e.setDamage(damage - (damage * (projectileProtection * .05)));
        }
      }
      case SPLASH_POTION -> {
        int protection = enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL);
        if (protection > 0) {
          protection = Math.max(protection, 20);
          double damage = e.getDamage();
          e.setDamage(damage - (damage * (protection * .04)));
          PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).damageHealthBar(e.getDamage());
          e.setDamage(0);
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Ignores damage taken if the player killed the damager by counterattacks.
   *
   * @param attributes player's attributes
   * @param random     rng
   * @param damager    damager
   * @param damagee    player taking damage
   * @return if the damager died
   */
  private boolean ifCountered(Map<AethelAttribute, Double> attributes, Random random, Entity damager, Player damagee) {
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
   * @return if dodged
   */
  private boolean ifDodged(Map<AethelAttribute, Double> attributes, Random random) {
    return attributes.get(AethelAttribute.DODGE_CHANCE) > random.nextDouble() * 100;
  }

  /**
   * Ignore damage taken if the player's toughness is higher,
   * otherwise toughness mitigates damage by a flat amount.
   *
   * @param e          entity damage by entity event
   * @param attributes player's attributes
   * @param damagee    player taking damage
   * @return if tougher than damage
   */
  private boolean ifTougher(EntityDamageByEntityEvent e, Map<AethelAttribute, Double> attributes, Player damagee) {
    double toughness = damagee.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + attributes.get(AethelAttribute.TOUGHNESS);
    e.setDamage(Math.max(e.getDamage() - (toughness / 2), 0));
    return e.getDamage() == 0;
  }

  /**
   * Mitigates the damage taken based on the player's armor value and protection enchantments.
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
   * Mitigates the damage taken based on the player's resistance effect.
   *
   * @param e       entity damage by entity event
   * @param damagee player taking damage
   */
  private void mitigateResistance(EntityDamageByEntityEvent e, Player damagee) {
    if (damagee.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      int resistance = damagee.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
      double damage = e.getDamage();
      e.setDamage(damage - (damage * (resistance * 0.05)));
    }
  }

  /**
   * Damages the worn armors' durability based on the damage taken.
   *
   * @param damagee player taking damage
   * @param damage  damage taken
   */
  private void damageArmorDurability(Player damagee, double damage) {
    int durabilityDamage = (int) Math.max(damage / 4, 1);
    ItemDurability.increaseDamage(damagee, EquipmentSlot.HEAD, durabilityDamage);
    ItemDurability.increaseDamage(damagee, EquipmentSlot.CHEST, durabilityDamage);
    ItemDurability.increaseDamage(damagee, EquipmentSlot.LEGS, durabilityDamage);
    ItemDurability.increaseDamage(damagee, EquipmentSlot.FEET, durabilityDamage);
  }
}
