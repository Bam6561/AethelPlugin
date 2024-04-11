package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.rpg.*;
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
 * @version 1.22.11
 * @since 1.9.4
 */
public class DamageListener implements Listener {
  /**
   * Ignored damage causes.
   */
  private static final Set<EntityDamageEvent.DamageCause> ignoredDamageCauses = Set.of(EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.KILL);

  /**
   * No parameter constructor.
   */
  public DamageListener() {
  }

  /**
   * Processes damage dealt and taken interactions.
   *
   * @param e entity damage event
   */
  @EventHandler
  private void onEntityDamage(EntityDamageEvent e) {
    if (e instanceof EntityDamageByEntityEvent event) {
      if (!(e.getEntity() instanceof LivingEntity)) {
        return;
      }

      if (event.getDamager() instanceof Player attacker) {
        triggerDamageDealtPassives(event, attacker);
      }
      if (event.getEntity() instanceof Player defender) {
        triggerDamageTakenPassives(event, defender);
      }

      calculateDamageDealt(event);
    } else if (e.getEntity() instanceof Player defender && !ignoredDamageCauses.contains(e.getCause())) {
      EntityDamageEvent.DamageCause cause = e.getCause();
      if (mitigateEnvironmentalDamage(e, cause, defender)) {
        e.setCancelled(true);
        return;
      }
      final double finalDamage = e.getDamage();
      switch (cause) {
        case BLOCK_EXPLOSION, CONTACT, FIRE, HOT_FLOOR, LAVA -> damageArmorDurability(defender, finalDamage);
      }
      Plugin.getData().getRpgSystem().getRpgPlayers().get(defender.getUniqueId()).getHealth().damage(finalDamage);
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
   * @param e        entity damage by entity event
   * @param attacker attacking player
   */
  private void triggerDamageDealtPassives(EntityDamageByEntityEvent e, Player attacker) {
    if (attacker.getAttackCooldown() < 0.75 || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK || !(e.getEntity() instanceof LivingEntity defender)) {
      return;
    }

    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(attacker.getUniqueId());
    Map<Equipment.Abilities.SlotPassive, PassiveAbility> damageDealtTriggers = rpgPlayer.getEquipment().getAbilities().getTriggerPassives().get(PassiveTriggerType.DAMAGE_DEALT);
    if (damageDealtTriggers.isEmpty()) {
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
          targetUUID = attacker.getUniqueId();
        } else {
          targetUUID = defender.getUniqueId();
        }
        ability.doEffect(rpgPlayer.getUUID(), targetUUID);
      }
    }
  }

