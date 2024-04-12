package me.dannynguyen.aethel;

import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.rpg.*;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Represents plugin's scheduled repeating tasks.
 *
 * @author Danny Nguyen
 * @version 1.22.18
 * @since 1.22.2
 */
public class PluginTask {
  /**
   * No parameter constructor.
   */
  public PluginTask() {
  }

  /**
   * Compares the player's main hand item for updating {@link Equipment}.
   */
  public void updateMainHandEquipment() {
    Map<UUID, RpgPlayer> rpgPlayers = Plugin.getData().getRpgSystem().getRpgPlayers();
    for (UUID uuid : rpgPlayers.keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        Equipment equipment = rpgPlayers.get(uuid).getEquipment();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType() != (equipment.getHeldItem()).getType()) {
          equipment.setHeldItem(heldItem);
          equipment.readSlot(heldItem, RpgEquipmentSlot.HAND);
        }
      }
    }
  }

  /**
   * Spawn particles and processes damage taken from damage over time {@link Status statuses}.
   */
  public void triggerStatuses() {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    for (UUID uuid : entityStatuses.keySet()) {
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      if (Bukkit.getEntity(uuid) instanceof LivingEntity entity && (statuses.containsKey(StatusType.BLEED) || statuses.containsKey(StatusType.ELECTROCUTE) || statuses.containsKey(StatusType.SOAKED))) {
        World world = entity.getWorld();
        Location bodyLocation = entity.getLocation().add(0, 1, 0);
        DamageMitigation mitigation = new DamageMitigation(entity);

        if (statuses.containsKey(StatusType.SOAKED)) {
          world.spawnParticle(Particle.DRIPPING_DRIPSTONE_WATER, bodyLocation, 3, 0.25, 0.5, 0.25);
        }
        if (statuses.containsKey(StatusType.BLEED)) {
          world.spawnParticle(Particle.BLOCK_DUST, bodyLocation, 3, 0.25, 0.5, 0.25, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
          double damage = statuses.get(StatusType.BLEED).getStackAmount() * 0.2;
          final double finalDamage = mitigation.mitigateProtectionResistance(damage);

          entity.damage(0.01);
          if (entity instanceof Player player && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
            Health health = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getHealth();
            health.damage(finalDamage);
          } else {
            entity.setHealth(Math.max(0, entity.getHealth() + 0.1 - finalDamage));
          }
        }
        if (statuses.containsKey(StatusType.ELECTROCUTE)) {
          world.spawnParticle(Particle.WAX_OFF, bodyLocation, 3, 0.25, 0.5, 0.25);
          double damage = statuses.get(StatusType.ELECTROCUTE).getStackAmount() * 0.2;
          final double finalDamage = mitigation.mitigateProtectionResistance(damage);

          entity.damage(0.01);
          if (entity instanceof Player player && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
            Health health = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getHealth();
            health.damage(finalDamage);
            if (health.getCurrentHealth() < 0) {
              propagateElectrocuteStacks(entity, health.getCurrentHealth());
            }
          } else {
            double remainingHealth = entity.getHealth() + 0.1 - finalDamage;
            entity.setHealth(Math.max(0, remainingHealth));
            if (entity.getHealth() == 0) {
              propagateElectrocuteStacks(entity, remainingHealth);
            }
          }
        }
      }
    }
  }

  /**
   * Triggers {@link PassiveTriggerType#BELOW_HEALTH} {@link PassiveAbility passive abilities}.
   * <p>
   * {@link PassiveTriggerType#BELOW_HEALTH}{@link PassiveAbility} can only be triggered on self.
   */
  public void triggerBelowHealthPassives() {
    for (RpgPlayer rpgPlayer : Plugin.getData().getRpgSystem().getRpgPlayers().values()) {
      Map<Equipment.Abilities.SlotPassive, PassiveAbility> belowHealthTriggers = rpgPlayer.getEquipment().getAbilities().getTriggerPassives().get(PassiveTriggerType.BELOW_HEALTH);
      if (belowHealthTriggers.isEmpty()) {
        continue;
      }
      for (PassiveAbility ability : belowHealthTriggers.values()) {
        if (ability.isOnCooldown()) {
          continue;
        }
        double healthPercent = Double.parseDouble(ability.getConditionData().get(0));
        if (rpgPlayer.getHealth().getHealthPercent() <= healthPercent) {
          boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
          if (self) {
            ability.doEffect(rpgPlayer.getUUID(), rpgPlayer.getUUID());
          }
        }
      }
    }
  }

  /**
   * Decay players' overcapped {@link Health overshields}.
   * <p>
   * An overshield begins to decay when current health
   * exceeds max health by a factor greater than x1.2.
   */
  public void updateOvershields() {
    Map<UUID, RpgPlayer> rpgPlayers = Plugin.getData().getRpgSystem().getRpgPlayers();
    for (UUID uuid : rpgPlayers.keySet()) {
      if (Bukkit.getPlayer(uuid) != null) {
        rpgPlayers.get(uuid).getHealth().decayOvershield();
      }
    }
  }

  /**
   * Update players' {@link Health health in action bar} display.
   */
  public void updateActionDisplay() {
    Map<UUID, RpgPlayer> rpgPlayers = Plugin.getData().getRpgSystem().getRpgPlayers();
    for (UUID uuid : rpgPlayers.keySet()) {
      if (Bukkit.getPlayer(uuid) != null) {
        rpgPlayers.get(uuid).getHealth().updateActionDisplay();
      }
    }
  }

  /**
   * Spawns particle trails for currently tracked locations.
   */
  public void trackLocations() {
    Map<UUID, Location> trackedLocations = Plugin.getData().getPluginSystem().getTrackedLocations();
    for (UUID uuid : trackedLocations.keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      Location here = player.getLocation().add(0, 1, 0);
      Location there = trackedLocations.get(uuid);
      if (here.getWorld().getName().equals(there.getWorld().getName())) {
        if (there.getWorld().getNearbyEntities(there, 3, 3, 3).contains(player)) {
          player.sendMessage(ChatColor.GREEN + "[Tracking Location] Destination reached.");
          trackedLocations.remove(uuid);
        }
        Vector direction = there.toVector().subtract(here.toVector()).normalize();
        player.spawnParticle(Particle.FLAME, here.add(direction), 1, 0, 0, 0, 0);
        player.spawnParticle(Particle.FLAME, here.add(direction.clone().multiply(2)), 1, 0, 0, 0, 0);
        player.spawnParticle(Particle.FLAME, here.add(direction.clone().multiply(4)), 1, 0, 0, 0, 0);
        player.spawnParticle(Particle.FLAME, here.add(direction.clone().multiply(8)), 1, 0, 0, 0, 0);
      } else {
        player.sendMessage(ChatColor.RED + "[Tracking Location] Destination in different world.");
        trackedLocations.remove(uuid);
      }
    }
  }

  /**
   * Refreshes potion effects to players who've met enchantment level requirements.
   * <ul>
   *  <li>Feather Falling >= 5: Slow Falling
   *  <li>Fire Protection >= 10: Fire Resistance
   * </ul>
   */
  public void refreshEnchantmentEffects() {
    Map<Enchantment, Set<UUID>> sufficientEnchantments = Plugin.getData().getRpgSystem().getSufficientEnchantments();
    for (UUID uuid : sufficientEnchantments.get(Enchantment.PROTECTION_FALL)) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 101, 0, false));
      }
    }
    for (UUID uuid : sufficientEnchantments.get(Enchantment.PROTECTION_FIRE)) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.setFireTicks(-20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 101, 0, false));
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
    for (Entity entity : sourceEntity.getNearbyEntities(4, 4, 4)) {
      if (entity instanceof LivingEntity livingEntity) {
        nearbyLivingEntities.add(livingEntity);
      }
    }

    if (!nearbyLivingEntities.isEmpty()) {
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
}
