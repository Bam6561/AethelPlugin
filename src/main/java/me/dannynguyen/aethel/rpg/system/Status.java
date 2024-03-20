package me.dannynguyen.aethel.rpg.system;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.rpg.enums.StatusType;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents statuses that affect Living Entities.
 *
 * @author Danny Nguyen
 * @version 1.16.7
 * @since 1.14.7
 */
public class Status {
  /**
   * Entity UUID.
   */
  private final UUID uuid;

  /**
   * Status type.
   */
  private final StatusType statusType;

  /**
   * If stack instances are cumulative.
   */
  private final boolean isCumulative;

  /**
   * Status's individual stack applications.
   * <p>
   * Stack instances are represented by their Bukkit task ID.
   * </p>
   */
  private final Map<Integer, Integer> stackInstances = new HashMap<>();

  /**
   * Number of status stacks.
   */
  private int stackAmount;

  /**
   * Associates a new status with its initial stacks and application.
   *
   * @param uuid       entity uuid
   * @param statusType type of status
   * @param stacks     initial amount of stacks
   * @param ticks      initial stack application duration
   */
  public Status(@NotNull UUID uuid, @NotNull StatusType statusType, int stacks, int ticks) {
    this.uuid = Objects.requireNonNull(uuid, "Null uuid");
    this.statusType = Objects.requireNonNull(statusType, "Null status type");
    this.isCumulative = statusType.isCumulative();
    int taskId = Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> removeStacks(stacks), ticks).getTaskId();
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> stackInstances.remove(taskId), ticks);
    this.stackAmount = stacks;
    this.stackInstances.put(taskId, stacks);
  }

  /**
   * Adds a number of stacks to the status.
   *
   * @param stacks number of stacks to add
   * @param ticks  duration in ticks
   */
  public void addStacks(int stacks, int ticks) {
    int taskId = Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> removeStacks(stacks), ticks).getTaskId();
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> stackInstances.remove(taskId), ticks);
    if (isCumulative) {
      setStackAmount(stackAmount + stacks);
    } else {
      if (stacks > stackAmount) {
        setStackAmount(stacks);
      }
    }
    stackInstances.put(taskId, stacks);
  }

  /**
   * Removes a number of stacks from the status.
   *
   * @param stacks number of stacks to remove
   */
  public void removeStacks(int stacks) {
    if (isCumulative) {
      setStackAmount(stackAmount - stacks);
    } else {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        int highest = Integer.MIN_VALUE;
        for (int stackInstance : stackInstances.values()) {
          if (stackInstance >= highest) {
            highest = stackInstance;
          }
        }
        setStackAmount(highest);
      }, 1);
    }
    removeStatusTypeIfEmpty();
  }

  /**
   * Removes the status type if no stack instances remain.
   */
  private void removeStatusTypeIfEmpty() {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      if (stackInstances.isEmpty()) {
        Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
        if (entityStatuses.get(uuid) != null) {
          Map<StatusType, Status> statuses = entityStatuses.get(uuid);
          statuses.remove(statusType);
          if (statuses.isEmpty()) {
            entityStatuses.remove(uuid);
          }
        }
      }
    }, 1);
  }

  /**
   * Gets the status's stack durations.
   *
   * @return status's stack durations
   */
  @NotNull
  public Map<Integer, Integer> getStackInstances() {
    return this.stackInstances;
  }

  /**
   * Gets the status's number of stacks.
   *
   * @return number of status stacks
   */
  public int getStackAmount() {
    return this.stackAmount;
  }

  /**
   * Sets the status's amount of stacks
   *
   * @param stackAmount amount of stacks
   */
  public void setStackAmount(int stackAmount) {
    this.stackAmount = stackAmount;
  }
}
