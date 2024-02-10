package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Inventory click event listener for PlayerStat menus.
 *
 * @author Danny Nguyen
 * @version 1.9.13
 * @since 1.4.7
 */
public class PlayerStatMenuClick {
  /**
   * Inventory click event.
   */
  private final InventoryClickEvent e;

  /**
   * Player who clicked.
   */
  private final Player user;

  /**
   * Slot clicked.
   */
  private final int slotClicked;

  /**
   * Associates an inventory click event with its user in the context of an open PlayerStat menu.
   *
   * @param e inventory click event
   */
  public PlayerStatMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.slotClicked = e.getSlot();
  }

  /**
   * Retrieves a player's stat category page.
   */
  public void readMainClick() {
    if (e.getSlot() > 8) {
      String requestedPlayer = user.getMetadata(PluginPlayerMeta.PLAYER.getMeta()).get(0).asString();
      String itemName = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
      user.setMetadata(PluginPlayerMeta.CATEGORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), itemName));

      user.openInventory(new PlayerStatMenu(user, requestedPlayer).openCategoryPage(itemName, 0));
      switch (itemName) {
        case "Entity Types", "Materials" -> user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.PLAYERSTAT_SUBSTAT.menu));
        default -> user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.PLAYERSTAT_STAT.menu));
      }
    }
  }

  /**
   * Either:
   * - increments or decrements a player's statistic page
   * - returns to the main menu
   * - gets a player's statistic value.
   */
  public void readStatClick() {
    switch (e.getSlot()) {
      case 0 -> previousPage();
      case 3, 4 -> { // Player Heads
      }
      case 5 -> returnToMainMenu();
      case 8 -> nextPage();
      default -> new PlayerStatMessage(e, user).sendStat();
    }
  }

  /**
   * Either:
   * - increments or decrements a player's substatistic page
   * - returns to the main menu
   * - gets a player's substatistic value.
   */
  public void readSubstatClick() {
    switch (e.getSlot()) {
      case 0 -> previousPage();
      case 3, 4 -> { // Player Heads
      }
      case 5 -> returnToMainMenu();
      case 8 -> nextPage();
      default -> new PlayerStatMessage(e, user).sendSubstat();
    }
  }

  /**
   * Opens the previous stat category page.
   */
  private void previousPage() {
    String requestedPlayerName = user.getMetadata(PluginPlayerMeta.PLAYER.getMeta()).get(0).asString();
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

    user.openInventory(new PlayerStatMenu(user, requestedPlayerName).openCategoryPage(categoryName, pageRequest - 1));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.PLAYERSTAT_SUBSTAT.menu));
  }

  /**
   * Opens a PlayerStat menu.
   */
  private void returnToMainMenu() {
    String requestedPlayerName = user.getMetadata(PluginPlayerMeta.PLAYER.getMeta()).get(0).asString();
    user.openInventory(new PlayerStatMenu(user, requestedPlayerName).openMainMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.PLAYERSTAT_CATEGORY.menu));
    user.setMetadata(PluginPlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "0"));
  }

  /**
   * Opens the next stat category page.
   */
  private void nextPage() {
    String requestedPlayerName = user.getMetadata(PluginPlayerMeta.PLAYER.getMeta()).get(0).asString();
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

    user.openInventory(new PlayerStatMenu(user, requestedPlayerName).openCategoryPage(categoryName, pageRequest + 1));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.PLAYERSTAT_SUBSTAT.menu));
  }
}
