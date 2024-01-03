package me.dannynguyen.aethel.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandTemplate does something. We just don't know what yet.
 *
 * @author Danny Nguyen
 * @version 1.3.2
 * @since 1.2.3
 */
public class CommandTemplate implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }
    return true;
  }
}
