package me.dannynguyen.aethel.systems.rpg;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Types of health conditions.
 *
 * @author Danny Nguyen
 * @version 1.13.12
 * @since 1.13.10
 */
public enum RpgHealthCondition {
  /**
   * Below max health.
   */
  WOUNDED,

  /**
   * At max health.
   */
  NORMAL,

  /**
   * Above max health.
   */
  OVERSHIELD;

  /**
   * Gets the RPG player's health condition.
   *
   * @param uuid player's UUID
   * @return RPG health condition
   */
  @Nullable
  public static RpgHealthCondition getCondition(@NotNull UUID uuid) {
    RpgHealth health = PluginData.rpgSystem.getRpgPlayers().get(Objects.requireNonNull(uuid, "Null uuid")).getHealth();
    double currentHealth = health.getCurrentHealth();
    double maxHealth = health.getMaxHealth();
    if (currentHealth < maxHealth) {
      return WOUNDED;
    } else if (currentHealth == maxHealth) {
      return NORMAL;
    } else if (currentHealth > maxHealth) {
      return OVERSHIELD;
    }
    return null;
  }
}