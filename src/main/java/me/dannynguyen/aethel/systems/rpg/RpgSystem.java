package me.dannynguyen.aethel.systems.rpg;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents RPG profiles in memory.
 *
 * @author Danny Nguyen
 * @version 1.12.0
 * @since 1.8.10
 */
public class RpgSystem {
  /**
   * RPG profiles.
   */
  private final Map<UUID, RpgProfile> rpgProfiles = new HashMap<>();

  /**
   * Player held items.
   */
  private final Map<UUID, ItemStack> playerHeldItemMap = new HashMap<>();

  /**
   * Loads an RPG profile into memory.
   *
   * @param playerUUID player's UUID
   */
  public void loadRpgPlayer(UUID playerUUID) {
    RpgProfile rpgProfile = new RpgProfile(playerUUID);
    rpgProfile.loadEquipmentAttributes();
    rpgProfile.loadHealthBar();
    rpgProfiles.put(playerUUID, rpgProfile);
  }

  /**
   * Gets RPG profiles.
   *
   * @return RPG profiles
   */
  @NotNull
  public Map<UUID, RpgProfile> getRpgProfiles() {
    return this.rpgProfiles;
  }

  /**
   * Gets player held items.
   *
   * @return player held items
   */
  @NotNull
  public Map<UUID, ItemStack> getPlayerHeldItemMap() {
    return this.playerHeldItemMap;
  }
}
