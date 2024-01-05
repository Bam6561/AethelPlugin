package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * DevMode is a command invocation that allows the user
 * to bypass different conditions for commands and interactions.
 *
 * @author Danny Nguyen
 * @version 1.4.4
 * @since 1.4.4
 */
public class DevMode implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }
    Player player = (Player) sender;
    if (player.isOp()) {
      readRequest(player, args.length);
    } else {
      player.sendMessage(ChatColor.RED + "Insufficient permissions.");
    }
    return true;
  }

  /**
   * Checks if the DevMode request was formatted correctly before toggling the setting.
   *
   * @param player             interacting player
   * @param numberOfParameters number of parameters
   */
  private void readRequest(Player player, int numberOfParameters) {
    switch (numberOfParameters) {
      case 0 -> toggleDevMode(player);
      default -> player.sendMessage("Unrecognized parameters");
    }
  }

  /**
   * Toggles Developer mode on or off.
   *
   * @param player interacting player
   */
  private void toggleDevMode(Player player) {
    if (!player.hasMetadata("devmode")) {
      player.setMetadata("devmode", new FixedMetadataValue(AethelPlugin.getInstance(), "on"));
      player.sendMessage(ChatColor.GREEN + "[Dev Mode] " + ChatColor.WHITE + "On");
    } else {
      player.removeMetadata("devmode", AethelPlugin.getInstance());
      player.sendMessage(ChatColor.RED + "[Dev Mode] " + ChatColor.WHITE + "Off");
    }
  }
}