package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
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
 * @version 1.7.3
 * @since 1.4.7
 */
public class PlayerStats implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }
    readRequest(player, args);
    return true;
  }

  /**
   * Checks if the command request was formatted correctly
   * before opening a player's PlayerStatsMain inventory.
   *
   * @param player interacting player
   * @param args   user provided parameters
   */
  private void readRequest(Player player, String[] args) {
    switch (args.length) {
      case 0 -> openPlayerStatsSelf(player);
      case 1 -> interpretParameter(player, args[0]);
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Either opens a PlayerStatsMain inventory belonging to another player or opens a PlayerStatsPast inventory.
   *
   * @param player    interacting player
   * @param parameter player given parameter
   */
  private void interpretParameter(Player player, String parameter) {
    if (parameter.equals("past") || parameter.equals("p")) {
      player.openInventory(PlayerStatsPast.createInventory(player));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "playerstats.past"));
    } else {
      openPlayerStatsOther(player, parameter);
    }
  }

  /**
   * Opens a PlayerStatsMain inventory with the intent to select a stat category.
   *
   * @param player interacting player
   */
  private void openPlayerStatsSelf(Player player) {
    player.setMetadata("player",
        new FixedMetadataValue(AethelPlugin.getInstance(), player.getName()));

    player.openInventory(PlayerStatsMain.openPlayerStatsMainPage(player, player.getName()));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstats.category"));
  }

  /**
   * Opens a PlayerStatsMain inventory belonging to another player with the intent to select a stat category.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   */
  private void openPlayerStatsOther(Player player, String requestedPlayerName) {
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(requestedPlayerName);
    if (requestedPlayer.hasPlayedBefore()) {
      player.setMetadata("player",
          new FixedMetadataValue(AethelPlugin.getInstance(), requestedPlayer.getName()));

      player.openInventory(PlayerStatsMain.openPlayerStatsMainPage(player, requestedPlayer.getName()));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "playerstats.category"));
    } else {
      player.sendMessage(ChatColor.RED + requestedPlayerName + " has never played on this server.");
    }
  }
}