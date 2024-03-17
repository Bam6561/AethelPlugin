package me.dannynguyen.aethel.systems.rpg.ability;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.rpg.Status;
import me.dannynguyen.aethel.systems.rpg.StatusType;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a triggered passive ability.
 *
 * @author Danny Nguyen
 * @version 1.16.16
 * @since 1.16.16
 */
public class PassiveAbilityTrigger {
  /**
   * Entity statuses.
   */
  private static final Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();

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
    Map<StatusType, Status> statuses;
    if (!entityStatuses.containsKey(targetUUID)) {
      entityStatuses.put(targetUUID, new HashMap<>());
    }
    statuses = entityStatuses.get(targetUUID);

    StatusType statusType = StatusType.valueOf(ability.getAbility().toString());
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
}
