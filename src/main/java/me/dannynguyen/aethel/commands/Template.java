package me.dannynguyen.aethel.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Template is a starting template for commands.
 * It'll do something. We just don't know what yet.
 *
 * @author Danny Nguyen
 * @version 1.4.1
 * @since 1.2.3
 */
public class Template implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }
    return true;
  }
}
