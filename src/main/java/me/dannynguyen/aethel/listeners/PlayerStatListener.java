package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.PlayerStatProfile;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * PlayerStatListener is an inventory listener for the PlayerStat command.
 *
 * @author Danny Nguyen
 * @version 1.4.8
 * @since 1.4.7
 */
public class PlayerStatListener {
  /**
   * Retrieves a player's statistic value.
   *
   * @param e inventory click event
   */
  public void readPlayerStatProfileClick(InventoryClickEvent e) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      Player player = (Player) e.getWhoClicked();
      switch (e.getSlot()) {
        case 0 -> previousStatPage(player);
        case 2 -> { // Help Context
        }
        case 3 -> { // Player Head
        }
        case 8 -> nextStatPage(player);
        default -> sendStat(e, player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Opens the next previous page.
   *
   * @param player interacting player
   */
  private void previousStatPage(Player player) {
    int pageRequest = player.getMetadata("page").get(0).asInt();
    player.openInventory(new PlayerStatProfile().
        openStatPage(player, null, pageRequest - 1));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-profile"));
  }

  /**
   * Sends a statistic value.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  private void sendStat(InventoryClickEvent e, Player player) {
    String itemName = ChatColor.stripColor(new ItemReader().readItemName(e.getCurrentItem()));
    Statistic stat = Statistic.valueOf(itemName.replace(" ", "_").toUpperCase());
    player.sendMessage(ChatColor.AQUA + itemName + ": " + ChatColor.WHITE + player.getStatistic(stat));
  }

  /**
   * Opens the next stat page.
   *
   * @param player interacting player
   */
  private void nextStatPage(Player player) {
    int pageRequest = player.getMetadata("page").get(0).asInt();
    player.openInventory(new PlayerStatProfile().
        openStatPage(player, null, pageRequest + 1));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-profile"));
  }
}
