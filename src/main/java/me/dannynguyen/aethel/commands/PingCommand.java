package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.enums.PluginMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command invocation that responds to the user with their server latency.
 *
 * @author Danny Nguyen
 * @version 1.9.11
 * @since 1.0.1
 */
public class PingCommand implements CommandExecutor {
  /**
   * Executes the Ping command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command arguments
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.ping")) {
        user.sendMessage("Pong! " + ChatColor.GRAY + user.getPing() + "ms");
      } else {
        user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
      }
    } else {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
    }
    return true;
  }
}
