package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.interfaces.MenuClick;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.plugin.MenuInput;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Inventory click event listener for {@link StatCommand} menus.
 * <p>
 * Called with {@link MenuListener}.
 *
 * @author Danny Nguyen
 * @version 1.24.2
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
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      String owner = Bukkit.getOfflinePlayer(menuInput.getTarget()).getName();
      String item = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));

      menuInput.setCategory(item);
      user.openInventory(new StatMenu(user, owner).getCategoryPage(item, 0));
      switch (item) {
        case "Entity Types", "Materials" -> menuInput.setMenu(MenuListener.Menu.PLAYERSTAT_SUBSTAT);
        default -> menuInput.setMenu(MenuListener.Menu.PLAYERSTAT_STAT);
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
      case 0 -> new MenuChange().previousPage();
      case 3, 4 -> { // Player Heads
      }
      case 5 -> new MenuToggle().toggleShareMode();
      case 6 -> new MenuChange().returnToMenu();
      case 8 -> new MenuChange().nextPage();
      default -> new StatBroadcast().sendStat();
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
      case 0 -> new MenuChange().previousPage();
      case 3, 4 -> { // Player Heads
      }
      case 5 -> new MenuToggle().toggleShareMode();
      case 6 -> new MenuChange().returnToMenu();
      case 8 -> new MenuChange().nextPage();
      default -> new StatBroadcast().sendSubstat();
    }
  }

  /**
   * Represents a menu change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.12
   * @since 1.23.12
   */
  private class MenuChange {
    /**
     * No parameter constructor.
     */
    MenuChange() {
    }

    /**
     * Opens the previous stat category page.
     */
    private void previousPage() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      String owner = Bukkit.getOfflinePlayer(menuInput.getTarget()).getName();
      String category = menuInput.getCategory();
      int pageRequest = menuInput.getPage();

      user.openInventory(new StatMenu(user, owner).getCategoryPage(category, pageRequest - 1));
      menuInput.setMenu(MenuListener.Menu.PLAYERSTAT_SUBSTAT);
    }

    /**
     * Opens a {@link StatMenu}.
     */
    private void returnToMenu() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      String owner = Bukkit.getOfflinePlayer(menuInput.getTarget()).getName();

      user.openInventory(new StatMenu(user, owner).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.PLAYERSTAT_CATEGORY);
      menuInput.setPage(0);
    }

    /**
     * Opens the next stat category page.
     */
    private void nextPage() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      String owner = Bukkit.getOfflinePlayer(menuInput.getTarget()).getName();
      String category = menuInput.getCategory();
      int pageRequest = menuInput.getPage();

      user.openInventory(new StatMenu(user, owner).getCategoryPage(category, pageRequest + 1));
      menuInput.setMenu(MenuListener.Menu.PLAYERSTAT_SUBSTAT);
    }
  }

  /**
   * Represents a menu toggle operation.
   *
   * @author Danny Nguyen
   * @version 1.24.2
   * @since 1.24.2
   */
  private class MenuToggle {
    /**
     * No parameter constructor.
     */
    MenuToggle() {
    }

    /**
     * Toggles stat share mode.
     */
    private void toggleShareMode() {
      Inventory menu = e.getInventory();
      Material mode = menu.getItem(5).getType();
      switch (mode) {
        case BOOK -> menu.setItem(5, ItemCreator.createItem(Material.WRITABLE_BOOK, ChatColor.AQUA + "Share Stat"));
        case WRITABLE_BOOK -> menu.setItem(5, ItemCreator.createItem(Material.BOOK, ChatColor.AQUA + "View Stat"));
      }
    }
  }

  /**
   * Represents the retrieval and broadcast of a player statistic.
   *
   * @author Danny Nguyen
   * @version 1.24.2
   * @since 1.4.10
   */
  private class StatBroadcast {
    /**
     * User's UUID.
     */
    private final UUID uuid = user.getUniqueId();

    /**
     * OfflinePlayer object of the player statistic owner.
     */
    private final OfflinePlayer owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().getTarget());

    /**
     * Player statistic owner's name.
     */
    private final String ownerName = owner.getName();

    /**
     * Requested player statistic.
     */
    private final String requestedStat = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));

    /**
     * Whether to broadcast the value to all online players.
     */
    private final boolean isGlobalBroadcast = e.getInventory().getItem(5).getType() == Material.WRITABLE_BOOK;

    /**
     * No parameter constructor.
     */
    private StatBroadcast() {
    }

    /**
     * Sends a player's statistic value.
     */
    private void sendStat() {
      Statistic stat = Statistic.valueOf(TextFormatter.formatEnum(requestedStat));
      String statName = ChatColor.DARK_PURPLE + ownerName + " " + ChatColor.YELLOW + requestedStat;
      String statValue = formatStatValue(requestedStat, stat);
      String message = statName + " " + statValue;

      broadcastMessage(message, statName, List.of(statValue));
    }

    /**
     * Sends a player's substatistic value.
     */
    private void sendSubstat() {
      String substat = ChatColor.stripColor(TextFormatter.formatEnum(requestedStat));
      String category = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().getCategory();
      String stat = ChatColor.DARK_PURPLE + ownerName + " " + ChatColor.GOLD + requestedStat;
      List<String> statValues = getSubstatValues(category, substat);

      StringBuilder message = new StringBuilder(stat);
      for (String value : statValues) {
        message.append(" ").append(value);
      }

      broadcastMessage(message.toString(), stat, statValues);
    }

    /**
     * Sends the statistic value to the requester or global chat.
     *
     * @param message    message to be sent
     * @param stat       statistic name
     * @param statValues statistic values
     */
    private void broadcastMessage(String message, String stat, List<String> statValues) {
      if (!isGlobalBroadcast) {
        user.sendMessage(message);
        return;
      }

      if (user.getUniqueId().equals(owner.getUniqueId())) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(Message.NOTIFICATION_GLOBAL.getMessage() + message);
        }
      } else {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(Message.NOTIFICATION_GLOBAL.getMessage() + ChatColor.DARK_PURPLE + user.getName() + ChatColor.WHITE + " -> " + message);
        }
      }
      Plugin.getData().getPastStatHistory().addPastStat(stat, statValues);
    }

    /**
     * Formats a statistic's value based on its name.
     *
     * @param item item name
     * @param stat stat
     * @return formatted statistic value
     */
    private String formatStatValue(String item, Statistic stat) {
      switch (item) {
        case "Play One Minute", "Time Since Death", "Time Since Rest", "Total World Time" -> {
          return ChatColor.WHITE + ticksToDaysHoursMinutes(owner.getStatistic(stat));
        }
        default -> {
          if (!item.contains("One Cm")) {
            return ChatColor.WHITE + String.valueOf(owner.getStatistic(stat));
          } else {
            return ChatColor.WHITE + String.valueOf(owner.getStatistic(stat) / 100) + " meters";
          }
        }
      }
    }

    /**
     * Retrieves the requested player's substat values.
     *
     * @param category category
     * @param substat  substat name
     * @return substat value
     */
    private List<String> getSubstatValues(String category, String substat) {
      List<String> statValues = new ArrayList<>();
      if (category.equals("Entity Types")) {
        EntityType entityType = EntityType.valueOf(substat);

        int kills = owner.getStatistic(Statistic.KILL_ENTITY, entityType);
        int deaths = owner.getStatistic(Statistic.ENTITY_KILLED_BY, entityType);

        statValues.add(ChatColor.YELLOW + "Killed " + ChatColor.WHITE + kills);
        statValues.add(ChatColor.YELLOW + "Deaths by " + ChatColor.WHITE + deaths);
      } else {
        Material material = Material.valueOf(substat);

        int mined = owner.getStatistic(Statistic.MINE_BLOCK, material);
        int crafted = owner.getStatistic(Statistic.CRAFT_ITEM, material);
        int used = owner.getStatistic(Statistic.USE_ITEM, material);
        int broke = owner.getStatistic(Statistic.BREAK_ITEM, material);
        int pickedUp = owner.getStatistic(Statistic.PICKUP, material);
        int dropped = owner.getStatistic(Statistic.DROP, material);

        statValues.add(ChatColor.YELLOW + "Mined " + ChatColor.WHITE + mined);
        statValues.add(ChatColor.YELLOW + "Crafted " + ChatColor.WHITE + crafted);
        statValues.add(ChatColor.YELLOW + "Used " + ChatColor.WHITE + used);
        statValues.add(ChatColor.YELLOW + "Broke " + ChatColor.WHITE + broke);
        statValues.add(ChatColor.YELLOW + "Picked Up " + ChatColor.WHITE + pickedUp);
        statValues.add(ChatColor.YELLOW + "Dropped " + ChatColor.WHITE + dropped);
      }
      return statValues;
    }

    /**
     * Gets a time duration in ticks and converts to days, hours, and minutes.
     *
     * @param ticks ticks
     * @return days, hours, and minutes
     */
    private String ticksToDaysHoursMinutes(long ticks) {
      long days = ticks / 1728000L % 30;
      long hours = ticks / 72000L % 24;
      long minutes = ticks / 1200L % 60;
      return (days == 0 ? "" : days + "d ") + (hours == 0 ? "" : hours + "h ") + (minutes == 0 ? "" : minutes + "m ");
    }
  }
}