  /**
   * Triggers {@link PassiveTriggerType#DAMAGE_TAKEN} {@link PassiveAbility passive abilities}.
   *
   * @param e        entity damage by entity event
   * @param defender defending player
   */
  private void triggerDamageTakenPassives(EntityDamageByEntityEvent e, Player defender) {
    if (!(e.getDamager() instanceof LivingEntity attacker)) {
      return;
    }

    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(defender.getUniqueId());
    Map<Equipment.Abilities.SlotPassive, PassiveAbility> damageTakenTriggers = rpgPlayer.getEquipment().getAbilities().getTriggerPassives().get(PassiveTriggerType.DAMAGE_TAKEN);
    if (damageTakenTriggers.isEmpty()) {
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
          targetUUID = defender.getUniqueId();
        } else {
          targetUUID = attacker.getUniqueId();
        }
        ability.doEffect(rpgPlayer.getUUID(), targetUUID);
      }
    }
  }

  /**
   * Calculates damage dealt.
   *
   * @param e entity damage by entity event
   */
  private void calculateDamageDealt(EntityDamageByEntityEvent e) {
    RpgSystem rpgSystem = Plugin.getData().getRpgSystem();
    Entity attacker = e.getDamager();
    LivingEntity defender = (LivingEntity) e.getEntity();
    DamageMitigation mitigation = new DamageMitigation(defender);

    if (e.getDamager() instanceof LivingEntity livingAttacker) {
      PersistentDataContainer entityTags = livingAttacker.getPersistentDataContainer();
      Buffs buffs = rpgSystem.getBuffs().get(livingAttacker.getUniqueId());
      Random random = new Random();

      ifCriticallyHit(e, entityTags, buffs, random);

      Map<StatusType, Status> statuses = rpgSystem.getStatuses().get(e.getEntity().getUniqueId());
      if (statuses != null) {
        ifFracture(e, defender, statuses);
        ifVulnerable(e, statuses);
      }
    }

    if (defender instanceof Player defendingPlayer && mitigateSpecificCauseDamage(e, attacker, defendingPlayer, mitigation)) {
      e.setCancelled(true);
      return;
    }

    Random random = new Random();
    PersistentDataContainer entityTags = defender.getPersistentDataContainer();
    Buffs buffs = rpgSystem.getBuffs().get(defender.getUniqueId());

    if (ifCountered(e.getCause(), entityTags, buffs, random, attacker, defender)) {
      e.setCancelled(true);
      return;
    } else if (ifDodged(entityTags, buffs, random, defender)) {
      e.setCancelled(true);
      return;
    } else if (ifTougher(e, entityTags, buffs, defender)) {
      e.setCancelled(true);
      return;
    }

    e.setDamage(mitigation.mitigateArmorProtectionResistance(e.getDamage()));

    final double finalDamage = e.getDamage();
    if (defender instanceof Player defendingPlayer) {
      damageArmorDurability(defendingPlayer, finalDamage);
      rpgSystem.getRpgPlayers().get(defender.getUniqueId()).getHealth().damage(finalDamage);
    } else {
      defender.setHealth(Math.max(0, defender.getHealth() - finalDamage));
    }
    e.setDamage(0);
  }

  /**
   * Mitigates environmental damage taken based on the player's {@link Equipment.Enchantments}.
   *
   * @param e        entity damage event
   * @param cause    damage cause
   * @param defender defending player
   * @return if no damage is taken
   */
  private boolean mitigateEnvironmentalDamage(EntityDamageEvent e, EntityDamageEvent.DamageCause cause, Player defender) {
    DamageMitigation mitigation = new DamageMitigation(defender);
    switch (cause) {
      case FALL -> e.setDamage(mitigation.mitigateFall(e.getDamage()));
      case DRAGON_BREATH, FLY_INTO_WALL, MAGIC, POISON, WITHER -> e.setDamage(mitigation.mitigateProtection(e.getDamage()));
      case FIRE, FIRE_TICK, HOT_FLOOR, LAVA -> e.setDamage(mitigation.mitigateFire(e.getDamage()));
      case BLOCK_EXPLOSION -> {
        e.setDamage(mitigation.mitigateExplosion(e.getDamage()));
        if (e.getDamage() <= 0) {
          return true;
        }

        PersistentDataContainer entityTags = defender.getPersistentDataContainer();
        Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(defender.getUniqueId());

        if (ifDodged(entityTags, buffs, new Random(), defender)) {
          return true;
        } else if (ifTougher(e, entityTags, buffs, defender)) {
          return true;
        }
        e.setDamage(mitigation.mitigateArmorProtection(e.getDamage()));
      }
    }
    e.setDamage(mitigation.mitigateResistance(e.getDamage()));
    return false;
  }

  /**
   * If the attacker dealt a critical hit, multiply the damage by its modifier.
   *
   * @param e          entity damage by entity event
   * @param entityTags entity's persistent tags
   * @param buffs      {@link Buffs}
   * @param random     rng
   */
  private void ifCriticallyHit(EntityDamageByEntityEvent e, PersistentDataContainer entityTags, Buffs buffs, Random random) {
    double criticalChanceBase = entityTags.getOrDefault(Key.ATTRIBUTE_CRITICAL_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double criticalChanceBuff = 0.0;
    if (buffs != null) {
      criticalChanceBuff = buffs.getAethelAttribute(AethelAttribute.CRITICAL_CHANCE);
    }

    if (criticalChanceBase + criticalChanceBuff > random.nextDouble() * 100) {
      LivingEntity entity = (LivingEntity) e.getEntity();
      World world = entity.getWorld();
      world.spawnParticle(Particle.CRIT, entity.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5);
      world.playSound(entity.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 0.65f, 1);

      double criticalDamageBase = entityTags.getOrDefault(Key.ATTRIBUTE_CRITICAL_DAMAGE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      double criticalDamageBuff = 0.0;
      if (buffs != null) {
        criticalDamageBuff = buffs.getAethelAttribute(AethelAttribute.CRITICAL_DAMAGE);
      }

      e.setDamage(e.getDamage() * (1.25 + (criticalDamageBase + criticalDamageBuff) / 100));
    }
  }

  /**
   * If the target has {@link StatusType#FRACTURE}, multiply the damage by its number of stacks.
   *
   * @param e        entity damage by entity event
   * @param defender defending entity
   * @param statuses entity's statuses
   */
  private void ifFracture(EntityDamageByEntityEvent e, LivingEntity defender, Map<StatusType, Status> statuses) {
    if (statuses.containsKey(StatusType.FRACTURE)) {
      int armor = (int) defender.getAttribute(Attribute.GENERIC_ARMOR).getValue();
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
   * @param attacker   attacking entity
   * @param defender   defending player
   * @param mitigation {@link DamageMitigation}
   * @return if no damage taken or magic damage was taken/mitigated
   */
  private boolean mitigateSpecificCauseDamage(EntityDamageByEntityEvent e, Entity attacker, Player defender, DamageMitigation mitigation) {
    switch (e.getCause()) {
      case ENTITY_EXPLOSION -> {
        e.setDamage(mitigation.mitigateExplosion(e.getDamage()));
        if (e.getDamage() <= 0) {
          return true;
        }
      }
      case MAGIC -> {
        e.setDamage(mitigation.mitigateProtectionResistance(e.getDamage()));
        Plugin.getData().getRpgSystem().getRpgPlayers().get(defender.getUniqueId()).getHealth().damage(e.getDamage());
        e.setDamage(0);
        return true;
      }
      case PROJECTILE -> {
        int projectileProtection = Plugin.getData().getRpgSystem().getRpgPlayers().get(defender.getUniqueId()).getEquipment().getEnchantments().getTotalEnchantments().get(Enchantment.PROTECTION_PROJECTILE);
        if (projectileProtection >= 10) {
          PlayerInventory pInv = defender.getInventory();
          switch (attacker.getType()) {
            case ARROW -> {
              PotionType potionType = ((Arrow) attacker).getBasePotionType();
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
    if (attacker.getType() == EntityType.AREA_EFFECT_CLOUD) {
      e.setDamage(mitigation.mitigateProtectionResistance(e.getDamage()));
      Plugin.getData().getRpgSystem().getRpgPlayers().get(defender.getUniqueId()).getHealth().damage(e.getDamage());
      e.setDamage(0);
      return true;
    }
    return false;
  }

  /**
   * Ignores damage taken if the defender killed the attacker by counterattacks.
   * <p>
   * The number of counterattacks done is based on the defender's attack speed.
   * <p>
   * Projectile attacks cannot trigger counterattacks.
   *
   * @param cause      damage cause
   * @param entityTags entity's persistent tags
   * @param buffs      {@link Buffs}
   * @param random     rng
   * @param attacker   attacking entity
   * @param defender   defending entity
   * @return if the attacker died
   */
  private boolean ifCountered(EntityDamageEvent.DamageCause cause, PersistentDataContainer entityTags, Buffs buffs, Random random, Entity attacker, LivingEntity defender) {
    if (cause == EntityDamageEvent.DamageCause.PROJECTILE || !(attacker instanceof LivingEntity livingAttacker)) {
      return false;
    }

    double counterChanceBase = entityTags.getOrDefault(Key.ATTRIBUTE_COUNTER_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double counterChanceBuff = 0.0;
    if (buffs != null) {
      counterChanceBuff = buffs.getAethelAttribute(AethelAttribute.COUNTER_CHANCE);
    }

    if (counterChanceBase + counterChanceBuff > random.nextDouble() * 100) {
      World world = livingAttacker.getWorld();
      world.spawnParticle(Particle.FIREWORKS_SPARK, livingAttacker.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
      world.playSound(livingAttacker.getEyeLocation(), Sound.ENTITY_ALLAY_HURT, SoundCategory.PLAYERS, 0.65f, 0.75f);

      int attackSpeed;
      if (defender.getAttribute(Attribute.GENERIC_ATTACK_SPEED) != null) {
        attackSpeed = (int) defender.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue();
      } else {
        attackSpeed = 1;
      }

      double counterDamage = attackSpeed * defender.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();

      livingAttacker.damage(0.01);
      if (livingAttacker instanceof Player) {
        Health health = Plugin.getData().getRpgSystem().getRpgPlayers().get(livingAttacker.getUniqueId()).getHealth();
        health.damage(counterDamage);
        return health.getCurrentHealth() <= 0.0;
      } else {
        livingAttacker.setHealth(Math.max(0, livingAttacker.getHealth() - counterDamage));
        return livingAttacker.getHealth() == 0.0;
      }
    }
    return false;
  }

  /**
   * Ignore damage taken if the entity dodged.
   *
   * @param entityTags entity's persistent tags
   * @param buffs      {@link Buffs}
   * @param random     rng
   * @param defender   defending entity
   * @return if dodged
   */
  private boolean ifDodged(PersistentDataContainer entityTags, Buffs buffs, Random random, LivingEntity defender) {
    double dodgeChanceBase = entityTags.getOrDefault(Key.ATTRIBUTE_DODGE_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double dodgeChanceBuff = 0.0;
    if (buffs != null) {
      dodgeChanceBuff = buffs.getAethelAttribute(AethelAttribute.DODGE_CHANCE);
    }

    if (dodgeChanceBase + dodgeChanceBuff > random.nextDouble() * 100) {
      World world = defender.getWorld();
      world.spawnParticle(Particle.EXPLOSION_NORMAL, defender.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
      world.playSound(defender.getEyeLocation(), Sound.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 0.65f, 0);
      return true;
    }
    return false;
  }

  /**
   * Ignore damage taken if the defender's toughness is higher,
   * otherwise toughness mitigates damage by a flat amount.
   *
   * @param e          entity damage event
   * @param entityTags entity's persistent tags
   * @param buffs      {@link Buffs}
   * @param defender   defending entity
   * @return if tougher than damage
   */
  private boolean ifTougher(EntityDamageEvent e, PersistentDataContainer entityTags, Buffs buffs, LivingEntity defender) {
    double toughnessBase = entityTags.getOrDefault(Key.ATTRIBUTE_ARMOR_TOUGHNESS.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double toughnessBuff = 0.0;
    if (buffs != null) {
      toughnessBuff = buffs.getAethelAttribute(AethelAttribute.ARMOR_TOUGHNESS);
    }
    double toughness = defender.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + toughnessBase + toughnessBuff;

    e.setDamage(Math.max(e.getDamage() - (toughness / 2), 0));
    if (e.getDamage() == 0) {
      World world = defender.getWorld();
      world.spawnParticle(Particle.END_ROD, defender.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
      world.playSound(defender.getEyeLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 1, 0);
      return true;
    }
    return false;
  }

  /**
   * Damages the worn armors' durability based on the damage taken.
   *
   * @param defender player taking damage
   * @param damage   damage taken
   */
  private void damageArmorDurability(Player defender, double damage) {
    int durabilityDamage = (int) Math.max(damage / 4, 1);
    ItemDurability.increaseDamage(defender, EquipmentSlot.HEAD, durabilityDamage);
    ItemDurability.increaseDamage(defender, EquipmentSlot.CHEST, durabilityDamage);
    ItemDurability.increaseDamage(defender, EquipmentSlot.LEGS, durabilityDamage);
    ItemDurability.increaseDamage(defender, EquipmentSlot.FEET, durabilityDamage);
  }
}
