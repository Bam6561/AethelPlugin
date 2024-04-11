package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.rpg.*;
import me.dannynguyen.aethel.rpg.abilities.Abilities;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import me.dannynguyen.aethel.utils.item.ItemDurability;
import org.bukkit.*;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Collection of damage done, taken, and healed listeners.
 *
 * @author Danny Nguyen
 * @version 1.22.7
 * @since 1.9.4
 */
public class HealthListener implements Listener {
  /**
   * Ignored damage causes.
   */
  private static final Set<EntityDamageEvent.DamageCause> ignoredDamageCauses = Set.of(EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.KILL);

  /**
   * No parameter constructor.
   */
  public HealthListener() {
  }

  /**
   * Processes player damage dealt and taken interactions.
   *
   * @param e entity damage event
   */
  @EventHandler
  private void onEntityDamage(EntityDamageEvent e) {
    if (e instanceof EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player || event.getEntity() instanceof Player) {
        if (event.getDamager() instanceof Player damager && !(event.getEntity() instanceof Player)) { // PvE
          triggerDamageDealtPassives(event, damager);
          processPlayerDamageDone(event, damager);
        } else {
          Player damagee = (Player) event.getEntity();
          DamageMitigation mitigation = new DamageMitigation(damagee);
          if (event.getDamager() instanceof Player damager) { // PvP, otherwise EvP
            triggerDamageDealtPassives(event, damager);
            processPlayerDamageDone(event, damager);
          }
          triggerDamageTakenPassives(event, damagee);
          processPlayerDamageTaken(event, damagee, mitigation);
        }
      }
    } else if (e.getEntity() instanceof Player damagee && !ignoredDamageCauses.contains(e.getCause())) {
      DamageMitigation mitigation = new DamageMitigation(damagee);
      EntityDamageEvent.DamageCause cause = e.getCause();
      if (mitigateEnvironmentalDamage(e, cause, damagee, mitigation)) {
        e.setCancelled(true);
        return;
      }
      final double finalDamage = e.getDamage();
      switch (cause) {
        case BLOCK_EXPLOSION, CONTACT, FIRE, HOT_FLOOR, LAVA -> damageArmorDurability(damagee, finalDamage);
      }
      Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId()).getHealth().damage(finalDamage);
      e.setDamage(0);
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
      Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getHealth().heal(e.getAmount());
    }
  }

  /**
   * Triggers {@link PassiveTriggerType#DAMAGE_DEALT} {@link PassiveAbility passive abilities}.
   *
   * @param e       entity damage by entity event
   * @param damager interacting player
   */
  private void triggerDamageDealtPassives(EntityDamageByEntityEvent e, Player damager) {
    if (damager.getAttackCooldown() < 0.75 || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
      return;
    }
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(damager.getUniqueId());
    Map<Abilities.SlotPassive, PassiveAbility> damageDealtTriggers = rpgPlayer.getAbilities().getTriggerPassives().get(PassiveTriggerType.DAMAGE_DEALT);
    if (damageDealtTriggers.isEmpty() || !(e.getEntity() instanceof LivingEntity damagee)) {
      return;
    }

    Random random = new Random();
    for (PassiveAbility ability : damageDealtTriggers.values()) {
      if (ability.isOnCooldown()) {
        continue;
      }
      double chance = Double.parseDouble(ability.getConditionData().get(0));
      if (chance > random.nextDouble() * 100) {
        boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
        UUID targetUUID;
        if (self) {
          targetUUID = damager.getUniqueId();
        } else {
          targetUUID = damagee.getUniqueId();
        }
        ability.doEffect(rpgPlayer, targetUUID);
      }
    }
  }

  /**
   * Triggers {@link PassiveTriggerType#DAMAGE_TAKEN} {@link PassiveAbility passive abilities}.
   *
   * @param e       entity damage by entity event
   * @param damagee interacting player
   */
  private void triggerDamageTakenPassives(EntityDamageByEntityEvent e, Player damagee) {
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId());
    Map<Abilities.SlotPassive, PassiveAbility> damageTakenTriggers = rpgPlayer.getAbilities().getTriggerPassives().get(PassiveTriggerType.DAMAGE_TAKEN);
    if (damageTakenTriggers.isEmpty() || !(e.getDamager() instanceof LivingEntity damager)) {
      return;
    }

    Random random = new Random();
    for (PassiveAbility ability : damageTakenTriggers.values()) {
      if (ability.isOnCooldown()) {
        continue;
      }
      double chance = Double.parseDouble(ability.getConditionData().get(0));
      if (chance > random.nextDouble() * 100) {
        boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
        UUID targetUUID;
        if (self) {
          targetUUID = damagee.getUniqueId();
        } else {
          targetUUID = damager.getUniqueId();
        }
        ability.doEffect(rpgPlayer, targetUUID);
      }
    }
  }

  /**
   * Processes damage done to the entity by the player.
   *
   * @param e       entity damage by entity event
   * @param damager interacting player
   */
  private void processPlayerDamageDone(EntityDamageByEntityEvent e, Player damager) {
    if (!(e.getEntity() instanceof LivingEntity damagee)) {
      return;
    }

    PersistentDataContainer dataContainer = damager.getPersistentDataContainer();
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(damager.getUniqueId());
    Buffs buffs = rpgPlayer.getBuffs();
    Random random = new Random();
    if (buffs == null) {
      ifCriticallyHit(e, dataContainer, random);
    } else {
      ifCriticallyHit(e, dataContainer, buffs, random);
    }

    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    UUID uuid = e.getEntity().getUniqueId();

    if (entityStatuses.containsKey(uuid)) {
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      ifFracture(e, damagee, statuses);
      ifVulnerable(e, statuses);
    }

    final double finalDamage = e.getDamage();
    e.setDamage(finalDamage);
  }

  /**
   * Processes damage taken by the player.
   *
   * @param e          entity damage by entity event
   * @param damagee    interacting player
   * @param mitigation {@link DamageMitigation}
   */
  private void processPlayerDamageTaken(EntityDamageByEntityEvent e, Player damagee, DamageMitigation mitigation) {
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId());
    Entity damager = e.getDamager();

    if (mitigateSpecificCauseDamage(e, damager, damagee, mitigation)) {
      e.setCancelled(true);
      return;
    }

    Random random = new Random();
    PersistentDataContainer dataContainer = damagee.getPersistentDataContainer();
    Buffs buffs = rpgPlayer.getBuffs();
    if (buffs == null) {
      if (ifCountered(e.getCause(), dataContainer, random, damager, damagee)) {
        e.setCancelled(true);
        return;
      } else if (ifDodged(dataContainer, random, damagee)) {
        e.setCancelled(true);
        return;
      } else if (ifTougher(e, dataContainer, damagee)) {
        e.setCancelled(true);
        return;
      }
    } else {
      if (ifCountered(e.getCause(), dataContainer, buffs, random, damager, damagee)) {
        e.setCancelled(true);
        return;
      } else if (ifDodged(dataContainer, buffs, random, damagee)) {
        e.setCancelled(true);
        return;
      } else if (ifTougher(e, dataContainer, buffs, damagee)) {
        e.setCancelled(true);
        return;
      }
    }

    e.setDamage(mitigation.mitigateArmorProtectionResistance(e.getDamage()));

    final double finalDamage = e.getDamage();
    damageArmorDurability(damagee, finalDamage);
    rpgPlayer.getHealth().damage(finalDamage);
    e.setDamage(0);
  }

  /**
   * Mitigates environmental damage taken based on the player's
   * {@link Enchantments}.
   *
   * @param e          entity damage event
   * @param cause      damage cause
   * @param damagee    player taking damage
   * @param mitigation {@link DamageMitigation}
   * @return if no damage is taken
   */
  private boolean mitigateEnvironmentalDamage(EntityDamageEvent e, EntityDamageEvent.DamageCause cause, Player damagee, DamageMitigation mitigation) {
    switch (cause) {
      case FALL -> e.setDamage(mitigation.mitigateFall(e.getDamage()));
      case DRAGON_BREATH, FLY_INTO_WALL, MAGIC, POISON, WITHER -> e.setDamage(mitigation.mitigateProtection(e.getDamage()));
      case FIRE, FIRE_TICK, HOT_FLOOR, LAVA -> e.setDamage(mitigation.mitigateFire(e.getDamage()));
      case BLOCK_EXPLOSION -> {
        e.setDamage(mitigation.mitigateExplosion(e.getDamage()));
        if (e.getDamage() <= 0) {
          return true;
        }
        RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId());
        PersistentDataContainer dataContainer = damagee.getPersistentDataContainer();
        Buffs buffs = rpgPlayer.getBuffs();
        if (buffs == null) {
          if (ifDodged(dataContainer, new Random(), damagee)) {
            return true;
          } else if (ifTougher(e, dataContainer, damagee)) {
            return true;
          }
        } else {
          if (ifDodged(dataContainer, buffs, new Random(), damagee)) {
            return true;
          } else if (ifTougher(e, dataContainer, buffs, damagee)) {
            return true;
          }
        }
        e.setDamage(mitigation.mitigateArmorProtection(e.getDamage()));
      }
    }
    e.setDamage(mitigation.mitigateResistance(e.getDamage()));
    return false;
  }

  /**
   * If the player dealt a critical hit, multiply the damage by its modifier.
   *
   * @param e             entity damage by entity event
   * @param dataContainer player's persistent tags
   * @param random        rng
   */
  private void ifCriticallyHit(EntityDamageByEntityEvent e, PersistentDataContainer dataContainer, Random random) {
    double criticalChanceBase = dataContainer.getOrDefault(Key.ATTRIBUTE_CRITICAL_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    if (criticalChanceBase > random.nextDouble() * 100) {
      LivingEntity entity = (LivingEntity) e.getEntity();
      World world = entity.getWorld();
      world.spawnParticle(Particle.CRIT, entity.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5);
      world.playSound(entity.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 0.65f, 1);
      double criticalDamageBase = dataContainer.getOrDefault(Key.ATTRIBUTE_CRITICAL_DAMAGE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      e.setDamage(e.getDamage() * (1.25 + (criticalDamageBase / 100)));
    }
  }

  /**
   * If the player dealt a critical hit, multiply the damage by its modifier.
   *
   * @param e             entity damage by entity event
   * @param dataContainer player's persistent tags
   * @param buffs         {@link Buffs}
   * @param random        rng
   */
  private void ifCriticallyHit(EntityDamageByEntityEvent e, PersistentDataContainer dataContainer, Buffs buffs, Random random) {
    double criticalChanceBase = dataContainer.getOrDefault(Key.ATTRIBUTE_CRITICAL_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    if (criticalChanceBase + buffs.getAethelAttributeBuff(AethelAttribute.CRITICAL_CHANCE) > random.nextDouble() * 100) {
      LivingEntity entity = (LivingEntity) e.getEntity();
      World world = entity.getWorld();
      world.spawnParticle(Particle.CRIT, entity.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5);
      world.playSound(entity.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 0.65f, 1);
      double criticalDamageBase = dataContainer.getOrDefault(Key.ATTRIBUTE_CRITICAL_DAMAGE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      e.setDamage(e.getDamage() * (1.25 + (criticalDamageBase + buffs.getAethelAttributeBuff(AethelAttribute.CRITICAL_DAMAGE)) / 100));
    }
  }

  /**
   * If the target has {@link StatusType#FRACTURE}, multiply the damage by its number of stacks.
   *
   * @param e        entity damage by entity event
   * @param damagee  entity taking damage
   * @param statuses entity's statuses
   */
  private void ifFracture(EntityDamageByEntityEvent e, LivingEntity damagee, Map<StatusType, Status> statuses) {
    if (statuses.containsKey(StatusType.FRACTURE)) {
      int armor = (int) damagee.getAttribute(Attribute.GENERIC_ARMOR).getValue();
      armor = armor - statuses.get(StatusType.FRACTURE).getStackAmount();
      double damage = e.getDamage();
      e.setDamage(damage - (damage * Math.min(armor * 0.2, .4)));
    }
  }

  /**
   * If the target has the {@link StatusType#VULNERABLE}, multiply the damage by its number of stacks.
   *
   * @param e        entity damage by entity event
   * @param statuses entity's statuses
   */
  private void ifVulnerable(EntityDamageByEntityEvent e, Map<StatusType, Status> statuses) {
    if (statuses.containsKey(StatusType.VULNERABLE)) {
      int vulnerable = statuses.get(StatusType.VULNERABLE).getStackAmount();
      e.setDamage(e.getDamage() * (1 + (vulnerable * 0.025)));
    }
  }

  /**
   * Mitigates specific cause damage taken based on their respective protection enchantment.
   *
   * @param e          entity damage by entity event
   * @param damager    damaging entity
   * @param damagee    player taking damage
   * @param mitigation {@link DamageMitigation}
   * @return if no damage taken or magic damage was taken/mitigated
   */
  private boolean mitigateSpecificCauseDamage(EntityDamageByEntityEvent e, Entity damager, Player damagee, DamageMitigation mitigation) {
    switch (e.getCause()) {
      case ENTITY_EXPLOSION -> {
        e.setDamage(mitigation.mitigateExplosion(e.getDamage()));
        if (e.getDamage() <= 0) {
          return true;
        }
      }
      case MAGIC -> {
        e.setDamage(mitigation.mitigateProtectionResistance(e.getDamage()));
        Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId()).getHealth().damage(e.getDamage());
        e.setDamage(0);
        return true;
      }
      case PROJECTILE -> {
        int projectileProtection = Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId()).getEnchantments().getTotalEnchantments().get(Enchantment.PROTECTION_PROJECTILE);
        if (projectileProtection >= 10) {
          PlayerInventory pInv = damagee.getInventory();
          switch (damager.getType()) {
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
          e.setDamage(mitigation.mitigateProjectile(e.getDamage()));
        }
      }
    }
    if (damager.getType() == EntityType.AREA_EFFECT_CLOUD) {
      e.setDamage(mitigation.mitigateProtectionResistance(e.getDamage()));
      Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId()).getHealth().damage(e.getDamage());
      e.setDamage(0);
      return true;
    }
    return false;
  }

  /**
   * Ignores damage taken if the player killed the damager by counterattacks.
   * <p>
   * The number of counterattacks done is based on the player's attack speed.
   * <p>
   * Projectile attacks cannot trigger counterattacks.
   *
   * @param cause         damage cause
   * @param dataContainer player's persistent tags
   * @param random        rng
   * @param damager       damager
   * @param damagee       player taking damage
   * @return if the damager died
   */
  private boolean ifCountered(EntityDamageEvent.DamageCause cause, PersistentDataContainer dataContainer, Random random, Entity damager, Player damagee) {
    if (cause == EntityDamageEvent.DamageCause.PROJECTILE || !(damager instanceof LivingEntity attacker)) {
      return false;
    }

    double counterChanceBase = dataContainer.getOrDefault(Key.ATTRIBUTE_COUNTER_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    if (counterChanceBase > random.nextDouble() * 100) {
      World world = attacker.getWorld();
      world.spawnParticle(Particle.FIREWORKS_SPARK, attacker.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
      world.playSound(attacker.getEyeLocation(), Sound.ENTITY_ALLAY_HURT, SoundCategory.PLAYERS, 0.5f, 0.75f);
      attacker.damage((int) damagee.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue() * damagee.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());
      return attacker.getHealth() <= 0.0;
    }
    return false;
  }

  /**
   * Ignores damage taken if the player killed the damager by counterattacks.
   * <p>
   * The number of counterattacks done is based on the player's attack speed.
   * <p>
   * Projectile attacks cannot trigger counterattacks.
   *
   * @param cause         damage cause
   * @param dataContainer player's persistent tags
   * @param buffs         {@link Buffs}
   * @param random        rng
   * @param damager       damager
   * @param damagee       player taking damage
   * @return if the damager died
   */
  private boolean ifCountered(EntityDamageEvent.DamageCause cause, PersistentDataContainer dataContainer, Buffs buffs, Random random, Entity damager, Player damagee) {
    if (cause == EntityDamageEvent.DamageCause.PROJECTILE || !(damager instanceof LivingEntity attacker)) {
      return false;
    }

    double counterChanceBase = dataContainer.getOrDefault(Key.ATTRIBUTE_COUNTER_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    if (counterChanceBase + buffs.getAethelAttributeBuff(AethelAttribute.COUNTER_CHANCE) > random.nextDouble() * 100) {
      World world = attacker.getWorld();
      world.spawnParticle(Particle.FIREWORKS_SPARK, attacker.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
      world.playSound(attacker.getEyeLocation(), Sound.ENTITY_ALLAY_HURT, SoundCategory.PLAYERS, 0.65f, 0.75f);
      attacker.damage((int) damagee.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue() * damagee.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());
      return attacker.getHealth() <= 0.0;
    }
    return false;
  }

  /**
   * Ignore damage taken if the player dodged.
   *
   * @param dataContainer player's persistent tags
   * @param random        rng
   * @param damagee       player taking damage
   * @return if dodged
   */
  private boolean ifDodged(PersistentDataContainer dataContainer, Random random, Player damagee) {
    double dodgeChanceBase = dataContainer.getOrDefault(Key.ATTRIBUTE_DODGE_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    if (dodgeChanceBase > random.nextDouble() * 100) {
      World world = damagee.getWorld();
      world.spawnParticle(Particle.EXPLOSION_NORMAL, damagee.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
      world.playSound(damagee.getEyeLocation(), Sound.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 0.65f, 0);
      return true;
    }
    return false;
  }

  /**
   * Ignore damage taken if the player dodged.
   *
   * @param dataContainer player's persistent tags
   * @param buffs         {@link Buffs}
   * @param random        rng
   * @param damagee       player taking damage
   * @return if dodged
   */
  private boolean ifDodged(PersistentDataContainer dataContainer, Buffs buffs, Random random, Player damagee) {
    double dodgeChanceBase = dataContainer.getOrDefault(Key.ATTRIBUTE_DODGE_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    if (dodgeChanceBase + buffs.getAethelAttributeBuff(AethelAttribute.DODGE_CHANCE) > random.nextDouble() * 100) {
      World world = damagee.getWorld();
      world.spawnParticle(Particle.EXPLOSION_NORMAL, damagee.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
      world.playSound(damagee.getEyeLocation(), Sound.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 0.65f, 0);
      return true;
    }
    return false;
  }

  /**
   * Ignore damage taken if the player's toughness is higher,
   * otherwise toughness mitigates damage by a flat amount.
   *
   * @param e             entity damage event
   * @param dataContainer player's persistent tags
   * @param damagee       player taking damage
   * @return if tougher than damage
   */
  private boolean ifTougher(EntityDamageEvent e, PersistentDataContainer dataContainer, Player damagee) {
    double toughnessBase = dataContainer.getOrDefault(Key.ATTRIBUTE_ARMOR_TOUGHNESS.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double toughness = damagee.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + toughnessBase;
    e.setDamage(Math.max(e.getDamage() - (toughness / 2), 0));
    if (e.getDamage() == 0) {
      World world = damagee.getWorld();
      world.spawnParticle(Particle.END_ROD, damagee.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
      world.playSound(damagee.getEyeLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 1, 0);
      return true;
    }
    return false;
  }

  /**
   * Ignore damage taken if the player's toughness is higher,
   * otherwise toughness mitigates damage by a flat amount.
   *
   * @param e             entity damage event
   * @param dataContainer player's persistent tags
   * @param buffs         {@link Buffs}
   * @param damagee       player taking damage
   * @return if tougher than damage
   */
  private boolean ifTougher(EntityDamageEvent e, PersistentDataContainer dataContainer, Buffs buffs, Player damagee) {
    double toughnessBase = dataContainer.getOrDefault(Key.ATTRIBUTE_ARMOR_TOUGHNESS.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double toughness = damagee.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + toughnessBase + buffs.getAethelAttributeBuff(AethelAttribute.ARMOR_TOUGHNESS);
    e.setDamage(Math.max(e.getDamage() - (toughness / 2), 0));
    if (e.getDamage() == 0) {
      World world = damagee.getWorld();
      world.spawnParticle(Particle.END_ROD, damagee.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
      world.playSound(damagee.getEyeLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 1, 0);
      return true;
    }
    return false;
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
