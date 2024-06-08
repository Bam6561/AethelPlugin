package me.bam6561.aethelplugin.commands.playerstat;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Message;
import me.bam6561.aethelplugin.interfaces.MenuClick;
import me.bam6561.aethelplugin.listeners.MenuListener;
import me.bam6561.aethelplugin.listeners.MessageListener;
import me.bam6561.aethelplugin.plugin.MenuInput;
import me.bam6561.aethelplugin.utils.EntityReader;
import me.bam6561.aethelplugin.utils.TextFormatter;
import me.bam6561.aethelplugin.utils.item.ItemCreator;
import me.bam6561.aethelplugin.utils.item.ItemReader;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Inventory click event listener for {@link StatCommand} menus.
 * <p>
 * Called with {@link MenuListener}.
 *
 * @author Danny Nguyen
 * @version 1.26.1
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
      case 1, 4 -> { // Player Heads
      }
      case 2 -> new MenuToggle().toggleShareLeaderboard();
      case 3 -> new MenuToggle().toggleShareVisibility();
      case 7 -> new MenuChange().returnToMenu();
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
      case 4 -> { // Player Head
      }
      case 1 -> new MenuChange().searchSubstat();
      case 2 -> new MenuToggle().toggleShareLeaderboard();
      case 3 -> new MenuToggle().toggleShareVisibility();
      case 5 -> new MenuToggle().toggleEntityTypeSubstat();
      case 6 -> new MenuToggle().toggleMaterialSubstat();
      case 7 -> new MenuChange().returnToMenu();
      case 8 -> new MenuChange().nextPage();
      default -> new StatBroadcast().sendSubstat();
    }
  }

  /**
   * Represents a menu change operation.
   *
   * @author Danny Nguyen
   * @version 1.26.1
   * @since 1.23.12
   */
  private class MenuChange {
    /**
     * No parameter constructor.
     */
    MenuChange() {
    }

    /**
     * Searches for matching substats by name.
     */
    private void searchSubstat() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input search term.");
      user.closeInventory();
      Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput().setMessageInput(MessageListener.Type.STAT_SUBSTATISTIC_SEARCH);
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
   * @version 1.25.12
   * @since 1.24.2
   */
  private class MenuToggle {
    /**
     * No parameter constructor.
     */
    MenuToggle() {
    }

    /**
     * Toggles stat share leaderboard.
     */
    private void toggleShareLeaderboard() {
      if (!canViewServerLeaderboards()) {
        return;
      }

      Inventory menu = e.getInventory();
      Material mode = menu.getItem(2).getType();
      String category = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput().getCategory();
      switch (mode) {
        case IRON_INGOT -> {
          menu.setItem(2, ItemCreator.createItem(Material.DIAMOND, ChatColor.AQUA + "Leaderboard"));
          switch (category) {
            case "Entity Types" -> menu.setItem(5, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Killed"));
            case "Materials" -> menu.setItem(6, ItemCreator.createItem(Material.IRON_PICKAXE, ChatColor.AQUA + "Mined"));
          }
        }
        case DIAMOND -> {
          menu.setItem(2, ItemCreator.createItem(Material.IRON_INGOT, ChatColor.AQUA + "Personal"));
          switch (category) {
            case "Entity Types" -> menu.setItem(5, new ItemStack(Material.AIR));
            case "Materials" -> menu.setItem(6, new ItemStack(Material.AIR));
          }
        }
      }
    }

    /**
     * Toggles stat share visibility.
     */
    private void toggleShareVisibility() {
      Inventory menu = e.getInventory();
      Material mode = menu.getItem(3).getType();
      switch (mode) {
        case BOOK -> menu.setItem(3, ItemCreator.createItem(Material.WRITABLE_BOOK, ChatColor.AQUA + "Share Stat"));
        case WRITABLE_BOOK -> menu.setItem(3, ItemCreator.createItem(Material.BOOK, ChatColor.AQUA + "View Stat"));
      }
    }

    /**
     * Toggles type of entity type substat to share.
     */
    private void toggleEntityTypeSubstat() {
      Inventory menu = e.getInventory();
      Material mode = menu.getItem(5).getType();
      switch (mode) {
        case IRON_SWORD -> menu.setItem(5, ItemCreator.createItem(Material.SKELETON_SKULL, ChatColor.AQUA + "Killed"));
        case SKELETON_SKULL -> menu.setItem(5, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Deaths"));
      }
    }

    /**
     * Toggles type of material substat to share.
     */
    private void toggleMaterialSubstat() {
      Inventory menu = e.getInventory();
      Material mode = menu.getItem(6).getType();
      switch (mode) {
        case IRON_PICKAXE -> menu.setItem(6, ItemCreator.createItem(Material.CRAFTING_TABLE, ChatColor.AQUA + "Crafted"));
        case CRAFTING_TABLE -> menu.setItem(6, ItemCreator.createItem(Material.BRUSH, ChatColor.AQUA + "Used"));
        case BRUSH -> menu.setItem(6, ItemCreator.createItem(Material.BONE_MEAL, ChatColor.AQUA + "Broke"));
        case BONE_MEAL -> menu.setItem(6, ItemCreator.createItem(Material.CHEST, ChatColor.AQUA + "Picked Up"));
        case CHEST -> menu.setItem(6, ItemCreator.createItem(Material.POINTED_DRIPSTONE, ChatColor.AQUA + "Dropped"));
        case POINTED_DRIPSTONE -> menu.setItem(6, ItemCreator.createItem(Material.IRON_PICKAXE, ChatColor.AQUA + "Mined"));
      }
    }

    /**
     * The user must have a spyglass in their hand, off-hand,
     * or trinket slot to view server leaderboards.
     *
     * @return if the user can view server leaderboards
     */
    private boolean canViewServerLeaderboards() {
      if (EntityReader.hasTrinket(user, Material.SPYGLASS)) {
        return true;
      } else {
        user.sendMessage(ChatColor.RED + "[PlayerStats] No spyglass in hand, off-hand, or trinket slot.");
        return false;
      }
    }
  }

  /**
   * Represents the retrieval and broadcast of a player statistic.
   *
   * @author Danny Nguyen
   * @version 1.26.1
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
     * Whether the stat request is a leaderboard.
     */
    private final boolean isLeaderboard = e.getInventory().getItem(2).getType() == Material.DIAMOND;

    /**
     * Whether to broadcast the value to all online players.
     */
    private final boolean isGlobalBroadcast = e.getInventory().getItem(3).getType() == Material.WRITABLE_BOOK;

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

      if (isLeaderboard) {
        broadcastLeaderboardStat(getLeaderboardStats(stat), requestedStat);
        return;
      }

      String statName = ChatColor.DARK_PURPLE + ownerName + " " + ChatColor.YELLOW + requestedStat;
      String statValue = formatStatValue(owner, requestedStat, stat);
      String message = statName + " " + statValue;

      broadcastPersonalStat(message, statName, List.of(statValue));
    }

    /**
     * Sends a player's substatistic value.
     */
    private void sendSubstat() {
      String substat = ChatColor.stripColor(TextFormatter.formatEnum(requestedStat));
      String category = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().getCategory();

      if (isLeaderboard) {
        Inventory menu = e.getInventory();
        switch (category) {
          case "Entity Types" -> {
            Material mode = menu.getItem(5).getType();
            switch (mode) {
              case IRON_SWORD -> broadcastLeaderboardStat(getLeaderboardStats(category, Statistic.KILL_ENTITY, substat), requestedStat + " Killed");
              case SKELETON_SKULL -> broadcastLeaderboardStat(getLeaderboardStats(category, Statistic.ENTITY_KILLED_BY, substat), requestedStat + " Deaths");
            }
          }
          case "Materials" -> {
            Material mode = menu.getItem(6).getType();
            switch (mode) {
              case IRON_PICKAXE -> broadcastLeaderboardStat(getLeaderboardStats(category, Statistic.MINE_BLOCK, substat), requestedStat + " Mined");
              case CRAFTING_TABLE -> broadcastLeaderboardStat(getLeaderboardStats(category, Statistic.CRAFT_ITEM, substat), requestedStat + " Crafted");
              case BRUSH -> broadcastLeaderboardStat(getLeaderboardStats(category, Statistic.USE_ITEM, substat), requestedStat + " Used");
              case BONE_MEAL -> broadcastLeaderboardStat(getLeaderboardStats(category, Statistic.BREAK_ITEM, substat), requestedStat + " Broke");
              case CHEST -> broadcastLeaderboardStat(getLeaderboardStats(category, Statistic.PICKUP, substat), requestedStat + " Picked Up");
              case POINTED_DRIPSTONE -> broadcastLeaderboardStat(getLeaderboardStats(category, Statistic.DROP, substat), requestedStat + " Dropped");
            }
          }
        }
        return;
      }

      String stat = ChatColor.DARK_PURPLE + ownerName + " " + ChatColor.GOLD + requestedStat;
      List<String> statValues = getSubstatValues(category, substat);
      if (statValues == null) {
        return;
      }

      StringBuilder message = new StringBuilder(stat);
      for (String value : statValues) {
        message.append(" ").append(value);
      }

      broadcastPersonalStat(message.toString(), stat, statValues);
    }

    /**
     * Sends the statistic value to the requester or global chat.
     *
     * @param message    message to be sent
     * @param stat       statistic name
     * @param statValues statistic values
     */
    private void broadcastPersonalStat(String message, String stat, List<String> statValues) {
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
     * Sends the leaderboard statistic values to the requester or global chat.
     *
     * @param messages messages to be sent
     * @param stat     statistic name
     */
    private void broadcastLeaderboardStat(List<String> messages, String stat) {
      if (!isGlobalBroadcast) {
        user.sendMessage(ChatColor.YELLOW + stat);
        for (String message : messages) {
          user.sendMessage(message);
        }
        return;
      }

      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.sendMessage(Message.NOTIFICATION_GLOBAL.getMessage() + ChatColor.DARK_PURPLE + user.getName() + ChatColor.WHITE + " -> " + ChatColor.YELLOW + stat);
        for (String message : messages) {
          onlinePlayer.sendMessage(message);
        }
      }
    }

    /**
     * Gets a top 5 leaderboard of stats.
     *
     * @param stat requested statistic
     * @return top 5 leaderboard of stats
     */
    private List<String> getLeaderboardStats(Statistic stat) {
      List<PlayerStat> playerStatValues = new ArrayList<>();
      for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
        playerStatValues.add(new PlayerStat(offlinePlayer, offlinePlayer.getStatistic(stat)));
      }

      Comparator<PlayerStat> playerStatValueComparator = Comparator.comparing(PlayerStat::getValue);
      playerStatValues.sort(playerStatValueComparator);

      List<String> messages = new ArrayList<>();
      int limit;
      if (playerStatValues.size() >= 5) {
        limit = playerStatValues.size() - 5;
      } else {
        limit = 0;
      }

      int leaderboardIndex = 1;
      for (int i = playerStatValues.size() - 1; i >= limit; i--) {
        PlayerStat playerStat = playerStatValues.get(i);
        messages.add(ChatColor.AQUA + "" + (leaderboardIndex) + ". " + ChatColor.DARK_PURPLE + playerStat.getOfflinePlayer().getName() + " " + formatStatValue(playerStat.getOfflinePlayer(), requestedStat, stat));
        leaderboardIndex++;
      }
      return messages;
    }

    /**
     * Gets a top 5 leaderboard of substats.
     *
     * @param category substat category
     * @param stat     requested statistic
     * @param substat  requested substatistic
     * @return top 5 leaderboard of substats
     */
    private List<String> getLeaderboardStats(String category, Statistic stat, String substat) {
      List<PlayerStat> playerStatValues = new ArrayList<>();
      switch (category) {
        case "Entity Types" -> {
          EntityType entityType = EntityType.valueOf(substat);
          for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            playerStatValues.add(new PlayerStat(offlinePlayer, offlinePlayer.getStatistic(stat, entityType)));
          }
        }
        case "Materials" -> {
          Material material = Material.valueOf(substat);
          for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            playerStatValues.add(new PlayerStat(offlinePlayer, offlinePlayer.getStatistic(stat, material)));
          }
        }
      }
      Comparator<PlayerStat> playerStatValueComparator = Comparator.comparing(PlayerStat::getValue);
      playerStatValues.sort(playerStatValueComparator);

      List<String> messages = new ArrayList<>();
      int limit;
      if (playerStatValues.size() >= 5) {
        limit = playerStatValues.size() - 5;
      } else {
        limit = 0;
      }

      int leaderboardIndex = 1;
      for (int i = playerStatValues.size() - 1; i >= limit; i--) {
        PlayerStat playerStat = playerStatValues.get(i);
        messages.add(ChatColor.AQUA + "" + (leaderboardIndex) + ". " + ChatColor.DARK_PURPLE + playerStat.getOfflinePlayer().getName() + " " + ChatColor.WHITE + playerStat.getValue());
        leaderboardIndex++;
      }
      return messages;
    }

    /**
     * Formats a statistic's value based on its name.
     *
     * @param owner stat owner
     * @param item  item name
     * @param stat  stat
     * @return formatted statistic value
     */
    private String formatStatValue(OfflinePlayer owner, String item, Statistic stat) {
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
        if (substat.equals("UNKNOWN")) {
          return null;
        }

        EntityType entityType = EntityType.valueOf(substat);

        int kills = owner.getStatistic(Statistic.KILL_ENTITY, entityType);
        int deaths = owner.getStatistic(Statistic.ENTITY_KILLED_BY, entityType);

        statValues.add(ChatColor.YELLOW + "Killed " + ChatColor.WHITE + kills);
        statValues.add(ChatColor.YELLOW + "Deaths " + ChatColor.WHITE + deaths);
      } else if (category.equals("Materials")) {
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

    /**
     * Represents an offline player's stat value.
     *
     * @author Danny Nguyen
     * @version 1.25.12
     * @since 1.25.12
     */
    private class PlayerStat {
      /**
       * Offline player.
       */
      private final OfflinePlayer offlinePlayer;

      /**
       * Stat value.
       */
      private final int value;

      /**
       * Associates the player stat with its offline player and value.
       *
       * @param offlinePlayer offline player
       * @param value         stat value
       */
      PlayerStat(OfflinePlayer offlinePlayer, int value) {
        this.offlinePlayer = offlinePlayer;
        this.value = value;
      }

      /**
       * Gets the offline player.
       *
       * @return offline player
       */
      private OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
      }

      /**
       * Gets the offline player's stat value.
       *
       * @return stat value
       */
      private int getValue() {
        return this.value;
      }
    }
  }
}
