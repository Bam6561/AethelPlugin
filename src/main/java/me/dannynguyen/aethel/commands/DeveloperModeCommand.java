package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.PluginMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Command invocation that allows the user to
 * bypass conditions for various interactions.
 *
 * @author Danny Nguyen
 * @version 1.14.5
 * @since 1.4.6
 */
public class DeveloperModeCommand implements CommandExecutor {
  /**
   * Executes the DeveloperMode command.
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
      if (user.hasPermission("aethel.developermode")) {
        readRequest(user, args);
      } else {
        user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before toggling developer mode.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    int numberOfParameters = args.length;
    if (numberOfParameters == 0) {
      toggleDeveloperMode(user);
    } else {
      user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Toggles Developer mode on or off for the user.
   *
   * @param user user
   */
  private void toggleDeveloperMode(Player user) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    if (!playerMeta.containsKey(PlayerMeta.DEVELOPER)) {
      playerMeta.put(PlayerMeta.DEVELOPER, "1");
      user.sendMessage(ChatColor.GREEN + "[Developer Mode On]");
    } else {
      playerMeta.remove(PlayerMeta.DEVELOPER);
      user.sendMessage(ChatColor.RED + "[Developer Mode Off]");
    }
  }
}