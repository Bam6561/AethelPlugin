package me.dannynguyen.aethel.inventories.playerstat;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * PlayerStatSend sends statistics to the player
 * and saves their most recent statistic lookup.
 *
 * @author Danny Nguyen
 * @version 1.4.11
 * @since 1.4.10
 */
public class PlayerStatSend {
  /**
   * Sends a statistic value.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void sendStat(InventoryClickEvent e, Player player) {
    String statOwner = player.getMetadata("stat-owner").get(0).asString();
    String itemName = ChatColor.stripColor(new ItemReader().readItemName(e.getCurrentItem()));
    Statistic stat = Statistic.valueOf(itemName.replace(" ", "_").toUpperCase());

    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(statOwner);

    String statNameString = ChatColor.DARK_PURPLE + statOwner + " " + ChatColor.YELLOW + itemName;
    String statValueString = formatStatValue(itemName, stat, requestedPlayer);

    if (!e.getClick().isShiftClick()) {
      player.sendMessage(statNameString + " " + statValueString);
    } else {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.sendMessage(ChatColor.GREEN + "[!] " + statNameString + " " + statValueString);
      }
      AethelPlugin.getInstance().getResources().getPlayerStatData().
          addToPastStats(statNameString, Collections.singletonList(statValueString));
    }
  }

  /**
   * Sends a substatistic value.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void sendSubstat(InventoryClickEvent e, Player player) {
    String statOwner = player.getMetadata("stat-owner").get(0).asString();
    String itemName = ChatColor.stripColor(new ItemReader().readItemName(e.getCurrentItem()));
    String statCategory = player.getMetadata("stat-category").get(0).asString();
    String substatName = ChatColor.stripColor(itemName.replace(" ", "_").toUpperCase());

    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(statOwner);

    String statNameString;
    ArrayList<String> statValueStrings = new ArrayList<>();

    if (statCategory.equals("Entity Types")) {
      EntityType entityType = EntityType.valueOf(substatName);

      int kills = player.getStatistic(Statistic.KILL_ENTITY, entityType);
      int deaths = player.getStatistic(Statistic.ENTITY_KILLED_BY, entityType);

      statNameString = ChatColor.DARK_PURPLE + statOwner + " " + ChatColor.GOLD + itemName;
      statValueStrings.add(ChatColor.YELLOW + "Killed " + ChatColor.WHITE + kills);
      statValueStrings.add(ChatColor.YELLOW + "Killed By " + ChatColor.WHITE + deaths);
    } else {
      Material material = Material.valueOf(substatName);

      int mined = requestedPlayer.getStatistic(Statistic.MINE_BLOCK, material);
      int crafted = requestedPlayer.getStatistic(Statistic.CRAFT_ITEM, material);
      int used = requestedPlayer.getStatistic(Statistic.USE_ITEM, material);
      int broke = requestedPlayer.getStatistic(Statistic.BREAK_ITEM, material);
      int pickup = requestedPlayer.getStatistic(Statistic.PICKUP, material);
      int drop = requestedPlayer.getStatistic(Statistic.DROP, material);

      statNameString = ChatColor.DARK_PURPLE + statOwner + " " + ChatColor.GOLD + itemName;
      statValueStrings.add(ChatColor.YELLOW + "Mined " + ChatColor.WHITE + mined);
      statValueStrings.add(ChatColor.YELLOW + "Crafted " + ChatColor.WHITE + crafted);
      statValueStrings.add(ChatColor.YELLOW + "Used " + ChatColor.WHITE + used);
      statValueStrings.add(ChatColor.YELLOW + "Broke " + ChatColor.WHITE + broke);
      statValueStrings.add(ChatColor.YELLOW + "Picked Up " + ChatColor.WHITE + pickup);
      statValueStrings.add(ChatColor.YELLOW + "Dropped " + ChatColor.WHITE + drop);
    }

    StringBuilder message = new StringBuilder(statNameString);
    for (String valueString : statValueStrings) {
      message.append(" " + valueString);
    }

    if (!e.getClick().isShiftClick()) {
      player.sendMessage(message.toString());
    } else {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.sendMessage(ChatColor.GREEN + "[!] " + message);
      }
      AethelPlugin.getInstance().getResources().getPlayerStatData().
          addToPastStats(statNameString, statValueStrings);
    }
  }

  /**
   * Formats a statistic's value based on its name.
   *
   * @param itemName        item name
   * @param stat            stat
   * @param requestedPlayer requested player
   * @return formatted statistic value
   */
  private String formatStatValue(String itemName, Statistic stat,
                                 OfflinePlayer requestedPlayer) {
    String statValueString;
    switch (itemName) {
      case "Play One Minute", "Time Since Death", "Time Since Rest", "Total World Time" -> statValueString =
          ChatColor.WHITE + tickTimeConversion(Long.valueOf(requestedPlayer.getStatistic(stat)));
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
  private String tickTimeConversion(long ticks) {
    long days = ticks / 1728000L % 30;
    long hours = ticks / 72000L % 24;
    long minutes = ticks / 1200L % 60;
    return (days == 0 ? "" : days + "d ") + (hours == 0 ? "" : hours + "h ") + (minutes == 0 ? "" : minutes + "m ");
  }
}
