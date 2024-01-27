package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Ping is a command invocation that responds with the user's server latency.
 *
 * @author Danny Nguyen
 * @version 1.7.9
 * @since 1.0.1
 */
public class Ping implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(PluginPermission.PING.permission)) {
      user.sendMessage("Pong! " + ChatColor.GRAY + user.getPing() + "ms");
    }
    return true;
  }
}
