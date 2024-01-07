package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.playerstat.PlayerStatMain;
import me.dannynguyen.aethel.inventories.playerstat.PlayerStatSend;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * PlayerStatListener is an inventory listener for the PlayerStat command.
 *
 * @author Danny Nguyen
 * @version 1.4.11
 * @since 1.4.7
 */
public class PlayerStatListener {
  /**
   * Retrieves a player's stat category page.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void readPlayerStatCategoryClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      if (e.getSlot() > 8) {
        String requestedPlayerName = player.getMetadata("stat-owner").get(0).asString();
        String itemName = ChatColor.stripColor(new ItemReader().readItemName(e.getCurrentItem()));
        player.openInventory(new PlayerStatMain().
            openPlayerStatCategoryPage(player, requestedPlayerName, itemName, 0));

        switch (itemName) {
          case "Entity Types", "Materials" -> player.setMetadata("inventory",
              new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-substat"));
          default -> player.setMetadata("inventory",
              new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-stat"));
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
  public void readPlayerStatStatClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0 -> previousStatPage(player);
        case 3, 4 -> { // Player Heads
        }
        case 5 -> returnToCategoryPage(player);
        case 8 -> nextStatPage(player);
        default -> new PlayerStatSend().sendStat(e, player);
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
  public void readPlayerStatSubstatClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0 -> previousStatPage(player);
        case 3, 4 -> { // Player Heads
        }
        case 5 -> returnToCategoryPage(player);
        case 8 -> nextStatPage(player);
        default -> new PlayerStatSend().sendSubstat(e, player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Opens the previous stat page.
   *
   * @param player interacting player
   */
  private void previousStatPage(Player player) {
    String requestedPlayerName = player.getMetadata("stat-owner").get(0).asString();
    String categoryName = player.getMetadata("stat-category").get(0).asString();
    int pageRequest = player.getMetadata("page").get(0).asInt();

    player.openInventory(new PlayerStatMain().
        openPlayerStatCategoryPage(player, requestedPlayerName, categoryName, pageRequest - 1));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-substat"));
  }

  /**
   * Opens a PlayerStatMain inventory.
   *
   * @param player interacting player
   */
  private void returnToCategoryPage(Player player) {
    String requestedPlayerName = player.getMetadata("stat-owner").get(0).asString();
    player.openInventory(new PlayerStatMain().openPlayerStatMainPage(player, requestedPlayerName));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-category"));
  }

  /**
   * Opens the next stat page.
   *
   * @param player interacting player
   */
  private void nextStatPage(Player player) {
    String requestedPlayerName = player.getMetadata("stat-owner").get(0).asString();
    String categoryName = player.getMetadata("stat-category").get(0).asString();
    int pageRequest = player.getMetadata("page").get(0).asInt();

    player.openInventory(new PlayerStatMain().
        openPlayerStatCategoryPage(player, requestedPlayerName, categoryName, pageRequest + 1));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-substat"));
  }
}
