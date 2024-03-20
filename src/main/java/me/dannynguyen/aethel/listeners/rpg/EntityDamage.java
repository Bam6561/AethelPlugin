package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.rpg.*;
import me.dannynguyen.aethel.systems.rpg.ability.PassiveAbility;
import me.dannynguyen.aethel.systems.rpg.ability.PassiveAbilityTrigger;
import me.dannynguyen.aethel.systems.rpg.ability.SlotPassiveAbility;
import me.dannynguyen.aethel.systems.rpg.ability.Trigger;
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
import org.bukkit.potion.PotionType;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Entity damage done, taken, and healed listener.
 *
 * @author Danny Nguyen
 * @version 1.17.0
 * @since 1.9.4
 */
public class EntityDamage implements Listener {
  /**
   * Ignored damage causes.
   */
  private static final Set<EntityDamageEvent.DamageCause> ignoredDamageCauses = Set.of(EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.KILL);

  /**
   * No parameter constructor.
   */
  public EntityDamage() {
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
          PlayerDamageMitigation mitigation = new PlayerDamageMitigation(damagee);
          if (event.getDamager() instanceof Player damager) { // PvP, otherwise EvP
            triggerDamageDealtPassives(event, damager);
            calculatePlayerDamageDone(event, damager);
          }
          triggerDamageTakenPassives(event, damagee);
          calculatePlayerDamageTaken(event, damagee, mitigation);
        }
      }
    } else if (e.getEntity() instanceof Player damagee && !ignoredDamageCauses.contains(e.getCause())) {
      PlayerDamageMitigation mitigation = new PlayerDamageMitigation(damagee);
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
   * Triggers damage dealt passive abilities.
   *
   * @param e       entity damage by entity event
   * @param damager interacting player
   */
  private void triggerDamageDealtPassives(EntityDamageByEntityEvent e, Player damager) {
    if (damager.getAttackCooldown() >= 0.75 && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
      Map<SlotPassiveAbility, PassiveAbility> damageDealtTriggers = Plugin.getData().getRpgSystem().getRpgPlayers().get(damager.getUniqueId()).getEquipment().getTriggerPassives().get(Trigger.DAMAGE_DEALT);
      if (!damageDealtTriggers.isEmpty()) {
        if (e.getEntity() instanceof LivingEntity damagee) {
          Random random = new Random();
          for (PassiveAbility ability : damageDealtTriggers.values()) {
            if (!ability.isOnCooldown()) {
              switch (ability.getAbilityType().getEffect()) {
                case STACK_INSTANCE -> readOnDamageStackInstance(random, ability, damager.getUniqueId(), damagee.getUniqueId());
                case CHAIN_DAMAGE -> readOnDamageChainDamage(random, ability, damager.getUniqueId(), damagee.getUniqueId());
              }
            }
          }
        }
      }
    }
  }

  /**
   * Triggers damage taken passive abilities.
   *
   * @param e       entity damage by entity event
   * @param damagee interacting player
   */
  private void triggerDamageTakenPassives(EntityDamageByEntityEvent e, Player damagee) {
    Map<SlotPassiveAbility, PassiveAbility> damageTakenTriggers = Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId()).getEquipment().getTriggerPassives().get(Trigger.DAMAGE_TAKEN);
    if (!damageTakenTriggers.isEmpty()) {
      if (e.getDamager() instanceof LivingEntity damager) {
        Random random = new Random();
        for (PassiveAbility ability : damageTakenTriggers.values()) {
          if (!ability.isOnCooldown()) {
            switch (ability.getAbilityType().getEffect()) {
              case STACK_INSTANCE -> readOnDamageStackInstance(random, ability, damagee.getUniqueId(), damager.getUniqueId());
              case CHAIN_DAMAGE -> readOnDamageChainDamage(random, ability, damager.getUniqueId(), damagee.getUniqueId());
            }
          }
        }
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
    Map<AethelAttribute, Double> attributes = Plugin.getData().getRpgSystem().getRpgPlayers().get(damager.getUniqueId()).getAethelAttributes();
    Random random = new Random();
    ifCriticallyHit(e, attributes, random);
    ifVulnerable(e);
    final double finalDamage = e.getDamage();
    e.setDamage(finalDamage);
  }

  /**
   * Calculates damage taken by the player.
   *
   * @param e          entity damage by entity event
   * @param damagee    interacting player
   * @param mitigation player damage mitigation
   */
  private void calculatePlayerDamageTaken(EntityDamageByEntityEvent e, Player damagee, PlayerDamageMitigation mitigation) {
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId());
    Entity damager = e.getDamager();

    if (mitigateSpecificCauseDamage(e, damager, damagee, mitigation)) {
      e.setCancelled(true);
      return;
    }

    Map<AethelAttribute, Double> attributes = rpgPlayer.getAethelAttributes();
    Random random = new Random();

    if (ifCountered(e.getCause(), attributes, random, damager, damagee)) {
      e.setCancelled(true);
      return;
    } else if (ifDodged(attributes, random)) {
      e.setCancelled(true);
      return;
    } else if (ifTougher(e, attributes, damagee)) {
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
   * Mitigates environmental damage taken based on the player's environmental protection enchantments.
   *
   * @param e          entity damage event
   * @param cause      damage cause
   * @param damagee    player taking damage
   * @param mitigation player damage mitigation
   * @return if no damage is taken
   */
  private boolean mitigateEnvironmentalDamage(EntityDamageEvent e, EntityDamageEvent.DamageCause cause, Player damagee, PlayerDamageMitigation mitigation) {
    switch (cause) {
      case FALL -> e.setDamage(mitigation.mitigateFall(e.getDamage()));
      case DRAGON_BREATH, FLY_INTO_WALL, MAGIC, POISON, WITHER -> e.setDamage(mitigation.mitigateProtection(e.getDamage()));
      case FIRE, FIRE_TICK, HOT_FLOOR, LAVA -> e.setDamage(mitigation.mitigateFire(e.getDamage()));
      case BLOCK_EXPLOSION -> {
        e.setDamage(mitigation.mitigateExplosion(e.getDamage()));
        if (e.getDamage() <= 0) {
          return true;
        }
        Map<AethelAttribute, Double> attributes = Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId()).getAethelAttributes();
        if (ifDodged(attributes, new Random())) {
          return true;
        } else if (ifTougher(e, attributes, damagee)) {
          return true;
        }
        e.setDamage(mitigation.mitigateArmorProtection(e.getDamage()));
      }
    }
    e.setDamage(mitigation.mitigateResistance(e.getDamage()));
    return false;
  }

  /**
   * Checks if the stack instance effect was successful before applying stack instances.
   *
   * @param random    rng
   * @param ability   passive ability
   * @param selfUUID  self UUID
   * @param otherUUID entity UUID
   */
  private void readOnDamageStackInstance(Random random, PassiveAbility ability, UUID selfUUID, UUID otherUUID) {
    double chance = Double.parseDouble(ability.getTriggerData().get(0));
    if (chance > random.nextDouble() * 100) {
      boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
      UUID targetUUID;
      if (self) {
        targetUUID = selfUUID;
      } else {
        targetUUID = otherUUID;
      }
      new PassiveAbilityTrigger(ability).applyStackInstance(targetUUID);
    }
  }

  /**
   * Checks if the chain damage effect was successful before dealing chain damage.
   *
   * @param random    rng
   * @param ability   passive ability
   * @param selfUUID  self UUID
   * @param otherUUID entity UUID
   */
  private void readOnDamageChainDamage(Random random, PassiveAbility ability, UUID selfUUID, UUID otherUUID) {
    double chance = Double.parseDouble(ability.getTriggerData().get(0));

    if (chance > random.nextDouble() * 100) {
      boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
      UUID targetUUID;
      if (self) {
        targetUUID = selfUUID;
      } else {
        targetUUID = otherUUID;
      }
      new PassiveAbilityTrigger(ability).chainDamage(targetUUID);
    }
  }

  /**
   * If the player dealt a critical hit, multiply the damage by its modifier.
   *
   * @param e          entity damage by entity event
   * @param attributes player's attributes
   * @param random     rng
   */
  private void ifCriticallyHit(EntityDamageByEntityEvent e, Map<AethelAttribute, Double> attributes, Random random) {
    if (attributes.get(AethelAttribute.CRITICAL_CHANCE) > random.nextDouble() * 100) {
      e.setDamage(e.getDamage() * (1.25 + (attributes.get(AethelAttribute.CRITICAL_DAMAGE) / 100)));
    }
  }

  /**
   * If the target has the Vulnerable status, multiply the damage by its number of stacks.
   *
   * @param e entity damage by entity event
   */
  private void ifVulnerable(EntityDamageByEntityEvent e) {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    UUID uuid = e.getEntity().getUniqueId();
    if (entityStatuses.containsKey(uuid)) {
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      if (statuses.containsKey(StatusType.VULNERABLE)) {
        int vulnerable = statuses.get(StatusType.VULNERABLE).getStackAmount();
        e.setDamage(e.getDamage() * (1 + (vulnerable * 0.025)));
      }
    }
  }

  /**
   * Mitigates specific cause damage taken based on their respective protection enchantment.
   *
   * @param e          entity damage by entity event
   * @param damager    damaging entity
   * @param damagee    player taking damage
   * @param mitigation player damage mitigation
   * @return if no damage taken or magic damage was taken/mitigated
   */
  private boolean mitigateSpecificCauseDamage(EntityDamageByEntityEvent e, Entity damager, Player damagee, PlayerDamageMitigation mitigation) {
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
        int projectileProtection = Plugin.getData().getRpgSystem().getRpgPlayers().get(damagee.getUniqueId()).getEquipment().getTotalEnchantments().get(Enchantment.PROTECTION_PROJECTILE);
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
   * Projectile attacks cannot trigger counter attacks.
   * </p>
   *
   * @param cause      damage cause
   * @param attributes player's attributes
   * @param random     rng
   * @param damager    damager
   * @param damagee    player taking damage
   * @return if the damager died
   */
  private boolean ifCountered(EntityDamageEvent.DamageCause cause, Map<AethelAttribute, Double> attributes, Random random, Entity damager, Player damagee) {
    if (cause != EntityDamageEvent.DamageCause.PROJECTILE && damager instanceof LivingEntity attacker) {
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
   * @param e          entity damage event
   * @param attributes player's attributes
   * @param damagee    player taking damage
   * @return if tougher than damage
   */
  private boolean ifTougher(EntityDamageEvent e, Map<AethelAttribute, Double> attributes, Player damagee) {
    double toughness = damagee.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + attributes.get(AethelAttribute.ARMOR_TOUGHNESS);
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
