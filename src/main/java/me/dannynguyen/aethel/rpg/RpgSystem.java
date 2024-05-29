package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.enums.rpg.StatusType;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents RPG metadata in memory.
 *
 * @author Danny Nguyen
 * @version 1.27.0
 * @since 1.8.10
 */
public class RpgSystem {
  /**
   * Entities affected by {@link Buffs}.
   */
  private final Map<UUID, Buffs> buffs = new HashMap<>();

  /**
   * Entities affected by {@link Status statuses}.
   */
  private final Map<UUID, Map<StatusType, Status>> statuses = new HashMap<>();

  /**
   * Entities affected by Overshield.
   * <p>
   * Overshield is a condition when entities' current
   * health exceeds max health by a factor of x1.2.
   */
  private final Set<UUID> overshields = new HashSet<>();

  /**
   * {@link RpgPlayer RPG players}.
   */
  private final Map<UUID, RpgPlayer> rpgPlayers = new HashMap<>();

  /**
   * Players with sufficient {@link Equipment.Enchantments} level requirements.
   */
  private final Map<Enchantment, Set<UUID>> sufficientEnchantments = Map.of(
      Enchantment.FEATHER_FALLING, new HashSet<>(), Enchantment.FIRE_PROTECTION, new HashSet<>());

  /**
   * Players under max health.
   */
  private final Set<UUID> wounded = new HashSet<>();

  /**
   * No parameter constructor.
   */
  public RpgSystem() {
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

  /**
   * Gets entities affected by Overshield.
   *
   * @return entities affected by Overshield
   */
  @NotNull
  public Set<UUID> getOvershields() {
    return this.overshields;
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
   * Gets players with sufficient {@link Equipment.Enchantments} level requirements.
   *
   * @return players with sufficient {@link Equipment.Enchantments} level requirements
   */
  @NotNull
  public Map<Enchantment, Set<UUID>> getSufficientEnchantments() {
    return this.sufficientEnchantments;
  }

  /**
   * Gets players under max health.
   *
   * @return players under max health
   */
  @NotNull
  public Set<UUID> getWounded() {
    return this.wounded;
  }
}
