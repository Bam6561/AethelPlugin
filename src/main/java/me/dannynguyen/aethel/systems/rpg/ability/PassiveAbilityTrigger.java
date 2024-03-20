package me.dannynguyen.aethel.systems.rpg.ability;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.rpg.PlayerDamageMitigation;
import me.dannynguyen.aethel.systems.rpg.RpgPlayer;
import me.dannynguyen.aethel.systems.rpg.Status;
import me.dannynguyen.aethel.systems.rpg.StatusType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a triggered passive ability.
 *
 * @author Danny Nguyen
 * @version 1.17.2
 * @since 1.16.16
 */
public class PassiveAbilityTrigger {
  /**
   * Passive ability.
   */
  private final PassiveAbility ability;

  /**
   * Passive ability's trigger data.
   */
  private final List<String> triggerData;

  /**
   * Passive ability's effect data.
   */
  private final List<String> effectData;

  /**
   * Associates a passive ability trigger with an ability.
   *
   * @param ability ability to be triggered
   */
  public PassiveAbilityTrigger(@NotNull PassiveAbility ability) {
    this.ability = Objects.requireNonNull(ability, "Null ability");
    this.triggerData = ability.getTriggerData();
    this.effectData = ability.getEffectData();
  }

  /**
   * Applies stack instances.
   *
   * @param targetUUID entity to receive stack instances
   */
  public void applyStackInstance(UUID targetUUID) {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    Map<StatusType, Status> statuses;
    if (!entityStatuses.containsKey(targetUUID)) {
      entityStatuses.put(targetUUID, new HashMap<>());
    }
    statuses = entityStatuses.get(targetUUID);

    StatusType statusType = StatusType.valueOf(ability.getAbilityType().toString());
    int stacks = Integer.parseInt(effectData.get(1));
    int ticks = Integer.parseInt(effectData.get(2));
    if (statuses.containsKey(statusType)) {
      statuses.get(statusType).addStacks(stacks, ticks);
    } else {
      statuses.put(statusType, new Status(targetUUID, statusType, stacks, ticks));
    }

    int cooldown = Integer.parseInt(triggerData.get(1));
    if (cooldown > 0) {
      ability.setOnCooldown(true);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> ability.setOnCooldown(false), cooldown);
    }
  }

  /**
   * Chains damage between entities.
   *
   * @param targetUUID source of chain damage location
   */
  public void chainDamage(UUID targetUUID) {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();

    double chainDamage = Double.parseDouble(effectData.get(1));
    double meters = Double.parseDouble(effectData.get(2));

    Map<LivingEntity, Integer> soakedTargets = new HashMap<>();
    getSoakedTargets(entityStatuses, soakedTargets, targetUUID, meters);

    for (LivingEntity livingEntity : soakedTargets.keySet()) {
      Map<StatusType, Status> statuses = entityStatuses.get(livingEntity.getUniqueId());
      if (livingEntity instanceof Player player) {
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
          RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(livingEntity.getUniqueId());
          double damage = chainDamage * (1 + statuses.get(StatusType.SOAKED).getStackAmount() / 50.0);
          player.damage(0.1);
          rpgPlayer.getHealth().damage(new PlayerDamageMitigation(player).mitigateProtectionResistance(damage));
        }
      } else {
        livingEntity.damage(0.1);
        livingEntity.setHealth(Math.max(0, livingEntity.getHealth() + 0.1 - chainDamage * (1 + statuses.get(StatusType.SOAKED).getStackAmount() / 50.0)));
      }
    }

    int cooldown = Integer.parseInt(triggerData.get(1));
    if (cooldown > 0) {
      ability.setOnCooldown(true);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> ability.setOnCooldown(false), cooldown);
    }
  }

  /**
   * Recursively finds new soaked targets around the source entity.
   *
   * @param entityStatuses entity statuses
   * @param soakedTargets  soaked targets
   * @param targetUUID     source entity
   * @param meters         distance
   */
  private void getSoakedTargets(Map<UUID, Map<StatusType, Status>> entityStatuses, Map<LivingEntity, Integer> soakedTargets, UUID targetUUID, Double meters) {
    List<LivingEntity> newSoakedTargets = new ArrayList<>();
    for (Entity entity : Bukkit.getEntity(targetUUID).getNearbyEntities(meters, meters, meters)) {
      if (entity instanceof LivingEntity livingEntity) {
        UUID livingEntityUUID = livingEntity.getUniqueId();
        if (entityStatuses.containsKey(livingEntityUUID) && entityStatuses.get(livingEntityUUID).containsKey(StatusType.SOAKED)) {
          if (!soakedTargets.containsKey(livingEntity)) {
            newSoakedTargets.add(livingEntity);
            soakedTargets.put(livingEntity, entityStatuses.get(livingEntityUUID).get(StatusType.SOAKED).getStackAmount());
          }
        }
      }
    }
    if (newSoakedTargets.isEmpty()) {
      return;
    }
    for (LivingEntity livingEntity : newSoakedTargets) {
      getSoakedTargets(entityStatuses, soakedTargets, livingEntity.getUniqueId(), meters);
    }
  }
}
