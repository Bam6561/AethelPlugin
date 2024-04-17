package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.plugin.MenuInput;
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
 * @version 1.23.12
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
        new Request(user, args).readRequest();
      } else {
        user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Represents a Developer command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @author Danny Nguyen
   * @version 1.23.12
   * @since 1.23.12
   */
  private record Request(Player user, String[] args) {
    /**
     * Checks if the command request was formatted correctly before
     * {@link MenuInput#setIsDeveloper toggling} developer mode.
     */
    private void readRequest() {
      int numberOfParameters = args.length;
      if (numberOfParameters == 0) {
        toggleDeveloperMode();
      } else {
        user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * {@link MenuInput#setIsDeveloper Toggles} developer mode on or off for the user.
     */
    private void toggleDeveloperMode() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      if (!menuInput.isDeveloper()) {
        menuInput.setIsDeveloper(true);
        user.sendMessage(ChatColor.GREEN + "[Developer Mode On]");
      } else {
        menuInput.setIsDeveloper(false);
        user.sendMessage(ChatColor.RED + "[Developer Mode Off]");
      }
    }
  }
}