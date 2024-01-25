package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * DeveloperMode is a command invocation that allows the user
 * to bypass different conditions for commands and interactions.
 *
 * @author Danny Nguyen
 * @version 1.7.4
 * @since 1.4.4
 */
public class DeveloperMode implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }

    if (player.isOp()) {
      readRequest(player, args.length);
    } else {
      player.sendMessage(ChatColor.RED + "Insufficient permissions.");
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before toggling the setting.
   *
   * @param player             interacting player
   * @param numberOfParameters number of parameters
   */
  private void readRequest(Player player, int numberOfParameters) {
    if (numberOfParameters == 0) {
      toggleDeveloperMode(player);
    } else {
      player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Toggles Developer mode on or off for the player.
   *
   * @param player interacting player
   */
  private void toggleDeveloperMode(Player player) {
    if (!player.hasMetadata("developer")) {
      player.setMetadata("developer", new FixedMetadataValue(AethelPlugin.getInstance(), "on"));
      player.sendMessage(ChatColor.GREEN + "[Developer Mode On]");
    } else {
      player.removeMetadata("developer", AethelPlugin.getInstance());
      player.sendMessage(ChatColor.RED + "[Developer Mode Off]");
    }
  }
}