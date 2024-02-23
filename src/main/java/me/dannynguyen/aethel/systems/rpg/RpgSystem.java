package me.dannynguyen.aethel.systems.rpg;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents RPG players in memory.
 *
 * @author Danny Nguyen
 * @version 1.12.3
 * @since 1.8.10
 */
public class RpgSystem {
  /**
   * RPG players.
   */
  private final Map<UUID, RpgPlayer> rpgPlayers = new HashMap<>();

  /**
   * Player held items.
   */
  private final Map<UUID, ItemStack> playerHeldItemMap = new HashMap<>();

  /**
   * Loads an RPG player into memory.
   *
   * @param player interacting player
   */
  public void loadRpgPlayer(@NotNull Player player) {
    Objects.requireNonNull(player, "Null player");
    RpgPlayer rpgPlayer = new RpgPlayer(player);
    rpgPlayer.loadEquipmentAttributes();
    rpgPlayer.loadJewelry();
    rpgPlayer.loadHealthBar();
    rpgPlayers.put(player.getUniqueId(), rpgPlayer);
  }

  /**
   * Gets RPG players.
   *
   * @return RPG players
   */
  @NotNull
  public Map<UUID, RpgPlayer> getRpgPlayers() {
    return this.rpgPlayers;
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
