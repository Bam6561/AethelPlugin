package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.rpg.*;
import me.dannynguyen.aethel.rpg.abilities.Abilities;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import me.dannynguyen.aethel.utils.item.ItemDurability;
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
import org.bukkit.potion.PotionType;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Collection of damage done, taken, and healed listeners.
 *
 * @author Danny Nguyen
 * @version 1.21.0
 * @since 1.9.4
 */
public class HealthEvent implements Listener {
  /**
   * Ignored damage causes.
   */
  private static final Set<EntityDamageEvent.DamageCause> ignoredDamageCauses = Set.of(EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.KILL);

  /**
   * No parameter constructor.
   */
  public HealthEvent() {
  }

  /**
   * Calculates player damage dealt and taken interactions.
   *
   * @param e entity damage event
   */
  @EventHandler
  private void onEntityDamage(EntityDamageEvent e) {
    if (e instanceof EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player || event.getEntity() instanceof Player) {
        if (event.getDamager() instanceof Player damager && !(event.getEntity() instanceof Player)) { // PvE
          triggerDamageDealtPassives(event, damager);
          calculatePlayerDamageDone(event, damager);
        } else {
          Player damagee = (Player) event.getEntity();
          DamageMitigation mitigation = new DamageMitigation(damagee);
          if (event.getDamager() instanceof Player damager) { // PvP, otherwise EvP
            triggerDamageDealtPassives(event, damager);
            calculatePlayerDamageDone(event, damager);
          }
          triggerDamageTakenPassives(event, damagee);
          calculatePlayerDamageTaken(event, damagee, mitigation);
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
   * Calculates damage healed by players.
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
   * Calculates damage done to the entity by the player.
   *
   * @param e       entity damage by entity event
   * @param damager interacting player
   */
  private void calculatePlayerDamageDone(EntityDamageByEntityEvent e, Player damager) {
    if (!(e.getEntity() instanceof LivingEntity damagee)) {
      return;
    }

    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(damager.getUniqueId());
    Map<AethelAttribute, Double> attributes = rpgPlayer.getAethelAttributes().getAttributes();
    Map<AethelAttribute, Double> buffs = rpgPlayer.getBuffs().getAethelAttributes();
    Random random = new Random();
    ifCriticallyHit(e, buffs, attributes, random);

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
   * Calculates damage taken by the player.
   *
   * @param e          entity damage by entity event
   * @param damagee    interacting player
   * @param mitigation {@link DamageMitigation}
   */
  private void calculatePlayerDamageTaken(EntityDamageByEntityEvent e, Player damagee, DamageMitigation mitigation) {
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId());
    Entity damager = e.getDamager();

    if (mitigateSpecificCauseDamage(e, damager, damagee, mitigation)) {
      e.setCancelled(true);
      return;
    }

    Map<AethelAttribute, Double> buffs = rpgPlayer.getBuffs().getAethelAttributes();
    Map<AethelAttribute, Double> attributes = rpgPlayer.getAethelAttributes().getAttributes();
    Random random = new Random();

    if (ifCountered(e.getCause(), attributes, buffs, random, damager, damagee)) {
      e.setCancelled(true);
      return;
    } else if (ifDodged(attributes, buffs, random)) {
      e.setCancelled(true);
      return;
    } else if (ifTougher(e, attributes, buffs, damagee)) {
      e.setCancelled(true);
      return;
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
        Map<AethelAttribute, Double> buffs = rpgPlayer.getBuffs().getAethelAttributes();
        Map<AethelAttribute, Double> attributes = rpgPlayer.getAethelAttributes().getAttributes();
        if (ifDodged(attributes, buffs, new Random())) {
          return true;
        } else if (ifTougher(e, attributes, buffs, damagee)) {
          return true;
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
   * @param e          entity damage by entity event
   * @param attributes {@link AethelAttributes}
   * @param buffs      {@link Buffs}
   * @param random     rng
   */
  private void ifCriticallyHit(EntityDamageByEntityEvent e, Map<AethelAttribute, Double> attributes, Map<AethelAttribute, Double> buffs, Random random) {
    double criticalChance = attributes.get(AethelAttribute.CRITICAL_CHANCE) + buffs.get(AethelAttribute.CRITICAL_CHANCE);
    if (criticalChance > random.nextDouble() * 100) {
      double criticalDamage = 1.25 + attributes.get(AethelAttribute.CRITICAL_DAMAGE) + buffs.get(AethelAttribute.CRITICAL_DAMAGE);
      e.setDamage(e.getDamage() * (criticalDamage / 100));
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
   * @param cause      damage cause
   * @param attributes {@link AethelAttributes}
   * @param buffs      {@link Buffs}
   * @param random     rng
   * @param damager    damager
   * @param damagee    player taking damage
   * @return if the damager died
   */
  private boolean ifCountered(EntityDamageEvent.DamageCause cause, Map<AethelAttribute, Double> attributes, Map<AethelAttribute, Double> buffs, Random random, Entity damager, Player damagee) {
    if (cause == EntityDamageEvent.DamageCause.PROJECTILE || !(damager instanceof LivingEntity attacker)) {
      return false;
    }

    double counterChance = attributes.get(AethelAttribute.COUNTER_CHANCE) + buffs.get(AethelAttribute.COUNTER_CHANCE);
    if (counterChance > random.nextDouble() * 100) {
      attacker.damage((int) damagee.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue() * damagee.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());
      return attacker.getHealth() <= 0.0;
    }
    return false;
  }

  /**
   * Ignore damage taken if the player dodged.
   *
   * @param attributes {@link AethelAttributes}
   * @param buffs      {@link Buffs}
   * @param random     rng
   * @return if dodged
   */
  private boolean ifDodged(Map<AethelAttribute, Double> attributes, Map<AethelAttribute, Double> buffs, Random random) {
    double dodgeChance = attributes.get(AethelAttribute.DODGE_CHANCE) + buffs.get(AethelAttribute.DODGE_CHANCE);
    return dodgeChance > random.nextDouble() * 100;
  }

  /**
   * Ignore damage taken if the player's toughness is higher,
   * otherwise toughness mitigates damage by a flat amount.
   *
   * @param e          entity damage event
   * @param attributes {@link AethelAttributes}
   * @param buffs      {@link Buffs}
   * @param damagee    player taking damage
   * @return if tougher than damage
   */
  private boolean ifTougher(EntityDamageEvent e, Map<AethelAttribute, Double> attributes, Map<AethelAttribute, Double> buffs, Player damagee) {
    double toughness = damagee.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + attributes.get(AethelAttribute.ARMOR_TOUGHNESS) + buffs.get(AethelAttribute.ARMOR_TOUGHNESS);
    e.setDamage(Math.max(e.getDamage() - (toughness / 2), 0));
    return e.getDamage() == 0;
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
