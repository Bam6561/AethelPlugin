package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.playerstat.PlayerStatMain;
import me.dannynguyen.aethel.inventories.playerstat.PlayerStatPast;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * PlayerStat is a command invocation that retrieves a player's statistics.
 *
 * @author Danny Nguyen
 * @version 1.4.12
 * @since 1.4.7
 */
public class PlayerStat implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }
    Player player = (Player) sender;
    readRequest(player, args);
    return true;
  }

  /**
   * Checks if the command request was formatted correctly
   * before opening a player's PlayerStatMain inventory.
   *
   * @param player interacting player
   * @param args   player provided parameters
   */
  private void readRequest(Player player, String[] args) {
    switch (args.length) {
      case 0 -> openPlayerStatSelf(player);
      case 1 -> interpretParameter(player, args[0]);
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Either opens a PlayerStatMain inventory belonging to another player or opens a PlayerStatPast inventory.
   *
   * @param player    interacting player
   * @param parameter player given parameter
   */
  private void interpretParameter(Player player, String parameter) {
    if (parameter.equals("past")) {
      player.openInventory(PlayerStatPast.createInventory(player));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-past"));
    } else {
      openPlayerStatOther(player, parameter);
    }
  }

  /**
   * Opens a PlayerStatMain inventory with the intent to select a stat category.
   *
   * @param player interacting player
   */
  private void openPlayerStatSelf(Player player) {
    player.setMetadata("stat-owner",
        new FixedMetadataValue(AethelPlugin.getInstance(), player.getName()));
    player.setMetadata("stat-category",
        new FixedMetadataValue(AethelPlugin.getInstance(), "categories"));
    player.openInventory(PlayerStatMain.openPlayerStatMainPage(player, player.getName()));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-category"));
  }

  /**
   * Opens a PlayerStatMain inventory belonging to another player with the intent to select a stat category.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   */
  private void openPlayerStatOther(Player player, String requestedPlayerName) {
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(requestedPlayerName);
    if (requestedPlayer.hasPlayedBefore()) {
      player.setMetadata("stat-owner",
          new FixedMetadataValue(AethelPlugin.getInstance(), requestedPlayer.getName()));
      player.setMetadata("stat-category",
          new FixedMetadataValue(AethelPlugin.getInstance(), "categories"));
      player.openInventory(PlayerStatMain.openPlayerStatMainPage(player, requestedPlayer.getName()));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-category"));
    } else {
      player.sendMessage(ChatColor.RED + requestedPlayerName + " has never played on this server.");
    }
  }
}