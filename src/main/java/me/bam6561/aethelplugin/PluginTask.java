package me.bam6561.aethelplugin;

import me.bam6561.aethelplugin.enums.plugin.Key;
import me.bam6561.aethelplugin.enums.rpg.AethelAttribute;
import me.bam6561.aethelplugin.enums.rpg.StatusType;
import me.bam6561.aethelplugin.enums.rpg.abilities.PassiveTriggerType;
import me.bam6561.aethelplugin.plugin.PluginSystem;
import me.bam6561.aethelplugin.rpg.*;
import me.bam6561.aethelplugin.rpg.abilities.PassiveAbility;
import me.bam6561.aethelplugin.utils.EntityReader;
import me.bam6561.aethelplugin.utils.entity.DamageMitigation;
import me.bam6561.aethelplugin.utils.entity.HealthChange;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Represents plugin's scheduled repeating tasks.
 *
 * @author Danny Nguyen
 * @version 1.27.0
 * @since 1.22.2
 */
public class PluginTask {
  /**
   * No parameter constructor.
   */
  public PluginTask() {
  }

  /**
   * Spawn particles and processes damage taken from damage over time {@link Status statuses}.
   */
  public void triggerStatuses() {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    for (UUID uuid : entityStatuses.keySet()) {
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      if (!(Bukkit.getEntity(uuid) instanceof LivingEntity entity)) {
        continue;
      }

      if (statuses.containsKey(StatusType.BLEED) || statuses.containsKey(StatusType.ELECTROCUTE) || statuses.containsKey(StatusType.SOAK)) {
        World world = entity.getWorld();
        Location bodyLocation = entity.getLocation().add(0, 1, 0);
        DamageMitigation mitigation = new DamageMitigation(entity);

        if (statuses.containsKey(StatusType.SOAK)) {
          world.spawnParticle(Particle.DRIPPING_DRIPSTONE_WATER, bodyLocation, 3, 0.25, 0.5, 0.25);
        }

        if (statuses.containsKey(StatusType.BLEED)) {
          world.spawnParticle(Particle.BLOCK, bodyLocation, 3, 0.25, 0.5, 0.25, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
          double damage = statuses.get(StatusType.BLEED).getStackAmount() * 0.2;
          final double finalDamage = mitigation.mitigateProtectionResistance(damage);

          if (entity instanceof Player player) {
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
              new HealthChange(player).damage(finalDamage);
            }
          } else {
            new HealthChange(entity).damage(finalDamage);
          }
        }

        if (statuses.containsKey(StatusType.ELECTROCUTE)) {
          world.spawnParticle(Particle.WAX_OFF, bodyLocation, 3, 0.25, 0.5, 0.25);
          double damage = statuses.get(StatusType.ELECTROCUTE).getStackAmount() * 0.2;
          final double finalDamage = mitigation.mitigateProtectionResistance(damage);

          if (entity instanceof Player player) {
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
              new HealthChange(player).damage(finalDamage);
              double remainingHealth = player.getPersistentDataContainer().get(Key.RPG_HEALTH.getNamespacedKey(), PersistentDataType.DOUBLE);
              if (remainingHealth < 0) {
                propagateElectrocuteStacks(player, remainingHealth);
              }
            }
          } else {
            new HealthChange(entity).damage(finalDamage);
            double remainingHealth = entity.getPersistentDataContainer().get(Key.RPG_HEALTH.getNamespacedKey(), PersistentDataType.DOUBLE);
            if (remainingHealth < 0) {
              propagateElectrocuteStacks(entity, remainingHealth);
            }
          }
        }
      }
    }
  }

  /**
   * Triggers {@link PassiveTriggerType#INTERVAL} {@link PassiveAbility passive abilities}.
   * <p>
   * {@link PassiveTriggerType#INTERVAL} {@link PassiveAbility} can only be triggered on self.
   */
  public void triggerIntervalPassives() {
    RpgSystem rpgSystem = Plugin.getData().getRpgSystem();
    Map<UUID, RpgPlayer> rpgPlayers = rpgSystem.getRpgPlayers();

    for (RpgPlayer rpgPlayer : rpgPlayers.values()) {
      Map<Equipment.Abilities.SlotPassive, PassiveAbility> intervalTriggers = rpgPlayer.getEquipment().getAbilities().getTriggerPassives().get(PassiveTriggerType.INTERVAL);
      if (intervalTriggers.isEmpty()) {
        continue;
      }
      for (PassiveAbility ability : intervalTriggers.values()) {
        if (ability.isOnCooldown()) {
          continue;
        }
        boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
        if (self) {
          ability.doEffect(rpgPlayer.getUUID(), rpgPlayer.getUUID());
        }
      }
    }
  }

  /**
   * Triggers {@link PassiveTriggerType#BELOW_HEALTH} {@link PassiveAbility passive abilities}.
   * <p>
   * {@link PassiveTriggerType#BELOW_HEALTH} {@link PassiveAbility} can only be triggered on self.
   */
  public void triggerBelowHealthPassives() {
    RpgSystem rpgSystem = Plugin.getData().getRpgSystem();
    Map<UUID, RpgPlayer> rpgPlayers = rpgSystem.getRpgPlayers();
    Set<UUID> wounded = rpgSystem.getWounded();

    for (UUID uuid : wounded) {
      RpgPlayer rpgPlayer = rpgPlayers.get(uuid);
      Map<Equipment.Abilities.SlotPassive, PassiveAbility> belowHealthTriggers = rpgPlayer.getEquipment().getAbilities().getTriggerPassives().get(PassiveTriggerType.BELOW_HEALTH);
      if (belowHealthTriggers.isEmpty()) {
        continue;
      }

      Player player = Bukkit.getPlayer(rpgPlayer.getUUID());
      PersistentDataContainer entityTags = player.getPersistentDataContainer();
      Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(rpgPlayer.getUUID());

      double currentHealth = entityTags.getOrDefault(Key.RPG_HEALTH.getNamespacedKey(), PersistentDataType.DOUBLE, player.getHealth());
      double genericMaxHealthBuff = 0.0;
      double maxHealthBuff = 0.0;
      if (buffs != null) {
        genericMaxHealthBuff = buffs.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        maxHealthBuff = buffs.getAethelAttribute(AethelAttribute.MAX_HEALTH);
      }
      double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + genericMaxHealthBuff + maxHealthBuff;

      for (PassiveAbility ability : belowHealthTriggers.values()) {
        if (ability.isOnCooldown()) {
          continue;
        }
        double healthPercent = Double.parseDouble(ability.getConditionData().get(0));
        if ((currentHealth / maxHealth) * 100 <= healthPercent) {
          boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
          if (self) {
            ability.doEffect(rpgPlayer.getUUID(), rpgPlayer.getUUID());
          }
        }
      }
    }
  }

  /**
   * Decay entities' {@link RpgSystem#getOvershields() overcapped shields}.
   */
  public void decayOvershields() {
    Set<UUID> overshields = Plugin.getData().getRpgSystem().getOvershields();
    for (UUID uuid : overshields) {
      LivingEntity livingEntity = (LivingEntity) Bukkit.getEntity(uuid);
      new HealthChange(livingEntity).overshield();
    }
  }

  /**
   * Update players' {@link Settings#isHealthActionVisible() health in action bar display}.
   */
  public void updateActionDisplay() {
    Map<UUID, RpgPlayer> rpgPlayers = Plugin.getData().getRpgSystem().getRpgPlayers();
    for (UUID uuid : rpgPlayers.keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        new HealthChange(player).updateActionDisplay();
      }
    }
  }

  /**
   * Spawns particle trails for {@link PluginSystem#getTrackedLocations() currently tracked locations}.
   */
  public void trackLocations() {
    Map<UUID, Location> trackedLocations = Plugin.getData().getPluginSystem().getTrackedLocations();
    for (UUID uuid : trackedLocations.keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      if (!EntityReader.hasTrinket(player, Material.COMPASS)) {
        player.sendMessage(ChatColor.RED + "[Tracking Location] No compass in hand, off-hand, or trinket slot.");
        trackedLocations.remove(uuid);
        return;
      }
      Location here = player.getLocation().add(0, 1, 0);
      Location there = trackedLocations.get(uuid);
      if (!here.getWorld().getName().equals(there.getWorld().getName())) {
        player.sendMessage(ChatColor.RED + "[Tracking Location] Destination in different world.");
        trackedLocations.remove(uuid);
        return;
      }
      if (there.getWorld().getNearbyEntities(there, 3, 3, 3).contains(player)) {
        player.sendMessage(ChatColor.GREEN + "[Tracking Location] Destination reached.");
        trackedLocations.remove(uuid);
        return;
      }

      Vector direction = there.toVector().subtract(here.toVector()).normalize();
      player.spawnParticle(Particle.FLAME, here.add(direction), 1, 0, 0, 0, 0);
      player.spawnParticle(Particle.FLAME, here.add(direction.clone().multiply(2)), 1, 0, 0, 0, 0);
      player.spawnParticle(Particle.FLAME, here.add(direction.clone().multiply(4)), 1, 0, 0, 0, 0);
      player.spawnParticle(Particle.FLAME, here.add(direction.clone().multiply(8)), 1, 0, 0, 0, 0);
    }
  }

  /**
   * Refreshes potion effects to players who've met
   * {@link RpgSystem#getSufficientEnchantments() enchantment level requirements}.
   * <ul>
   *  <li>Feather Falling >= 5: Slow Falling
   *  <li>Fire Protection >= 10: Fire Resistance
   * </ul>
   */
  public void refreshEnchantmentEffects() {
    Map<Enchantment, Set<UUID>> sufficientEnchantments = Plugin.getData().getRpgSystem().getSufficientEnchantments();
    for (UUID uuid : sufficientEnchantments.get(Enchantment.FEATHER_FALLING)) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 101, 0, true, false));
      }
    }
    for (UUID uuid : sufficientEnchantments.get(Enchantment.FIRE_PROTECTION)) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.setFireTicks(-20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 101, 0, true, false));
      }
    }
  }

  /**
   * Propagates remaining electrocute stacks to nearby targets.
   *
   * @param sourceEntity    source entity that died
   * @param remainingHealth negative health value
   */
  private void propagateElectrocuteStacks(LivingEntity sourceEntity, double remainingHealth) {
    Set<LivingEntity> nearbyLivingEntities = new HashSet<>();
    for (Entity entity : sourceEntity.getNearbyEntities(3, 3, 3)) {
      if (entity instanceof LivingEntity livingEntity) {
        nearbyLivingEntities.add(livingEntity);
      }
    }
    if (nearbyLivingEntities.isEmpty()) {
      return;
    }

    double remainingStacks = Math.abs(remainingHealth / 0.2);
    int appliedStacks = (int) Math.max(1, remainingStacks / nearbyLivingEntities.size());
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    for (LivingEntity livingEntity : nearbyLivingEntities) {
      UUID uuid = livingEntity.getUniqueId();
      if (!entityStatuses.containsKey(uuid)) {
        entityStatuses.put(uuid, new HashMap<>());
      }
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      if (statuses.containsKey(StatusType.ELECTROCUTE)) {
        statuses.get(StatusType.ELECTROCUTE).addStacks(appliedStacks, 60);
      } else {
        statuses.put(StatusType.ELECTROCUTE, new Status(uuid, StatusType.ELECTROCUTE, appliedStacks, 60));
      }
    }
  }
}
