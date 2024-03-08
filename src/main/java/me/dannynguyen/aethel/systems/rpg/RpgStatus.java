package me.dannynguyen.aethel.systems.rpg;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents statuses that affect Living Entities.
 *
 * @author Danny Nguyen
 * @version 1.14.8
 * @since 1.14.7
 */
public class RpgStatus {
  /**
   * Number of status stacks.
   */
  private int stackAmount;

  /**
   * Status's individual stack applications.
   * <p>
   * Stack applications are represented by their Bukkit task ID.
   * </p>
   */
  private final Map<Integer, Integer> stackApplications = new HashMap<>();

  /**
   * Associates a new status with its initial stacks and application.
   *
   * @param stackAmount initial amount of stacks
   * @param taskId      initial stack task ID
   */
  public RpgStatus(int stackAmount, int taskId) {
    this.stackAmount = stackAmount;
    this.stackApplications.put(taskId, stackAmount);
  }

  /**
   * Adds a number of stacks to the status.
   *
   * @param stacks number of stacks to add
   * @param taskId stack task ID
   */
  public void addStacks(int stacks, int taskId) {
    setStackAmount(stackAmount + stacks);
    stackApplications.put(taskId, stacks);
  }

  /**
   * Removes a number of stacks from the status.
   *
   * @param stacks number of stacks to remove
   */
  public void removeStacks(int stacks) {
    setStackAmount(stackAmount - stacks);
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
   * Gets the status's stack durations.
   *
   * @return status's stack durations
   */
  public Map<Integer, Integer> getStackApplications() {
    return this.stackApplications;
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
