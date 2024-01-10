package me.dannynguyen.aethel.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Ping is a command invocation that responds with the player's server latency.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.0.1
 */
public class Ping implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }
    player.sendMessage("Pong! " + ChatColor.GRAY + player.getPing() + "ms");
    return true;
  }
}
