package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginMetadata;
import me.dannynguyen.aethel.enums.PluginPermission;
import me.dannynguyen.aethel.inventories.playerstats.PlayerStatsMain;
import me.dannynguyen.aethel.inventories.playerstats.PlayerStatsPast;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * PlayerStats is a command invocation that retrieves a player's statistics.
 * <p>
 * Additional Parameters:
 * - "past", "p": opens an inventory with the last 9 shown stats
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.7.6
 * @since 1.4.7
 */
public class PlayerStats implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(PluginPermission.PLAYERSTATS.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly
   * before opening a player's PlayerStats main menu.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openPlayerStatsSelf(user);
      case 1 -> interpretParameter(user, args[0]);
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Either opens a PlayerStats main menu belonging to another player or opens a PlayerStatsPast menu.
   *
   * @param user      user
   * @param parameter player given parameter
   */
  private void interpretParameter(Player user, String parameter) {
    if (parameter.equals("past") || parameter.equals("p")) {
      user.openInventory(PlayerStatsPast.createInventory(user));
      user.setMetadata(PluginMetadata.INVENTORY.data,
          new FixedMetadataValue(AethelPlugin.getInstance(), PluginMetadata.PLAYERSTATS_PAST.data));
    } else {
      openPlayerStatsOther(user, parameter);
    }
  }

  /**
   * Opens a PlayerStats menu.
   *
   * @param user interacting player
   */
  private void openPlayerStatsSelf(Player user) {
    user.setMetadata(PluginMetadata.PLAYER.data,
        new FixedMetadataValue(AethelPlugin.getInstance(), user.getName()));

    user.openInventory(PlayerStatsMain.openMainMenu(user, user.getName()));
    user.setMetadata(PluginMetadata.INVENTORY.data,
        new FixedMetadataValue(AethelPlugin.getInstance(), PluginMetadata.PLAYERSTATS_CATEGORY.data));
  }

  /**
   * Opens a PlayerStats menu belonging to another player.
   *
   * @param user            user
   * @param requestedPlayer requested player's name
   */
  private void openPlayerStatsOther(Player user, String requestedPlayer) {
    OfflinePlayer player = Bukkit.getOfflinePlayer(requestedPlayer);
    if (player.hasPlayedBefore()) {
      user.setMetadata(PluginMetadata.PLAYER.data,
          new FixedMetadataValue(AethelPlugin.getInstance(), player.getName()));

      user.openInventory(PlayerStatsMain.openMainMenu(user, player.getName()));
      user.setMetadata(PluginMetadata.INVENTORY.data,
          new FixedMetadataValue(AethelPlugin.getInstance(), PluginMetadata.PLAYERSTATS_CATEGORY.data));
    } else {
      user.sendMessage(ChatColor.RED + requestedPlayer + " has never played on this server.");
    }
  }
}