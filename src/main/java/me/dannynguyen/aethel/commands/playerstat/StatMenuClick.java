package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.MenuMeta;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.plugin.interfaces.MenuClickEvent;
import me.dannynguyen.aethel.util.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

/**
 * Inventory click event listener for {@link StatCommand} menus.
 * <p>
 * Called with {@link me.dannynguyen.aethel.plugin.listeners.MenuClick}.
 *
 * @author Danny Nguyen
 * @version 1.17.18
 * @since 1.4.7
 */
public class StatMenuClick implements MenuClickEvent {
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
   * Associates an inventory click event with its user in
   * the context of an open {@link StatCommand} menu.
   *
   * @param e inventory click event
   */
  public StatMenuClick(@NotNull InventoryClickEvent e) {
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
      String owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getTarget()).getName();
      String item = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
      playerMeta.put(PlayerMeta.CATEGORY, item);

      user.openInventory(new StatMenu(user, owner).getCategoryPage(item, 0));
      switch (item) {
        case "Entity Types", "Materials" -> playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.PLAYERSTAT_SUBSTAT.getMeta());
        default -> playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.PLAYERSTAT_STAT.getMeta());
      }
    }
  }

  /**
   * Either:
   * <ul>
   *  <li>increments or decrements a player's statistic page
   *  <li>returns to the {@link StatMenu}
   *  <li>gets a player's statistic value
   * </ul>
   */
  public void interpretStatClick() {
    switch (slot) {
      case 0 -> previousPage();
      case 3, 4 -> { // Player Heads
      }
      case 5 -> returnToMenu();
      case 8 -> nextPage();
      default -> new StatBroadcast(e, user).sendStat();
    }
  }

  /**
   * Either:
   * <ul>
   *  <li>increments or decrements a player's substatistic page
   *  <li>returns to the {@link StatMenu}
   *  <li>gets a player's substatistic value
   * </ul>
   */
  public void interpretSubstatClick() {
    switch (slot) {
      case 0 -> previousPage();
      case 3, 4 -> { // Player Heads
      }
      case 5 -> returnToMenu();
      case 8 -> nextPage();
      default -> new StatBroadcast(e, user).sendSubstat();
    }
  }

  /**
   * Opens the previous stat category page.
   */
  private void previousPage() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    String owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getTarget()).getName();
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    user.openInventory(new StatMenu(user, owner).getCategoryPage(category, pageRequest - 1));
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.PLAYERSTAT_SUBSTAT.getMeta());
  }

  /**
   * Opens a {@link StatMenu}.
   */
  private void returnToMenu() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    String owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getTarget()).getName();
    user.openInventory(new StatMenu(user, owner).getMainMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.PLAYERSTAT_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
  }

  /**
   * Opens the next stat category page.
   */
  private void nextPage() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    String owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getTarget()).getName();
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    user.openInventory(new StatMenu(user, owner).getCategoryPage(category, pageRequest + 1));
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.PLAYERSTAT_SUBSTAT.getMeta());
  }
}