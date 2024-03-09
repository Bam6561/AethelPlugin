package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.systems.plugin.Message;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that responds to the user with their server latency.
 *
 * @author Danny Nguyen
 * @version 1.9.21
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
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.ping")) {
        user.sendMessage("Pong! " + ChatColor.GRAY + user.getPing() + "ms");
      } else {
        user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }
}
