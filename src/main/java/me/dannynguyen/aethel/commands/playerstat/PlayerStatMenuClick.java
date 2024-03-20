package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.interfaces.MenuClick;
import me.dannynguyen.aethel.systems.plugin.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

/**
 * Inventory click event listener for PlayerStat menus.
 *
 * @author Danny Nguyen
 * @version 1.17.6
 * @since 1.4.7
 */
public class PlayerStatMenuClick implements MenuClick {
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
  private final int slot;

  /**
   * Associates an inventory click event with its user in the context of an open PlayerStat menu.
   *
   * @param e inventory click event
   */
  public PlayerStatMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.slot = e.getSlot();
  }

  /**
   * Retrieves a player's stat category page.
   */
  public void interpretMenuClick() {
    if (slot > 8) {
      Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
      String owner = playerMeta.get(PlayerMeta.PLAYER);
      String item = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
      playerMeta.put(PlayerMeta.CATEGORY, item);

      user.openInventory(new PlayerStatMenu(user, owner).getCategoryPage(item, 0));
      switch (item) {
        case "Entity Types", "Materials" -> playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.PLAYERSTAT_SUBSTAT.getMeta());
        default -> playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.PLAYERSTAT_STAT.getMeta());
      }
    }
  }

  /**
   * Either:
   * <p>
   * - increments or decrements a player's statistic page
   * </p>
   * <p>
   * - returns to the main menu
   * </p>
   * <p>
   * - gets a player's statistic value.
   * </p>
   */
  public void readStatClick() {
    switch (slot) {
      case 0 -> previousPage();
      case 3, 4 -> { // Player Heads
      }
      case 5 -> returnToMenu();
      case 8 -> nextPage();
      default -> new StatMessage(e, user).sendStat();
    }
  }

  /**
   * Either:
   * <p>
   * - increments or decrements a player's substatistic page
   * </p>
   * <p>
   * - returns to the main menu
   * </p>
   * <p>
   * - gets a player's substatistic value.
   * </p>
   */
  public void readSubstatClick() {
    switch (slot) {
      case 0 -> previousPage();
      case 3, 4 -> { // Player Heads
      }
      case 5 -> returnToMenu();
      case 8 -> nextPage();
      default -> new StatMessage(e, user).sendSubstat();
    }
  }

  /**
   * Opens the previous stat category page.
   */
  private void previousPage() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    String owner = playerMeta.get(PlayerMeta.PLAYER);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    user.openInventory(new PlayerStatMenu(user, owner).getCategoryPage(category, pageRequest - 1));
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.PLAYERSTAT_SUBSTAT.getMeta());
  }

  /**
   * Opens a PlayerStat menu.
   */
  private void returnToMenu() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    String owner = playerMeta.get(PlayerMeta.PLAYER);
    user.openInventory(new PlayerStatMenu(user, owner).getMainMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.PLAYERSTAT_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
  }

  /**
   * Opens the next stat category page.
   */
  private void nextPage() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    String owner = playerMeta.get(PlayerMeta.PLAYER);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    user.openInventory(new PlayerStatMenu(user, owner).getCategoryPage(category, pageRequest + 1));
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.PLAYERSTAT_SUBSTAT.getMeta());
  }
}
