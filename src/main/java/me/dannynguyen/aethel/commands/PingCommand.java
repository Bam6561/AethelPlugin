package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.enums.PluginMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Ping is a command invocation that responds with the user's server latency.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.0.1
 */
public class PingCommand implements CommandExecutor {
  public enum Permission {
    PING("aethel.ping");

    public final String permission;

    Permission(String permission) {
      this.permission = permission;
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(Permission.PING.permission)) {
      user.sendMessage("Pong! " + ChatColor.GRAY + user.getPing() + "ms");
    }
    return true;
  }
}
