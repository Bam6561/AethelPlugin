package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.ItemReader;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the retrieval and broadcast of a player statistic.
 *
 * @author Danny Nguyen
 * @version 1.9.15
 * @since 1.4.10
 */
public class StatMessage {
  /**
   * Player who requested the value.
   */
  private final Player user;

  /**
   * Player statistic owner's name.
   */
  private final String ownerName;

  /**
   * OfflinePlayer object of the player statistic owner.
   */
  private final OfflinePlayer requestedPlayer;

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
  public StatMessage(@NotNull InventoryClickEvent e, @NotNull Player user) {
    Objects.requireNonNull(e, "Null inventory click event");
    this.user = Objects.requireNonNull(user, "Null user");
    this.ownerName = user.getMetadata(PluginPlayerMeta.PLAYER.getMeta()).get(0).asString();
    this.requestedPlayer = Bukkit.getOfflinePlayer(ownerName);
    this.requestedStat = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
    this.isGlobalBroadcast = e.isShiftClick();
  }

  /**
   * Sends a statistic value.
   */
  public void sendStat() {
    Statistic stat = Statistic.valueOf(TextFormatter.formatEnum(requestedStat));
    String statName = ChatColor.DARK_PURPLE + ownerName + " " + ChatColor.YELLOW + requestedStat;
    String statValue = formatStatValue(requestedStat, stat);
    String message = statName + " " + statValue;

    broadcastMessage(message, statName, List.of(statValue));
  }

  /**
   * Sends a substatistic value.
   */
  public void sendSubstat() {
    String substatName = ChatColor.stripColor(TextFormatter.formatEnum(requestedStat));
    String category = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    String statName = ChatColor.DARK_PURPLE + ownerName + " " + ChatColor.GOLD + requestedStat;
    List<String> statValues = loadSubStatValues(category, substatName);

    StringBuilder message = new StringBuilder(statName);
    for (String value : statValues) {
      message.append(" ").append(value);
    }

    broadcastMessage(message.toString(), statName, statValues);
  }

  /**
   * Sends the statistic value to the requester or global chat.
   *
   * @param message    message to be sent
   * @param statName   statistic name
   * @param statValues statistic values
   */
  private void broadcastMessage(String message, String statName, List<String> statValues) {
    if (!isGlobalBroadcast) {
      user.sendMessage(message);
    } else {
      if (user.getName().equals(ownerName)) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(PluginMessage.Success.NOTIFICATION_GLOBAL.message + message);
        }
      } else {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(PluginMessage.Success.NOTIFICATION_GLOBAL.message + ChatColor.DARK_PURPLE + user.getName() + ChatColor.WHITE + " -> " + message);
        }
      }
      PluginData.pastStatHistory.addPastStat(statName, statValues);
    }
  }

  /**
   * Formats a statistic's value based on its name.
   *
   * @param itemName item name
   * @param stat     stat
   * @return formatted statistic value
   */
  private String formatStatValue(String itemName, Statistic stat) {
    switch (itemName) {
      case "Play One Minute", "Time Since Death", "Time Since Rest", "Total World Time" -> {
        return ChatColor.WHITE + tickTimeConversion(requestedPlayer.getStatistic(stat));
      }
      default -> {
        if (!itemName.contains("One Cm")) {
          return ChatColor.WHITE + String.valueOf(requestedPlayer.getStatistic(stat));
        } else {
          return ChatColor.WHITE + String.valueOf(requestedPlayer.getStatistic(stat) / 100) + " meters";
        }
      }
    }
  }

  /**
   * Retrieves the requested player's substat values.
   *
   * @param category    substat category
   * @param substatName substat name
   */
  private List<String> loadSubStatValues(String category, String substatName) {
    List<String> statValues = new ArrayList<>();
    if (category.equals("Entity Types")) {
      EntityType entityType = EntityType.valueOf(substatName);

      int kills = requestedPlayer.getStatistic(Statistic.KILL_ENTITY, entityType);
      int deaths = requestedPlayer.getStatistic(Statistic.ENTITY_KILLED_BY, entityType);

      statValues.add(ChatColor.YELLOW + "Killed " + ChatColor.WHITE + kills);
      statValues.add(ChatColor.YELLOW + "Deaths by " + ChatColor.WHITE + deaths);
    } else {
      Material material = Material.valueOf(substatName);

      int mined = requestedPlayer.getStatistic(Statistic.MINE_BLOCK, material);
      int crafted = requestedPlayer.getStatistic(Statistic.CRAFT_ITEM, material);
      int used = requestedPlayer.getStatistic(Statistic.USE_ITEM, material);
      int broke = requestedPlayer.getStatistic(Statistic.BREAK_ITEM, material);
      int pickedUp = requestedPlayer.getStatistic(Statistic.PICKUP, material);
      int dropped = requestedPlayer.getStatistic(Statistic.DROP, material);

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
   * Gets a time duration in ticks and converts to readable conventional time.
   *
   * @return conventional time duration
   */
  private String tickTimeConversion(long ticks) {
    long days = ticks / 1728000L % 30;
    long hours = ticks / 72000L % 24;
    long minutes = ticks / 1200L % 60;

    return (days == 0 ? "" : days + "d ") + (hours == 0 ? "" : hours + "h ") + (minutes == 0 ? "" : minutes + "m ");
  }
}
