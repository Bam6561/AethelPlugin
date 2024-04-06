package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.enums.rpg.StatusType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents RPG metadata in memory.
 *
 * @author Danny Nguyen
 * @version 1.20.12
 * @since 1.8.10
 */
public class RpgSystem {
  /**
   * {@link RpgPlayer RPG players}.
   */
  private final Map<UUID, RpgPlayer> rpgPlayers = new HashMap<>();

  /**
   * Players with sufficient {@link Enchantments} level requirements.
   */
  private final Map<Enchantment, Set<UUID>> sufficientEnchantments = new HashMap<>(Map.of(
      Enchantment.PROTECTION_FALL, new HashSet<>(),
      Enchantment.PROTECTION_FIRE, new HashSet<>()));

  /**
   * Living entities affected by {@link Buffs}.
   */
  private final Map<UUID, Buffs> buffs = new HashMap<>();

  /**
   * Living entities affected by {@link Status statuses}.
   */
  private final Map<UUID, Map<StatusType, Status>> statuses = new HashMap<>();

  /**
   * No parameter constructor.
   */
  public RpgSystem() {
  }

  /**
   * Loads an {@link RpgPlayer} into memory.
   *
   * @param player interacting player
   */
  public void loadRpgPlayer(@NotNull Player player) {
    rpgPlayers.put(Objects.requireNonNull(player, "Null player").getUniqueId(), new RpgPlayer(player));
  }

  /**
   * Gets {@link RpgPlayer RPG players}.
   *
   * @return {@link RpgPlayer RPG players}
   */
  @NotNull
  public Map<UUID, RpgPlayer> getRpgPlayers() {
    return this.rpgPlayers;
  }

  /**
   * Gets players with sufficient {@link Enchantments} level requirements.
   *
   * @return players with sufficient {@link Enchantments} level requirements
   */
  @NotNull
  public Map<Enchantment, Set<UUID>> getSufficientEnchantments() {
    return this.sufficientEnchantments;
  }

  /**
   * Gets entities affected by {@link Buffs}.
   * <p>
   * Note: Do not call any removals on the Map directly!
   * Use built-in function {@link Buffs#removeAllBuffs()}
   * to safely clean up attribute buffs and their timers instead.
   *
   * @return entities with {@link Buffs}
   */
  @NotNull
  public Map<UUID, Buffs> getBuffs() {
    return this.buffs;
  }

  /**
   * Gets entities affected by {@link Status statuses}.
   *
   * @return entities with {@link Status statuses}
   */
  @NotNull
  public Map<UUID, Map<StatusType, Status>> getStatuses() {
    return this.statuses;
  }
}
