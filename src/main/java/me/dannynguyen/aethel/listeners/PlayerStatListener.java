package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.PlayerStatMain;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * PlayerStatListener is an inventory listener for the PlayerStat command.
 *
 * @author Danny Nguyen
 * @version 1.4.9
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
        case 2 -> { // Help Context
        }
        case 3, 4 -> { // Player Head
        }
        case 8 -> nextStatPage(player);
        default -> sendStat(e, player);
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
        case 2 -> { // Help Context
        }
        case 3, 4 -> { // Player Head
        }
        case 8 -> nextStatPage(player);
        default -> sendSubstat(e, player);
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
   * Sends a substatistic value.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  private void sendSubstat(InventoryClickEvent e, Player player) {
    String statCategory = player.getMetadata("stat-category").get(0).asString();
    String substatName = ChatColor.stripColor(new ItemReader().readItemName(
        e.getCurrentItem()).replace(" ", "_").toUpperCase());

    if (statCategory.equals("Entity Types")) {
      EntityType entityType = EntityType.valueOf(substatName);

      int kills = player.getStatistic(Statistic.KILL_ENTITY, entityType);
      int deaths = player.getStatistic(Statistic.ENTITY_KILLED_BY, entityType);

      player.sendMessage(ChatColor.AQUA + "[" + substatName + "] " +
          "Killed: " + ChatColor.WHITE + kills + " " + ChatColor.AQUA +
          "Killed By: " + ChatColor.WHITE + deaths);
    } else {
      Material material = Material.valueOf(substatName);

      int mined = player.getStatistic(Statistic.MINE_BLOCK, material);
      int crafted = player.getStatistic(Statistic.CRAFT_ITEM, material);
      int used = player.getStatistic(Statistic.USE_ITEM, material);
      int broke = player.getStatistic(Statistic.BREAK_ITEM, material);
      int pickup = player.getStatistic(Statistic.PICKUP, material);
      int drop = player.getStatistic(Statistic.DROP, material);

      player.sendMessage(ChatColor.AQUA + "[" + substatName + "] " +
          "Mined: " + ChatColor.WHITE + mined + " " + ChatColor.AQUA +
          "Crafted: " + ChatColor.WHITE + crafted + " " + ChatColor.AQUA +
          "Used: " + ChatColor.WHITE + used + " " + ChatColor.AQUA +
          "Broke: " + ChatColor.WHITE + broke + " " + ChatColor.AQUA +
          "Picked Up: " + ChatColor.WHITE + pickup + " " + ChatColor.AQUA +
          "Dropped: " + ChatColor.WHITE + drop);
    }
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
