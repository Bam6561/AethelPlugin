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
 * @version 1.23.4
 * @since 1.16.4
 */
public class Settings {
  /**
   * Settings owner's UUID.
   */
  private final UUID uuid;

  /**
   * {@link ActiveAbility} binds by {@link RpgEquipmentSlot}.
   */
  private final Map<RpgEquipmentSlot, Set<Integer>> abilityBoundEquipmentSlots = new HashMap<>();

  /**
   * {@link ActiveAbility} binds by hotbar slot.
   */
  private final Map<Integer, Set<RpgEquipmentSlot>> abilityBoundHotbar = new HashMap<>();

  /**
   * If health bar visible.
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
    createActiveAbilityBinds();
    loadSettings();
  }

  /**
   * Creates a blank sets of {@link ActiveAbility} binds.
   */
  private void createActiveAbilityBinds() {
    for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
      abilityBoundEquipmentSlots.put(eSlot, new HashSet<>());
    }
    for (int i = 0; i < 10; i++) {
      abilityBoundHotbar.put(i, new HashSet<>());
    }
  }

  /**
   * Loads the player's settings from a file if it exists.
   */
  private void loadSettings() {
    File file = new File(Directory.SETTINGS.getFile().getPath() + "/" + uuid.toString() + "_set.txt");
    if (!file.exists()) {
      return;
    }

    try {
      Scanner scanner = new Scanner(file);

      String[] settings = scanner.nextLine().split(", ");
      int slotOrder = 0;
      for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
        for (String hotbarString : settings[slotOrder].split(" ")) {
          if (!hotbarString.isBlank()) {
            int hotbarSlot = Integer.parseInt(hotbarString);
            abilityBoundEquipmentSlots.get(eSlot).add(hotbarSlot);
            abilityBoundHotbar.get(hotbarSlot).add(eSlot);
          }
        }
        slotOrder++;
      }

      settings = scanner.nextLine().split(" ");
      healthBarVisible = Boolean.parseBoolean(settings[0]);
      healthActionVisible = Boolean.parseBoolean(settings[1]);
      scanner.close();
    } catch (IOException ex) {
      Bukkit.getLogger().warning(Message.UNABLE_TO_READ_FILE.getMessage() + file.getName());
    }
  }

  /**
   * Saves the player's settings to a file.
   */
  public void saveSettings() {
    File file = new File(Directory.SETTINGS.getFile().getPath() + "/" + uuid.toString() + "_set.txt");
    try {
      FileWriter fw = new FileWriter(file);

      StringBuilder activeAbilityBinds = new StringBuilder();
      for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
        StringBuilder activeAbilityBind = new StringBuilder();
        for (int hotbarSlot : abilityBoundEquipmentSlots.get(eSlot)) {
          activeAbilityBind.append(hotbarSlot).append(" ");
        }
        activeAbilityBinds.append(activeAbilityBind).append(" , ");
      }
      fw.write(activeAbilityBinds.substring(0, activeAbilityBinds.length() - 2));

      fw.write("\n");
      fw.write(healthBarVisible + " " + healthActionVisible);
      fw.close();
    } catch (IOException ex) {
      Bukkit.getLogger().warning("[Aethel] Failed to write " + uuid + "'s settings to file.");
    }
  }

  /**
   * Resets {@link ActiveAbility} binds.
   */
  public void resetActiveAbilityBinds() {
    for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
      abilityBoundEquipmentSlots.put(eSlot, new HashSet<>());
    }
    for (int i = 0; i < 10; i++) {
      abilityBoundHotbar.put(i, new HashSet<>());
    }
  }

  /**
   * Sets the {@link ActiveAbility} bind.
   *
   * @param eSlot       {@link RpgEquipmentSlot}
   * @param hotbarSlots hotbar slots
   */
  public void setActiveAbilityBind(@NotNull RpgEquipmentSlot eSlot, Set<Integer> hotbarSlots) {
    abilityBoundEquipmentSlots.get(Objects.requireNonNull(eSlot, "Null slot")).addAll(Objects.requireNonNull(hotbarSlots, "Null hotbar slots"));
    for (int hotbarSlot : hotbarSlots) {
      abilityBoundHotbar.get(hotbarSlot).add(eSlot);
    }
  }

  /**
   * Toggles the visibility of the health bar.
   */
  public void toggleHealthBarVisibility() {
    BossBar healthBar = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getDisplays().getBar();
    if (isHealthBarVisible()) {
      healthBarVisible = false;
      healthBar.setVisible(false);
    } else {
      healthBarVisible = true;
      healthBar.setVisible(true);
      new HealthModification(Bukkit.getPlayer(uuid)).updateDisplays();
    }
  }

  /**
   * Toggles the visibility of health in the action bar.
   */
  public void toggleHealthActionVisibility() {
    healthActionVisible = !healthActionVisible;
  }

  /**
   * Gets {@link ActiveAbility} binds by {@link RpgEquipmentSlot}.
   *
   * @return {@link ActiveAbility} binds by {@link RpgEquipmentSlot}
   */
  @NotNull
  public Map<RpgEquipmentSlot, Set<Integer>> getAbilityBoundEquipmentSlots() {
    return this.abilityBoundEquipmentSlots;
  }

  /**
   * Gets {@link ActiveAbility} binds by hotbar slot.
   *
   * @return {@link ActiveAbility} binds by hotbar slot.
   */
  @NotNull
  public Map<Integer, Set<RpgEquipmentSlot>> getAbilityBoundHotbar() {
    return this.abilityBoundHotbar;
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
