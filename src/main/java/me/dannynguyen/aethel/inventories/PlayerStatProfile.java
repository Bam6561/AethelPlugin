package me.dannynguyen.aethel.inventories;

import me.dannynguyen.aethel.creators.ItemCreator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * PlayerStatProfile is an inventory under the PlayerStat
 * command that contains all of a player's statistics.
 *
 * @author Danny Nguyen
 * @version 1.4.7
 * @since 1.4.7
 */
public class PlayerStatProfile {
  /**
   * Creates and names a PlayerStatProfile inventory.
   *
   * @param player interacting player
   * @return PlayerStatProfile inventory
   */
  public Inventory createInventory(Player player) {
    Inventory inv = Bukkit.createInventory(player, 54, ChatColor.DARK_GRAY + "PlayerStat "
        + ChatColor.DARK_PURPLE + player.getDisplayName());
    addPlayerStatistics(inv);
    return inv;
  }

  /**
   * Creates and names a PlayerStatProfile inventory to the requested player.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   * @return PlayerStatProfile inventory
   */
  public Inventory createInventory(Player player, String requestedPlayerName) {
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(requestedPlayerName);
    if (requestedPlayer.hasPlayedBefore()) {
      Inventory inv = Bukkit.createInventory(player, 54, ChatColor.DARK_GRAY + "PlayerStat "
          + ChatColor.DARK_PURPLE + requestedPlayer.getName());
      addPlayerStatistics(inv);
      return inv;
    } else {
      player.sendMessage(ChatColor.RED + requestedPlayer.getName() + "has never played on this server.");
      return createInventory(player);
    }
  }

  /**
   * Adds player statistics represented as items to the inventory.
   *
   * @param inv interacting inventory
   */
  private void addPlayerStatistics(Inventory inv) {
    ItemCreator itemCreator = new ItemCreator();
    Statistic[] stats = Statistic.values();

    for (int i = 9; i < 54; i++) {
      inv.setItem(i, itemCreator.createItem(Material.PAPER, stats[i].name()));
    }
  }
}
