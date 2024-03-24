package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.interfaces.MenuClick;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import me.dannynguyen.aethel.util.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Inventory click event listener for {@link StatCommand} menus.
 * <p>
 * Called with {@link MenuEvent}.
 *
 * @author Danny Nguyen
 * @version 1.17.19
 * @since 1.4.7
 */
public class StatMenuClick implements MenuClick {
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
      PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
      String owner = Bukkit.getOfflinePlayer(pluginPlayer.getTarget()).getName();
      String item = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));

      pluginPlayer.setCategory(item);
      user.openInventory(new StatMenu(user, owner).getCategoryPage(item, 0));
      switch (item) {
        case "Entity Types", "Materials" -> pluginPlayer.setMenu(MenuEvent.Menu.PLAYERSTAT_SUBSTAT);
        default -> pluginPlayer.setMenu(MenuEvent.Menu.PLAYERSTAT_STAT);
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
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
    String owner = Bukkit.getOfflinePlayer(pluginPlayer.getTarget()).getName();
    String category = pluginPlayer.getCategory();
    int pageRequest = pluginPlayer.getPage();

    user.openInventory(new StatMenu(user, owner).getCategoryPage(category, pageRequest - 1));
    pluginPlayer.setMenu(MenuEvent.Menu.PLAYERSTAT_SUBSTAT);
  }

  /**
   * Opens a {@link StatMenu}.
   */
  private void returnToMenu() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
    String owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getTarget()).getName();

    user.openInventory(new StatMenu(user, owner).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.PLAYERSTAT_CATEGORY);
    pluginPlayer.setPage(0);
  }

  /**
   * Opens the next stat category page.
   */
  private void nextPage() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
    String owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getTarget()).getName();
    String category = pluginPlayer.getCategory();
    int pageRequest = pluginPlayer.getPage();

    user.openInventory(new StatMenu(user, owner).getCategoryPage(category, pageRequest + 1));
    pluginPlayer.setMenu(MenuEvent.Menu.PLAYERSTAT_SUBSTAT);
  }
}
