package me.dannynguyen.aethel.commands.playerstats;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.playerstats.object.PlayerStatsValues;
import me.dannynguyen.aethel.utility.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * PlayerStatsPast is an inventory that shows past shared statistics.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.4.10
 */
public class PlayerStatsPast {
  /**
   * Creates and names a PlayerStatsPast inventory.
   *
   * @param user user
   * @return PlayerStatsPast inventory
   */
  public static Inventory createInventory(Player user) {
    Inventory inv = Bukkit.createInventory(user, 9,
        ChatColor.DARK_GRAY + "PlayerStats " + ChatColor.DARK_PURPLE + "Past");
    addPastStats(inv);
    return inv;
  }

  /**
   * Adds past stat values to the PlayerStatsPast inventory.
   *
   * @param inv interacting inventory
   */
  private static void addPastStats(Inventory inv) {
    int i = 0;
    for (PlayerStatsValues statValue : PluginData.playerStatsData.getPastStatsValues()) {
      inv.setItem(i, ItemCreator.createItem(Material.PAPER, statValue.getName(), statValue.getValues()));
      i++;
    }
  }
}
