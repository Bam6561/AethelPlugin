package me.dannynguyen.aethel.inventories.playerstats;

import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.objects.playerstats.PlayerStatsValues;
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
   * @param player interacting player
   * @return PlayerStatsPast inventory
   */
  public static Inventory createInventory(Player player) {
    Inventory inv = Bukkit.createInventory(player, 9,
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
    for (PlayerStatsValues statValue : AethelResources.playerStatsData.getPastStatsValues()) {
      inv.setItem(i, ItemCreator.createItem(Material.PAPER, statValue.getName(), statValue.getValues()));
      i++;
    }
  }
}
