package me.dannynguyen.aethel.commands.playerstats;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryListener;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * PlayerStatsInventoryListener is an inventory listener for the PlayerStats inventory.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.4.7
 */
public class PlayerStatsInventoryListener {
  /**
   * Retrieves a user's stat category page.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void readMainClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      if (e.getSlot() > 8) {
        String requestedPlayerName = user.getMetadata(
            PluginPlayerMeta.Namespace.PLAYER.namespace).get(0).asString();
        String itemName = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
        user.setMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace,
            new FixedMetadataValue(Plugin.getInstance(), itemName));

        user.openInventory(PlayerStatsInventory.
            openCategoryPage(user, requestedPlayerName, itemName, 0));
        switch (itemName) {
          case "Entity Types", "Materials" -> user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
              new FixedMetadataValue(Plugin.getInstance(),
                  InventoryListener.Inventory.PLAYERSTATS_SUBSTAT.inventory));
          default -> user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
              new FixedMetadataValue(Plugin.getInstance(),
                  InventoryListener.Inventory.PLAYERSTATS_STAT.inventory));
        }
      }
    }
    e.setCancelled(true);
  }

  /**
   * Retrieves a user's statistic value.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void readStatClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0 -> previousStatPage(user);
        case 3, 4 -> { // Player Heads
        }
        case 5 -> returnToMainPage(user);
        case 8 -> nextStatPage(user);
        default -> PlayerStatsSend.sendStat(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Retrieves a user's substatistic value.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void readSubstatClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0 -> previousStatPage(user);
        case 3, 4 -> { // Player Heads
        }
        case 5 -> returnToMainPage(user);
        case 8 -> nextStatPage(user);
        default -> PlayerStatsSend.sendSubstat(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Opens the previous stat page.
   *
   * @param user user
   */
  private static void previousStatPage(Player user) {
    String requestedPlayerName = user.getMetadata(PluginPlayerMeta.Namespace.PLAYER.namespace).get(0).asString();
    String categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt();

    user.openInventory(PlayerStatsInventory.
        openCategoryPage(user, requestedPlayerName, categoryName, pageRequest - 1));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(),
            InventoryListener.Inventory.PLAYERSTATS_SUBSTAT.inventory));
  }

  /**
   * Opens a PlayerStats inventory.
   *
   * @param user user
   */
  private static void returnToMainPage(Player user) {
    String requestedPlayerName = user.getMetadata(PluginPlayerMeta.Namespace.PLAYER.namespace).get(0).asString();
    user.openInventory(PlayerStatsInventory.openMainMenu(user, requestedPlayerName));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.PLAYERSTATS_CATEGORY.inventory));
    user.setMetadata(PluginPlayerMeta.Namespace.PAGE.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "0"));
  }

  /**
   * Opens the next stat page.
   *
   * @param user user
   */
  private static void nextStatPage(Player user) {
    String requestedPlayerName = user.getMetadata(PluginPlayerMeta.Namespace.PLAYER.namespace).get(0).asString();
    String categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt();

    user.openInventory(PlayerStatsInventory.
        openCategoryPage(user, requestedPlayerName, categoryName, pageRequest + 1));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.PLAYERSTATS_SUBSTAT.inventory));
  }
}
