package me.dannynguyen.aethel.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Ping is a command invocation that returns the player's latency.
 *
 * @author Danny Nguyen
 * @version 1.0.1
 * @since 1.0.1
 */
public class Ping implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      player.sendMessage(ChatColor.GRAY + String.valueOf(player.getPing()) + "ms");
    }
    return true;
  }
}
