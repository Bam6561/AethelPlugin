package me.dannynguyen.aethel.listeners.inventory;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.playerstats.PlayerStatsMain;
import me.dannynguyen.aethel.inventories.playerstats.utility.PlayerStatsSend;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * PlayerStatsInventory is an inventory listener for the PlayerStats command.
 *
 * @author Danny Nguyen
 * @version 1.7.3
 * @since 1.4.7
 */
public class PlayerStatsInventory {
  /**
   * Retrieves a player's stat category page.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void readMainClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      if (e.getSlot() > 8) {
        String requestedPlayerName = player.getMetadata("player").get(0).asString();
        String itemName = ChatColor.stripColor(ItemReader.readItemName(e.getCurrentItem()));
        player.setMetadata("category", new FixedMetadataValue(AethelPlugin.getInstance(), itemName));

        player.openInventory(PlayerStatsMain.
            openPlayerStatsCategoryPage(player, requestedPlayerName, itemName, 0));
        switch (itemName) {
          case "Entity Types", "Materials" -> player.setMetadata("inventory",
              new FixedMetadataValue(AethelPlugin.getInstance(), "playerstats.substat"));
          default -> player.setMetadata("inventory",
              new FixedMetadataValue(AethelPlugin.getInstance(), "playerstats.stat"));
        }
      }
      e.setCancelled(true);
    }
  }

  /**
   * Retrieves a player's statistic value.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void readStatClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0 -> previousStatPage(player);
        case 3, 4 -> { // Player Heads
        }
        case 5 -> returnToMainPage(player);
        case 8 -> nextStatPage(player);
        default -> PlayerStatsSend.sendStat(e, player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Retrieves a player's substatistic value.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void readSubstatClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0 -> previousStatPage(player);
        case 3, 4 -> { // Player Heads
        }
        case 5 -> returnToMainPage(player);
        case 8 -> nextStatPage(player);
        default -> PlayerStatsSend.sendSubstat(e, player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Opens the previous stat page.
   *
   * @param player interacting player
   */
  private static void previousStatPage(Player player) {
    String requestedPlayerName = player.getMetadata("player").get(0).asString();
    String categoryName = player.getMetadata("category").get(0).asString();
    int pageRequest = player.getMetadata("page").get(0).asInt();

    player.openInventory(PlayerStatsMain.
        openPlayerStatsCategoryPage(player, requestedPlayerName, categoryName, pageRequest - 1));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstats.substat"));
  }

  /**
   * Opens a PlayerStatsMain inventory.
   *
   * @param player interacting player
   */
  private static void returnToMainPage(Player player) {
    String requestedPlayerName = player.getMetadata("player").get(0).asString();
    player.openInventory(PlayerStatsMain.openPlayerStatsMainPage(player, requestedPlayerName));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstats.category"));
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
  }

  /**
   * Opens the next stat page.
   *
   * @param player interacting player
   */
  private static void nextStatPage(Player player) {
    String requestedPlayerName = player.getMetadata("player").get(0).asString();
    String categoryName = player.getMetadata("category").get(0).asString();
    int pageRequest = player.getMetadata("page").get(0).asInt();

    player.openInventory(PlayerStatsMain.
        openPlayerStatsCategoryPage(player, requestedPlayerName, categoryName, pageRequest + 1));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstats.substat"));
  }
}
