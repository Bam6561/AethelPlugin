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
 * @version 1.18.7
 * @since 1.16.4
 */
public class Settings {
  /**
   * Settings owner's UUID.
   */
  private final UUID uuid;

  /**
   * {@link ActiveAbility} crouch binds by {@link RpgEquipmentSlot}.
   */
  private final Map<RpgEquipmentSlot, Integer> abilityBoundEquipmentSlots = new HashMap<>();

  /**
   * {@link ActiveAbility} crouch binds by hotbar slot.
   */
  private final Map<Integer, RpgEquipmentSlot> abilityBoundHotbar = new HashMap<>();

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
    createActiveAbilityBinds();
    initializeSettings();
  }

  /**
   * Creates a blank sets of {@link ActiveAbility} crouch binds.
   */
  private void createActiveAbilityBinds() {
    for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
      abilityBoundEquipmentSlots.put(eSlot, -1);
      abilityBoundHotbar.put(-1, eSlot);
    }
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
        abilityBoundEquipmentSlots.put(RpgEquipmentSlot.HAND, Integer.parseInt(settings[0]));
        abilityBoundEquipmentSlots.put(RpgEquipmentSlot.OFF_HAND, Integer.parseInt(settings[1]));
        abilityBoundEquipmentSlots.put(RpgEquipmentSlot.HEAD, Integer.parseInt(settings[2]));
        abilityBoundEquipmentSlots.put(RpgEquipmentSlot.CHEST, Integer.parseInt(settings[3]));
        abilityBoundEquipmentSlots.put(RpgEquipmentSlot.LEGS, Integer.parseInt(settings[4]));
        abilityBoundEquipmentSlots.put(RpgEquipmentSlot.FEET, Integer.parseInt(settings[5]));
        abilityBoundEquipmentSlots.put(RpgEquipmentSlot.NECKLACE, Integer.parseInt(settings[6]));
        abilityBoundEquipmentSlots.put(RpgEquipmentSlot.RING, Integer.parseInt(settings[7]));
        for (RpgEquipmentSlot eSlot : abilityBoundEquipmentSlots.keySet()) {
          abilityBoundHotbar.put(abilityBoundEquipmentSlots.get(eSlot), eSlot);
        }

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
        fw.write(abilityBoundEquipmentSlots.get(eSlot) + " ");
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
      abilityBoundEquipmentSlots.put(eSlot, -1);
      abilityBoundHotbar.put(-1, eSlot);
    }
  }

  /**
   * Sets the {@link ActiveAbility} crouch bind.
   *
   * @param eSlot    {@link RpgEquipmentSlot}
   * @param heldSlot hotbar slot
   */
  public void setActiveAbilityCrouchBind(@NotNull RpgEquipmentSlot eSlot, int heldSlot) {
    abilityBoundEquipmentSlots.put(Objects.requireNonNull(eSlot, "Null slot"), heldSlot);
    abilityBoundHotbar.put(heldSlot, eSlot);
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
   * Gets {@link ActiveAbility} crouch binds by {@link RpgEquipmentSlot}.
   *
   * @return {@link ActiveAbility} crouch binds by {@link RpgEquipmentSlot}
   */
  public Map<RpgEquipmentSlot, Integer> getAbilityBoundEquipmentSlots() {
    return this.abilityBoundEquipmentSlots;
  }

  /**
   * Gets {@link ActiveAbility} crouch binds by hotbar.
   *
   * @return {@link ActiveAbility} crouch binds by hotbar.
   */
  public Map<Integer, RpgEquipmentSlot> getAbilityBoundHotbar() {
    return this.abilityBoundHotbar;
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
