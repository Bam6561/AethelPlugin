package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.Message;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.util.ItemReader;
import me.dannynguyen.aethel.util.TextFormatter;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents the retrieval and broadcast of a player statistic.
 * <p>
 * Used with {@link StatMenuClick}.
 *
 * @author Danny Nguyen
 * @version 1.17.18
 * @since 1.4.10
 */
class StatBroadcast {
  /**
   * Player who requested the value.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * OfflinePlayer object of the player statistic owner.
   */
  private final OfflinePlayer owner;

  /**
   * Player statistic owner's name.
   */
  private final String ownerName;

  /**
   * Requested player statistic.
   */
  private final String requestedStat;

  /**
   * Whether to broadcast the value to all online players.
   */
  private final boolean isGlobalBroadcast;

  /**
   * Associates a user with the player statistic request and whether to broadcast its value globally.
   *
   * @param e    inventory click event
   * @param user user
   */
  StatBroadcast(@NotNull InventoryClickEvent e, @NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.requestedStat = ChatColor.stripColor(ItemReader.readName(Objects.requireNonNull(e, "Null inventory click event").getCurrentItem()));
    this.uuid = user.getUniqueId();
    this.owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getTarget());
    this.ownerName = owner.getName();
    this.isGlobalBroadcast = e.isShiftClick();
  }

  /**
   * Sends a {@link StatisticCategory statistic} value.
   */
  protected void sendStat() {
    Statistic stat = Statistic.valueOf(TextFormatter.formatEnum(requestedStat));
    String statName = ChatColor.DARK_PURPLE + ownerName + " " + ChatColor.YELLOW + requestedStat;
    String statValue = formatStatValue(requestedStat, stat);
    String message = statName + " " + statValue;

    broadcastMessage(message, statName, List.of(statValue));
  }

  /**
   * Sends a {@link SubstatisticCategory substatistic} value.
   */
  protected void sendSubstat() {
    String substat = ChatColor.stripColor(TextFormatter.formatEnum(requestedStat));
    String category = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid).get(PlayerMeta.CATEGORY);
    String stat = ChatColor.DARK_PURPLE + ownerName + " " + ChatColor.GOLD + requestedStat;
    List<String> statValues = getSubstatValues(category, substat);

    StringBuilder message = new StringBuilder(stat);
    for (String value : statValues) {
      message.append(" ").append(value);
    }

    broadcastMessage(message.toString(), stat, statValues);
  }

  /**
   * Sends the statistic value to the requester or global chat.
   *
   * @param message    message to be sent
   * @param stat       statistic name
   * @param statValues statistic values
   */
  private void broadcastMessage(String message, String stat, List<String> statValues) {
    if (!isGlobalBroadcast) {
      user.sendMessage(message);
    } else {
      if (user.getUniqueId().equals(owner.getUniqueId())) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(Message.NOTIFICATION_GLOBAL.getMessage() + message);
        }
      } else {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(Message.NOTIFICATION_GLOBAL.getMessage() + ChatColor.DARK_PURPLE + user.getName() + ChatColor.WHITE + " -> " + message);
        }
      }
      Plugin.getData().getPastStatHistory().addPastStat(stat, statValues);
    }
  }

  /**
   * Formats a statistic's value based on its name.
   *
   * @param item item name
   * @param stat stat
   * @return formatted statistic value
   */
  private String formatStatValue(String item, Statistic stat) {
    switch (item) {
      case "Play One Minute", "Time Since Death", "Time Since Rest", "Total World Time" -> {
        return ChatColor.WHITE + ticksToDaysHoursMinutes(owner.getStatistic(stat));
      }
      default -> {
        if (!item.contains("One Cm")) {
          return ChatColor.WHITE + String.valueOf(owner.getStatistic(stat));
        } else {
          return ChatColor.WHITE + String.valueOf(owner.getStatistic(stat) / 100) + " meters";
        }
      }
    }
  }

  /**
   * Retrieves the requested player's substat values.
   *
   * @param category {@link SubstatisticCategory}
   * @param substat  substat name
   * @return substat value
   */
  private List<String> getSubstatValues(String category, String substat) {
    List<String> statValues = new ArrayList<>();
    if (category.equals("Entity Types")) {
      EntityType entityType = EntityType.valueOf(substat);

      int kills = owner.getStatistic(Statistic.KILL_ENTITY, entityType);
      int deaths = owner.getStatistic(Statistic.ENTITY_KILLED_BY, entityType);

      statValues.add(ChatColor.YELLOW + "Killed " + ChatColor.WHITE + kills);
      statValues.add(ChatColor.YELLOW + "Deaths by " + ChatColor.WHITE + deaths);
    } else {
      Material material = Material.valueOf(substat);

      int mined = owner.getStatistic(Statistic.MINE_BLOCK, material);
      int crafted = owner.getStatistic(Statistic.CRAFT_ITEM, material);
      int used = owner.getStatistic(Statistic.USE_ITEM, material);
      int broke = owner.getStatistic(Statistic.BREAK_ITEM, material);
      int pickedUp = owner.getStatistic(Statistic.PICKUP, material);
      int dropped = owner.getStatistic(Statistic.DROP, material);

      statValues.add(ChatColor.YELLOW + "Mined " + ChatColor.WHITE + mined);
      statValues.add(ChatColor.YELLOW + "Crafted " + ChatColor.WHITE + crafted);
      statValues.add(ChatColor.YELLOW + "Used " + ChatColor.WHITE + used);
      statValues.add(ChatColor.YELLOW + "Broke " + ChatColor.WHITE + broke);
      statValues.add(ChatColor.YELLOW + "Picked Up " + ChatColor.WHITE + pickedUp);
      statValues.add(ChatColor.YELLOW + "Dropped " + ChatColor.WHITE + dropped);
    }
    return statValues;
  }

  /**
   * Gets a time duration in ticks and converts to days, hours, and minutes.
   *
   * @param ticks ticks
   * @return days, hours, and minutes
   */
  private String ticksToDaysHoursMinutes(long ticks) {
    long days = ticks / 1728000L % 30;
    long hours = ticks / 72000L % 24;
    long minutes = ticks / 1200L % 60;
    return (days == 0 ? "" : days + "d ") + (hours == 0 ? "" : hours + "h ") + (minutes == 0 ? "" : minutes + "m ");
  }
}