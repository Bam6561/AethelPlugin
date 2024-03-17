package me.dannynguyen.aethel.utility;

/**
 * Calculates damage done and taken.
 *
 * @author Danny Nguyen
 * @version 1.16.13
 * @since 1.16.13
 */
public class DamageCalculator {
  /**
   * Utility methods only.
   */
  private DamageCalculator() {
  }

  /**
   * Mitigates damage based on protection and resistance levels.
   *
   * @param damage     damage
   * @param protection protection levels
   * @param resistance resistance levels
   * @return damage taken
   */
  public static double mitigateProtectionResistance(double damage, int protection, int resistance) {
    damage = damage - (damage * (protection * 0.04));
    damage = damage - (damage * (resistance * 0.05));
    return damage;
  }
}
