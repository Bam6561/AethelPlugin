package me.dannynguyen.aethel.systems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents RPG profiles in memory.
 *
 * @author Danny Nguyen
 * @version 1.10.7
 * @since 1.8.10
 */
public class RpgSystem {
  /**
   * RPG profiles.
   */
  private final Map<Player, RpgProfile> rpgProfiles = new HashMap<>();

  /**
   * Player held items.
   */
  private final Map<Player, ItemStack> playerHeldItemMap = new HashMap<>();

  /**
   * Loads an RPG profile into memory.
   *
   * @param player player
   */
  public void loadRpgPlayer(Player player) {
    RpgProfile rpgProfile = new RpgProfile(player);
    rpgProfile.loadEquipmentAttributes();
    rpgProfile.loadHealthBar();
    rpgProfiles.put(player, rpgProfile);
  }

  /**
   * Gets RPG profiles.
   *
   * @return RPG profiles
   */
  @NotNull
  public Map<Player, RpgProfile> getRpgProfiles() {
    return this.rpgProfiles;
  }

  /**
   * Gets player held items.
   *
   * @return player held items
   */
  @NotNull
  public Map<Player, ItemStack> getPlayerHeldItemMap() {
    return this.playerHeldItemMap;
  }
}
