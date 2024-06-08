package me.bam6561.aethelplugin.rpg;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.rpg.StatusType;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents {@link StatusType statuses} that affect entities.
 *
 * @author Danny Nguyen
 * @version 1.23.10
 * @since 1.14.7
 */
public class Status {
  /**
   * Entity UUID.
   */
  private final UUID uuid;

  /**
   * {@link StatusType}
   */
  private final StatusType type;

  /**
   * If stack instances are cumulative.
   */
  private final boolean isCumulative;

  /**
   * Individual stack applications.
   * <p>
   * Stack instances are represented by their Bukkit task ID.
   */
  private final Map<Integer, Integer> stackInstances = new HashMap<>();

  /**
   * Number of stacks.
   */
  private int stackAmount;

  /**
   * Associates a new status with its initial stacks and application.
   *
   * @param uuid   entity uuid
   * @param type   {@link StatusType}
   * @param stacks initial amount of stacks
   * @param ticks  initial stack application duration
   */
  public Status(@NotNull UUID uuid, @NotNull StatusType type, int stacks, int ticks) {
    this.uuid = Objects.requireNonNull(uuid, "Null uuid");
    this.type = Objects.requireNonNull(type, "Null status type");
    this.isCumulative = type.isCumulative();
    int taskId = Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> removeStacks(stacks), ticks).getTaskId();
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> stackInstances.remove(taskId), ticks);
    this.stackAmount = stacks;
    this.stackInstances.put(taskId, stacks);
  }

  /**
   * Adds a number of stacks to the {@link StatusType}.
   *
   * @param stacks number of stacks to add
   * @param ticks  duration in ticks
   */
  public void addStacks(int stacks, int ticks) {
    int taskId = Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> removeStacks(stacks), ticks).getTaskId();
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> stackInstances.remove(taskId), ticks);
    if (isCumulative) {
      stackAmount = stackAmount + stacks;
    } else {
      if (stacks > stackAmount) {
        stackAmount = stacks;
      }
    }
    stackInstances.put(taskId, stacks);
  }

  /**
   * Removes a number of stacks from the {@link StatusType}.
   *
   * @param stacks number of stacks to remove
   */
  private void removeStacks(int stacks) {
    if (isCumulative) {
      stackAmount = stackAmount - stacks;
    } else {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        int highest = Integer.MIN_VALUE;
        for (int stackInstance : stackInstances.values()) {
          if (stackInstance >= highest) {
            highest = stackInstance;
          }
        }
        stackAmount = highest;
      }, 1);
    }
    removeStatusTypeIfEmpty();
  }

  /**
   * Removes the {@link StatusType} if no stack instances remain.
   */
  private void removeStatusTypeIfEmpty() {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      if (stackInstances.isEmpty()) {
        Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
        if (entityStatuses.get(uuid) != null) {
          Map<StatusType, Status> statuses = entityStatuses.get(uuid);
          statuses.remove(type);
          if (statuses.isEmpty()) {
            entityStatuses.remove(uuid);
          }
        }
      }
    }, 1);
  }

  /**
   * Gets the {@link StatusType}'s stack durations.
   *
   * @return {@link StatusType}'s stack durations
   */
  @NotNull
  public Map<Integer, Integer> getStackInstances() {
    return this.stackInstances;
  }

  /**
   * Gets the {@link StatusType}'s number of stacks.
   *
   * @return number of {@link StatusType}'s stacks
   */
  public int getStackAmount() {
    return this.stackAmount;
  }
}
