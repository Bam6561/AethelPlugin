package me.dannynguyen.aethel.commands.playerstats;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * PlayerStatsSend is a utility class that sends statistics to
 * the player and saves their most recent statistic lookup.
 *
 * @author Danny Nguyen
 * @version 1.9.0
 * @since 1.4.10
 */
public class PlayerStatsSend {
  /**
   * Sends a statistic value.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void sendStat(InventoryClickEvent e, Player user) {
    String statOwner = user.getMetadata(PluginPlayerMeta.Namespace.PLAYER.namespace).get(0).asString();
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(statOwner);

    String itemName = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));

    Statistic stat = Statistic.valueOf(itemName.replace(" ", "_").toUpperCase());
    String statNameString = ChatColor.DARK_PURPLE + statOwner + " " + ChatColor.YELLOW + itemName;
    String statValueString = formatStatValue(itemName, stat, requestedPlayer);

    if (!e.getClick().isShiftClick()) {
      user.sendMessage(statNameString + " " + statValueString);
    } else {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.sendMessage(PluginMessage.Success.NOTIFICATION_GLOBAL.message
            + statNameString + " " + statValueString);
      }
      PluginData.playerStatsData.addToPastStats(statNameString, List.of(statValueString));
    }
  }

  /**
   * Sends a substatistic value.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void sendSubstat(InventoryClickEvent e, Player user) {
    String statOwner = user.getMetadata(PluginPlayerMeta.Namespace.PLAYER.namespace).get(0).asString();
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(statOwner);

    String itemName = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
    String substatName = ChatColor.stripColor(itemName.replace(" ", "_").toUpperCase());

    String statCategory = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    List<String> statValues = new ArrayList<>();

    if (statCategory.equals("Entity Types")) {
      loadEntityTypeSubstatValues(requestedPlayer, substatName, statValues);
    } else {
      loadMaterialSubstatValues(requestedPlayer, substatName, statValues);
    }

    String statName = ChatColor.DARK_PURPLE + statOwner + " " + ChatColor.GOLD + itemName;
    StringBuilder message = new StringBuilder(statName);
    for (String value : statValues) {
      message.append(" ").append(value);
    }

    if (!e.getClick().isShiftClick()) {
      user.sendMessage(message.toString());
    } else {
      if (!user.getName().equals(statOwner)) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(PluginMessage.Success.NOTIFICATION_GLOBAL.message +
              ChatColor.DARK_PURPLE + user.getName() + ChatColor.WHITE + " -> " + message);
        }
      } else {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(PluginMessage.Success.NOTIFICATION_GLOBAL.message + message);
        }
      }
    }
    PluginData.playerStatsData.addToPastStats(statName, statValues);
  }

  /**
   * Formats a statistic's value based on its name.
   *
   * @param itemName        item name
   * @param stat            stat
   * @param requestedPlayer requested player
   * @return formatted statistic value
   */
  private static String formatStatValue(String itemName, Statistic stat,
                                        OfflinePlayer requestedPlayer) {
    String statValueString;
    switch (itemName) {
      case "Play One Minute", "Time Since Death",
          "Time Since Rest", "Total World Time" -> statValueString =
          ChatColor.WHITE + tickTimeConversion(requestedPlayer.getStatistic(stat));
      default -> {
        if (!itemName.contains("One Cm")) {
          statValueString =
              ChatColor.WHITE + String.valueOf(requestedPlayer.getStatistic(stat));
        } else {
          statValueString =
              ChatColor.WHITE + String.valueOf(requestedPlayer.getStatistic(stat) / 100) + " meters";
        }
      }
    }
    return statValueString;
  }

  /**
   * Gets a time duration in ticks and converts to readable conventional time.
   *
   * @return conventional time duration
   */
  private static String tickTimeConversion(long ticks) {
    long days = ticks / 1728000L % 30;
    long hours = ticks / 72000L % 24;
    long minutes = ticks / 1200L % 60;
    return (days == 0 ? "" : days + "d ") + (hours == 0 ? "" : hours + "h ") +
        (minutes == 0 ? "" : minutes + "m ");
  }

  /**
   * Retrieves the requested player's specific entity type substat values.
   *
   * @param requestedPlayer requested player
   * @param substatName     substat name
   * @param statValues      substat values
   */
  private static void loadEntityTypeSubstatValues(OfflinePlayer requestedPlayer, String substatName,
                                                  List<String> statValues) {
    EntityType entityType = EntityType.valueOf(substatName);

    int kills = requestedPlayer.getStatistic(Statistic.KILL_ENTITY, entityType);
    int deaths = requestedPlayer.getStatistic(Statistic.ENTITY_KILLED_BY, entityType);

    statValues.add(ChatColor.YELLOW + "Killed " + ChatColor.WHITE + kills);
    statValues.add(ChatColor.YELLOW + "Deaths by " + ChatColor.WHITE + deaths);
  }

  /**
   * Retrieves the requested player's specific material substat values.
   *
   * @param requestedPlayer requested player
   * @param substatName     substat name
   * @param statValues      substat values
   */
  private static void loadMaterialSubstatValues(OfflinePlayer requestedPlayer, String substatName,
                                                List<String> statValues) {
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
}