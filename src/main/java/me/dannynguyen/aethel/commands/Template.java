package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.enums.PluginMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Template is a starting template for commands.
 * It'll do something. We just don't know what yet.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.2.3
 */
public class Template implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(Permission.TEMPLATE.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before doing something.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      default -> interpretParameters(user, args);
    }
  }

  /**
   * Either does something or another thing.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void interpretParameters(Player user, String[] args) {

  }

  private enum Permission {
    TEMPLATE("");

    public final String permission;

    Permission(String permission) {
      this.permission = permission;
    }
  }

  private enum Success {
    TEMPLATE("");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }

  private enum Failure {
    TEMPLATE("");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }
}
