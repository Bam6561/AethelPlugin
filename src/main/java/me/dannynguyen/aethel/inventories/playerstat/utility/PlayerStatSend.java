package me.dannynguyen.aethel.inventories.playerstat.utility;

import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * PlayerStatSend is a utility class that sends statistics to
 * the player and saves their most recent statistic lookup.
 *
 * @author Danny Nguyen
 * @version 1.5.5
 * @since 1.4.10
 */
public class PlayerStatSend {
  /**
   * Sends a statistic value.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void sendStat(InventoryClickEvent e, Player player) {
    String statOwner = player.getMetadata("page-owner").get(0).asString();
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(statOwner);

    String itemName = ChatColor.stripColor(ItemReader.readItemName(e.getCurrentItem()));

    Statistic stat = Statistic.valueOf(itemName.replace(" ", "_").toUpperCase());
    String statNameString = ChatColor.DARK_PURPLE + statOwner + " " + ChatColor.YELLOW + itemName;
    String statValueString = formatStatValue(itemName, stat, requestedPlayer);

    if (!e.getClick().isShiftClick()) {
      player.sendMessage(statNameString + " " + statValueString);
    } else {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.sendMessage(ChatColor.GREEN + "[!] " + statNameString + " " + statValueString);
      }
      AethelResources.playerStatData.addToPastStats(statNameString, Collections.singletonList(statValueString));
    }
  }

  /**
   * Sends a substatistic value.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void sendSubstat(InventoryClickEvent e, Player player) {
    String statOwner = player.getMetadata("page-owner").get(0).asString();
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(statOwner);

    String itemName = ChatColor.stripColor(ItemReader.readItemName(e.getCurrentItem()));
    String substatName = ChatColor.stripColor(itemName.replace(" ", "_").toUpperCase());

    String statCategory = player.getMetadata("category").get(0).asString();
    ArrayList<String> statValues = new ArrayList<>();

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
      player.sendMessage(message.toString());
    } else {
      if (!player.getName().equals(statOwner)) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(ChatColor.GREEN + "[!] " +
              ChatColor.DARK_PURPLE + player.getName() + ChatColor.WHITE + " -> " + message);
        }
      } else {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(ChatColor.GREEN + "[!] " + message);
        }
      }
    }
    AethelResources.playerStatData.addToPastStats(statName, statValues);
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
    return (days == 0 ? "" : days + "d ") + (hours == 0 ? "" : hours + "h ") + (minutes == 0 ? "" : minutes + "m ");
  }

  /**
   * Retrieves the requested player's specific entity type substat values.
   *
   * @param requestedPlayer requested player
   * @param substatName     substat's name
   * @param statValues      substat's values
   */
  private static void loadEntityTypeSubstatValues(OfflinePlayer requestedPlayer, String substatName,
                                                  ArrayList<String> statValues) {
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
   * @param substatName     substat's name
   * @param statValues      substat's values
   */
  private static void loadMaterialSubstatValues(OfflinePlayer requestedPlayer, String substatName,
                                                ArrayList<String> statValues) {
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
