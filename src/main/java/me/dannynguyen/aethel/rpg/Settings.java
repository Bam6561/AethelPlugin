package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Directory;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.abilities.ActiveAbility;
import me.dannynguyen.aethel.utils.entity.HealthChange;
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
 * @version 1.25.8
 * @since 1.16.4
 */
public class Settings {
  /**
   * Settings owner's UUID.
   */
  private final UUID uuid;

  /**
   * {@link Equipment} {@link ActiveAbility} right click binds by {@link RpgEquipmentSlot}.
   */
  private final Map<RpgEquipmentSlot, Set<Integer>> activeAbilityRightClickBoundEquipmentSlots = new HashMap<>();

  /**
   * {@link Equipment} {@link ActiveAbility} right click binds by hotbar slot.
   */
  private final Map<Integer, Set<RpgEquipmentSlot>> activeAbilityRightClickBoundHotbar = new HashMap<>();

  /**
   * {@link Equipment} {@link ActiveAbility} crouch binds by {@link RpgEquipmentSlot}.
   */
  private final Map<RpgEquipmentSlot, Set<Integer>> activeAbilityCrouchBoundEquipmentSlots = new HashMap<>();

  /**
   * {@link Equipment} {@link ActiveAbility} crouch binds by hotbar slot.
   */
  private final Map<Integer, Set<RpgEquipmentSlot>> activeAbilityCrouchBoundHotbar = new HashMap<>();

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
    createActiveAbilityRightClickBinds();
    createActiveAbilityCrouchBinds();
    loadSettings();
  }

  /**
   * Creates a blank sets of {@link Equipment} {@link ActiveAbility} right click binds.
   */
  private void createActiveAbilityRightClickBinds() {
    for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
      activeAbilityRightClickBoundEquipmentSlots.put(eSlot, new HashSet<>());
    }
    for (int i = 0; i < 10; i++) {
      activeAbilityRightClickBoundHotbar.put(i, new HashSet<>());
    }
  }

  /**
   * Creates a blank sets of {@link Equipment} {@link ActiveAbility} crouch binds.
   */
  private void createActiveAbilityCrouchBinds() {
    for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
      activeAbilityCrouchBoundEquipmentSlots.put(eSlot, new HashSet<>());
    }
    for (int i = 0; i < 10; i++) {
      activeAbilityCrouchBoundHotbar.put(i, new HashSet<>());
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
            activeAbilityRightClickBoundEquipmentSlots.get(eSlot).add(hotbarSlot);
            activeAbilityRightClickBoundHotbar.get(hotbarSlot).add(eSlot);
          }
        }
        slotOrder++;
      }

      settings = scanner.nextLine().split(", ");
      slotOrder = 0;
      for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
        for (String hotbarString : settings[slotOrder].split(" ")) {
          if (!hotbarString.isBlank()) {
            int hotbarSlot = Integer.parseInt(hotbarString);
            activeAbilityCrouchBoundEquipmentSlots.get(eSlot).add(hotbarSlot);
            activeAbilityCrouchBoundHotbar.get(hotbarSlot).add(eSlot);
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

      StringBuilder activeAbilityRightClickBinds = new StringBuilder();
      for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
        StringBuilder activeAbilityRightClickBind = new StringBuilder();
        for (int hotbarSlot : activeAbilityRightClickBoundEquipmentSlots.get(eSlot)) {
          activeAbilityRightClickBind.append(hotbarSlot).append(" ");
        }
        activeAbilityRightClickBinds.append(activeAbilityRightClickBind).append(" , ");
      }
      fw.write(activeAbilityRightClickBinds.substring(0, activeAbilityRightClickBinds.length() - 2));
      fw.write("\n");

      StringBuilder activeAbilityCrouchBinds = new StringBuilder();
      for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
        StringBuilder activeAbilityCrouchBind = new StringBuilder();
        for (int hotbarSlot : activeAbilityCrouchBoundEquipmentSlots.get(eSlot)) {
          activeAbilityCrouchBind.append(hotbarSlot).append(" ");
        }
        activeAbilityCrouchBinds.append(activeAbilityCrouchBind).append(" , ");
      }
      fw.write(activeAbilityCrouchBinds.substring(0, activeAbilityCrouchBinds.length() - 2));
      fw.write("\n");

      fw.write(healthBarVisible + " " + healthActionVisible);
      fw.close();
    } catch (IOException ex) {
      Bukkit.getLogger().warning("[Aethel] Failed to write " + uuid + "'s settings to file.");
    }
  }

  /**
   * Resets {@link Equipment} {@link ActiveAbility} right click binds.
   */
  public void resetActiveAbilityRightClickBinds() {
    for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
      activeAbilityRightClickBoundEquipmentSlots.put(eSlot, new HashSet<>());
    }
    for (int i = 0; i < 10; i++) {
      activeAbilityRightClickBoundHotbar.put(i, new HashSet<>());
    }
  }

  /**
   * Sets the {@link Equipment} {@link ActiveAbility} right click bind.
   *
   * @param eSlot       {@link RpgEquipmentSlot}
   * @param hotbarSlots hotbar slots
   */
  public void setActiveAbilityRightClickBind(@NotNull RpgEquipmentSlot eSlot, Set<Integer> hotbarSlots) {
    activeAbilityRightClickBoundEquipmentSlots.get(Objects.requireNonNull(eSlot, "Null slot")).addAll(Objects.requireNonNull(hotbarSlots, "Null hotbar slots"));
    for (int hotbarSlot : hotbarSlots) {
      activeAbilityRightClickBoundHotbar.get(hotbarSlot).add(eSlot);
    }
  }

  /**
   * Resets {@link Equipment} {@link ActiveAbility} crouch binds.
   */
  public void resetActiveAbilityCrouchBinds() {
    for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
      activeAbilityCrouchBoundEquipmentSlots.put(eSlot, new HashSet<>());
    }
    for (int i = 0; i < 10; i++) {
      activeAbilityCrouchBoundHotbar.put(i, new HashSet<>());
    }
  }

  /**
   * Sets the {@link Equipment} {@link ActiveAbility} crouch bind.
   *
   * @param eSlot       {@link RpgEquipmentSlot}
   * @param hotbarSlots hotbar slots
   */
  public void setActiveAbilityCrouchBind(@NotNull RpgEquipmentSlot eSlot, Set<Integer> hotbarSlots) {
    activeAbilityCrouchBoundEquipmentSlots.get(Objects.requireNonNull(eSlot, "Null slot")).addAll(Objects.requireNonNull(hotbarSlots, "Null hotbar slots"));
    for (int hotbarSlot : hotbarSlots) {
      activeAbilityCrouchBoundHotbar.get(hotbarSlot).add(eSlot);
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
      new HealthChange(Bukkit.getPlayer(uuid)).updateDisplays();
      healthBar.removeAll();
      healthBar.addPlayer(Bukkit.getPlayer(uuid));
    }
  }

  /**
   * Toggles the visibility of health in the action bar.
   */
  public void toggleHealthActionVisibility() {
    healthActionVisible = !healthActionVisible;
  }

  /**
   * Gets {@link Equipment}  {@link ActiveAbility} right click binds by {@link RpgEquipmentSlot}.
   *
   * @return {@link Equipment} {@link ActiveAbility} right click binds by {@link RpgEquipmentSlot}
   */
  @NotNull
  public Map<RpgEquipmentSlot, Set<Integer>> getActiveAbilityRightClickBoundEquipmentSlots() {
    return this.activeAbilityRightClickBoundEquipmentSlots;
  }

  /**
   * Gets {@link Equipment} {@link ActiveAbility} right click binds by hotbar slot.
   *
   * @return {@link Equipment} {@link ActiveAbility} right click binds by hotbar slot.
   */
  @NotNull
  public Map<Integer, Set<RpgEquipmentSlot>> getActiveAbilityRightClickBoundHotbar() {
    return this.activeAbilityRightClickBoundHotbar;
  }

  /**
   * Gets {@link Equipment}  {@link ActiveAbility} crouch binds by {@link RpgEquipmentSlot}.
   *
   * @return {@link Equipment} {@link ActiveAbility} crouch binds by {@link RpgEquipmentSlot}
   */
  @NotNull
  public Map<RpgEquipmentSlot, Set<Integer>> getActiveAbilityCrouchBoundEquipmentSlots() {
    return this.activeAbilityCrouchBoundEquipmentSlots;
  }

  /**
   * Gets {@link Equipment} {@link ActiveAbility} crouch binds by hotbar slot.
   *
   * @return {@link Equipment} {@link ActiveAbility} crouch binds by hotbar slot.
   */
  @NotNull
  public Map<Integer, Set<RpgEquipmentSlot>> getActiveAbilityCrouchBoundHotbar() {
    return this.activeAbilityCrouchBoundHotbar;
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
