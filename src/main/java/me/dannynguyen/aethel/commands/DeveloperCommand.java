package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.Message;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to bypass conditions for various interactions.
 * <p>
 * Registered through {@link Plugin}.
 *
 * @author Danny Nguyen
 * @version 1.17.17
 * @since 1.4.6
 */
public class DeveloperCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public DeveloperCommand() {
  }

  /**
   * Executes the DeveloperMode command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command parameters
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.developermode")) {
        readRequest(user, args);
      } else {
        user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly
   * before toggling {@link me.dannynguyen.aethel.plugin.system.PluginPlayer developer mode}.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    int numberOfParameters = args.length;
    if (numberOfParameters == 0) {
      toggleDeveloperMode(user);
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Toggles {@link me.dannynguyen.aethel.plugin.system.PluginPlayer developer mode} on or off for the user.
   *
   * @param user user
   */
  private void toggleDeveloperMode(Player user) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
    if (!pluginPlayer.isDeveloper()) {
      pluginPlayer.setIsDeveloper(true);
      user.sendMessage(ChatColor.GREEN + "[Developer Mode On]");
    } else {
      pluginPlayer.setIsDeveloper(false);
      user.sendMessage(ChatColor.RED + "[Developer Mode Off]");
    }
  }
}