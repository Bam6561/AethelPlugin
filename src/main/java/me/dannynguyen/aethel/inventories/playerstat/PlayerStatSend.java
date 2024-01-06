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
 * @version 1.4.10
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
    String statValueString = ChatColor.WHITE + String.valueOf(requestedPlayer.getStatistic(stat));

    player.sendMessage(statNameString + ": " + statValueString);
    AethelPlugin.getInstance().getResources().getPlayerStatData().
        addToPastStats(statNameString, Collections.singletonList(statValueString));
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
      statValueStrings.add(ChatColor.YELLOW + "Killed: " + ChatColor.WHITE + kills);
      statValueStrings.add(ChatColor.YELLOW + "Killed By: " + ChatColor.WHITE + deaths);
    } else {
      Material material = Material.valueOf(substatName);

      int mined = requestedPlayer.getStatistic(Statistic.MINE_BLOCK, material);
      int crafted = requestedPlayer.getStatistic(Statistic.CRAFT_ITEM, material);
      int used = requestedPlayer.getStatistic(Statistic.USE_ITEM, material);
      int broke = requestedPlayer.getStatistic(Statistic.BREAK_ITEM, material);
      int pickup = requestedPlayer.getStatistic(Statistic.PICKUP, material);
      int drop = requestedPlayer.getStatistic(Statistic.DROP, material);

      statNameString = ChatColor.DARK_PURPLE + statOwner + " " + ChatColor.GOLD + itemName;
      statValueStrings.add(ChatColor.YELLOW + "Mined: " + ChatColor.WHITE + mined);
      statValueStrings.add(ChatColor.YELLOW + "Crafted: " + ChatColor.WHITE + crafted);
      statValueStrings.add(ChatColor.YELLOW + "Used: " + ChatColor.WHITE + used);
      statValueStrings.add(ChatColor.YELLOW + "Broke: " + ChatColor.WHITE + broke);
      statValueStrings.add(ChatColor.YELLOW + "Picked Up: " + ChatColor.WHITE + pickup);
      statValueStrings.add(ChatColor.YELLOW + "Dropped: " + ChatColor.WHITE + drop);
    }

    StringBuilder message = new StringBuilder(statNameString + ":");
    for (String valueString : statValueStrings) {
      message.append(" " + valueString);
    }

    player.sendMessage(message.toString());
    AethelPlugin.getInstance().getResources().getPlayerStatData().
        addToPastStats(statNameString, statValueStrings);
  }
}
