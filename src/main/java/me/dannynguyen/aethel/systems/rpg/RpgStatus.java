package me.dannynguyen.aethel.systems.rpg;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents statuses that affect Living Entities.
 *
 * @author Danny Nguyen
 * @version 1.14.7
 * @since 1.14.7
 */
public class RpgStatus {
  /**
   * Status's timer tasks.
   */
  private final Set<Integer> timerIds = new HashSet<>();

  /**
   * Number of status stacks.
   */
  private int stacks = 0;
}
