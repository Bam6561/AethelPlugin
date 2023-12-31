package me.dannynguyen.aethel.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Debug is a command invocation that responds with debug values.
 *
 * @author Danny Nguyen
 * @version 1.2.3
 * @since 1.2.3
 */
public class Debug implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    String message = "No value set.";
    if (!(sender instanceof Player)) {
      Bukkit.getLogger().warning(message);
    } else {
      Player player = (Player) sender;
      player.sendMessage(message);
    }
    return true;
  }
}
