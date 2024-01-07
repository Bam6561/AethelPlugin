package me.dannynguyen.aethel.inventories.playerstat;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.objects.PlayerStatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * ShowItemPast is an inventory under the PlayerStat command that shows past shared statistics.
 *
 * @author Danny Nguyen
 * @version 1.4.11
 * @since 1.4.10
 */
public class PlayerStatPast {
  /**
   * Creates and names a PlayerStatPast inventory.
   *
   * @param player interacting player
   * @return PlayerStatPast inventory
   */
  public Inventory createInventory(Player player) {
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
  private void addPastStats(Inventory inv) {
    ArrayList<PlayerStatMessage> messages = AethelPlugin.getInstance().
        getResources().getPlayerStatData().getPastStatMessages();

    int index = 0;
    ItemCreator itemCreator = new ItemCreator();
    for (PlayerStatMessage message : messages) {
      inv.setItem(index, itemCreator.createItem(Material.PAPER, message.getStatName(), message.getStats()));
      index++;
    }
  }
}
