package me.dannynguyen.aethel.systems.rpg;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents RPG players in memory.
 *
 * @author Danny Nguyen
 * @version 1.14.7
 * @since 1.8.10
 */
public class RpgSystem {
  /**
   * RPG players.
   */
  private final Map<UUID, RpgPlayer> rpgPlayers = new HashMap<>();

  /**
   * Players with sufficient enchantment level requirements.
   */
  private final Map<Enchantment, Set<UUID>> sufficientEnchantments = new HashMap<>(Map.of(
      Enchantment.PROTECTION_FALL, new HashSet<>(),
      Enchantment.PROTECTION_FIRE, new HashSet<>()));

  /**
   * Living entities affected by statuses.
   */
  private final Map<UUID, Map<StatusType, Status>> statuses = new HashMap<>();

  /**
   * No parameter constructor.
   */
  public RpgSystem() {

  }

  /**
   * Loads an RPG player into memory.
   *
   * @param player interacting player
   */
  public void loadRpgPlayer(@NotNull Player player) {
    rpgPlayers.put(Objects.requireNonNull(player, "Null player").getUniqueId(), new RpgPlayer(player));
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
   * Gets players with sufficient enchantment level requirements.
   *
   * @return players with sufficient enchantment level requirements
   */
  @NotNull
  public Map<Enchantment, Set<UUID>> getSufficientEnchantments() {
    return this.sufficientEnchantments;
  }

  /**
   * Gets entities affected by statuses.
   *
   * @return entities with statuses
   */
  @NotNull
  public Map<UUID, Map<StatusType, Status>> getStatuses() {
    return this.statuses;
  }
}
