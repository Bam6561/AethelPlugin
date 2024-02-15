package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginEnum;
import me.dannynguyen.aethel.listeners.MenuClick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that retrieves a player's statistics.
 * <p>
 * Additional Parameters:
 * - "past", "p": opens a menu with the last 27 shown stats
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.9.21
 * @since 1.4.7
 */
public class PlayerStatCommand implements CommandExecutor {
  /**
   * Executes the PlayerStat command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command arguments
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.playerstat")) {
        readRequest(user, args);
      } else {
        user.sendMessage(PluginEnum.Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(PluginEnum.Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly
   * before opening a player's PlayerStat main menu.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openPlayerStatSelf(user);
      case 1 -> interpretParameter(user, args[0]);
      default -> user.sendMessage(PluginEnum.Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Either opens a PlayerStat main menu belonging to another player or opens a PastStat menu.
   *
   * @param user      user
   * @param parameter player given parameter
   */
  private void interpretParameter(Player user, String parameter) {
    if (parameter.equals("p") || parameter.equals("past")) {
      user.openInventory(new PastStatMenu(user).openMenu());
      user.setMetadata(PluginEnum.PlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), MenuClick.Menu.PLAYERSTAT_PAST.menu));
    } else {
      openPlayerStatOther(user, parameter);
    }
  }

  /**
   * Opens a PlayerStat main menu belonging to the user.
   *
   * @param user user
   */
  private void openPlayerStatSelf(Player user) {
    user.setMetadata(PluginEnum.PlayerMeta.PLAYER.getMeta(), new FixedMetadataValue(Plugin.getInstance(), user.getName()));
    user.openInventory(new PlayerStatMenu(user, user.getName()).openMainMenu());
    user.setMetadata(PluginEnum.PlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), MenuClick.Menu.PLAYERSTAT_CATEGORY.menu));
  }

  /**
   * Opens a PlayerStat main menu belonging to another player.
   *
   * @param user            user
   * @param requestedPlayer requested player's name
   */
  private void openPlayerStatOther(Player user, String requestedPlayer) {
    OfflinePlayer player = Bukkit.getOfflinePlayer(requestedPlayer);
    if (player.hasPlayedBefore()) {
      user.setMetadata(PluginEnum.PlayerMeta.PLAYER.getMeta(), new FixedMetadataValue(Plugin.getInstance(), player.getName()));
      user.openInventory(new PlayerStatMenu(user, player.getName()).openMainMenu());
      user.setMetadata(PluginEnum.PlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), MenuClick.Menu.PLAYERSTAT_CATEGORY.menu));
    } else {
      user.sendMessage(ChatColor.RED + requestedPlayer + " has never played on this server.");
    }
  }
}