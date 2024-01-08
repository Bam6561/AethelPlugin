package me.dannynguyen.aethel.inventories.playerstat;

import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.objects.PlayerStatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * ShowItemPast is an inventory under the PlayerStat command that shows past shared statistics.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.4.10
 */
public class PlayerStatPast {
  /**
   * Creates and names a PlayerStatPast inventory.
   *
   * @param player interacting player
   * @return PlayerStatPast inventory
   */
  public static Inventory createInventory(Player player) {
    Inventory inv = Bukkit.createInventory(player, 9,
        ChatColor.DARK_GRAY + "PlayerStat " + ChatColor.DARK_PURPLE + "Past");
    addPastStats(inv);
    return inv;
  }

  /**
   * Adds past stats to the PlayerStatPast inventory.
   *
   * @param inv interacting inventory
   */
  private static void addPastStats(Inventory inv) {
    int index = 0;
    for (PlayerStatMessage message : AethelResources.playerStatData.getPastStatMessages()) {
      inv.setItem(index, ItemCreator.createItem(Material.PAPER, message.getStatName(), message.getStats()));
      index++;
    }
  }
}
