package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.rpg.*;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import me.dannynguyen.aethel.utils.entity.DamageMitigation;
import me.dannynguyen.aethel.utils.entity.HealthChange;
import me.dannynguyen.aethel.utils.item.DurabilityChange;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Collection of damage done, taken, and healed listeners.
 *
 * @author Danny Nguyen
 * @version 1.26.7
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
    if (!(e.getEntity() instanceof LivingEntity)) {
      return;
    }

    if (e instanceof EntityDamageByEntityEvent event) {
      new EntityDamage(event).calculateDamage();
      return;
    }

    if (!ignoredDamageCauses.contains(e.getCause())) {
      new EnvironmentDamage(e).calculateDamage();
    }
  }

  /**
   * Processes damage healed by entities.
   *
   * @param e entity regain health event
   */
  @EventHandler
  private void onRegainHealth(EntityRegainHealthEvent e) {
    if (e.getEntity() instanceof LivingEntity livingEntity) {
      new HealthChange(livingEntity).heal(e.getAmount());
      e.setCancelled(true);
    }
  }

  /**
   * Represents an entity damaging another entity.
   *
   * @author Danny Nguyen
   * @version 1.26.8
   * @since 1.23.13
   */
  private static class EntityDamage {
    /**
     * RNG.
     */
    private final Random random = new Random();

    /**
     * Entity damage by entity event.
     */
    private final EntityDamageEvent e;

    /**
     * Attacking entity.
     */
    private final Entity attacker;

    /**
     * Defending entity.
     */
    private final LivingEntity defender;

    /**
     * Damage mitigation.
     */
    private final DamageMitigation mitigation;

    /**
     * Defending entity's statuses.
     */
    private final Map<StatusType, Status> defenderStatuses;

    /**
     * Attacking entity's persistent tags.
     */
    private final PersistentDataContainer attackerEntityTags;

    /**
     * Defending entity's persistent tags.
     */
    private final PersistentDataContainer defenderEntityTags;

    /**
     * Attacking entity's buffs.
     */
    private final Buffs attackerBuffs;

    /**
     * Defending entity's buffs.
     */
    private final Buffs defenderBuffs;

    /**
     * Associates the entity damage by entity event with all its components.
     *
     * @param e entity damage by entity event
     */
    EntityDamage(EntityDamageByEntityEvent e) {
      this.e = e;
      this.attacker = e.getDamager();
      this.defender = (LivingEntity) e.getEntity();
      this.mitigation = new DamageMitigation(defender);
      this.attackerEntityTags = attacker.getPersistentDataContainer();
      this.defenderEntityTags = defender.getPersistentDataContainer();
      RpgSystem rpgSystem = Plugin.getData().getRpgSystem();
      this.defenderStatuses = rpgSystem.getStatuses().get(defender.getUniqueId());
      Map<UUID, Buffs> entityBuffs = rpgSystem.getBuffs();
      this.attackerBuffs = entityBuffs.get(attacker.getUniqueId());
      this.defenderBuffs = entityBuffs.get(defender.getUniqueId());
    }

    /**
     * Calculates damage dealt by entity.
     */
    private void calculateDamage() {
      if (attacker.getType() == EntityType.FROG && defender.getType() == EntityType.MAGMA_CUBE) {
        return;
      }

      if (attacker instanceof LivingEntity) {
        ifCriticallyHit();
        if (defenderStatuses != null) {
          ifVulnerable();
        }
      }

      if (mitigateSpecificCauseDamage()) {
        e.setCancelled(true);
        return;
      }

      if (ifBlocked()) {
        e.setCancelled(true);
        return;
      }
      if (ifCountered()) {
        e.setCancelled(true);
        return;
      } else if (ifDodged()) {
        e.setCancelled(true);
        return;
      } else if (ifTougher()) {
        e.setCancelled(true);
        return;
      }

      e.setDamage(mitigation.mitigateArmorProtectionResistance(e.getDamage()));
      final double finalDamage = e.getDamage();
      e.setDamage(0.01);

      if (defender instanceof Player defenderPlayer) {
        triggerDamageTakenPassives(defenderPlayer);
      }

      new HealthChange(defender).damage(finalDamage);

      if (defender.getHealth() != 0.0 && attacker instanceof Player attackerPlayer) {
        triggerDamageDealtPassives(attackerPlayer);
      }
    }

    /**
     * If the attacker dealt a critical hit, multiply the damage by its modifier.
     */
    private void ifCriticallyHit() {
      double criticalChanceBase = attackerEntityTags.getOrDefault(Key.ATTRIBUTE_CRITICAL_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      double criticalChanceBuff = 0.0;
      if (attackerBuffs != null) {
        criticalChanceBuff = attackerBuffs.getAethelAttribute(AethelAttribute.CRITICAL_CHANCE);
      }

      if (criticalChanceBase + criticalChanceBuff > random.nextDouble() * 100) {
        World world = defender.getWorld();
        world.spawnParticle(Particle.CRIT, defender.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5);
        world.playSound(defender.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 0.65f, 1);

        double criticalDamageBase = attackerEntityTags.getOrDefault(Key.ATTRIBUTE_CRITICAL_DAMAGE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
        double criticalDamageBuff = 0.0;
        if (attackerBuffs != null) {
          criticalDamageBuff = attackerBuffs.getAethelAttribute(AethelAttribute.CRITICAL_DAMAGE);
        }

        e.setDamage(e.getDamage() * (1.25 + (criticalDamageBase + criticalDamageBuff) / 100));
      }
    }

    /**
     * If the target has the {@link StatusType#VULNERABLE}, multiply the damage by its number of stacks.
     */
    private void ifVulnerable() {
      if (defenderStatuses.containsKey(StatusType.VULNERABLE)) {
        int vulnerable = defenderStatuses.get(StatusType.VULNERABLE).getStackAmount();
        e.setDamage(e.getDamage() * (1 + (vulnerable * 0.025)));
      }
    }

    /**
     * Mitigates specific cause damage taken based on their respective protection enchantment.
     *
     * @return if no damage taken or magic damage was taken/mitigated
     */
    private boolean mitigateSpecificCauseDamage() {
      switch (e.getCause()) {
        case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> {
          if (ifBlocked()) {
            return true;
          }

          e.setDamage(mitigation.mitigateExplosion(e.getDamage()));
          if (e.getDamage() <= 0) {
            return true;
          }
        }
        case MAGIC -> {
          final double finalDamage = mitigation.mitigateProtectionResistance(e.getDamage());
          e.setDamage(0.01);

          if (defender instanceof Player defenderPlayer) {
            triggerDamageTakenPassives(defenderPlayer);
          }

          new HealthChange(defender).damage(finalDamage);
          return true;
        }
        case PROJECTILE -> {
          if (ifBlocked()) {
            return true;
          }

          int projectileProtection = defender.getPersistentDataContainer().getOrDefault(Key.ENCHANTMENT_PROJECTILE_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
          if (projectileProtection > 0) {
            e.setDamage(mitigation.mitigateProjectile(e.getDamage()));
          }

          if (defender instanceof Player defendingPlayer && projectileProtection >= 10) {
            PlayerInventory pInv = defendingPlayer.getInventory();
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
          }
        }
      }
      if (attacker.getType() == EntityType.AREA_EFFECT_CLOUD) {
        final double finalDamage = mitigation.mitigateProtectionResistance(e.getDamage());
        e.setDamage(0.01);

        if (defender instanceof Player defenderPlayer) {
          triggerDamageTakenPassives(defenderPlayer);
        }

        new HealthChange(defender).damage(finalDamage);
        return true;
      }
      return false;
    }

    /**
     * If the player blocked the attack with a shield.
     *
     * @return if the player blocked the attack with a shield
     */
    private boolean ifBlocked() {
      if (!(defender instanceof Player defendingPlayer)) {
        return false;
      }
      if (!(defendingPlayer.isBlocking() && getDirectionAngle() <= 90)) {
        return false;
      }
      if (attacker instanceof LivingEntity livingAttacker) {
        ItemStack hand = livingAttacker.getEquipment().getItemInMainHand();
        if (ItemReader.isNotNullOrAir(hand)) {
          switch (hand.getType()) {
            case WOODEN_AXE, STONE_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE -> {
              defender.getWorld().playSound(defender.getLocation(), Sound.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 1, 1);
              defendingPlayer.setCooldown(Material.SHIELD, 100);
            }
          }
        }
      }
      EntityEquipment defenderEquipment = defender.getEquipment();
      ItemStack offhand = defenderEquipment.getItemInOffHand();
      ItemStack hand = defenderEquipment.getItemInMainHand();
      if (offhand.getType() == Material.SHIELD && !offhand.getItemMeta().isUnbreakable()) {
        DurabilityChange.increaseDamage(defender, defender.getEquipment(), EquipmentSlot.OFF_HAND, Math.max(1, (int) e.getDamage() / 4));
        defender.getWorld().playSound(defender.getEyeLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1, 1);
        return true;
      } else if (hand.getType() == Material.SHIELD && !hand.getItemMeta().isUnbreakable()) {
        DurabilityChange.increaseDamage(defender, defender.getEquipment(), EquipmentSlot.HAND, Math.max(1, (int) e.getDamage() / 4));
        defender.getWorld().playSound(defender.getEyeLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1, 1);
        return true;
      }
      return true;
    }

    /**
     * Ignores damage taken if the defender killed the attacker by counterattacks.
     * <p>
     * The number of counterattacks done is based on the defender's attack speed.
     * <p>
     * Projectile attacks cannot trigger counterattacks.
     *
     * @return if the attacker died
     */
    private boolean ifCountered() {
      if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE || !(attacker instanceof LivingEntity livingAttacker)) {
        return false;
      }

      double counterChanceBase = defenderEntityTags.getOrDefault(Key.ATTRIBUTE_COUNTER_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      double counterChanceBuff = 0.0;
      if (defenderBuffs != null) {
        counterChanceBuff = defenderBuffs.getAethelAttribute(AethelAttribute.COUNTER_CHANCE);
      }

      double feintSkillBase = attackerEntityTags.getOrDefault(Key.ATTRIBUTE_FEINT_SKILL.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      double feintSkillBuff = 0.0;
      if (attackerBuffs != null) {
        feintSkillBuff = attackerBuffs.getAethelAttribute(AethelAttribute.FEINT_SKILL);
      }

      if (counterChanceBase + counterChanceBuff - feintSkillBase - feintSkillBuff > random.nextDouble() * 100) {
        World world = defender.getWorld();
        world.spawnParticle(Particle.FIREWORKS_SPARK, defender.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
        world.playSound(defender.getEyeLocation(), Sound.ENTITY_ALLAY_HURT, SoundCategory.PLAYERS, 0.65f, 0.75f);

        int attackSpeed;
        if (defender.getAttribute(Attribute.GENERIC_ATTACK_SPEED) != null) {
          attackSpeed = (int) defender.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue();
        } else {
          attackSpeed = 1;
        }

        double counterDamage = attackSpeed * defender.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
        final double finalDamage = new DamageMitigation(livingAttacker).mitigateArmorProtectionResistance(counterDamage);

        if (livingAttacker instanceof Player player) {
          if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            new HealthChange(player).damage(finalDamage);
          }
        } else {
          new HealthChange(livingAttacker).damage(finalDamage);
        }
        return attackerEntityTags.get(Key.RPG_HEALTH.getNamespacedKey(), PersistentDataType.DOUBLE) <= 0.0;
      }
      return false;
    }

    /**
     * Ignore damage taken if the entity dodged.
     *
     * @return if dodged
     */
    private boolean ifDodged() {
      double dodgeChanceBase = defenderEntityTags.getOrDefault(Key.ATTRIBUTE_DODGE_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      double dodgeChanceBuff = 0.0;
      if (defenderBuffs != null) {
        dodgeChanceBuff = defenderBuffs.getAethelAttribute(AethelAttribute.DODGE_CHANCE);
      }

      double accuracySkillBase = attackerEntityTags.getOrDefault(Key.ATTRIBUTE_ACCURACY_SKILL.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      double accuracySkillBuff = 0.0;
      if (attackerBuffs != null) {
        accuracySkillBuff = attackerBuffs.getAethelAttribute(AethelAttribute.ACCURACY_SKILL);
      }

      if (dodgeChanceBase + dodgeChanceBuff - accuracySkillBase - accuracySkillBuff > random.nextDouble() * 100) {
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
     * @return if tougher than damage
     */
    private boolean ifTougher() {
      double toughnessBase = defenderEntityTags.getOrDefault(Key.ATTRIBUTE_ARMOR_TOUGHNESS.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      double toughnessBuff = 0.0;
      if (defenderBuffs != null) {
        toughnessBuff = defenderBuffs.getAethelAttribute(AethelAttribute.ARMOR_TOUGHNESS);
      }
      double toughness = defender.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + toughnessBase + toughnessBuff;
      if (defenderStatuses != null && defenderStatuses.containsKey(StatusType.BATTER)) {
        toughness = Math.max(0, toughness - defenderStatuses.get(StatusType.BATTER).getStackAmount());
      }

      e.setDamage(Math.max(0, e.getDamage() - (toughness / 2)));
      if (e.getDamage() == 0) {
        World world = defender.getWorld();
        world.spawnParticle(Particle.END_ROD, defender.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
        world.playSound(defender.getEyeLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 1, 0);
        return true;
      }
      return false;
    }

    /**
     * Triggers {@link PassiveTriggerType#DAMAGE_DEALT} {@link PassiveAbility passive abilities}.
     *
     * @param attacker attacking player
     */
    private void triggerDamageDealtPassives(Player attacker) {
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
     * @param defender defending player
     */
    private void triggerDamageTakenPassives(Player defender) {
      RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(defender.getUniqueId());
      Map<Equipment.Abilities.SlotPassive, PassiveAbility> damageTakenTriggers = rpgPlayer.getEquipment().getAbilities().getTriggerPassives().get(PassiveTriggerType.DAMAGE_TAKEN);
      if (damageTakenTriggers.isEmpty()) {
        return;
      }

      boolean livingAttacker = attacker instanceof LivingEntity;

      Random random = new Random();
      for (PassiveAbility ability : damageTakenTriggers.values()) {
        if (ability.isOnCooldown()) {
          continue;
        }
        double chance = Double.parseDouble(ability.getConditionData().get(0));
        if (chance > random.nextDouble() * 100) {
          boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
          if (livingAttacker) {
            UUID targetUUID;
            if (self) {
              targetUUID = defender.getUniqueId();
            } else {
              targetUUID = attacker.getUniqueId();
            }
            ability.doEffect(rpgPlayer.getUUID(), targetUUID);
          } else {
            if (self) {
              ability.doEffect(rpgPlayer.getUUID(), defender.getUniqueId());
            }
          }
        }
      }
    }

    /**
     * Gets an attacker's direction angle from the location of the defender.
     *
     * @return attacker's direction angle
     */
    private double getDirectionAngle() {
      Location defenderLocation = defender.getLocation();
      Vector defenderDirection = defenderLocation.getDirection();
      Vector entityLocationVector = defenderLocation.toVector();
      Vector entityDirection = attacker.getLocation().toVector().subtract(entityLocationVector);

      double x1 = defenderDirection.getX();
      double z1 = defenderDirection.getZ();
      double x2 = entityDirection.getX();
      double z2 = entityDirection.getZ();

      double dotProduct = x1 * x2 + z1 * z2;
      double vectorLengths = Math.sqrt(Math.pow(x1, 2) + Math.pow(z1, 2)) * Math.sqrt(Math.pow(x2, 2) + Math.pow(z2, 2));
      return Math.acos(dotProduct / vectorLengths) * (180 / Math.PI);
    }
  }

  /**
   * Represents environmental damage taken by an entity.
   *
   * @author Danny Nguyen
   * @version 1.25.10
   * @since 1.23.13
   */
  private class EnvironmentDamage {
    /**
     * RNG.
     */
    private final Random random = new Random();

    /**
     * Entity damage event.
     */
    private final EntityDamageEvent e;

    /**
     * Defending entity.
     */
    private final LivingEntity defender;

    /**
     * Defending entity's persistent tags.
     */
    private final PersistentDataContainer defenderEntityTags;

    /**
     * Defending entity's statuses.
     */
    private final Map<StatusType, Status> defenderStatuses;

    /**
     * Defending entity's buffs.
     */
    private final Buffs defenderBuffs;

    /**
     * Associates the entity damage event with all its components.
     *
     * @param e entity damage event
     */
    EnvironmentDamage(EntityDamageEvent e) {
      this.e = e;
      this.defender = (LivingEntity) e.getEntity();
      this.defenderEntityTags = defender.getPersistentDataContainer();
      RpgSystem rpgSystem = Plugin.getData().getRpgSystem();
      this.defenderStatuses = rpgSystem.getStatuses().get(defender.getUniqueId());
      this.defenderBuffs = rpgSystem.getBuffs().get(defender.getUniqueId());
    }

    /**
     * Calculates damage dealt by environment.
     */
    private void calculateDamage() {
      if (mitigateEnvironmentalDamage()) {
        e.setCancelled(true);
        return;
      }

      final double finalDamage = e.getDamage();
      e.setDamage(0.01);

      if (defender instanceof Player defenderPlayer) {
        triggerDamageTakenPassives(defenderPlayer);
      }

      new HealthChange(defender).damage(finalDamage);
    }

    /**
     * Mitigates environmental damage taken based on the entity's {@link Equipment.Enchantments}.
     *
     * @return if no damage is taken
     */
    private boolean mitigateEnvironmentalDamage() {
      DamageMitigation mitigation = new DamageMitigation(defender);
      switch (e.getCause()) {
        case FALL -> e.setDamage(mitigation.mitigateFall(e.getDamage()));
        case DRAGON_BREATH, FLY_INTO_WALL, MAGIC, POISON, WITHER -> e.setDamage(mitigation.mitigateProtection(e.getDamage()));
        case FIRE, FIRE_TICK, HOT_FLOOR, LAVA -> e.setDamage(mitigation.mitigateFire(e.getDamage()));
        case BLOCK_EXPLOSION -> {
          if (ifBlocked()) {
            return true;
          }

          e.setDamage(mitigation.mitigateExplosion(e.getDamage()));
          if (e.getDamage() <= 0) {
            return true;
          }

          if (ifDodged()) {
            return true;
          } else if (ifTougher()) {
            return true;
          }

          e.setDamage(mitigation.mitigateArmorProtection(e.getDamage()));
        }
      }
      e.setDamage(mitigation.mitigateResistance(e.getDamage()));
      return false;
    }

    /**
     * If the player blocked with a shield.
     *
     * @return if the player blocked with a shield
     */
    private boolean ifBlocked() {
      if (!(defender instanceof Player defendingPlayer)) {
        return false;
      }
      if (!(defendingPlayer.isBlocking())) {
        return false;
      }
      EntityEquipment defenderEquipment = defender.getEquipment();
      ItemStack offhand = defenderEquipment.getItemInOffHand();
      ItemStack hand = defenderEquipment.getItemInMainHand();
      if (offhand.getType() == Material.SHIELD && !offhand.getItemMeta().isUnbreakable()) {
        DurabilityChange.increaseDamage(defender, defender.getEquipment(), EquipmentSlot.OFF_HAND, Math.max(1, (int) e.getDamage() / 4));
        defender.getWorld().playSound(defender.getEyeLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1, 1);
        return true;
      } else if (hand.getType() == Material.SHIELD && !hand.getItemMeta().isUnbreakable()) {
        DurabilityChange.increaseDamage(defender, defender.getEquipment(), EquipmentSlot.HAND, Math.max(1, (int) e.getDamage() / 4));
        defender.getWorld().playSound(defender.getEyeLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1, 1);
        return true;
      }
      return true;
    }

    /**
     * Ignore damage taken if the entity dodged.
     *
     * @return if dodged
     */
    private boolean ifDodged() {
      double dodgeChanceBase = defenderEntityTags.getOrDefault(Key.ATTRIBUTE_DODGE_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      double dodgeChanceBuff = 0.0;
      if (defenderBuffs != null) {
        dodgeChanceBuff = defenderBuffs.getAethelAttribute(AethelAttribute.DODGE_CHANCE);
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
     * @return if tougher than damage
     */
    private boolean ifTougher() {
      double toughnessBase = defenderEntityTags.getOrDefault(Key.ATTRIBUTE_ARMOR_TOUGHNESS.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
      double toughnessBuff = 0.0;
      if (defenderBuffs != null) {
        toughnessBuff = defenderBuffs.getAethelAttribute(AethelAttribute.ARMOR_TOUGHNESS);
      }
      double toughness = defender.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + toughnessBase + toughnessBuff;
      if (defenderStatuses != null && defenderStatuses.containsKey(StatusType.BATTER)) {
        toughness = Math.max(0, toughness - defenderStatuses.get(StatusType.BATTER).getStackAmount());
      }

      e.setDamage(Math.max(0, e.getDamage() - (toughness / 2)));
      if (e.getDamage() == 0) {
        World world = defender.getWorld();
        world.spawnParticle(Particle.END_ROD, defender.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.05);
        world.playSound(defender.getEyeLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 1, 0);
        return true;
      }
      return false;
    }
  }

  /**
   * Triggers {@link PassiveTriggerType#DAMAGE_TAKEN} {@link PassiveAbility passive abilities}.
   *
   * @param defender defending player
   */
  private void triggerDamageTakenPassives(Player defender) {
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
        if (self) {
          ability.doEffect(rpgPlayer.getUUID(), defender.getUniqueId());
        }
      }
    }
  }
}
