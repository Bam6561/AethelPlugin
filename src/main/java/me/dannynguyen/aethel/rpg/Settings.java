package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Directory;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.abilities.ActiveAbility;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Represents an {@link RpgPlayer}'s settings.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.16.4
 */
public class Settings {
  /**
   * Settings owner's UUID.
   */
  private final UUID uuid;

  /**
   * {@link ActiveAbility} crouch binds.
   */
  private final Map<RpgEquipmentSlot, Integer> activeAbilityCrouchBinds = createBlankActiveAbilityCrouchBinds();

  /**
   * If {@link Health health bar} visible.
   */
  private boolean healthBarVisible = false;

  /**
   * If {@link Health health in action bar} visible.
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
   * Creates a blank set of {@link ActiveAbility} crouch binds.
   *
   * @return blank {@link ActiveAbility} crouch binds
   */
  private Map<RpgEquipmentSlot, Integer> createBlankActiveAbilityCrouchBinds() {
    Map<RpgEquipmentSlot, Integer> activeAbilityCrouchBinds = new HashMap<>();
    for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
      activeAbilityCrouchBinds.put(eSlot, -1);
    }
    return activeAbilityCrouchBinds;
  }

  /**
   * Initializes the player's settings from a file if it exists.
   */
  private void initializeSettings() {
    File file = new File(Directory.SETTINGS.getFile().getPath() + "/" + uuid.toString() + "_set.txt");
    if (file.exists()) {
      try {
        Scanner scanner = new Scanner(file);
        String[] settings = scanner.nextLine().split(" ");
        activeAbilityCrouchBinds.put(RpgEquipmentSlot.HAND, Integer.parseInt(settings[0]));
        activeAbilityCrouchBinds.put(RpgEquipmentSlot.OFF_HAND, Integer.parseInt(settings[1]));
        activeAbilityCrouchBinds.put(RpgEquipmentSlot.HEAD, Integer.parseInt(settings[2]));
        activeAbilityCrouchBinds.put(RpgEquipmentSlot.CHEST, Integer.parseInt(settings[3]));
        activeAbilityCrouchBinds.put(RpgEquipmentSlot.LEGS, Integer.parseInt(settings[4]));
        activeAbilityCrouchBinds.put(RpgEquipmentSlot.FEET, Integer.parseInt(settings[5]));
        activeAbilityCrouchBinds.put(RpgEquipmentSlot.NECKLACE, Integer.parseInt(settings[6]));
        activeAbilityCrouchBinds.put(RpgEquipmentSlot.RING, Integer.parseInt(settings[7]));

        settings = scanner.nextLine().split(" ");
        healthBarVisible = Boolean.parseBoolean(settings[0]);
        healthActionVisible = Boolean.parseBoolean(settings[1]);
        scanner.close();
      } catch (IOException ex) {
        Bukkit.getLogger().warning(Message.UNABLE_TO_READ_FILE.getMessage() + file.getName());
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
      for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
        fw.write(activeAbilityCrouchBinds.get(eSlot) + " ");
      }
      fw.write("\n");
      fw.write(healthBarVisible + " " + healthActionVisible);
      fw.close();
    } catch (IOException ex) {
      Bukkit.getLogger().warning("[Aethel] Failed to write " + uuid + "'s settings to file.");
    }
  }

  /**
   * Resets {@link ActiveAbility} crouch binds.
   */
  public void resetActiveAbilityCrouchBinds() {
    for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
      activeAbilityCrouchBinds.put(eSlot, -1);
    }
  }

  /**
   * Sets the {@link ActiveAbility} crouch bind.
   *
   * @param eSlot    {@link RpgEquipmentSlot}
   * @param heldSlot hotbar slot
   */
  public void setActiveAbilityCrouchBind(RpgEquipmentSlot eSlot, int heldSlot) {
    activeAbilityCrouchBinds.put(eSlot, heldSlot);
  }

  /**
   * Toggles the visibility of the {@link Health health bar}.
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
   * Toggles the visibility of {@link Health health in the action bar}.
   */
  public void toggleHealthActionVisibility() {
    healthActionVisible = !healthActionVisible;
  }

  /**
   * Gets {@link ActiveAbility} crouch binds.
   *
   * @return {@link ActiveAbility} crouch binds
   */
  public Map<RpgEquipmentSlot, Integer> getActiveAbilityCrouchBinds() {
    return this.activeAbilityCrouchBinds;
  }

  /**
   * Gets if {@link Health health bar} is displayed.
   *
   * @return if {@link Health health bar} displayed
   */
  public boolean isHealthBarVisible() {
    return this.healthBarVisible;
  }

  /**
   * Gets if {@link Health health in the action bar} is displayed.
   *
   * @return if {@link Health health in the action bar} displayed
   */
  public boolean isHealthActionVisible() {
    return this.healthActionVisible;
  }
}
