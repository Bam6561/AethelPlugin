package me.dannynguyen.aethel.rpg;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents an {@link RpgPlayer}'s displays.
 *
 * @author Danny Nguyen
 * @version 1.22.20
 * @since 1.22.20
 */
public class Displays {
  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * Health bar display.
   */
  private final BossBar healthBar = Bukkit.createBossBar("Health", BarColor.RED, BarStyle.SEGMENTED_10);

  /**
   * Associates RPG displays with a player.
   *
   * @param player   interacting player
   * @param settings {@link Settings}
   */
  public Displays(@NotNull Player player, @NotNull Settings settings) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    healthBar.setVisible(Objects.requireNonNull(settings, "Null settings").isHealthBarVisible());
  }

  /**
   * Gets the displays owner's UUID.
   *
   * @return displays owner's UUID
   */
  @NotNull
  public UUID getUUID() {
    return this.uuid;
  }

  /**
   * Gets the health bar.
   *
   * @return health bar
   */
  @NotNull
  public BossBar getBar() {
    return this.healthBar;
  }
}
