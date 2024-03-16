package me.dannynguyen.aethel.systems.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.Directory;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

/**
 * Represents an RPG player's settings.
 *
 * @author Danny Nguyen
 * @version 1.16.4
 * @since 1.16.4
 */
public class Settings {
  /**
   * Settings owner's UUID.
   */
  private final UUID uuid;

  /**
   * if health bar visible.
   */
  private boolean healthBarVisible = false;

  /**
   * If health in action bar visible.
   */
  private boolean healthActionVisible = false;

  /**
   * Associates a player with their settings.
   *
   * @param uuid player uuid
   */
  public Settings(@NotNull UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid, "Null UUID");
    initializeSettings();
  }

  /**
   * Initializes the player's settings from a file if it exists.
   */
  private void initializeSettings() {
    File file = new File(Directory.SETTINGS.getFile().getPath() + "/" + uuid.toString() + "_set.txt");
    if (file.exists()) {
      try {
        Scanner scanner = new Scanner(file);
        healthBarVisible = Boolean.parseBoolean(scanner.nextLine());
        healthActionVisible = Boolean.parseBoolean(scanner.nextLine());
      } catch (IOException ex) {
        Bukkit.getLogger().warning("[Aethel] Unable to read file: " + file.getName());
      }
    }
  }

  /**
   * Saves the player's settings to a file.
   */
  public void saveSettings() {
    File file = new File(Directory.SETTINGS.getFile().getPath() + "/" + uuid.toString() + "_set.txt");
    try {
      FileWriter fw = new FileWriter(file);
      fw.write(healthBarVisible + "\n");
      fw.write(healthActionVisible + "\n");
      fw.close();
    } catch (IOException ex) {
      Bukkit.getLogger().warning("[Aethel] Failed to write " + uuid + "'s settings to file.");
    }
  }

  /**
   * Toggles the visibility of the health bar.
   */
  public void toggleHealthBarVisibility() {
    Health health = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getHealth();
    BossBar healthBar = health.getBar();
    if (isHealthBarVisible()) {
      healthBarVisible = false;
      healthBar.setVisible(false);
    } else {
      healthBarVisible = true;
      healthBar.setVisible(true);
      health.updateDisplays();
    }
  }

  /**
   * Toggles the visibility of health in the action bar.
   */
  public void toggleHealthActionVisibility() {
    healthActionVisible = !healthActionVisible;
  }

  /**
   * Gets if health bar is displayed.
   *
   * @return if health bar displayed
   */
  public boolean isHealthBarVisible() {
    return this.healthBarVisible;
  }

  /**
   * Gets if health in the action bar is displayed.
   *
   * @return if health in the action bar displayed
   */
  public boolean isHealthActionVisible() {
    return this.healthActionVisible;
  }
}
