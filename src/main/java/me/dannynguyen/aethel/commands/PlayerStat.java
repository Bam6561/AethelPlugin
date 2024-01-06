package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.PlayerStatMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * PlayerStat is a command invocation that retrieves a player's statistics.
 *
 * @author Danny Nguyen
 * @version 1.4.9
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
   * Checks if the PlayerStat command request was formatted
   * correctly before opening a player's PlayerStat inventory.
   *
   * @param player interacting player
   * @param args   player provided parameters
   */
  private void readRequest(Player player, String[] args) {
    switch (args.length) {
      case 0 -> openPlayerStatSelf(player);
      case 1 -> openPlayerStatOther(player, args[0]);
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Opens a PlayerStatMain inventory with the intent to select a stat category.
   *
   * @param player interacting player
   */
  private void openPlayerStatSelf(Player player) {
    player.openInventory(new PlayerStatMain().openPlayerStatMainPage(player, player.getName()));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-category"));
    player.setMetadata("stat-owner",
        new FixedMetadataValue(AethelPlugin.getInstance(), player.getName()));
  }

  /**
   * Opens a PlayerStatMain inventory belonging to another player with the intent to select a stat category.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   */
  private void openPlayerStatOther(Player player, String requestedPlayerName) {
    player.openInventory(new PlayerStatMain().openPlayerStatMainPage(player, requestedPlayerName));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "playerstat-category"));
    player.setMetadata("stat-owner",
        new FixedMetadataValue(AethelPlugin.getInstance(), requestedPlayerName));
  }
}